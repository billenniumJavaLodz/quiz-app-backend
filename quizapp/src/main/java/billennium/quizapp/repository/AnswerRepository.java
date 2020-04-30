package billennium.quizapp.repository;

import billennium.quizapp.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findByTextIgnoreCaseAndCorrectAnswer(String text, boolean correctAnswer);
}
