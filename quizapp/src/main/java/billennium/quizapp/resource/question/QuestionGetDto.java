package billennium.quizapp.resource.question;

import billennium.quizapp.resource.answer.AnswerGetDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class QuestionGetDto extends QuestionBaseDto {
   private List<AnswerGetDto> answers;
}
