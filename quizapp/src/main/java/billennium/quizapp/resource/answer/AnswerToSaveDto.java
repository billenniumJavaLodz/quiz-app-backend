package billennium.quizapp.resource.answer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@AllArgsConstructor
@Builder
public class AnswerToSaveDto {
    private String code;
    private boolean correctAnswer;
    private String text;
}
