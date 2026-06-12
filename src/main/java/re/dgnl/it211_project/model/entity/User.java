package re.dgnl.it211_project.model.entity;

import re.dgnl.it211_project.model.RoleEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private RoleEnum role;
    private Boolean isActive = true;

    private String resetPasswordToken;
    private LocalDateTime resetTokenExpiry;
}