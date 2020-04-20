package billennium.quizapp.resource.quiz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDto {
    private Long id;
    private String text;
    private List<AnswerDto> answers;
    private Integer timeToAnswer;
}
