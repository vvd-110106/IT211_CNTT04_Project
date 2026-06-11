package re.dgnl.it211_project.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "enrollments")
@Data @NoArgsConstructor @AllArgsConstructor
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}