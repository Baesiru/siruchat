package baesiru.siruchat.domain.chat.controller.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatParticipantsResponse {
    private Long userId;
    private String nickname;
}
