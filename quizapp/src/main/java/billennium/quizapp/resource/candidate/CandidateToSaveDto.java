package billennium.quizapp.resource.candidate;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CandidateToSaveDto {
    Long quizId;
    String email;
}

