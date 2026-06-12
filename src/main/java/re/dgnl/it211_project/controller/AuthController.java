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
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Tai khoan da ton tai!");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(RoleEnum.valueOf(request.getRole()));
        newUser.setIsActive(true);

        userRepository.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("Dang ky thanh cong!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User khong ton tai"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai mat khau");
        }

        String accessToken = jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());
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

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User khong ton tai"));

            String newAccessToken = jwtTokenProvider.generateToken(username, user.getRole().name());
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token khong hop le hoac sai loai");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            TokenBlacklist blacklist = new TokenBlacklist();
            blacklist.setTokenString(token);
            blacklist.setRevokedAt(LocalDateTime.now());
            tokenBlacklistRepository.save(blacklist);
            return ResponseEntity.ok(Map.of("message", "Dang xuat thanh cong"));
        }
        return ResponseEntity.badRequest().body("Token khong hop le");
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser) {

        authService.changePassword(request, connectedUser);
        return ResponseEntity.ok(Map.of("message", "Doi mat khau thanh cong!"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        authService.forgotPassword(request.get("username"));
        return ResponseEntity.ok(Map.of("message", "Token da duoc tao, kiem tra log"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        authService.resetPassword(
                request.get("username"),
                request.get("token"),
                request.get("newPassword")
        );
        return ResponseEntity.ok(Map.of("message", "Doi mat khau thanh cong!"));
    }
}