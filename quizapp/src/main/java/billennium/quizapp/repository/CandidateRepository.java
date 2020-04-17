package billennium.quizapp.repository;

import billennium.quizapp.entity.Candidate;
import billennium.quizapp.projection.CandidateResultView;
import billennium.quizapp.projection.QuizExecutedView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, UUID> {

    Optional<Candidate> findByEmail(String email);

    @Query(value = "SELECT  c.quizExecuted.id as quizExecutedId, c.quizExecuted.quiz as quiz FROM Candidate c" +
            " JOIN  c.quizExecuted quizEexecution" +
            " JOIN  quizEexecution.quiz quizD" +
            " where c.id =:uuid")
    QuizExecutedView getCandidateWithQuiz(UUID uuid);

    @Query(value = "SELECT c.email as email, c.id as id, c.quizExecuted.result as quizResult," +
            " c.quizExecuted.quiz.title as quizTitle FROM Candidate  c " +
            "join c.quizExecuted quizE " +
            "join quizE.result " +
            "where quizE.quizStatus='DONE'")
    List<CandidateResultView> findAllCandidateWithResult();
}
