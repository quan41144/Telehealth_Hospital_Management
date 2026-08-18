package ra.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginAuthRequest {
    @NotBlank(message = "Không được để trống tên đăng nhập!")
    private String username;
    @NotBlank(message = "Không được để trống mật khẩu đăng nhập!")
    private String password;
}
