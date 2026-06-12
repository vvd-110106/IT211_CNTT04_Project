package re.dgnl.it211_project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import re.dgnl.it211_project.model.dto.EnrollRequest;
import re.dgnl.it211_project.model.entity.Submission;
import re.dgnl.it211_project.service.EnrollmentService;
import re.dgnl.it211_project.service.SubmissionService;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final SubmissionService submissionService;
    private final EnrollmentService enrollmentService;

    // FR-06: Đăng ký tham gia khóa học
    @PostMapping("/enroll")
    public ResponseEntity<?> enrollCourse(@Valid @RequestBody EnrollRequest request, Principal principal) {
        // Dùng DTO @Valid thay cho Map
        enrollmentService.enroll(principal.getName(), request.getCourseId());

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Đăng ký lớp môn học thành công"
        ));
    }

    // FR-07: Nộp báo cáo
    @PostMapping("/submissions/upload")
    public ResponseEntity<?> uploadReport(
            @RequestParam("courseId") Long courseId,
            @RequestParam("file") MultipartFile file,
            Principal principal) {

        if (file.isEmpty() || file.getSize() > 15 * 1024 * 1024) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "File khong hop le hoac dung luong vuot qua 15MB"
            ));
        }

        try {
            Submission savedSubmission = submissionService.saveSubmission(principal.getName(), courseId, file);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Nop bao cao bai tap thanh cong",
                    "data", Map.of(
                            "id", savedSubmission.getId(),
                            "reportUrl", savedSubmission.getReportUrl(),
                            "status", savedSubmission.getStatus(),
                            "submittedAt", savedSubmission.getSubmittedAt(),
                            "courseId", courseId
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}