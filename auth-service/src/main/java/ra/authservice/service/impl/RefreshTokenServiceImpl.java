package ra.authservice.service.impl;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.authservice.dto.request.RefreshTokenRequest;
import ra.authservice.dto.response.RefreshTokenResponse;
import ra.authservice.dto.response.RefreshTokenVerifyResponse;
import ra.authservice.entity.RefreshToken;
import ra.authservice.entity.User;
import ra.authservice.exception.ResourceNotFoundException;
import ra.authservice.exception.TokenExpiredException;
import ra.authservice.repository.RefreshTokenRepository;
import ra.authservice.repository.UserRepository;
import ra.authservice.service.RefreshTokenService;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenExpiration;
    @Override
    @Transactional
    public RefreshTokenResponse createRefreshToken(RefreshTokenRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại!"));
        refreshTokenRepository.deleteByUser(user);
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
                .user(user)
                .build();
        refreshTokenRepository.save(refreshToken);
        return RefreshTokenResponse.builder()
                .token(refreshToken.getToken())
                .build();
    }

    @Override
    public RefreshTokenVerifyResponse verifyExpiration(RefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            throw new TokenExpiredException("Refresh Token đã hết hạn!");
        }
        return RefreshTokenVerifyResponse.builder()
                .refreshToken(refreshToken.getToken())
                .expiryDate(refreshToken.getExpiryDate())
                .build();
    }
}
