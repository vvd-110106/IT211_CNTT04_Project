package re.dgnl.it211_project.service;

import re.dgnl.it211_project.model.entity.Course;
import re.dgnl.it211_project.model.entity.User;
import re.dgnl.it211_project.repository.CourseRepository;
import re.dgnl.it211_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    // --- User ---
    public Page<User> getUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    // --- Course ---
    public Page<Course> getCourses(int page, int size) {
        return courseRepository.findAll(PageRequest.of(page, size));
    }

    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, Course courseDetails) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học với ID: " + id));
        course.setCourseCode(courseDetails.getCourseCode());
        course.setCourseName(courseDetails.getCourseName());
        course.setCredit(courseDetails.getCredit());
        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy khóa học để xóa");
        }
        courseRepository.deleteById(id);
    }
}