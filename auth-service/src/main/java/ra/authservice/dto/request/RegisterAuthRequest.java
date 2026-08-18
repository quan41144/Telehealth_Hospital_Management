package ra.authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RegisterAuthRequest {
    @NotBlank(message = "Không được để trống tên đăng nhập!")
    private String username;
    @NotBlank(message = "Không được để trống mật khẩu đăng nhập!")
    private String password;
    @NotBlank(message = "Vui lòng nhập lại mật khẩu!")
    private String confirmPassword;
    @NotBlank(message = "Email không được để trống!")
    @Email(message = "Email không đúng định dạng!")
    private String email;
    @NotBlank(message = "Phone không được để trống!")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải đủ 10 chữ số!")
    private String phone;
}
