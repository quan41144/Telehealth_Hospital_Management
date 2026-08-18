package ra.authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ra.authservice.dto.request.LoginAuthRequest;
import ra.authservice.dto.request.RegainAccessTokenRequest;
import ra.authservice.dto.request.RegisterAuthRequest;
import ra.authservice.dto.request.VerifyEmailRequest;
import ra.authservice.dto.response.ApiResponse;
import ra.authservice.service.AuthService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginAuthRequest loginAuthRequest) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Đăng nhập thành công!",
                authService.login(loginAuthRequest),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody RegisterAuthRequest registerAuthRequest) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Đăng ký tài khoản mới thành công!",
                authService.register(registerAuthRequest),
                null,
                LocalDateTime.now()
        ), HttpStatus.CREATED);
    }
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(
            @RequestHeader(value = "X-User-Id", required = true) Long userId,
            @RequestHeader(value = "Authorization", required = true) String bearerToken) {
        authService.logout(userId, bearerToken);
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Đăng xuất thành công!",
                null,
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<?>> refreshToken(
            @RequestBody RegainAccessTokenRequest request,
            @RequestHeader(value = "Authorization", required = true) String bearerToken) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Lấy Access Token mới thành công!",
                authService.refreshToken(request, bearerToken),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<?>> verifyEmail(@Valid @RequestBody VerifyEmailRequest verifyEmailRequest) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Xác thực thành công!",
                authService.verifyEmail(verifyEmailRequest),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
}
