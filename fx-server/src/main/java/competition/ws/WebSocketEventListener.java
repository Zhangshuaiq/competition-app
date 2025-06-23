package competition.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
public class WebSocketEventListener {

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String teamId = headerAccessor.getFirstNativeHeader("teamId");
        if (teamId != null && !teamId.isEmpty()) {
            System.out.println("旧的客户端链接刷新了网页: " + sessionId);
            WsTeamMemberWaiter.addReconnectedTeam(sessionId, teamId);
        } else {
            System.out.println("新的客户端进入: " + sessionId);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        WsTeamMemberWaiter.removeConnectedTeam(sessionId);
    }
}