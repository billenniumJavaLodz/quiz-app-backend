package billennium.quizapp.resource.question;

import billennium.quizapp.resource.answer.AnswerToSaveDto;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class QuestionToSaveDto {
    List<AnswerToSaveDto> answers;
    Integer timeToAnswer;
    String text;
    String code;
    byte[] image;
}
