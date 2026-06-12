package re.dgnl.it211_project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import re.dgnl.it211_project.model.RoleEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private RoleEnum role;
    private Boolean isActive;
}