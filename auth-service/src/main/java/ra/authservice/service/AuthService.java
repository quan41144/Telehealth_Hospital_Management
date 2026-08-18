package ra.authservice.service;

import ra.authservice.dto.request.*;
import ra.authservice.dto.response.AuthInfoResponse;
import ra.authservice.dto.response.LoginAuthResponse;
import ra.authservice.entity.User;

import java.util.List;

public interface AuthService {
    User getValidUserById(Long userId);
    LoginAuthResponse login(LoginAuthRequest loginAuthRequest);
    String register(RegisterAuthRequest registerAuthRequest);
    String verifyEmail(VerifyEmailRequest request);
    String updatePassword(Long userId, UpdatePasswordRequest updatePasswordRequest);
    AuthInfoResponse updateRole(Long userId, UpdateRoleRequest updateRoleRequest);
    AuthInfoResponse updateInfoAuth(Long userId, UpdateInfoAuthRequest updateInfoAuthRequest);
    void logout(Long userId, String bearerToken);
    LoginAuthResponse refreshToken(RegainAccessTokenRequest regainAccessTokenRequest, String bearerToken);
    List<AuthInfoResponse> getAllUsers();
    AuthInfoResponse getUserById(Long userId);
    void deleteUserById(Long userId);
}
