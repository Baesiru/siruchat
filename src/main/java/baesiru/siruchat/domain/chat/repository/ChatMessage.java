package baesiru.siruchat.domain.chat.repository;

import baesiru.siruchat.domain.chat.repository.enums.MessageType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    private String id;
    private Long senderId;
    private Long roomId;
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType type;
    private LocalDateTime timestamp;
}
