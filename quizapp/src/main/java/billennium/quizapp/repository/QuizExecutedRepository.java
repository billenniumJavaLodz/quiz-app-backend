package billennium.quizapp.repository;

import billennium.quizapp.entity.QuizExecuted;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizExecutedRepository extends JpaRepository<QuizExecuted,Long> {
}
