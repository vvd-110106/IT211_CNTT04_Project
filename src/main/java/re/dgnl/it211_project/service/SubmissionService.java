package re.dgnl.it211_project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import re.dgnl.it211_project.model.StatusEnum;
import re.dgnl.it211_project.model.entity.Course;
import re.dgnl.it211_project.model.entity.Submission;
import re.dgnl.it211_project.model.entity.User;
import re.dgnl.it211_project.repository.CourseRepository;
import re.dgnl.it211_project.repository.SubmissionRepository;
import re.dgnl.it211_project.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public Submission saveGitHubSubmission(String username, Long courseId, String githubUrl) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Khong tim thay sinh vien: " + username));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay khoa hoc co ID: " + courseId));

        Submission submission = submissionRepository.findByStudentAndCourse(student, course)
                .orElse(new Submission());

        submission.setStudent(student);
        submission.setCourse(course);
        submission.setGithubUrl(githubUrl);
        submission.setStatus(StatusEnum.SUBMITTED);
        submission.setSubmittedAt(LocalDateTime.now());

        return submissionRepository.save(submission);
    }

    @Transactional
    public Submission saveSubmission(String username, Long courseId, MultipartFile file) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Khong tim thay sinh vien: " + username));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay khoa hoc co ID: " + courseId));

        String secureUrl = cloudinaryService.uploadFile(file);

        Submission submission = submissionRepository.findByStudentAndCourse(student, course)
                .orElse(new Submission());

        submission.setStudent(student);
        submission.setCourse(course);
        submission.setReportUrl(secureUrl);
        submission.setStatus(StatusEnum.SUBMITTED);
        submission.setSubmittedAt(LocalDateTime.now());

        return submissionRepository.save(submission);
    }
}