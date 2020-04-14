package billennium.quizapp.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizDefinitionDto {
    private Long id;
    private Integer numberOfQuestions;
    private Integer actualQuestion;
    private QuestionDto question;
}
