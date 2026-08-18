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
public class UpdatePasswordRequest {
    @NotBlank(message = "Không được để trống mật khẩu cũ!")
    private String oldPassword;
    @NotBlank(message = "Không được để trống mật khẩu mới!")
    private String newPassword;
    @NotBlank(message = "Vui lòng nhập lại mật khẩu!")
    private String confirmPassword;
}
