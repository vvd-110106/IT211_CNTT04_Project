package re.dgnl.it211_project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import re.dgnl.it211_project.model.RoleEnum;
import re.dgnl.it211_project.model.dto.LoginRequest;
import re.dgnl.it211_project.model.dto.RegisterRequest;
import re.dgnl.it211_project.model.entity.User;
import re.dgnl.it211_project.repository.TokenBlacklistRepository;
import re.dgnl.it211_project.repository.UserRepository;
import re.dgnl.it211_project.security.JwtTokenProvider;
import re.dgnl.it211_project.service.AuthService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private TokenBlacklistRepository tokenBlacklistRepository;

    @MockBean
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRole(RoleEnum.STUDENT);
        testUser.setIsActive(true);
    }

    @Test
    void register_Success() throws Exception {
        RegisterRequest request = new RegisterRequest("newuser", "password", "newuser@example.com", "New User", "STUDENT");

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value("Dang ky thanh cong!"));
    }

    @Test
    void register_UserConflict() throws Exception {
        RegisterRequest request = new RegisterRequest("testuser", "password", "testuser@example.com", "Test User", "STUDENT");

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$").value("Tai khoan da ton tai!"));
    }

    @Test
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "password");

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(request.getPassword(), testUser.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken(testUser.getUsername(), testUser.getRole().name())).thenReturn("mockAccessToken");
        when(jwtTokenProvider.generateRefreshToken(testUser.getUsername())).thenReturn("mockRefreshToken");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mockAccessToken"))
                .andExpect(jsonPath("$.refreshToken").value("mockRefreshToken"))
                .andExpect(jsonPath("$.role").value(testUser.getRole().name()));
    }

    @Test
    void login_InvalidPassword() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "wrongpassword");

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(request.getPassword(), testUser.getPassword())).thenReturn(false);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").value("Sai mat khau"));
    }

    @Test
    void logout_Success() throws Exception {
        String mockToken = "mockAccessToken";
        String authorizationHeader = "Bearer " + mockToken;

        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Dang xuat thanh cong"));
    }
}