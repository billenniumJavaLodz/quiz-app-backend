package billennium.quizapp.resource.question;

import billennium.quizapp.resource.answer.AnswerToSaveDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionToSaveDto {
    private List<AnswerToSaveDto> answers;
    private Integer timeToAnswer;
    private String text;
    private String code;
}
