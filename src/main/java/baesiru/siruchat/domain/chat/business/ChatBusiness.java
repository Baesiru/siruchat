package baesiru.siruchat.domain.chat.business;

import baesiru.siruchat.common.annotation.Business;
import baesiru.siruchat.domain.chat.controller.ChatMessageDto;
import baesiru.siruchat.domain.chat.controller.ChatMessageResponse;
import baesiru.siruchat.domain.chat.controller.ChatRoomCreateRequest;
import baesiru.siruchat.domain.chat.controller.ChatRoomResponse;
import baesiru.siruchat.domain.chat.repository.ChatMessage;
import baesiru.siruchat.domain.chat.repository.ChatRoom;
import baesiru.siruchat.domain.chat.service.ChatService;
import baesiru.siruchat.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Business
public class ChatBusiness {
    @Autowired
    private ChatService chatService;
    @Autowired
    private UserService userService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    public ChatRoomResponse createRoom(ChatRoomCreateRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("채팅방 이름은 필수입니다.");
        }
        if (request.getType() == null) {
            throw new IllegalArgumentException("채팅방 타입은 필수입니다.");
        }

        ChatRoom room = chatService.createRoom(request);
        return toRoomResponse(room);
    }

    public void joinRoom(Long roomId, Long userId) {
        chatService.addUserToRoom(roomId, userId);
    }

    public Page<ChatMessageResponse> getMessages(Long roomId, int page, int size) {
        Page<ChatMessage> messages = chatService.findMessagesByRoom(roomId, page, size);
        return messages.map(this::toMessageResponse);
    }

    public List<ChatRoomResponse> getRoomsByUser(Long userId) {
        List<ChatRoom> rooms = chatService.findRoomsByUser(userId);
        return rooms.stream()
                .map(this::toRoomResponse)
                .collect(Collectors.toList());
    }

    public void handleIncomingMessage(ChatMessageDto dto) {
        validateMessage(dto);

        String senderName = userService.getUserNameById(dto.getSenderId());

        ChatMessage savedMessage = chatService.saveMessage(dto);

        ChatMessageResponse response = toMessageResponse(savedMessage, senderName);

        messagingTemplate.convertAndSend("/sub/chat/room/" + dto.getRoomId(), response);
    }

    private void validateMessage(ChatMessageDto dto) {
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new IllegalArgumentException("메시지 내용이 비어 있습니다.");
        }
        if (dto.getSenderId() == null || dto.getRoomId() == null) {
            throw new IllegalArgumentException("보낸 사람과 방 ID는 필수입니다.");
        }
        if (dto.getType() == null) {
            throw new IllegalArgumentException("메시지 타입은 필수입니다.");
        }
    }

    private ChatRoomResponse toRoomResponse(ChatRoom room) {
        return new ChatRoomResponse(
                room.getId(),
                room.getName(),
                room.getType(),
                room.getCreatedAt()
        );
    }

    private ChatMessageResponse toMessageResponse(ChatMessage msg, String senderName) {
        return new ChatMessageResponse(
                senderName,
                msg.getContent(),
                msg.getType(),
                msg.getTimestamp()
        );
    }

    private ChatMessageResponse toMessageResponse(ChatMessage msg) {
        String senderName = userService.getUserNameById(msg.getSenderId());
        return toMessageResponse(msg, senderName);
    }
}
