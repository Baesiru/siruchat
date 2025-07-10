package baesiru.siruchat.domain.chat.repository;

import baesiru.siruchat.domain.chat.repository.enums.ChatRoomType;
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
public class ChatRoom {
    @Id
    private String id;
    private Long roomId;
    @Enumerated(EnumType.STRING)
    private ChatRoomType type;
    private String name;
    private LocalDateTime createdAt;
}
