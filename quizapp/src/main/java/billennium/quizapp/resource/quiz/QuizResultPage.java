package billennium.quizapp.resource.quiz;

import billennium.quizapp.resource.base.BasePage;
import billennium.quizapp.resource.candidate.CandidateResultDto;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
public class QuizResultPage extends BasePage {
    private List<CandidateResultDto> candidateResults;
}
