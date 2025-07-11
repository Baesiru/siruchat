package baesiru.siruchat.domain.chat.controller;

import baesiru.siruchat.common.annotation.AuthenticatedUser;
import baesiru.siruchat.common.api.Api;
import baesiru.siruchat.common.resolver.AuthUser;
import baesiru.siruchat.domain.chat.business.ChatBusiness;
import baesiru.siruchat.domain.chat.controller.model.ChatMessageDto;
import baesiru.siruchat.domain.chat.controller.model.request.ChatRoomCreateRequest;
import baesiru.siruchat.domain.chat.controller.model.response.ChatMessageResponse;
import baesiru.siruchat.domain.chat.controller.model.response.ChatParticipantsResponse;
import baesiru.siruchat.domain.chat.controller.model.response.ChatRoomResponse;
import baesiru.siruchat.domain.chat.controller.model.response.ChatRoomsResponse;
import baesiru.siruchat.domain.user.controller.response.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
public class ChatController {
    @Autowired
    private ChatBusiness chatBusiness;

    @MessageMapping("chat.message.{roomId}")
    public void sendMessage(@AuthenticatedUser AuthUser authUser, @DestinationVariable Long roomId, ChatMessageDto dto) {
        chatBusiness.sendMessage(authUser, roomId, dto);
    }

    @PostMapping("/api/chat/room")
    public Api<ChatRoomResponse> createRoom(@AuthenticatedUser AuthUser authUser, @RequestBody ChatRoomCreateRequest request) {
        return Api.OK(chatBusiness.createRoom(authUser, request));
    }

    @PostMapping("/api/chat/room/{roomId}/join")
    public Api<MessageResponse> joinRoom(@AuthenticatedUser AuthUser authUser, @PathVariable Long roomId) {
        chatBusiness.joinRoom(authUser, roomId);
        MessageResponse messageResponse = new MessageResponse("참여가 완료되었습니다");
        return Api.OK(messageResponse);
    }

    @GetMapping("/api/chat/room/{roomId}/messages")
    public Api<List<ChatMessageResponse>> getMessages(
            @AuthenticatedUser AuthUser authUser,
            @PathVariable Long roomId,
            @RequestParam(required = false) LocalDateTime cursorTime) {
        return Api.OK(chatBusiness.getMessages(authUser, roomId, cursorTime));
    }

    @GetMapping("/api/chat/rooms")
    public Api<List<ChatRoomsResponse>> getRooms(@AuthenticatedUser AuthUser authUser) {
        return Api.OK(chatBusiness.getRoomsByUser(authUser));
    }

    @PostMapping("/api/chat/room/{roomId}/detach")
    public Api<MessageResponse> detachRoom(@AuthenticatedUser AuthUser authUser, @PathVariable Long roomId) {
        chatBusiness.detachRoom(authUser, roomId);
        MessageResponse messageResponse = new MessageResponse("성공적으로 방을 나갔습니다.");
        return Api.OK(messageResponse);
    }

    @GetMapping("/api/chat/room/{roomId}")
    public Api<Object> getParticipants(@PathVariable Long roomId) {
        List<ChatParticipantsResponse> participantsResponses = chatBusiness.getParticipants(roomId);
        return Api.OK(participantsResponses);
    }

    @GetMapping("/api/chat/room/group")
    public Api<Object> getGroupRooms() {
        List<ChatRoomResponse> chatRoomResponses = chatBusiness.getRooms();
        return Api.OK(chatRoomResponses);
    }

    @GetMapping("/api/chat/user")
    public Api<Object> getUsers() {
        List<ChatParticipantsResponse> participantsResponses = chatBusiness.getUsers();
        return Api.OK(participantsResponses);
    }
}
