package billennium.quizapp.projection;

import billennium.quizapp.entity.QuizDefinition;

public interface QuizExecutedView {
    Long getQuizExecutedId();
    QuizDefinition getQuiz();
}
