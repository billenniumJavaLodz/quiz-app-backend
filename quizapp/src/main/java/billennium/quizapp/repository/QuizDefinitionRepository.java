package billennium.quizapp.repository;

import billennium.quizapp.entity.QuizDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizDefinitionRepository extends JpaRepository<QuizDefinition, Long> {
}
