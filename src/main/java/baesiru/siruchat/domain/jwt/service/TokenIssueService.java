package baesiru.siruchat.domain.jwt.service;


import baesiru.siruchat.common.errorcode.CommonErrorCode;
import baesiru.siruchat.common.errorcode.TokenErrorCode;
import baesiru.siruchat.common.exception.token.TokenException;
import baesiru.siruchat.common.exception.token.TokenSignatureException;
import baesiru.siruchat.domain.jwt.model.request.TokenDto;
import baesiru.siruchat.domain.jwt.model.response.TokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TokenIssueService {
    @Autowired
    private TokenHelperService tokenHelperService;

    public TokenResponse issueToken(String userId) {
        return Optional.ofNullable(userId).map(id -> {

            TokenDto accessToken = tokenHelperService.issueAccessToken(id);
            TokenDto refreshToken = tokenHelperService.issueRefreshToken(id);

            tokenHelperService.saveRefreshToken(refreshToken.getToken());

            return TokenResponse.of(accessToken, refreshToken);

        }).orElseThrow(() -> new TokenException(CommonErrorCode.NULL_POINT));
    }

    public TokenDto reIssueAccessToken(String refreshToken) {
        if(refreshToken != null && refreshToken.startsWith("Bearer ")) {
            String token = refreshToken.substring(7);
            return tokenHelperService.reIssueAccessToken(token);
        }
        throw new TokenSignatureException(TokenErrorCode.INVALID_TOKEN);
    }
}
