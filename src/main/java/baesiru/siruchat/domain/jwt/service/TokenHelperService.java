package baesiru.siruchat.domain.jwt.service;

import baesiru.siruchat.common.errorcode.CommonErrorCode;
import baesiru.siruchat.common.errorcode.TokenErrorCode;
import baesiru.siruchat.common.exception.token.TokenException;
import baesiru.siruchat.domain.jwt.ifs.TokenHelperIfs;
import baesiru.siruchat.domain.jwt.model.request.TokenDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class TokenHelperService {
    private final String REFRESH_TOKEN = "refreshtoken";
    private final String USER_ID = "userId";

    @Value("${jwt.refresh-token.plus-hour}")
    private int refreshTokenPlusHour;

    @Autowired
    private TokenHelperIfs tokenHelperIfs;
    @Autowired
    private HttpSession httpSession;

    public TokenDto issueAccessToken(String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put(USER_ID, userId);
        return tokenHelperIfs.issueAccessToken(data);
    }

    public TokenDto issueRefreshToken(String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put(USER_ID, userId);
        return tokenHelperIfs.issueRefreshToken(data);
    }

    public TokenDto reIssueAccessToken(String refreshToken) {
        String userId = validationToken(refreshToken);

        String storedToken = (String) httpSession.getAttribute(REFRESH_TOKEN);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new TokenException(TokenErrorCode.INVALID_TOKEN);
        }

        return issueAccessToken(userId);
    }

    public String validationToken(String token) {
        Map<String, Object> userData = tokenHelperIfs.validationTokenWithThrow(token);

        Object userId = userData.get(USER_ID);
        Objects.requireNonNull(userId, () -> {
            throw new TokenException(CommonErrorCode.NULL_POINT);
        });
        return userId.toString();
    }

    public void saveRefreshToken(String refreshToken) {
        int expirationInSeconds = refreshTokenPlusHour * 60 * 60; // 시간을 초 단위로 변환

        httpSession.setAttribute(REFRESH_TOKEN, refreshToken);
        httpSession.setMaxInactiveInterval(expirationInSeconds); // 세션 만료 시간 설정
    }

}
