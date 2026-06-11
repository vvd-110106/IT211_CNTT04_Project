package re.dgnl.it211_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import re.dgnl.it211_project.model.entity.Submission;
import re.dgnl.it211_project.model.entity.User;
import re.dgnl.it211_project.model.entity.Course;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Optional<Submission> findByStudentAndCourse(User student, Course course);

    Optional<Submission> findByStudentIdAndCourseId(Long studentId, Long courseId);
}