package billennium.quizapp.resource.candidate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateDto {
    private String id;
    private String email;
    private String quizStatus;
    private String quizTitle;
}
