package baesiru.siruchat.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {
    boolean existsByChatRoomIdAndUserId(Long chatRoomId, Long userId);

    List<ChatRoomUser> findByUserId(Long userId);
}
