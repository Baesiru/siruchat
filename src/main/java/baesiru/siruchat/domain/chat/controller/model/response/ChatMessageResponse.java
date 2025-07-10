package baesiru.siruchat.domain.chat.controller.model.response;

import baesiru.siruchat.domain.chat.repository.enums.MessageType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private Long senderId;
    private String content;
    private MessageType type;
    private LocalDateTime timestamp;
}
