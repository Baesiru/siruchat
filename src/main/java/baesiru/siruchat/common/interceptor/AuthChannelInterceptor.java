package baesiru.siruchat.common.interceptor;

import baesiru.siruchat.common.errorcode.TokenErrorCode;
import baesiru.siruchat.common.exception.token.TokenException;
import baesiru.siruchat.domain.jwt.helper.TokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class AuthChannelInterceptor implements ChannelInterceptor {
    @Autowired
    private TokenHelper tokenHelper;

    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT == accessor.getCommand()) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new TokenException(TokenErrorCode.INVALID_TOKEN);
            }

            String token = authHeader.substring(7);
            String userId = tokenHelper.validationTokenWithThrow(token).get("userId").toString();

            accessor.getSessionAttributes().put("userId", userId);
        }
        return message;
    }
}
