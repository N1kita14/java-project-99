package hexlet.code;

import net.datafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestDataProviderConfig {

    @Bean
    public Faker faker() {
        return new Faker();
    }
}
