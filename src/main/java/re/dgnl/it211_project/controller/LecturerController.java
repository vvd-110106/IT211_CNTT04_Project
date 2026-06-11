package re.dgnl.it211_project.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import re.dgnl.it211_project.model.dto.GradeRequest;
import re.dgnl.it211_project.service.GradingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/v1/lecturer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LECTURER')")
public class LecturerController {
    private final GradingService gradingService;

    @PostMapping(value = "/materials", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadMaterial(
            @RequestParam("courseId") Long courseId,
            @RequestParam("file") MultipartFile file) {

        String fileUrl = gradingService.uploadMaterial(courseId, file);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Upload tài liệu thành công!",
                "url", fileUrl
        ));
    }

    @PostMapping("/grade")
    public ResponseEntity<?> gradeSubmission(@Valid @RequestBody GradeRequest request) {
        // Gọi service để chấm điểm
        var result = gradingService.gradeSubmission(request);

        return ResponseEntity.ok(Map.of(
                "message", "Chấm điểm thành công!",
                "data", result
        ));
    }
}