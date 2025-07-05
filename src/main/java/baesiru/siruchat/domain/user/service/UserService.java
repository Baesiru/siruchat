package baesiru.siruchat.domain.user.service;

import baesiru.siruchat.common.errorcode.UserErrorCode;
import baesiru.siruchat.common.exception.user.NicknameExistsException;
import baesiru.siruchat.common.exception.user.UserNotFoundException;
import baesiru.siruchat.common.exception.user.UsernameExistsException;
import baesiru.siruchat.domain.user.repository.User;
import baesiru.siruchat.domain.user.repository.UserRepository;
import baesiru.siruchat.domain.user.repository.enums.UserStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void existsByNickname(String nickname) {
        boolean existsByNickname = userRepository.existsByNickname(nickname);
        if (existsByNickname) {
            throw new NicknameExistsException(UserErrorCode.EXISTS_USER_NICKNAME);
        }
    }

    public void existsByUsername(String username) {
        boolean existsByUsername = userRepository.existsByUsername(username);
        if (existsByUsername) {
            throw new UsernameExistsException(UserErrorCode.EXISTS_USERNAME);
        }
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        Optional<User> user = userRepository.findByUsernameAndStatus(username, UserStatus.REGISTERED);
        if (!user.isPresent()) {
            throw new UserNotFoundException(UserErrorCode.USER_NOT_FOUND);
        }
        return user.get();
    }

    public String getUserNameById(Long senderId) {
        return userRepository.findById(senderId)
                .map(User::getNickname)
                .orElse("알 수 없음");
    }
}
