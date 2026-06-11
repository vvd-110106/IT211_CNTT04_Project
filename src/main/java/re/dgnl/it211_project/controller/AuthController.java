package re.dgnl.it211_project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import re.dgnl.it211_project.model.RoleEnum;
import re.dgnl.it211_project.model.dto.ChangePasswordRequest;
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
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Tài khoản đã tồn tại!");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole(RoleEnum.STUDENT);
        newUser.setIsActive(true);

        userRepository.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("Đăng ký tài khoản thành công!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        String accessToken = jwtTokenProvider.generateToken(username);
        String refreshToken = jwtTokenProvider.generateRefreshToken(username);

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "role", user.getRole().name()
        ));
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String refreshToken = authorization.substring(7);
            if (jwtTokenProvider.validateToken(refreshToken)) {
                String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
                String newAccessToken = jwtTokenProvider.generateToken(username);
                return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token không hợp lệ");
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