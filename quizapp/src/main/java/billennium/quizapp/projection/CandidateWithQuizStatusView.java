package billennium.quizapp.projection;

import billennium.quizapp.entity.Candidate;

public interface CandidateWithQuizStatusView {
    Candidate getCandidate();

    String getQuizStatus();
}
