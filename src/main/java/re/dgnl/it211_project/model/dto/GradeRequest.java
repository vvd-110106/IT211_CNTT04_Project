package re.dgnl.it211_project.model.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data public class GradeRequest {
    @NotNull(message = "Mã số bài nộp bắt buộc nhập")
    private Long submissionId;

    @Min(value = 0, message = "Thang điểm tối thiểu là 0")
    @Max(value = 100, message = "Điểm số không được vượt quá 100")
    private Double score;

    private String feedback;
}