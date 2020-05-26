package billennium.quizapp.resource.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizBaseDto {
    private Long id;
    private String title;
    private Integer numberOfQuestions;
    private Integer totalTime;
    private String category;
}
