package billennium.quizapp.resource.question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class QuestionBaseDto {
    private Long id;
    private String text;
    private Integer timeToAnswer;
}
