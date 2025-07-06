package baesiru.siruchat.common.resolver;

import baesiru.siruchat.common.annotation.AuthenticatedUser;
import baesiru.siruchat.common.errorcode.CommonErrorCode;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Objects;

@Component
public class AuthenticatedMessageUserResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean annotation = parameter.hasParameterAnnotation(AuthenticatedUser.class);
        boolean parameterType = parameter.getParameterType().equals(AuthUser.class);
        return annotation && parameterType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Message<?> message) throws Exception {
        boolean required = parameter.getParameterAnnotation(AuthenticatedUser.class).required();
        RequestAttributes requestContext = Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes());

        Object userId = requestContext.getAttribute("userId",
                RequestAttributes.SCOPE_REQUEST);

        if (userId == null && required) {
            throw new RuntimeException(CommonErrorCode.MISSING_REQUIRED_HEADER.getDescription());
        }

        return (userId != null) ? AuthUser.builder().userId(userId.toString()).build() : null;

    }

}
