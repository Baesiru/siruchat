package baesiru.siruchat.common.interceptor;

import baesiru.siruchat.common.errorcode.TokenErrorCode;
import baesiru.siruchat.common.exception.token.NotPermittedException;
import baesiru.siruchat.domain.jwt.helper.TokenHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class AuthMvcInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenHelper tokenHelper;

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new NotPermittedException(TokenErrorCode.INVALID_TOKEN);
        }

        String token = authHeader.substring(7);

        String userId = tokenHelper.validationTokenWithThrow(token).get("userId").toString();

        request.setAttribute("userId", userId);
        return true;
    }
}
