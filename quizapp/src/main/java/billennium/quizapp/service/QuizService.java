package billennium.quizapp.service;

import billennium.quizapp.entity.Question;
import billennium.quizapp.entity.QuizDefinition;
import billennium.quizapp.projection.QuizExecutedView;
import billennium.quizapp.repository.CandidateRepository;
import billennium.quizapp.resource.AnswerDto;
import billennium.quizapp.resource.QuestionDto;
import billennium.quizapp.resource.QuizDefinitionDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final CandidateRepository candidateRepository;

    private final static Integer QUIZ_FIRST_QUESTION_NUMBER = 0;

    public QuizDefinitionDto getFirstQuizQuestion(String userId) {
        QuizExecutedView queryResult = candidateRepository.getCandidateWithQuiz(UUID.fromString(userId));
        if (queryResult != null) {
            return mapToQuizDefinitionModel(queryResult.getQuizExecutedId(), queryResult.getQuiz());
        } else {
            return new QuizDefinitionDto();
        }
    }


    private QuizDefinitionDto mapToQuizDefinitionModel(Long quizExecutedId, QuizDefinition quiz) {
        Question question = quiz.getQuestions().get(QUIZ_FIRST_QUESTION_NUMBER);
        List<AnswerDto> answerDtoList = question.getAnswers().stream().map(answer ->
                new ModelMapper().map(answer, AnswerDto.class)
        ).collect(Collectors.toList());

        QuestionDto questionModel = new ModelMapper().map(question, QuestionDto.class);
        questionModel.setAnswers(answerDtoList);

        return QuizDefinitionDto.builder()
                .id(quizExecutedId)
                .question(questionModel)
                .numberOfQuestions(quiz.getQuestions().size())
                .actualQuestion(QUIZ_FIRST_QUESTION_NUMBER).build();

    }
}