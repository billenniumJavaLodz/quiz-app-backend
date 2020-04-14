package billennium.quizapp.repository;

import billennium.quizapp.entity.QuizDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizDefinitionRepository extends JpaRepository<QuizDefinition, Long> {

}
