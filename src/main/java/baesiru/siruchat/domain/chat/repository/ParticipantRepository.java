package baesiru.siruchat.domain.chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends MongoRepository<Participant, String> {


    Optional<Participant> findByUserIdAndRoomId(Long userId, Long roomId);

    Optional<Participant> findByUserId(Long userId);

    List<Participant> findByUserIdAndActiveTrue(Long userId);
}
