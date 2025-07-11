package baesiru.siruchat.domain.sse.controller;
import baesiru.siruchat.domain.jwt.service.TokenHelperService;
import baesiru.siruchat.domain.sse.service.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class SseController {
    @Autowired
    private SseService sseService;
    @Autowired
    private TokenHelperService tokenHelperService;
    @GetMapping(value = "/open-api/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@RequestParam String token) {
        Long userId = Long.parseLong(tokenHelperService.validationToken(token));
        return sseService.connect(userId);
    }
}
