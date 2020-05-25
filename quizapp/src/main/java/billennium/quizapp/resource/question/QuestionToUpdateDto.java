package billennium.quizapp.resource.question;

import billennium.quizapp.resource.answer.AnswerToUpdateDto;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class QuestionToUpdateDto {
     Long id;
     List<AnswerToUpdateDto> answers;
     Integer timeToAnswer;
     String text;
     String code;
     byte[] image;
}
