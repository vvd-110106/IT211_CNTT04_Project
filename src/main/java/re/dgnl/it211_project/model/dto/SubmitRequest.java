package re.dgnl.it211_project.model.dto;
import lombok.Data;

@Data
public class SubmitRequest {
    private Long courseId;
    private String reportUrl;
    private String githubUrl;
}