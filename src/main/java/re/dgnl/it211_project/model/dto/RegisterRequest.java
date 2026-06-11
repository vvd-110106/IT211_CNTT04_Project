package re.dgnl.it211_project.model.dto;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String role;
}