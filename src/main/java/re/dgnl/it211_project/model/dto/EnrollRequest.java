package re.dgnl.it211_project.model.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollRequest {
    @NotNull(message = "courseId không được để trống")
    private Long courseId;
}