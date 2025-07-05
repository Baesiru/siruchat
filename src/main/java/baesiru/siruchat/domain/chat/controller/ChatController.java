package baesiru.siruchat.domain.chat.controller;

import baesiru.siruchat.domain.chat.business.ChatBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ChatController {
    @Autowired
    private ChatBusiness chatBusiness;

    @MessageMapping("/chat/message")
    public void handleMessage(ChatMessageDto dto) {
        chatBusiness.handleIncomingMessage(dto);
    }

    @PostMapping("/chat/rooms")
    public ChatRoomResponse createRoom(@RequestBody ChatRoomCreateRequest request) {
        return chatBusiness.createRoom(request);
    }

    @PostMapping("/chat/rooms/{roomId}/join")
    public void joinRoom(@PathVariable Long roomId, @RequestParam Long userId) {
        chatBusiness.joinRoom(roomId, userId);
    }

    @GetMapping("/chat/rooms/{roomId}/messages")
    public Page<ChatMessageResponse> getMessages(
            @PathVariable Long roomId,
            @RequestParam int page,
            @RequestParam int size) {
        return chatBusiness.getMessages(roomId, page, size);
    }

    @GetMapping("/chat/rooms")
    public List<ChatRoomResponse> getRooms(@RequestParam Long userId) {
        return chatBusiness.getRoomsByUser(userId);
    }
}
