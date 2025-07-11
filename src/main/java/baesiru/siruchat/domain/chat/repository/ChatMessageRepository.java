package baesiru.siruchat.domain.chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findTop100ByRoomIdAndTimestampAfterOrderByTimestampDesc(Long roomId, LocalDateTime maxTime);

    List<ChatMessage> findTop100ByRoomIdAndTimestampBeforeAndTimestampAfterOrderByTimestampDesc(Long roomId, LocalDateTime cursorTime, LocalDateTime maxTime);
    Optional<ChatMessage> findFirstByRoomIdOrderByTimestampDesc(Long roomId);
    Optional<ChatMessage> findFirstByRoomIdAndTimestampAfterOrderByTimestampDesc(Long roomId, LocalDateTime timestamp);
}
