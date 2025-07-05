package baesiru.siruchat.domain.chat.controller;

import baesiru.siruchat.domain.chat.repository.enums.ChatRoomType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomCreateRequest {
    private String name;
    private ChatRoomType type;
}
