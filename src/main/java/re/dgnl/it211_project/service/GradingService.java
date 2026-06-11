package re.dgnl.it211_project.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import re.dgnl.it211_project.model.StatusEnum;
import re.dgnl.it211_project.model.dto.*;
import re.dgnl.it211_project.model.entity.Submission;
import re.dgnl.it211_project.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GradingService {
    private final SubmissionRepository submissionRepository;
    private final Cloudinary cloudinary;

    public ApiResponse<Submission> gradeSubmission(GradeRequest request) {
        Submission submission = submissionRepository.findById(request.getSubmissionId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài nộp tương ứng"));

        if (submission.getStatus() == StatusEnum.PENDING || submission.getStatus() == null) {
            throw new RuntimeException("Sinh viên chưa thực hiện nộp bài, không thể chấm điểm");
        }

        submission.setScore(request.getScore());
        submission.setFeedback(request.getFeedback());
        submission.setStatus(StatusEnum.GRADED);

        Submission savedSubmission = submissionRepository.save(submission);
        return new ApiResponse<>(true, "Chấm điểm hoàn tất thành công", savedSubmission);
    }
    public String uploadMaterial(Long courseId, MultipartFile file) {
        try {
            String resourceType = file.getContentType().startsWith("image/") ? "image" : "raw";

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "resource_type", resourceType,
                    "folder", "materials/" + courseId // Lưu vào thư mục theo courseId cho gọn
            ));

            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            throw new RuntimeException("Upload file thất bại: " + e.getMessage());
        }
    }
}