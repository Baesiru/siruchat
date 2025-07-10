package baesiru.siruchat.domain.chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findTop100ByRoomIdOrderByTimestampDesc(Long roomId);

    List<ChatMessage> findTop100ByRoomIdAndTimestampBeforeOrderByTimestampDesc(Long roomId, LocalDateTime cursorTime);

    Optional<ChatMessage> findFirstByRoomIdOrderByTimestampDesc(Long roomId);
}
