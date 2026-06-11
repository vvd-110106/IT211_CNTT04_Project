package re.dgnl.it211_project.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dwpueerdh",
                "api_key", "933852662563287",
                "api_secret", "MeWliYCAqLVRiC52IvZkCZNl894"
        ));
    }
}