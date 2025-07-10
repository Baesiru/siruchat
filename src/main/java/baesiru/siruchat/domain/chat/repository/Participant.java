package baesiru.siruchat.domain.chat.repository;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Participant {
    @Id
    private String id;
    private Long roomId;
    private Long userId;
    private LocalDateTime joinedAt;
    private boolean active;

}
