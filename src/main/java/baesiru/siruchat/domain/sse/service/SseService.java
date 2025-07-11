package baesiru.siruchat.domain.sse.service;

import baesiru.siruchat.domain.chat.controller.model.response.ChatMessageResponse;
import baesiru.siruchat.domain.chat.repository.Participant;
import baesiru.siruchat.domain.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ChatService chatService;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private static final Long TIMEOUT = 60L * 60 * 1000;

    public SseEmitter connect(Long userId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitters.put(userId, emitter);
        String onlineKey = "online:" + userId;

        redisTemplate.opsForValue().set(onlineKey, "true");
        List<Long> roomIds = chatService.findParticipantsByUserIdAndActiveTrue(userId)
                .stream().map(Participant::getRoomId).toList();
        if (!roomIds.isEmpty()) {
            for (Long roomId : roomIds) {
                addSSeRoom(roomId, userId);            }
        }
        emitter.onCompletion(() -> disconnect(userId));
        emitter.onTimeout(() -> disconnect(userId));

        return emitter;
    }

    public void addSSeRoom(Long roomId, Long userId) {
        redisTemplate.opsForSet().add("room:" + roomId + ":users", userId.toString());
    }

    public void sendMessage(Long roomId, ChatMessageResponse message) {
        Set<Object> userIds = redisTemplate.opsForSet().members("room:" + roomId + ":users");
        if (userIds == null) return;

        for (Object userIdObj : userIds) {
            Long userId = Long.parseLong(userIdObj.toString());
            SseEmitter emitter = emitters.get(userId);
            if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("chat")
                            .data(message));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                    disconnect(userId);
                }
            }
        }
    }

    public void disconnect(Long userId) {
        emitters.remove(userId);
        redisTemplate.delete("online:" + userId);
        removeUserFromAllRooms(userId);
    }

    public void removeUserFromAllRooms(Long userId) {
        List<Long> roomIds = chatService.findParticipantsByUserIdAndActiveTrue(userId)
                .stream().map(Participant::getRoomId).toList();
        for (Long roomId : roomIds) {
            redisTemplate.opsForSet().remove("room:" + roomId + ":users", userId.toString());
        }
    }

    public boolean isOnline(Long userId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("online:" + userId));
    }
}
