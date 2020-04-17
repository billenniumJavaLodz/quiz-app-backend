package billennium.quizapp.projection;

import billennium.quizapp.entity.Result;

import java.util.UUID;

public interface CandidateResultView {
    String getEmail();

    UUID getId();

    String getQuizTitle();

    Result getQuizResult();
}
