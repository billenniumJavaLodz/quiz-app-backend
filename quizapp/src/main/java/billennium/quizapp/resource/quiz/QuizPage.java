package billennium.quizapp.resource.quiz;

import billennium.quizapp.resource.base.BasePage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizPage extends BasePage {
    private List<QuizBaseDto> quizzes;
}
