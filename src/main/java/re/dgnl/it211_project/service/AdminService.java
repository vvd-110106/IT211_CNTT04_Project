package re.dgnl.it211_project.service;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import re.dgnl.it211_project.model.dto.UserDTO;
import re.dgnl.it211_project.model.entity.Course;
import re.dgnl.it211_project.model.entity.User;
import re.dgnl.it211_project.repository.CourseRepository;
import re.dgnl.it211_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    // --- User ---
    public Page<UserDTO> getUsers(String keyword, Pageable pageable) {
        Page<User> userPage = (keyword != null && !keyword.isEmpty())
                ? userRepository.findByUsernameContaining(keyword, pageable)
                : userRepository.findAll(pageable);

        var dtoList = userPage.getContent().stream()
                .map(u -> new UserDTO(u.getId(), u.getUsername(), u.getRole(), u.getIsActive()))
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, userPage.getTotalElements());
    }

    public User saveUser(User user) { return userRepository.save(user); }
    public void deleteUser(Long id) { userRepository.deleteById(id); }

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