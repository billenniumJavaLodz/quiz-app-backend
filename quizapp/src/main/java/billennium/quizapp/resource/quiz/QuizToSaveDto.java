package billennium.quizapp.resource.quiz;

import billennium.quizapp.resource.question.QuestionBaseDto;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class QuizToSaveDto {
    String title;
    List<QuestionBaseDto> questions;
    String category;
}
