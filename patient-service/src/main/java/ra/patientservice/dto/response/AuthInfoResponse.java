package ra.patientservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AuthInfoResponse {
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String role;
}
