package re.dgnl.it211_project.config;

import re.dgnl.it211_project.model.RoleEnum;
import re.dgnl.it211_project.model.entity.Course;
import re.dgnl.it211_project.model.entity.User;
import re.dgnl.it211_project.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import re.dgnl.it211_project.repository.UserRepository;
import re.dgnl.it211_project.repository.CourseRepository;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            userRepository.save(new User(null, "admin", passwordEncoder.encode("123456"), RoleEnum.ADMIN, true));
            userRepository.save(new User(null, "giangvienA", passwordEncoder.encode("123456"), RoleEnum.LECTURER, true));
            userRepository.save(new User(null, "sinhvienB", passwordEncoder.encode("123456"), RoleEnum.STUDENT, true));
            System.out.println("Khởi tạo các tài khoản mẫu thành công (Mật khẩu: 123456)!");
        }

        if (courseRepository.findAll().isEmpty()) {
            courseRepository.save(new Course(null, "INT1306", "Java Web Service", 3));
            courseRepository.save(new Course(null, "INT1400", "He Co So Du Lieu", 3));
            System.out.println("Khởi tạo danh mục môn học PTIT mẫu thành công!");
        }
    }
}