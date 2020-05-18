package billennium.quizapp.resource.question;

import billennium.quizapp.resource.answer.AnswerToUpdateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionToUpdateDto {
    private Long id;
    private List<AnswerToUpdateDto> answers;
    private Integer timeToAnswer;
    private String text;
    private String code;
}
