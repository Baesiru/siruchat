package baesiru.siruchat.domain.chat.controller.model.response;

import baesiru.siruchat.domain.chat.repository.enums.ChatRoomType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponse {
    private Long id;
    private String name;
    private ChatRoomType type;
    private LocalDateTime createdAt;

}
