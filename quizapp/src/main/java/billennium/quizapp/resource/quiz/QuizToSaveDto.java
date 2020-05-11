package billennium.quizapp.resource.quiz;

import billennium.quizapp.resource.question.QuestionBaseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizToSaveDto {
    private String title;
    private List<QuestionBaseDto> questions;
}
