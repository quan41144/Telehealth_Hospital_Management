package ra.authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ra.authservice.dto.request.UpdatePasswordRequest;
import ra.authservice.dto.request.UpdateRoleRequest;
import ra.authservice.dto.response.ApiResponse;
import ra.authservice.service.AuthService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth/users")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal")
    @PutMapping("/{userId}/password")
    public ResponseEntity<ApiResponse<?>> updatePassword(
            @PathVariable Long userId,
            @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Thay đổi mật khẩu thành công!",
                authService.updatePassword(userId, updatePasswordRequest),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/role/{userId}")
    public ResponseEntity<ApiResponse<?>> updateRole(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateRoleRequest updateRoleRequest) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Cập nhật role thành công!",
                authService.updateRole(userId, updateRoleRequest),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
}
