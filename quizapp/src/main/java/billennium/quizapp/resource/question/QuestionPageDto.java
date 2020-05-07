package billennium.quizapp.resource.question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class QuestionPageDto {
    private Long totalElements;
    private Integer pageNumber;
    private Integer pageSize;
    private List<QuestionGetDto> questions;

}
