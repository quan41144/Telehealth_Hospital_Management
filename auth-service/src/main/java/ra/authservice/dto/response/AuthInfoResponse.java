package ra.authservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ra.authservice.entity.Role;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AuthInfoResponse {
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private Set<Role> role;
}
