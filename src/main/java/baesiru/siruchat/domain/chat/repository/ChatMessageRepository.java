package baesiru.siruchat.domain.chat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Page<ChatMessage> findByChatRoomId(Long roomId, Pageable pageable);

    Page<ChatMessage> findByChatRoomIdOrderByTimestampDesc(Long roomId, Pageable pageable);
}
