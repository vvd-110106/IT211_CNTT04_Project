package re.dgnl.it211_project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import re.dgnl.it211_project.model.RoleEnum;
import re.dgnl.it211_project.model.dto.ChangePasswordRequest;
import re.dgnl.it211_project.model.dto.LoginRequest;
import re.dgnl.it211_project.model.dto.RegisterRequest;
import re.dgnl.it211_project.model.entity.TokenBlacklist;
import re.dgnl.it211_project.model.entity.User;
import re.dgnl.it211_project.repository.TokenBlacklistRepository;
import re.dgnl.it211_project.repository.UserRepository;
import re.dgnl.it211_project.security.JwtTokenProvider;
import re.dgnl.it211_project.service.AuthService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Tài khoản đã tồn tại!");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(RoleEnum.valueOf(request.getRole()));
        newUser.setIsActive(true);

        userRepository.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("Đăng ký thành công!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) { // Dùng DTO
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai mật khẩu");
        }

        String accessToken = jwtTokenProvider.generateToken(user.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "role", user.getRole().name()
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String authorization) {
        String refreshToken = authorization.substring(7);

        if (jwtTokenProvider.validateToken(refreshToken) &&
                "refresh".equals(jwtTokenProvider.getTokenType(refreshToken))) {

            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            String newAccessToken = jwtTokenProvider.generateToken(username);
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ hoặc sai loại");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            // Lưu token vào blacklist để vô hiệu hóa
            TokenBlacklist blacklist = new TokenBlacklist();
            blacklist.setTokenString(token);
            blacklist.setRevokedAt(LocalDateTime.now());
            tokenBlacklistRepository.save(blacklist);
            return ResponseEntity.ok(Map.of("message", "Đăng xuất thành công"));
        }
        return ResponseEntity.badRequest().body("Token không hợp lệ");
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser) {

        authService.changePassword(request, connectedUser);
        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công!"));
    }
}