package re.dgnl.it211_project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import re.dgnl.it211_project.model.entity.Course;
import re.dgnl.it211_project.model.entity.Enrollment;
import re.dgnl.it211_project.model.entity.User;
import re.dgnl.it211_project.repository.CourseRepository;
import re.dgnl.it211_project.repository.EnrollmentRepository;
import re.dgnl.it211_project.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public void enroll(String username, Long courseId) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Sinh viên không tồn tại"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Khóa học không tồn tại"));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollmentRepository.save(enrollment);
    }
}