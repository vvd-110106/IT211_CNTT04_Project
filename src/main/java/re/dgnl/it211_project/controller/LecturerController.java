package re.dgnl.it211_project.controller;

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
public class LecturerController {
    private final GradingService gradingService;

//    @PostMapping("/materials")
//    public ResponseEntity<?> grade(@Valid @RequestBody GradeRequest request) {
//        return ResponseEntity.ok(gradingService.gradeSubmission(request));
//    }
@PostMapping(value = "/materials", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> uploadMaterial(
        @RequestParam("courseId") Long courseId,
        @RequestParam("file") MultipartFile file) {

    String fileUrl = gradingService.uploadMaterial(courseId, file);

    // Trả về dạng JSON

    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "message", "Upload thành công!",
            "url", fileUrl
    ));}
}