import SockJS from 'sockjs-client';
import Stomp from 'webstomp-client';

export default {
    install(Vue, options) {
        let stompClient = null;
        let connected = false;
        let onDisconnectCallbacks = []; // 存储断开连接时的回调函数

        const connect = (successCallback, errorCallback) => {
            if (connected && stompClient && stompClient.connected) {
                successCallback();
                return;
            }
            const url = localStorage.getItem('websocketSessionInfo');
            if (url) {
                // 如果有会话信息，则使用该信息尝试重新连接
                const connection = new SockJS(url);
                stompClient = Stomp.over(connection);
            } else {
                // 没有会话信息，建立新连接
                const connection = new SockJS(options.url);
                stompClient = Stomp.over(connection);
            }
            stompClient.connect({}, frame => {
                connected = true;
                sessionStorage.setItem("websocketSessionInfo", options.url)
                console.log('Connected: ' + frame);
                successCallback(frame);
            }, error => {
                console.error('Connection error: ' + error);
                connected = false;
                if (typeof errorCallback === 'function') {
                    errorCallback(error);
                }
                connect()
            });
        };
        

        const websocketPlugin = {
            connect,
            send: (destination, body = {}) => {
                try {
                    if (connected && stompClient && stompClient.connected) {
                        stompClient.send(destination, JSON.stringify(body), {});
                    } else {
                        console.error('WebSocket connection is not established.');
                    }
                } catch (error) {
                    console.error('Error sending message:', error);
                    // 在这里可以进行错误处理，比如重试或显示错误信息
                }
            },
            subscribe: (endpoint, callback) => {
                connect(() => {
                    try {
                        if (stompClient && stompClient.connected) {
                            return stompClient.subscribe(endpoint, message => {
                                callback(JSON.parse(message.body));
                            });
                        } else {
                            console.error('WebSocket connection is not established.');
                        }
                    } catch (error) {
                        console.error('Error subscribing to topic:', error);
                        // 处理错误
                    }
                });
            },
            unsubscribe: (subscription) => {
                if (subscription) {
                    subscription.unsubscribe();
                }
            },
            
            disconnect: () => {
                if (stompClient && connected) {
                    stompClient.disconnect(() => {
                        console.log("Disconnected");
                        connected = false;
                        onDisconnectCallbacks.forEach(callback => callback());
                    });
                }
            },
        };


        window.addEventListener('beforeunload', () => {
            connected = false;
            if (stompClient && stompClient.connected) {
                stompClient.disconnect();
            }
        });


        Vue.prototype.$websocket = websocketPlugin;
    }
};