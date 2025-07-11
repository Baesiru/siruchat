package baesiru.siruchat.domain.chat.service;

import baesiru.siruchat.domain.chat.controller.model.ChatMessageDto;
import baesiru.siruchat.domain.chat.controller.model.request.ChatRoomCreateRequest;
import baesiru.siruchat.domain.chat.repository.*;
import baesiru.siruchat.domain.chat.repository.enums.ChatRoomType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;


    @Transactional
    public ChatMessage saveMessage(Long userId, ChatMessageDto dto, Long roomId) {
        ChatMessage msg = new ChatMessage();
        msg.setRoomId(roomId);
        msg.setSenderId(userId);
        msg.setContent(dto.getContent());
        msg.setType(dto.getType());
        msg.setTimestamp(LocalDateTime.now());

        return chatMessageRepository.save(msg);
    }

    public Long findTopByOrderByRoomIdDesc() {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findTopByOrderByRoomIdDesc();
        if (chatRoom.isEmpty()) {
            return 0L;
        }
        return chatRoom.get().getRoomId();
    }

    public Participant saveParticipant(Participant partner) {
        return participantRepository.save(partner);
    }

    public ChatRoom findRoomByRoomId(Long roomId) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findByRoomId(roomId);
        if (chatRoom.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 방번호입니다.");
        }
        return chatRoom.get();
    }

    public Participant findParticipantByUserIdAndRoomId(Long userId, Long roomId) {
        Optional<Participant> participant = participantRepository.findByUserIdAndRoomId(userId, roomId);
        if (participant.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 참여자입니다.");
        }
        return participant.get();
    }

    public ChatRoom createRoom(ChatRoomCreateRequest request, Long roomId) {
        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(roomId)
                .type(request.getType())
                .name(request.getName())
                .createdAt(LocalDateTime.now())
                .build();
        return chatRoomRepository.save(chatRoom);
    }

    public List<ChatMessage> findMessagesByRoom(Long roomId, LocalDateTime cursorTime) {
        if (cursorTime == null) {
            return chatMessageRepository.findTop100ByRoomIdOrderByTimestampDesc(roomId);
        }
        else {
            return chatMessageRepository.findTop100ByRoomIdAndTimestampBeforeOrderByTimestampDesc(roomId, cursorTime);
        }
    }

    public List<Participant> findParticipantsByUserIdAndActiveTrue(Long userId) {
        List<Participant> participants = participantRepository.findByUserIdAndActiveTrue(userId);
        return participants;
    }

    public List<ChatRoom> findRoomNamesByRoomIds(List<Long> roomIds) {
        List<ChatRoom> roomNames = chatRoomRepository.findByRoomIdIn(roomIds);
        return roomNames;
    }

    public ChatMessage findFirstByRoomIdOrderByCreatedAtDesc(Long roomId) {
        Optional<ChatMessage> chatMessage = chatMessageRepository.findFirstByRoomIdOrderByTimestampDesc(roomId);
        return chatMessage.orElse(null);
    }

    public Participant findParticipantByUserId(Long userId) {
        Optional<Participant> participant = participantRepository.findByUserId(userId);
        if (participant.isEmpty()) {
            throw new IllegalArgumentException("유저가 존재하지 않습니다.");
        }
        return participant.get();
    }

    public void delete(Participant participant) {
        participantRepository.delete(participant);
    }

    public List<Participant> findParticipantsByRoomId(Long roomId) {
        List<Participant> participants = participantRepository.findByRoomId(roomId);
        return participants;
    }

    public List<ChatRoom> findGroupRooms() {
        List<ChatRoom> chatRooms = chatRoomRepository.findByType(ChatRoomType.GROUP);
        return chatRooms;
    }
}
