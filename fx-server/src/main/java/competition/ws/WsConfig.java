package competition.ws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.*;

import javax.annotation.Resource;

@Configuration
@EnableWebSocketMessageBroker
public class WsConfig implements WebSocketMessageBrokerConfigurer {


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册一个STOMP的endpoint，并指定使用SockJS协议
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS()
                .setStreamBytesLimit(512 * 1024)  // 增加流字节限制
                .setHttpMessageCacheSize(1000)    // 增加HTTP消息缓存
                .setDisconnectDelay(30 * 1000);   // 断开延迟30秒
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 配置一个简单的消息代理，客户端订阅路径前缀为"/topic"、"/queue"
        registry.enableSimpleBroker("/topic")
            .setTaskScheduler(taskScheduler())
            .setHeartbeatValue(new long[]{10000, 20000});
        // 配置一对一消息的前缀（可选）
        registry.setUserDestinationPrefix("/user");
        // 配置客户端发送消息的路径前缀
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(20);
        scheduler.setThreadNamePrefix("websocket-heartbeat-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(60);
        scheduler.initialize();
        return scheduler;
    }


    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration
                .setMessageSizeLimit(128 * 1024)      // 128KB
                .setSendBufferSizeLimit(512 * 1024)   // 512KB
                .setSendTimeLimit(20000)              // 20秒超时
                .setTimeToFirstMessage(30000);        // 首次消息30秒超时
    }

}
