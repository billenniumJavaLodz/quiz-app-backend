package billennium.quizapp.repository;

import billennium.quizapp.entity.QuizCategory;
import billennium.quizapp.entity.QuizDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizDefinitionRepository extends PagingAndSortingRepository<QuizDefinition, Long> {
    Page<QuizDefinition> findAllByCategory(QuizCategory category, Pageable pageable);
}
