package baesiru.siruchat.domain.user.controller;

import baesiru.siruchat.common.api.Api;
import baesiru.siruchat.domain.jwt.model.request.TokenDto;
import baesiru.siruchat.domain.jwt.model.response.TokenResponse;
import baesiru.siruchat.domain.user.business.UserBusiness;
import baesiru.siruchat.domain.user.controller.request.DuplicationNicknameRequest;
import baesiru.siruchat.domain.user.controller.request.DuplicationUsernameRequest;
import baesiru.siruchat.domain.user.controller.request.LoginRequest;
import baesiru.siruchat.domain.user.controller.request.RegisterRequest;
import baesiru.siruchat.domain.user.controller.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/open-api/user")
public class UserOpenApiController {
    @Autowired
    private UserBusiness userBusiness;

    @PostMapping("/duplication/nickname")
    public Api<MessageResponse> duplicationNickname(
            @RequestBody DuplicationNicknameRequest duplicationNicknameRequest) {

        MessageResponse messageResponse = userBusiness.duplicationNickname(duplicationNicknameRequest);

        return Api.OK(messageResponse);
    }

    @PostMapping("/duplication/username")
    public Api<MessageResponse> duplicationUsername(
            @RequestBody DuplicationUsernameRequest duplicationUsernameRequest) {

        MessageResponse messageResponse = userBusiness.duplicationUsername(duplicationUsernameRequest);
        return Api.OK(messageResponse);
    }

    @PostMapping("/register")
    public Api<MessageResponse> register(
            @RequestBody RegisterRequest registerRequest) {
        MessageResponse messageResponse = userBusiness.register(registerRequest);
        return Api.OK(messageResponse);
    }

    @PostMapping("/login")
    public Api<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = userBusiness.login(loginRequest);
        return Api.OK(tokenResponse);
    }

    @PostMapping("/reissue")
    public Api<TokenDto> reIssueAccessToken(@RequestHeader("Authorization") String refreshToken) {
        TokenDto response = userBusiness.reIssueAccessToken(refreshToken);
        return Api.OK(response);
    }
}
