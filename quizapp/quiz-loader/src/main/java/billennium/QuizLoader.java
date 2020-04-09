package billennium;

import billennium.quizapp.repository.QuizDefinitionRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = QuizDefinitionRepository.class)
public class QuizLoader {

    public static void main(String[] args) {
        SpringApplication.run(QuizLoader.class, args);
    }
}
