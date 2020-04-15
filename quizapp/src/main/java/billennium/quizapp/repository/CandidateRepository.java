package billennium.quizapp.repository;

import billennium.quizapp.entity.Candidate;
import billennium.quizapp.projection.QuizExecutedView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}
