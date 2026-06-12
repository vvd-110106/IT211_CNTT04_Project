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

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("123456"))
                    .role(RoleEnum.ADMIN)
                    .isActive(true)
                    .build());

            userRepository.save(User.builder()
                    .username("giangvienA")
                    .password(passwordEncoder.encode("123456"))
                    .role(RoleEnum.LECTURER)
                    .isActive(true)
                    .build());

            userRepository.save(User.builder()
                    .username("sinhvienB")
                    .password(passwordEncoder.encode("123456"))
                    .role(RoleEnum.STUDENT)
                    .isActive(true)
                    .build());

            System.out.println("Khởi tạo các tài khoản mẫu thành công (Mật khẩu: 123456)!");
        }

        if (courseRepository.findAll().isEmpty()) {
            courseRepository.save(new Course(null, "INT1306", "Java Web Service", 3));
            courseRepository.save(new Course(null, "INT1400", "He Co So Du Lieu", 3));
            System.out.println("Khởi tạo danh mục môn học PTIT mẫu thành công!");
        }
    }
}