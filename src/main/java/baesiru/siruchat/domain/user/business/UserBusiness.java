package baesiru.siruchat.domain.user.business;

import baesiru.siruchat.common.annotation.Business;
import baesiru.siruchat.common.errorcode.UserErrorCode;
import baesiru.siruchat.common.exception.user.LoginFailException;
import baesiru.siruchat.domain.jwt.model.request.TokenDto;
import baesiru.siruchat.domain.jwt.model.response.TokenResponse;
import baesiru.siruchat.domain.jwt.service.TokenIssueService;
import baesiru.siruchat.domain.user.controller.request.DuplicationNicknameRequest;
import baesiru.siruchat.domain.user.controller.request.DuplicationUsernameRequest;
import baesiru.siruchat.domain.user.controller.request.LoginRequest;
import baesiru.siruchat.domain.user.controller.request.RegisterRequest;
import baesiru.siruchat.domain.user.controller.response.MessageResponse;
import baesiru.siruchat.domain.user.repository.User;
import baesiru.siruchat.domain.user.repository.enums.UserStatus;
import baesiru.siruchat.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Business
public class UserBusiness {
    @Autowired
    private UserService userService;
    @Autowired
    private TokenIssueService tokenIssueService;
    @Autowired
    private ModelMapper modelMapper;

    public MessageResponse duplicationNickname(DuplicationNicknameRequest duplicationNicknameRequest) {
        String nickname = duplicationNicknameRequest.getNickname();
        userService.existsByNickname(nickname);
        return new MessageResponse("사용 가능한 닉네임입니다.");
    }

    public MessageResponse duplicationUsername(DuplicationUsernameRequest duplicationUsernameRequest) {
        String username = duplicationUsernameRequest.getUsername();
        userService.existsByUsername(username);
        return new MessageResponse("사용 가능한 아이디입니다.");
    }

    @Transactional
    public MessageResponse register(RegisterRequest registerRequest) {
        userService.existsByNickname(registerRequest.getNickname());
        userService.existsByUsername(registerRequest.getUsername());
        User user = modelMapper.map(registerRequest, User.class);
        user.setRegisteredAt(LocalDateTime.now());
        user.setStatus(UserStatus.REGISTERED);
        user.setPassword(BCrypt.hashpw(registerRequest.getPassword(), BCrypt.gensalt()));
        userService.save(user);
        return new MessageResponse("가입이 완료되었습니다.");
    }

    @Transactional
    public TokenResponse login(LoginRequest loginRequest) {
        User user = userService.findByUsername(loginRequest.getUsername());
        if (!BCrypt.checkpw(loginRequest.getPassword(), user.getPassword())) {
            throw new LoginFailException(UserErrorCode.LOGIN_FAIL);
        }
        user.setLastLoginAt(LocalDateTime.now());
        userService.save(user);
        TokenResponse tokenResponse = tokenIssueService.issueToken(user.getId().toString());
        return tokenResponse;
    }

    public TokenDto reIssueAccessToken(String refreshToken) {
        TokenDto tokenDto = tokenIssueService.reIssueAccessToken(refreshToken);
        return tokenDto;
    }
}
