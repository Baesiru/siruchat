package baesiru.siruchat.domain.user.repository;

import baesiru.siruchat.domain.user.repository.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNickname(String nickname);

    Optional<User> findByUsername(String username);

    boolean existsByNickname(String nickname);
    boolean existsByUsername(String username);


    Optional<User> findByUsernameAndStatus(String username, UserStatus userStatus);
}
