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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizService {

    private final CandidateRepository candidateRepository;
    private final QuizExecutedRepository quizExecutedRepository;
    private final AnswerRepository answerRepository;

    private static final long REQUEST_DELAY_TIME = 3;
    private static final long ANSWER_AFTER_TIME = 0;

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
        LocalDateTime actualTime = LocalDateTime.now();
        Optional<ResultDetails> answerToAdd = quizExecuted.getResultDetails().stream()
                .filter(result -> result.getQuestionId().equals(answersDTO.getQuestionId())).findFirst();

        answerToAdd.ifPresent(resultDetails -> {
            resultDetails.setGivenAnswerId(answersDTO.getAnswerId());
            resultDetails.setAnswerInTime(checkDate(actualTime, resultDetails.getTimeToResolve()));
            resultDetails.setTimeOfAnswer(actualTime);
        });
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
            if (answer.isAnswerInTime() && answer.getGivenAnswerId() != ANSWER_AFTER_TIME) {
                boolean correctAnswer = answerRepository.findById(answer.getGivenAnswerId())
                        .map(Answer::getCorrectAnswer)
                        .orElseThrow(QuizDefinitionException::new);

                if (correctAnswer) {
                    sumCorrectAnswers++;
                }
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
        setTimeToResolve(quizExecutedId, question);
        List<AnswerDto> answerDtoList = question.getAnswers().stream().map(answer ->
                new ModelMapper().map(answer, AnswerDto.class)
        ).collect(Collectors.toList());

        QuestionDto questionModel = new ModelMapper().map(question, QuestionDto.class);
        questionModel.setAnswers(answerDtoList);
        questionModel.setTimeToAnswer(question.getTimeToAnswerInSeconds());

        return QuizDefinitionDto.builder()
                .id(quizExecutedId)
                .question(questionModel)
                .numberOfQuestions(quiz.getQuestions().size())
                .actualQuestion(Math.toIntExact(actualQuestion))
                .build();
    }

    private void setTimeToResolve(Long quizExecutedId, Question question) {
        QuizExecuted quizExecuted = quizExecutedRepository.findById(quizExecutedId)
                .orElseThrow(QuizExecutedException::new);
        quizExecuted.getResultDetails().add(ResultDetails.builder()
                .questionId(question.getId())
                .timeToResolve(LocalDateTime.now().plusSeconds(question.getTimeToAnswerInSeconds() + REQUEST_DELAY_TIME))
                .build());
    }

    private boolean checkDate(LocalDateTime now, LocalDateTime timeLimit) {
        return now.isBefore(timeLimit) || now.isEqual(timeLimit);
    }
}
