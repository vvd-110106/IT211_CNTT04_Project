package re.dgnl.it211_project.service;

import re.dgnl.it211_project.model.StatusEnum;
import re.dgnl.it211_project.model.entity.*;
import re.dgnl.it211_project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SubmissionRepository submissionRepository;

    public void enrollCourse(Long courseId) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByUsername(currentUsername).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();

        if(!enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), course.getId())) {
            enrollmentRepository.save(new Enrollment(null, student, course));
        }
    }

    public void submitAssignment(Long courseId, String reportUrl) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByUsername(currentUsername).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();

        Submission submission = submissionRepository.findByStudentIdAndCourseId(student.getId(), course.getId())
                .orElse(Submission.builder().student(student).course(course).build());

        submission.setReportUrl(reportUrl);
        submission.setStatus(StatusEnum.SUBMITTED);
        submissionRepository.save(submission);
    }
}