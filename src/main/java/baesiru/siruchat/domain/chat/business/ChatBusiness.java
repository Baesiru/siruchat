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
import baesiru.siruchat.domain.sse.service.SseService;
import baesiru.siruchat.domain.user.repository.User;
import baesiru.siruchat.domain.user.service.UserService;
import io.micrometer.core.annotation.Timed;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Business
public class ChatBusiness {
    @Autowired
    private ChatService chatService;
    @Autowired
    private UserService userService;
    @Autowired
    private SseService sseService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisTemplate<String, Object> stringRedisTemplate;

    @Value("${spring.rabbitmq.chat.exchange}")
    private String CHAT_EXCHANGE;

    private static final String REDIS_ROOM_ID_KEY = "chat:roomId";


    @Transactional
    public void sendMessage(AuthUser authUser, Long roomId, ChatMessageDto dto) {
        Long userId = Long.parseLong(authUser.getUserId());
        ChatRoom chatRoom = chatService.findRoomByRoomId(roomId);
        dto.setTimestamp(LocalDateTime.now());
        if (chatRoom.getType() == ChatRoomType.ONE_TO_ONE) {
            Participant partner = chatService.findParticipantByRoomIdAndUserIdNot(roomId, userId);
            if (!partner.isActive()) {
                partner.setActive(true);
                partner.setJoinedAt(dto.getTimestamp());
                partner.setDeactivatedAt(null);
                chatService.saveParticipant(partner);
            }
        }
        ChatMessage chatMessage = ChatMessage.builder()
                .senderId(userId)
                .roomId(roomId)
                .content(dto.getContent())
                .type(MessageType.TALK)
                .timestamp(dto.getTimestamp())
                .build();

        ChatMessageResponse chatMessageResponse = ChatMessageResponse.builder()
                .content(dto.getContent())
                .timestamp(dto.getTimestamp())
                .senderId(userId)
                .type(MessageType.TALK)
                .roomId(roomId)
                .build();

        chatService.saveMessage(chatMessage);
        rabbitTemplate.convertAndSend(CHAT_EXCHANGE, "chat.room."+roomId, Api.OK(chatMessageResponse));
        sseService.sendMessage(roomId, chatMessageResponse);
    }

    @Transactional
    public void sendMessageAndSaveWithRbmq(AuthUser authUser, Long roomId, ChatMessageDto dto) {
        Long userId = Long.parseLong(authUser.getUserId());
        ChatRoom chatRoom = chatService.findRoomByRoomId(roomId);
        dto.setTimestamp(LocalDateTime.now());
        if (chatRoom.getType() == ChatRoomType.ONE_TO_ONE) {
            Participant partner = chatService.findParticipantByRoomIdAndUserIdNot(roomId, userId);
            if (!partner.isActive()) {
                partner.setActive(true);
                partner.setJoinedAt(dto.getTimestamp());
                partner.setDeactivatedAt(null);
                chatService.saveParticipant(partner);
            }
        }
        ChatMessage chatMessage = ChatMessage.builder()
                .senderId(userId)
                .roomId(roomId)
                .content(dto.getContent())
                .type(MessageType.TALK)
                .timestamp(dto.getTimestamp())
                .build();

        ChatMessageResponse chatMessageResponse = ChatMessageResponse.builder()
                .content(dto.getContent())
                .timestamp(dto.getTimestamp())
                .senderId(userId)
                .type(MessageType.TALK)
                .roomId(roomId)
                .build();

        rabbitTemplate.convertAndSend(CHAT_EXCHANGE, "chat.room."+roomId, Api.OK(chatMessageResponse));
        rabbitTemplate.convertAndSend(CHAT_EXCHANGE, "chat.save.message", chatMessage);
        sseService.sendMessage(roomId, chatMessageResponse);
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
        Long userId = Long.parseLong(authUser.getUserId());
        ChatRoom room = chatService.createRoom(request, roomId);
        Participant participant = Participant.builder()
                .roomId(roomId)
                .userId(userId)
                .joinedAt(LocalDateTime.now())
                .active(true)
                .build();
        chatService.saveParticipant(participant);

        sseService.addSSeRoom(roomId, userId);

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
        if (sseService.isOnline(partner.getUserId())) {
            sseService.addSSeRoom(roomId, partner.getUserId());
        }
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
        else {
            Participant participant = Participant.builder()
                    .roomId(roomId)
                    .userId(Long.parseLong(authUser.getUserId()))
                    .joinedAt(LocalDateTime.now())
                    .active(true)
                    .build();
            chatService.saveParticipant(participant);
        }
    }

    public List<ChatMessageResponse> getMessages(AuthUser authUser, Long roomId, LocalDateTime cursorTime) {
        Long userId = Long.parseLong(authUser.getUserId());
        Participant participant = chatService.findParticipantByUserIdAndRoomId(userId, roomId);

        LocalDateTime maxTime = getMaxTime(participant.getJoinedAt(), participant.getDeactivatedAt());

        List<ChatMessage> messages = chatService.findMessagesByRoom(roomId, cursorTime, maxTime);
        return messages.stream().map(message -> ChatMessageResponse.builder()
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .senderId(message.getSenderId())
                .type(MessageType.TALK)
                .build()).
                toList();
    }

    private LocalDateTime getMaxTime(LocalDateTime... times) {
        return Arrays.stream(times)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    public List<ChatRoomsResponse> getRoomsByUser(AuthUser authUser) {
        Long userId = Long.parseLong(authUser.getUserId());
        List<Participant> participants = chatService.findParticipantsByUserIdAndActiveTrue(userId);

        Map<Long, Participant> participantMap = participants.stream()
                .collect(Collectors.toMap(Participant::getRoomId, p -> p));

        List<Long> roomIds = participants.stream()
                .map(Participant::getRoomId)
                .toList();
        List<ChatRoom> rooms = chatService.findRoomNamesByRoomIds(roomIds);
        List<ChatRoomsResponse> responseList = rooms.stream()
                .map(chatRoom -> {
                    Long roomId = chatRoom.getRoomId();
                    String name = chatRoom.getName();
                    if (chatRoom.getType() == ChatRoomType.ONE_TO_ONE) {
                        Long partnerId = chatService.findParticipantByRoomIdAndUserIdNot(roomId, userId).getUserId();
                        name = userService.findById(partnerId).getNickname() + "님과의 채팅방";
                    }

                    Participant participant = participantMap.get(roomId);
                    LocalDateTime maxTime = getMaxTime(participant.getJoinedAt(), participant.getDeactivatedAt());


                    ChatMessage latestMessage = chatService.findFirstByRoomIdAndTimestampAfterOrderByTimestampDesc(roomId, maxTime);
                    ChatMessageResponse messageResponse = (latestMessage != null)
                            ? new ChatMessageResponse(
                            latestMessage.getSenderId(),
                            latestMessage.getContent(),
                            latestMessage.getType(),
                            latestMessage.getTimestamp(),
                            latestMessage.getRoomId())
                            : null;
                    return new ChatRoomsResponse(roomId, name, messageResponse);
                })
                .toList();

        return responseList;
    }

    @Transactional
    public void detachRoom(AuthUser authUser, Long roomId) {
        Long userId = Long.parseLong(authUser.getUserId());
        ChatRoom chatRoom = chatService.findRoomByRoomId(roomId);
        Participant participant = chatService.findParticipantByUserIdAndRoomId(userId, roomId);
        if (chatRoom.getType() == ChatRoomType.ONE_TO_ONE) {
            participant.setActive(false);
            participant.setDeactivatedAt(LocalDateTime.now());
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
