
package billennium.quizapp.repository;

import billennium.quizapp.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, UUID> {

    Optional<Candidate> findByEmail(String email);

    @Query(value = "SELECT c FROM Candidate c " +
            "JOIN FETCH c.quizExecuted quizEexecution " +
            "JOIN FETCH  quizEexecution.quiz   quizD  " +
            "JOIN FETCH  quizD.questions where c.id= :uuid")
    Optional<Candidate> getCandidateWithQuiz(UUID uuid);
}
