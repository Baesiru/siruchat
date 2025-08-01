package baesiru.siruchat.domain.chat.controller.model;

import baesiru.siruchat.domain.chat.repository.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private Long senderId;
    @NotBlank(message = "내용은 필수 입력 사항입니다.")
    private String content;
    @NotBlank(message = "메시지 타입은 필수 입력 사항입니다.")
    private MessageType type;
    private LocalDateTime timestamp;
}
