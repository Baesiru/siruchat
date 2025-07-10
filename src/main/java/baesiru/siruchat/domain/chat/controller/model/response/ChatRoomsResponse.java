package baesiru.siruchat.domain.chat.controller.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomsResponse {
    private Long roomId;
    private String roomName;
    private ChatMessageResponse message;
}
