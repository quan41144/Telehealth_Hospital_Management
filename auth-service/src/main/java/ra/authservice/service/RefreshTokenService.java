package ra.authservice.service;

import ra.authservice.dto.request.RefreshTokenRequest;
import ra.authservice.dto.response.RefreshTokenResponse;
import ra.authservice.dto.response.RefreshTokenVerifyResponse;
import ra.authservice.entity.RefreshToken;

public interface RefreshTokenService {
    RefreshTokenResponse createRefreshToken(RefreshTokenRequest request);
    RefreshTokenVerifyResponse verifyExpiration(RefreshToken refreshToken);
}
