package re.dgnl.it211_project.repository;
import re.dgnl.it211_project.model.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

}