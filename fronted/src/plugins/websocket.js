import SockJS from 'sockjs-client';
import Stomp from 'webstomp-client';

export default {
    install(Vue, options) {
        let stompClient = null;
        let sockJSConnection = null;
        let connected = false;
        let reconnectAttempts = 0;
        let maxReconnectAttempts = 5;
        let reconnectInterval = 2000;
        let maxReconnectInterval = 30000;
        let reconnectTimer = null;
        let heartbeatTimer = null;
        let isManualDisconnect = false;
        let connectionCheckTimer = null;

        // 新增：仅用于僵死连接检测的变量
        let subscriptions = new Map(); // 追踪订阅，用于重连时恢复
        let lastMessageTime = Date.now(); // 记录最后收到消息的时间
        let deadConnectionCheckTimer = null; // 僵死连接检测定时器

        const connect = (successCallback, errorCallback) => {
            if (connected && stompClient && stompClient.connected) {
                successCallback && successCallback();
                return;
            }

            // 清理之前的连接
            if (stompClient) {
                try {
                    stompClient.disconnect();
                } catch (e) {
                    console.log('清理旧连接时出错:', e);
                }
            }

            const url = localStorage.getItem('websocketSessionInfo') || options.url;
            sockJSConnection = new SockJS(url);
            stompClient = Stomp.over(sockJSConnection);

            // 监听SockJS底层事件
            setupSockJSEventListeners(sockJSConnection);

            // 配置心跳 - 保持原有设置
            stompClient.heartbeat.outgoing = 10000;
            stompClient.heartbeat.incoming = 10000;

            let teamId = "";
            const user = sessionStorage.getItem('user');
            const member = sessionStorage.getItem("member");

            if (user) {
                teamId = JSON.parse(user).shortName + "-" + member;
            }

            stompClient.connect(
                {teamId: teamId},
                frame => {
                    connected = true;
                    reconnectAttempts = 0;
                    reconnectInterval = 2000;
                    isManualDisconnect = false;
                    lastMessageTime = Date.now(); // 重置最后消息时间

                    localStorage.setItem("websocketSessionInfo", options.url);
                    console.log('✅ WebSocket连接成功');

                    // 更新连接状态
                    updateConnectionStatus('connected');

                    startHeartbeat(); // 保持原有心跳机制
                    startConnectionMonitoring();
                    startDeadConnectionMonitoring(); // 新增：启动僵死连接监控

                    // 恢复之前的订阅
                    restoreSubscriptions();

                    successCallback && successCallback(frame);
                },
                error => {
                    console.error('❌ WebSocket连接错误: ' + error);
                    connected = false;
                    updateConnectionStatus('disconnected');

                    if (typeof errorCallback === 'function') {
                        errorCallback(error);
                    }

                    // 延迟处理错误，避免立即重连
                    setTimeout(() => {
                        handleConnectionError(error);
                    }, 1000);
                }
            );
        };

        /**
         * 更新连接状态到localStorage
         */
        const updateConnectionStatus = (status, extra = {}) => {
            const statusInfo = {
                status: status, // 'connected', 'disconnected', 'reconnecting', 'server-unavailable'
                timestamp: Date.now(),
                reconnectAttempts: reconnectAttempts,
                ...extra
            };
            localStorage.setItem('websocketStatus', JSON.stringify(statusInfo));
            console.log('连接状态更新:', status);
        };

        /**
         * 设置SockJS底层事件监听
         */
        const setupSockJSEventListeners = (connection) => {
            connection.onopen = () => {
                console.log('SockJS连接已建立');
            };

            connection.onclose = (event) => {
                console.log('SockJS连接已关闭:', event.code, event.reason);
                connected = false;
                updateConnectionStatus('disconnected', { closeCode: event.code, closeReason: event.reason });

                if (!isManualDisconnect) {
                    setTimeout(() => {
                        handleConnectionError(`SockJS closed: ${event.code}`);
                    }, 1000);
                }
            };

            connection.onerror = (error) => {
                console.error('SockJS连接错误:', error);
                connected = false;
                updateConnectionStatus('disconnected', { error: error.toString() });

                if (!isManualDisconnect) {
                    setTimeout(() => {
                        handleConnectionError(error);
                    }, 1000);
                }
            };
        };

        /**
         * 启动连接状态监控
         */
        const startConnectionMonitoring = () => {
            stopConnectionMonitoring();

            connectionCheckTimer = setInterval(() => {
                if (!connected || !stompClient || !stompClient.connected) {
                    console.log('⚠️ 连接状态检查失败，尝试重连...');
                    connected = false;
                    updateConnectionStatus('disconnected', { reason: 'monitoring-check-failed' });

                    if (!isManualDisconnect) {
                        handleConnectionError('Connection lost during monitoring');
                    }
                }
            }, 30000); // 每30秒检查一次
        };

        /**
         * 停止连接状态监控
         */
        const stopConnectionMonitoring = () => {
            if (connectionCheckTimer) {
                clearInterval(connectionCheckTimer);
                connectionCheckTimer = null;
            }
        };

        /**
         * 新增：僵死连接监控 - 仅检测长时间无消息的情况
         */
        const startDeadConnectionMonitoring = () => {
            if (deadConnectionCheckTimer) {
                clearInterval(deadConnectionCheckTimer);
            }

            deadConnectionCheckTimer = setInterval(() => {
                const now = Date.now();
                const timeSinceLastMessage = now - lastMessageTime;

                // 只有在超过2分钟没有收到任何消息时才认为连接可能僵死
                if (connected && timeSinceLastMessage > 120000) { // 2分钟
                    console.log(`⚠️ 检测到可能的僵死连接: ${timeSinceLastMessage/1000}秒无消息`);
                    handleDeadConnection();
                }
            }, 60000); // 每60秒检查一次
        };

        /**
         * 新增：处理僵死连接
         */
        const handleDeadConnection = () => {
            console.log('🔧 处理僵死连接，强制重连...');

            // 强制断开并重连
            connected = false;
            isManualDisconnect = false;

            if (stompClient) {
                try {
                    stompClient.disconnect();
                } catch (e) {
                    console.log('断开僵死连接时出错:', e);
                }
            }

            setTimeout(() => {
                connect();
            }, 2000);
        };

        /**
         * 新增：恢复订阅
         */
        const restoreSubscriptions = () => {
            if (subscriptions.size === 0) {
                return;
            }

            console.log(`🔄 恢复 ${subscriptions.size} 个订阅...`);

            const oldSubscriptions = new Map(subscriptions);
            subscriptions.clear();

            oldSubscriptions.forEach((sub, id) => {
                try {
                    const subscription = stompClient.subscribe(sub.endpoint, message => {
                        lastMessageTime = Date.now(); // 更新最后消息时间
                        console.log(`📨 [${sub.endpoint}] 收到消息:`, message.body);

                        try {
                            sub.callback(JSON.parse(message.body));
                        } catch (parseError) {
                            sub.callback(message.body);
                        }
                    });

                    subscriptions.set(id, {
                        ...sub,
                        subscription: subscription
                    });

                    console.log(`✅ 恢复订阅: ${sub.endpoint}`);
                } catch (error) {
                    console.error(`❌ 恢复订阅失败 [${sub.endpoint}]:`, error);
                }
            });
        };

        /**
         * 处理连接错误
         */
        const handleConnectionError = async (error) => {
            console.log('处理连接错误:', error);

            if (isManualDisconnect) {
                console.log('手动断开连接，不进行重连');
                return;
            }

            // 检查服务器状态
            const serverStatus = await checkServerStatus();

            if (serverStatus.isServerDown) {
                console.log('🔴 服务器宕机，停止重连并清空会话信息');
                clearSessionAndStopReconnect();
                return;
            }

            if (serverStatus.isNetworkIssue) {
                console.log('🔄 网络问题，尝试重连...');
                attemptReconnect();
            }
        };

        /**
         * 检查服务器状态
         */
        const checkServerStatus = async () => {
            try {
                // 构建测试URL列表
                const testUrls = [];

                if (options.healthCheckUrl) {
                    testUrls.push(options.healthCheckUrl);
                }

                // 添加基础路径
                testUrls.push(options.url.replace(/\/ws$/, '/'));
                testUrls.push(options.url.replace(/\/ws$/, '/api/health'));

                for (const testUrl of testUrls) {
                    try {
                        const controller = new AbortController();
                        const timeoutId = setTimeout(() => controller.abort(), 3000);

                        const response = await fetch(testUrl, {
                            method: 'HEAD',
                            signal: controller.signal,
                            cache: 'no-cache'
                        });

                        clearTimeout(timeoutId);

                        // 任何HTTP响应都说明服务器在运行
                        if (response.status < 500) {
                            return {
                                isServerDown: false,
                                isNetworkIssue: true
                            };
                        }
                    } catch (fetchError) {
                        continue; // 尝试下一个URL
                    }
                }

                // 所有URL都失败
                return {
                    isServerDown: true,
                    isNetworkIssue: false
                };

            } catch (error) {
                console.log('服务器状态检查失败:', error);

                // 默认判断为网络问题，允许重连
                return {
                    isServerDown: false,
                    isNetworkIssue: true
                };
            }
        };

        /**
         * 尝试重连
         */
        const attemptReconnect = () => {
            if (reconnectAttempts >= maxReconnectAttempts) {
                console.log('❌ 达到最大重连次数，停止重连');
                clearSessionAndStopReconnect();
                return;
            }

            reconnectAttempts++;
            updateConnectionStatus('reconnecting', { attempt: reconnectAttempts, maxAttempts: maxReconnectAttempts });

            console.log(`🔄 第${reconnectAttempts}次重连尝试，${reconnectInterval/1000}秒后执行...`);

            if (reconnectTimer) {
                clearTimeout(reconnectTimer);
            }

            reconnectTimer = setTimeout(() => {
                console.log(`🚀 执行第${reconnectAttempts}次重连...`);
                connect(
                    () => {
                        console.log('✅ 重连成功！');
                        updateConnectionStatus('connected', { reconnected: true });
                    },
                    (error) => {
                        console.log('❌ 重连失败:', error);
                        // 指数退避算法
                        reconnectInterval = Math.min(reconnectInterval * 1.5, maxReconnectInterval);

                        // 继续尝试重连
                        setTimeout(() => {
                            attemptReconnect();
                        }, 2000);
                    }
                );
            }, reconnectInterval);
        };

        /**
         * 清空会话信息并停止重连
         */
        const clearSessionAndStopReconnect = () => {
            localStorage.removeItem('websocketSessionInfo');
            sessionStorage.removeItem('websocketSessionInfo');
            updateConnectionStatus('server-unavailable');

            if (reconnectTimer) {
                clearTimeout(reconnectTimer);
                reconnectTimer = null;
            }

            stopHeartbeat();
            stopConnectionMonitoring();

            if (deadConnectionCheckTimer) {
                clearInterval(deadConnectionCheckTimer);
                deadConnectionCheckTimer = null;
            }

            console.log('🗑️ 已清空会话信息，停止重连');
        };

        /**
         * 启动心跳检测 - 保持原有逻辑
         */
        const startHeartbeat = () => {
            stopHeartbeat();

            heartbeatTimer = setInterval(async () => {
                if (!connected || !stompClient || !stompClient.connected) {
                    console.log('💔 连接已断开，停止心跳检测');
                    stopHeartbeat();
                    return;
                }

                try {
                    stompClient.send('/app/heartbeat', JSON.stringify({
                        type: 'ping',
                        timestamp: Date.now()
                    }), {});

                } catch (error) {
                    console.log('💔 心跳发送失败，连接可能已断开');
                    connected = false;

                    if (!isManualDisconnect) {
                        handleConnectionError(error);
                    }
                }
            }, 15000);
        };

        /**
         * 停止心跳检测
         */
        const stopHeartbeat = () => {
            if (heartbeatTimer) {
                clearInterval(heartbeatTimer);
                heartbeatTimer = null;
            }
        };

        const websocketPlugin = {
            connect,

            send: (destination, body = {}) => {
                try {
                    if (connected && stompClient && stompClient.connected) {
                        stompClient.send(destination, JSON.stringify(body), {});
                    } else {
                        console.error('❌ WebSocket连接未建立，无法发送消息');
                        if (!isManualDisconnect) {
                            setTimeout(() => connect(), 1000);
                        }
                    }
                } catch (error) {
                    console.error('❌ 发送消息时出错:', error);
                    connected = false;

                    if (!isManualDisconnect) {
                        handleConnectionError(error);
                    }
                }
            },

            subscribe: (endpoint, callback) => {
                const subscriptionId = `${endpoint}_${Date.now()}_${Math.random()}`;

                connect(() => {
                    try {
                        if (stompClient && stompClient.connected) {
                            const subscription = stompClient.subscribe(endpoint, message => {
                                lastMessageTime = Date.now(); // 更新最后消息时间
                                console.log(`📨 [${endpoint}] 收到消息:`, message.body);

                                try {
                                    callback(JSON.parse(message.body));
                                } catch (parseError) {
                                    console.error('❌ 解析消息失败:', parseError);
                                    callback(message.body);
                                }
                            });

                            // 保存订阅信息，用于重连时恢复
                            subscriptions.set(subscriptionId, {
                                endpoint,
                                subscription,
                                callback
                            });

                            console.log(`✅ 订阅成功: ${endpoint}`);
                            return subscription;
                        } else {
                            console.error('❌ WebSocket连接未建立，无法订阅');
                        }
                    } catch (error) {
                        console.error('❌ 订阅主题时出错:', error);
                    }
                });
            },

            unsubscribe: (subscription) => {
                if (subscription) {
                    subscription.unsubscribe();
                }
            },

            disconnect: () => {
                isManualDisconnect = true;
                updateConnectionStatus('manually-disconnected');

                if (reconnectTimer) {
                    clearTimeout(reconnectTimer);
                    reconnectTimer = null;
                }

                stopHeartbeat();
                stopConnectionMonitoring();

                if (deadConnectionCheckTimer) {
                    clearInterval(deadConnectionCheckTimer);
                    deadConnectionCheckTimer = null;
                }

                if (stompClient && connected) {
                    stompClient.disconnect(() => {
                        console.log("🔌 已断开WebSocket连接");
                        connected = false;
                    });
                }
            },

            // 获取连接状态
            isConnected: () => connected,

            // 获取详细连接状态
            getConnectionStatus: () => {
                try {
                    const statusInfo = localStorage.getItem('websocketStatus');
                    return statusInfo ? JSON.parse(statusInfo) : null;
                } catch (e) {
                    return null;
                }
            },

            // 获取重连次数
            getReconnectAttempts: () => reconnectAttempts,

            // 手动触发重连
            forceReconnect: () => {
                isManualDisconnect = false;
                reconnectAttempts = 0;
                reconnectInterval = 2000;
                connect();
            },

            // 新增：手动处理僵死连接
            forceRecovery: () => {
                console.log('🔧 手动触发连接恢复...');
                handleDeadConnection();
            }
        };

        // 监听页面可见性变化
        document.addEventListener('visibilitychange', () => {
            if (document.visibilityState === 'visible' && !connected && !isManualDisconnect) {
                console.log('👁️ 页面重新可见，检查连接状态...');
                setTimeout(() => {
                    if (!connected) {
                        connect();
                    }
                }, 1000);
            }
        });

        // 监听网络状态变化
        window.addEventListener('online', () => {
            console.log('🌐 网络已恢复，尝试重连...');
            if (!connected && !isManualDisconnect) {
                reconnectAttempts = 0;
                reconnectInterval = 2000;
                setTimeout(() => connect(), 1000);
            }
        });

        window.addEventListener('offline', () => {
            console.log('📵 网络已断开');
            connected = false;
            updateConnectionStatus('network-offline');
        });

        // 页面卸载时断开连接
        window.addEventListener('beforeunload', () => {
            isManualDisconnect = true;
            connected = false;
            if (stompClient && stompClient.connected) {
                stompClient.disconnect();
            }
        });

        Vue.prototype.$websocket = websocketPlugin;
    }
};