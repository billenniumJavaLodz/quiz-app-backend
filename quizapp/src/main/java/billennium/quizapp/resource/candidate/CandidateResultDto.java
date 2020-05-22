package billennium.quizapp.resource.candidate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateResultDto {
    UUID id;
    String email;
    String quizTitle;
    Integer totalPoints;
    Integer scoredPoints;
    Long scoreInPercentage;
}
