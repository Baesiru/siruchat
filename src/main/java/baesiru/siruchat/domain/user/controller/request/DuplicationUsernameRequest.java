package baesiru.siruchat.domain.user.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DuplicationUsernameRequest {
    @Size(min = 1, max = 50, message = "계정 이름은 1자 이상, 50자 이하로 입력해주세요.")
    @NotBlank(message = "필수 입력 사항입니다.")
    private String username;
}
