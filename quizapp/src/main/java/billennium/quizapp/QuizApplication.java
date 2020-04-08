package billennium.quizapp;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class QuizApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(QuizApplication.class)
                .listeners(new ActiveProfileVerifier())
                .run(args);
    }
}
