package billennium.quizapp.resource.quiz;


import billennium.quizapp.resource.question.QuestionGetDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizGetDto extends QuizBaseDto {
    List<QuestionGetDto> questions;
}
