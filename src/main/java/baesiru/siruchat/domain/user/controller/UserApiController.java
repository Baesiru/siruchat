package baesiru.siruchat.domain.user.controller;

import baesiru.siruchat.common.annotation.AuthenticatedUser;
import baesiru.siruchat.common.resolver.AuthUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserApiController {
    @GetMapping("/test")
    public ResponseEntity<String> test(@AuthenticatedUser AuthUser authUser) {
        return ResponseEntity.ok(authUser.getUserId());
    }
}
