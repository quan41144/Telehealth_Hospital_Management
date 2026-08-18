package ra.authservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.authservice.common.RoleType;
import ra.authservice.dto.request.*;
import ra.authservice.dto.response.AuthInfoResponse;
import ra.authservice.dto.response.LoginAuthResponse;
import ra.authservice.dto.response.RefreshTokenResponse;
import ra.authservice.entity.Role;
import ra.authservice.entity.User;
import ra.authservice.exception.*;
import ra.authservice.repository.RefreshTokenRepository;
import ra.authservice.repository.RoleRepository;
import ra.authservice.repository.UserRepository;
import ra.authservice.security.jwt.JwtTokenProvider;
import ra.authservice.security.user_details.CustomUserDetails;
import ra.authservice.service.AuthService;
import ra.authservice.service.EmailService;
import ra.authservice.service.RefreshTokenService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final EmailService emailService;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public User getValidUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại!"));
        if (user.getDeleted()) {
            throw new ResourceNotFoundException("Tài khoản không tồn tại!");
        }
        if (!user.getEnabled()) {
            throw new BadRequestException("Tài khoản đã bị vô hiệu hóa!");
        }
        return user;
    }

    @Override
    public LoginAuthResponse login(LoginAuthRequest loginAuthRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginAuthRequest.getUsername(), loginAuthRequest.getPassword())
        );
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        if (user.getDeleted()) {
            throw new ResourceNotFoundException("Tài khoản không tồn tại!");
        }
        if (!user.getEnabled()) {
            throw new BadRequestException("Tài khoản đã bị vô hiệu hóa!");
        }
        if (!user.getIsVerified()) {
            throw new BadRequestException("Tài khoản chưa được xác thực email! Vui lòng xác thực email!");
        }
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        Long userId = userDetails.getUser().getId();
        String username = userDetails.getUsername();

        String accessToken = jwtTokenProvider.generateAccessToken(userId, username, roles);
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(userId);
        RefreshTokenResponse refreshTokenResponse = refreshTokenService.createRefreshToken(refreshTokenRequest);
        log.info("*******************Đăng nhập thành công!********************");
        return LoginAuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenResponse.getToken())
                .build();
    }

    @Override
    @Transactional
    public String register(RegisterAuthRequest registerAuthRequest) {
        if (userRepository.existsByUsername(registerAuthRequest.getUsername())) {
            throw new ConflictException("Tên đăng nhập " + registerAuthRequest.getUsername() + " đã tồn tại!");
        }
        if (userRepository.existsByEmail(registerAuthRequest.getEmail())) {
            throw new ConflictException("Email " + registerAuthRequest.getEmail() + " đã được sử dụng!");
        }
        if (userRepository.existsByPhone(registerAuthRequest.getPhone())) {
            throw new ConflictException("Số điện thoại " + registerAuthRequest.getPhone() + " đã được sử dụng!");
        }
        Role patientRole = roleRepository.findByRoleName(RoleType.ROLE_PATIENT)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ROLE_PATIENT!"));
        Set<Role> roles = new HashSet<>();
        roles.add(patientRole);

        User newUser = User.builder()
                .username(registerAuthRequest.getUsername())
                .password(passwordEncoder.encode(registerAuthRequest.getPassword()))
                .email(registerAuthRequest.getEmail())
                .phone(registerAuthRequest.getPhone())
                .roles(roles)
                .build();
        userRepository.save(newUser);
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        stringRedisTemplate.opsForValue().set("OTP_REG_" + registerAuthRequest.getEmail(), otp, 5, TimeUnit.MINUTES);
        emailService.sendOtpEmail(registerAuthRequest.getEmail(), otp);
        log.info("*****************Đăng ký tài khoản mới thành công!*****************");
        return "Đăng ký tài khoản mới thành công!";
    }

    @Override
    @Transactional
    public String verifyEmail(VerifyEmailRequest request) {
        String redisKey = "OTP_REG_" + request.getEmail();
        String savedOtp = stringRedisTemplate.opsForValue().get(redisKey);

        if (savedOtp == null) {
            throw new BadRequestException("Mã OTP đã hết hạn hoặc không tồn tại!");
        }
        if (!savedOtp.equals(request.getOtp())) {
            throw new BadRequestException("Mã OTP không chính xác!");
        }
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(" Không tìm thấy tài khoản có email " + request.getEmail()));
        user.setIsVerified(true);
        userRepository.save(user);
        stringRedisTemplate.delete(redisKey);
        return "Xác thực email thành công!";
    }

    @Override
    @Transactional
    public String updatePassword(Long userId, UpdatePasswordRequest updatePasswordRequest) {
        User user = getValidUserById(userId);
        if (!passwordEncoder.matches(updatePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Mật khẩu cũ không đúng!");
        }
        if (passwordEncoder.matches(updatePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("Vui lòng không nhập lại mật khẩu cũ!");
        }
        if (!updatePasswordRequest.getNewPassword().equals(updatePasswordRequest.getConfirmPassword())) {
            throw new BadRequestException("Vui lòng xác nhận lại đúng mật khẩu mới!");
        }
        user.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
        userRepository.save(user);
        log.info("**********************Thay đổi mật khẩu mới thành công!**********************");
        return "Thay đổi mật khẩu thành công!";
    }

    @Override
    @Transactional
    public AuthInfoResponse updateRole(Long userId, UpdateRoleRequest updateRoleRequest) {
        User user = getValidUserById(userId);
        Set<Role> updateRoles = new HashSet<>();
        if (updateRoleRequest.getRoles() != null && !updateRoleRequest.getRoles().isEmpty()) {
            for (RoleType roleType : updateRoleRequest.getRoles()) {
                Role role = roleRepository.findByRoleName(roleType)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy " + roleType));
                updateRoles.add(role);
            }
            user.setRoles(updateRoles);
            userRepository.save(user);
        }
        return AuthInfoResponse.builder()
                .userId(userId)
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(updateRoles)
                .build();
    }

    @Override
    @Transactional
    public AuthInfoResponse updateInfoAuth(Long userId, UpdateInfoAuthRequest updateInfoAuthRequest) {
        User user = getValidUserById(userId);
        if (updateInfoAuthRequest.getEmail() != null) {
            user.setEmail(updateInfoAuthRequest.getEmail());
        }
        if (updateInfoAuthRequest.getPhone() != null) {
            user.setPhone(updateInfoAuthRequest.getPhone());
        }
        userRepository.save(user);
        return AuthInfoResponse.builder()
                .userId(userId)
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRoles())
                .build();
    }

    @Override
    @Transactional
    public void logout(Long userId, String bearerToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại!"));
        refreshTokenRepository.deleteByUser(user);
        String token = jwtTokenProvider.extractToken(bearerToken);
        if (token != null) {
            long ttl = jwtTokenProvider.getRemainingTtl(token);
            if (ttl > 0) {
                redisTemplate.opsForValue().set("BL_" + token, "logout", ttl, TimeUnit.MILLISECONDS);
            }
        }
    }

    @Override
    public LoginAuthResponse refreshToken(RegainAccessTokenRequest regainAccessTokenRequest, String bearerToken) {
        String token = regainAccessTokenRequest.getRefreshToken();
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new TokenRefreshException("Refresh Token không hợp lệ hoặc đã hết hạn!");
        }
        refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenRefreshException("Refresh Token không tồn tại!"));
        Long userId = Long.parseLong(jwtTokenProvider.getUserIdFromToken(token));
        User user = getValidUserById(userId);
        if (bearerToken != null) {
            String oldAccessToken = jwtTokenProvider.extractToken(bearerToken);
            if (oldAccessToken != null) {
                long ttl = jwtTokenProvider.getRemainingTtl(oldAccessToken);
                if (ttl > 0) {
                    redisTemplate.opsForValue().set("BL_" + oldAccessToken, "refresh", ttl, TimeUnit.MILLISECONDS);
                }
            }
        }

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleName().toString())
                .collect(Collectors.toList());
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername(), roles);
        return LoginAuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(token)
                .build();
    }

    @Override
    public List<AuthInfoResponse> getAllUsers() {
        List<User> users = userRepository.findAll().stream()
                .filter(user -> !user.getDeleted())
                .toList();
        return users.stream()
                .map(user -> AuthInfoResponse.builder()
                        .userId(user.getId())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .role(user.getRoles())
                        .build()
                ).toList();
    }

    @Override
    public AuthInfoResponse getUserById(Long userId) {
        User user = getValidUserById(userId);
        return AuthInfoResponse.builder()
                .userId(userId)
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRoles())
                .build();
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại!"));
        if (user.getDeleted()) {
            throw new BadRequestException("Tài khoản không tồn tại!");
        }
        user.setDeleted(true);
        user.setEnabled(false);

        String suffix = "_del_" + System.currentTimeMillis();
        user.setUsername(user.getUsername() + suffix);
        user.setEmail(user.getEmail() + suffix);
        user.setPhone(user.getPhone() + suffix);
        userRepository.save(user);
        refreshTokenRepository.deleteByUser(user);
        log.info("******************Xóa thành công user!*****************");
    }
}
