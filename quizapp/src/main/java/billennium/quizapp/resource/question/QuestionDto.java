package billennium.quizapp.resource.question;

import billennium.quizapp.resource.answer.AnswerDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class QuestionDto extends QuestionBaseDto {
    private Integer timeToAnswer;
    private List<AnswerDto> answers;

}
