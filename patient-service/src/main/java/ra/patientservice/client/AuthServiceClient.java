package ra.patientservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ra.patientservice.dto.response.AuthInfoResponse;

@FeignClient(name = "auth-service", url = "http://localhost:8081")
public interface AuthServiceClient {
    @GetMapping("/api/v1/auth/users/{userId}")
    AuthInfoResponse getUserById(@PathVariable Long userId);
}
