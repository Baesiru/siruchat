package baesiru.siruchat.domain.chat.repository;

import baesiru.siruchat.domain.chat.repository.enums.ChatRoomType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    Optional<ChatRoom> findTopByOrderByRoomIdDesc();

    Optional<ChatRoom> findByRoomId(Long roomId);

    List<ChatRoom> findByRoomIdIn(List<Long> roomIds);

    List<ChatRoom> findByType(ChatRoomType chatRoomType);
}
