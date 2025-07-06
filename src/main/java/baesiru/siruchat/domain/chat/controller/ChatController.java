package baesiru.siruchat.domain.chat.controller;

import baesiru.siruchat.common.annotation.AuthenticatedUser;
import baesiru.siruchat.common.resolver.AuthUser;
import baesiru.siruchat.domain.chat.business.ChatBusiness;
import baesiru.siruchat.domain.chat.controller.model.ChatMessageDto;
import baesiru.siruchat.domain.chat.controller.model.request.ChatRoomCreateRequest;
import baesiru.siruchat.domain.chat.controller.model.response.ChatMessageResponse;
import baesiru.siruchat.domain.chat.controller.model.response.ChatRoomResponse;
import baesiru.siruchat.domain.chat.repository.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class ChatController {
    @Autowired
    private ChatBusiness chatBusiness;

    @MessageMapping("/chat/message")
    public void handleMessage(ChatMessageDto dto) {
        chatBusiness.handleIncomingMessage(dto);
    }

    @MessageMapping("/chat.send")
    public void sendMessage(@AuthenticatedUser AuthUser user, ChatMessage message) {
        log.info(">> Message from userId = {}", user.getUserId());
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
