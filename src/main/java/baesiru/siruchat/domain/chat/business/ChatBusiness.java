package baesiru.siruchat.domain.chat.business;

import baesiru.siruchat.common.annotation.Business;
import baesiru.siruchat.common.api.Api;
import baesiru.siruchat.common.resolver.AuthUser;
import baesiru.siruchat.domain.chat.controller.model.ChatMessageDto;
import baesiru.siruchat.domain.chat.controller.model.response.ChatMessageResponse;
import baesiru.siruchat.domain.chat.controller.model.request.ChatRoomCreateRequest;
import baesiru.siruchat.domain.chat.controller.model.response.ChatParticipantsResponse;
import baesiru.siruchat.domain.chat.controller.model.response.ChatRoomResponse;
import baesiru.siruchat.domain.chat.controller.model.response.ChatRoomsResponse;
import baesiru.siruchat.domain.chat.repository.*;
import baesiru.siruchat.domain.chat.repository.enums.ChatRoomType;
import baesiru.siruchat.domain.chat.repository.enums.MessageType;
import baesiru.siruchat.domain.chat.service.ChatService;
import baesiru.siruchat.domain.user.repository.User;
import baesiru.siruchat.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Business
public class ChatBusiness {
    @Autowired
    private ChatService chatService;
    @Autowired
    private UserService userService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisTemplate<String, Object> stringRedisTemplate;

    @Value("${spring.rabbitmq.chat.exchange}")
    private String CHAT_EXCHANGE;

    private static final String REDIS_ROOM_ID_KEY = "chat:roomId";




    @Transactional
    public void sendMessage(Long roomId, ChatMessageDto dto) {
        ChatMessage savedMessage = chatService.saveMessage(dto, roomId);
        ChatMessageResponse chatMessageResponse = ChatMessageResponse.builder()
                .content(savedMessage.getContent())
                .timestamp(savedMessage.getTimestamp())
                .senderId(savedMessage.getSenderId())
                .type(MessageType.TALK)
                .build();
        rabbitTemplate.convertAndSend(CHAT_EXCHANGE, "chat.room."+roomId, Api.OK(chatMessageResponse));
    }

    private Long generateRoomId() {
        ValueOperations<String, Object> ops = stringRedisTemplate.opsForValue();
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(REDIS_ROOM_ID_KEY))) {
            Long lastId = chatService.findTopByOrderByRoomIdDesc();
            ops.set(REDIS_ROOM_ID_KEY, String.valueOf(lastId != null ? lastId : 0L));
        }
        return ops.increment(REDIS_ROOM_ID_KEY);
    }

    @Transactional
    public ChatRoomResponse createRoom(AuthUser authUser, ChatRoomCreateRequest request) {
        Long roomId = generateRoomId();
        ChatRoom room = chatService.createRoom(request, roomId);
        Participant participant = Participant.builder()
                .roomId(roomId)
                .userId(Long.parseLong(authUser.getUserId()))
                .joinedAt(LocalDateTime.now())
                .active(true)
                .build();
        chatService.saveParticipant(participant);

        if (request.getType() == ChatRoomType.ONE_TO_ONE) {
            createOneToOneRoom(roomId, request.getPartnerId());
        }

        return ChatRoomResponse.builder()
                .name(room.getName())
                .roomId(roomId)
                .type(room.getType())
                .createdAt(room.getCreatedAt())
                .build();
    }

    private void createOneToOneRoom(Long roomId, Long partnerId) {
        Participant partner = Participant.builder()
                .roomId(roomId)
                .userId(partnerId)
                .joinedAt(LocalDateTime.now())
                .active(true)
                .build();
        chatService.saveParticipant(partner);
    }

    @Transactional
    public void joinRoom(AuthUser authUser, Long roomId) {
        Long userId = Long.parseLong(authUser.getUserId());
        ChatRoom chatRoom = chatService.findRoomByRoomId(roomId);

        if (chatRoom.getType() == ChatRoomType.ONE_TO_ONE) {
            Participant participant = chatService.findParticipantByUserIdAndRoomId(userId, roomId);
            participant.setActive(true);
            participant.setJoinedAt(LocalDateTime.now());
            chatService.saveParticipant(participant);
        }
        Participant participant = Participant.builder()
                .roomId(roomId)
                .userId(Long.parseLong(authUser.getUserId()))
                .joinedAt(LocalDateTime.now())
                .active(true)
                .build();
        chatService.saveParticipant(participant);
    }

    public List<ChatMessageResponse> getMessages(AuthUser authUser, Long roomId, LocalDateTime cursorTime) {
        Long userId = Long.parseLong(authUser.getUserId());
        chatService.findParticipantByUserIdAndRoomId(userId, roomId);
        List<ChatMessage> messages = chatService.findMessagesByRoom(roomId, cursorTime);
        return messages.stream().map(message -> ChatMessageResponse.builder()
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .senderId(message.getSenderId())
                .type(MessageType.TALK)
                .build()).
                toList();
    }

    public List<ChatRoomsResponse> getRoomsByUser(AuthUser authUser) {
        Long userId = Long.parseLong(authUser.getUserId());
        List<Participant> participants = chatService.findParticipantsByUserIdAndActiveTrue(userId);
        List<Long> roomIds = participants.stream().map(Participant::getRoomId).toList();
        List<ChatRoom> rooms = chatService.findRoomNamesByRoomIds(roomIds);
        List<ChatRoomsResponse> responseList = new ArrayList<>();
        for (ChatRoom chatRoom : rooms) {
            Long roomId = chatRoom.getRoomId();
            String name = chatRoom.getName();
            ChatMessage latestMessage = chatService.findFirstByRoomIdOrderByCreatedAtDesc(roomId);

            ChatMessageResponse messageResponse = null;
            if (latestMessage != null) {
                messageResponse = new ChatMessageResponse(
                        latestMessage.getSenderId(),
                        latestMessage.getContent(),
                        latestMessage.getType(),
                        latestMessage.getTimestamp()
                );
            }

            ChatRoomsResponse roomResponse = new ChatRoomsResponse(
                    roomId,
                    chatRoom.getName(),
                    messageResponse
            );

            responseList.add(roomResponse);
        }
        return responseList;
    }

    @Transactional
    public void detachRoom(AuthUser authUser, Long roomId) {
        Long userId = Long.parseLong(authUser.getUserId());
        ChatRoom chatRoom = chatService.findRoomByRoomId(roomId);
        Participant participant = chatService.findParticipantByUserId(userId);
        if (chatRoom.getType() == ChatRoomType.ONE_TO_ONE) {
            participant.setActive(false);
            chatService.saveParticipant(participant);
        }
        else {
            chatService.delete(participant);
        }
    }

    public List<ChatParticipantsResponse> getParticipants(Long roomId) {
        List<Participant> participants = chatService.findParticipantsByRoomId(roomId);
        List<Long> userIds = participants.stream().map(participant -> participant.getUserId()).toList();
        List<User> users = userService.findNicknamesByUserId(userIds);
        List<ChatParticipantsResponse> responseList = users.stream().map(user -> new ChatParticipantsResponse(user.getId(), user.getNickname())).toList();
        return responseList;
    }

    public List<ChatParticipantsResponse> getUsers() {
        List<User> users = userService.findAll();
        List<ChatParticipantsResponse> responseList = users.stream().map(user -> new ChatParticipantsResponse(user.getId(), user.getNickname())).toList();
        return responseList;
    }

    public List<ChatRoomResponse> getRooms() {
        List<ChatRoom> chatRooms = chatService.findGroupRooms();
        List<ChatRoomResponse> responseList = chatRooms.stream()
                .map(chatRoom -> new ChatRoomResponse(chatRoom.getName(), chatRoom.getRoomId(), chatRoom.getType(), chatRoom.getCreatedAt()))
                .toList();
        return responseList;
    }
}
