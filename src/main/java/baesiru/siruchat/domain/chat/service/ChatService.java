package baesiru.siruchat.domain.chat.service;

import baesiru.siruchat.domain.chat.controller.ChatMessageDto;
import baesiru.siruchat.domain.chat.controller.ChatRoomCreateRequest;
import baesiru.siruchat.domain.chat.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ChatRoomUserRepository chatRoomUserRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Transactional
    public ChatRoom createRoom(ChatRoomCreateRequest request) {
        ChatRoom room = new ChatRoom();
        room.setName(request.getName());
        room.setType(request.getType());
        room.setCreatedAt(LocalDateTime.now());

        ChatRoom savedRoom = chatRoomRepository.save(room);

        return savedRoom;
    }

    @Transactional
    public void addUserToRoom(Long roomId, Long userId) {
        if(chatRoomUserRepository.existsByChatRoomIdAndUserId(roomId, userId)) {
            return;
        }

        ChatRoomUser cru = new ChatRoomUser();
        cru.setChatRoomId(roomId);
        cru.setUserId(userId);
        cru.setJoinedAt(LocalDateTime.now());

        chatRoomUserRepository.save(cru);
    }

    public Page<ChatMessage> findMessagesByRoom(Long roomId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return chatMessageRepository.findByChatRoomIdOrderByTimestampDesc(roomId, pageable);
    }

    public List<ChatRoom> findRoomsByUser(Long userId) {
        List<ChatRoomUser> roomUsers = chatRoomUserRepository.findByUserId(userId);
        List<Long> roomIds = roomUsers.stream()
                .map(ChatRoomUser::getChatRoomId)
                .collect(Collectors.toList());

        return chatRoomRepository.findAllById(roomIds);
    }

    @Transactional
    public ChatMessage saveMessage(ChatMessageDto dto) {
        ChatMessage msg = new ChatMessage();
        msg.setChatRoomId(dto.getRoomId());
        msg.setSenderId(dto.getSenderId());
        msg.setContent(dto.getContent());
        msg.setType(dto.getType());
        msg.setTimestamp(LocalDateTime.now());

        return chatMessageRepository.save(msg);
    }
}
