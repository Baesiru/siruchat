package baesiru.siruchat.domain.chat.controller.model.response;

import baesiru.siruchat.domain.chat.repository.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private String senderName;
    private String content;
    private MessageType type;
    private LocalDateTime timestamp;
}
