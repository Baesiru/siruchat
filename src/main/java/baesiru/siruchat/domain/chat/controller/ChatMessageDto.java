package baesiru.siruchat.domain.chat.controller;

import baesiru.siruchat.domain.chat.repository.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private Long roomId;
    private Long senderId;
    private String content;
    private MessageType type;
}
