package billennium.quizapp.service;

import billennium.quizapp.entity.Answer;
import billennium.quizapp.entity.Question;
import billennium.quizapp.entity.QuizDefinition;
import billennium.quizapp.entity.QuizExecuted;
import billennium.quizapp.entity.QuizStatus;
import billennium.quizapp.entity.Result;
import billennium.quizapp.entity.ResultDetails;
import billennium.quizapp.exception.QuizDefinitionException;
import billennium.quizapp.exception.QuizExecutedException;
import billennium.quizapp.projection.QuizExecutedView;
import billennium.quizapp.repository.AnswerRepository;
import billennium.quizapp.repository.CandidateRepository;
import billennium.quizapp.repository.QuizExecutedRepository;
import billennium.quizapp.resource.quiz.AnswerDto;
import billennium.quizapp.resource.quiz.AnswersDto;
import billennium.quizapp.resource.quiz.QuestionDto;
import billennium.quizapp.resource.quiz.QuizDefinitionDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizService {

    private final CandidateRepository candidateRepository;
    private final QuizExecutedRepository quizExecutedRepository;
    private final AnswerRepository answerRepository;

    public QuizDefinitionDto begin(String candidateId, AnswersDto answersDto) {
        QuizExecutedView queryResult = candidateRepository.getCandidateWithQuiz(UUID.fromString(candidateId));
        if (queryResult != null) {
            return mapToQuizDefinitionModel(queryResult.getQuizExecutedId(), queryResult.getQuiz(), answersDto);
        } else {
            return new QuizDefinitionDto();
        }
    }

    private QuizDefinitionDto mapToQuizDefinitionModel(Long quizExecutedId, QuizDefinition quiz,
                                                       AnswersDto answersDTO) {
        if (answersDTO.getQuestionId() == null) {
            changeStatusQuizExecuted(quizExecutedId);
            Question firstQuestion = quiz.getQuestions().stream().findFirst().orElseThrow(QuizDefinitionException::new);
            return createQuizDefinitionDto(quizExecutedId, firstQuestion, quiz, firstQuestion.getId());
        } else {
            return goToNextQuestion(quizExecutedId, answersDTO, quiz);
        }
    }

    private QuizDefinitionDto goToNextQuestion(Long quizExecutedId, AnswersDto answersDTO, QuizDefinition quiz) {
        QuizExecuted quizExecuted = quizExecutedRepository.findById(quizExecutedId).orElseThrow(QuizExecutedException::new);
        addGivenAnswer(answersDTO, quizExecuted);
        return createQuizDefinitionDtoForNextQuestion(quiz, quizExecuted);
    }

    private void addGivenAnswer(AnswersDto answersDTO, QuizExecuted quizExecuted) {
        quizExecuted.getResultDetails().add(ResultDetails.builder()
                .questionId(answersDTO.getQuestionId())
                .givenAnswerId(answersDTO.getAnswerId())
                .build());
    }

    private QuizDefinitionDto createQuizDefinitionDtoForNextQuestion(QuizDefinition quiz, QuizExecuted quizExecuted) {
        if (quizExecuted.getResultDetails().size() == quiz.getQuestions().size()) {
            return finishQuiz(quizExecuted);
        } else {
            Question nextQuestion = quiz.getQuestions().get(quizExecuted.getResultDetails().size());
            return createQuizDefinitionDto(quizExecuted.getId(), nextQuestion, quiz, nextQuestion.getId());
        }
    }

    private QuizDefinitionDto finishQuiz(QuizExecuted quizExecuted) {
        quizExecuted.setQuizStatus(QuizStatus.DONE);
        checkAnswers(quizExecuted);
        return QuizDefinitionDto.builder().build();
    }

    void checkAnswers(QuizExecuted quizExecuted) {
        int sumCorrectAnswers = 0;
        for (ResultDetails answer : quizExecuted.getResultDetails()) {
            Boolean correctAnswer = answerRepository.findById(answer.getGivenAnswerId())
                    .map(Answer::getCorrectAnswer)
                    .orElseThrow(QuizDefinitionException::new);

            if (correctAnswer) {
                sumCorrectAnswers++;
            }
        }
        quizExecuted.setResult(Result.builder()
                .totalQuestions(quizExecuted.getResultDetails().size())
                .correctQuestions(sumCorrectAnswers)
                .build());
    }

    private void changeStatusQuizExecuted(Long quizExecutedId) {
        quizExecutedRepository.findById(quizExecutedId).ifPresent(quizExecuted ->
                quizExecuted.setQuizStatus(QuizStatus.IN_PROGRESS)
        );
    }

    private QuizDefinitionDto createQuizDefinitionDto(Long quizExecutedId, Question question, QuizDefinition quiz,
                                                      Long actualQuestion) {
        List<AnswerDto> answerDtoList = question.getAnswers().stream().map(answer ->
                new ModelMapper().map(answer, AnswerDto.class)
        ).collect(Collectors.toList());

        QuestionDto questionModel = new ModelMapper().map(question, QuestionDto.class);
        questionModel.setAnswers(answerDtoList);

        return QuizDefinitionDto.builder()
                .id(quizExecutedId)
                .question(questionModel)
                .numberOfQuestions(quiz.getQuestions().size())
                .actualQuestion(Math.toIntExact(actualQuestion))
                .build();
    }
}
