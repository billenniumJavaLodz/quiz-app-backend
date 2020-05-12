package billennium.quizapp.repository;

import billennium.quizapp.entity.QuizDefinition;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizDefinitionRepository extends PagingAndSortingRepository<QuizDefinition, Long> {
}
