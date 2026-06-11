package re.dgnl.it211_project.model.entity;

import re.dgnl.it211_project.model.RoleEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}