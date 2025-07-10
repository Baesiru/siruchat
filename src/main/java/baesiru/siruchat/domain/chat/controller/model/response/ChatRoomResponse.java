package baesiru.siruchat.domain.chat.controller.model.response;

import baesiru.siruchat.domain.chat.repository.enums.ChatRoomType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponse {
    private String name;
    private Long roomId;
    private ChatRoomType type;
    private LocalDateTime createdAt;

}
