package billennium.quizapp.service;

import billennium.quizapp.entity.Answer;
import billennium.quizapp.entity.Question;
import billennium.quizapp.entity.QuizCategory;
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
import billennium.quizapp.repository.QuestionRepository;
import billennium.quizapp.repository.QuizDefinitionRepository;
import billennium.quizapp.repository.QuizExecutedRepository;
import billennium.quizapp.resource.answer.AnswerDto;
import billennium.quizapp.resource.answer.AnswersDto;
import billennium.quizapp.resource.question.QuestionBaseDto;
import billennium.quizapp.resource.question.QuestionDto;
import billennium.quizapp.resource.quiz.QuizBaseDto;
import billennium.quizapp.resource.quiz.QuizCategoryDto;
import billennium.quizapp.resource.quiz.QuizDefinitionDto;
import billennium.quizapp.resource.quiz.QuizEndDto;
import billennium.quizapp.resource.quiz.QuizGetDto;
import billennium.quizapp.resource.quiz.QuizPage;
import billennium.quizapp.resource.quiz.QuizToSaveDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizService {

    private static final String DEFAULT_QUIZ_CATEGORY = "ALL";
    private final CandidateRepository candidateRepository;
    private final QuizExecutedRepository quizExecutedRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final QuizDefinitionRepository quizDefinitionRepository;

    private static final long REQUEST_DELAY_TIME = 3;
    private static final long ANSWER_AFTER_TIME = 0;
    private static final int FIRST_QUESTION = 1;

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
            return createQuizDefinitionDto(quizExecutedId, firstQuestion, quiz);
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
            return createQuizDefinitionDto(quizExecuted.getId(), nextQuestion, quiz);
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
                .totalQuestions(quizExecuted.getQuiz().getQuestions().size())
                .correctQuestions(sumCorrectAnswers)
                .build());
    }

    private void changeStatusQuizExecuted(Long quizExecutedId) {
        quizExecutedRepository.findById(quizExecutedId).ifPresent(quizExecuted ->
                quizExecuted.setQuizStatus(QuizStatus.IN_PROGRESS)
        );
    }

    private QuizDefinitionDto createQuizDefinitionDto(Long quizExecutedId, Question question, QuizDefinition quiz) {
        int actualQuestion = setTimeToResolve(quizExecutedId, question);
        List<AnswerDto> answerDtoList = question.getAnswers().stream().map(answer ->
                new ModelMapper().map(answer, AnswerDto.class)
        ).collect(Collectors.toList());

        QuestionDto questionModel = new ModelMapper().map(question, QuestionDto.class);
        questionModel.setAnswers(answerDtoList);
        questionModel.setTimeToAnswer(question.getTimeToAnswerInSeconds());
        questionModel.setImage(question.getImage());
        return QuizDefinitionDto.builder()
                .id(quizExecutedId)
                .question(questionModel)
                .numberOfQuestions(quiz.getQuestions().size())
                .actualQuestion(actualQuestion)
                .build();
    }

    private int setTimeToResolve(Long quizExecutedId, Question question) {
        QuizExecuted quizExecuted = quizExecutedRepository.findById(quizExecutedId)
                .orElseThrow(QuizExecutedException::new);
        quizExecuted.getResultDetails().add(ResultDetails.builder()
                .questionId(question.getId())
                .timeToResolve(LocalDateTime.now().plusSeconds(question.getTimeToAnswerInSeconds() + REQUEST_DELAY_TIME))
                .build());
        return quizExecuted.getResultDetails().size();
    }

    private boolean checkDate(LocalDateTime now, LocalDateTime timeLimit) {
        return now.isBefore(timeLimit) || now.isEqual(timeLimit);
    }

    public void stopQuiz(QuizEndDto quizEndDto) {
        Optional<QuizExecuted> quizToStop = quizExecutedRepository.findById(quizEndDto.getQuizId());
        quizToStop.ifPresent(quiz -> {
            quiz.setQuizStatus(QuizStatus.DONE);
            checkAnswers(quiz);
        });
    }

    public Long addQuiz(QuizToSaveDto quizToSaveDto) {
        return quizDefinitionRepository.save(QuizDefinition.builder()
                .title(quizToSaveDto.getTitle())
                .category(QuizCategory.valueOf(quizToSaveDto.getCategory()))
                .questions(mapQuestions(quizToSaveDto.getQuestions())).build()
        ).getId();
    }

    private List<Question> mapQuestions(List<QuestionBaseDto> questions) {
        return questions.stream().map(question ->
                questionRepository.findById(question.getId()).orElseThrow(RuntimeException::new)
        ).collect(Collectors.toList());
    }

    public QuizPage getQuizzes(Integer pageSize, Integer pageNumber, String category) {
        Page<QuizDefinition> quizDefinitionPage;
        if (category.equals(DEFAULT_QUIZ_CATEGORY)) {
            quizDefinitionPage = quizDefinitionRepository.findAll(PageRequest.of(pageNumber, pageSize));
        } else {
            quizDefinitionPage = quizDefinitionRepository.findAllByCategory(QuizCategory.valueOf(category), PageRequest.of(pageNumber, pageSize));
        }
        List<QuizBaseDto> quizBaseDtos = quizDefinitionPage.getContent().stream()
                .map(quizDefinition -> {
                    QuizBaseDto quizBaseDto = new ModelMapper().map(quizDefinition, QuizBaseDto.class);
                    quizBaseDto.setNumberOfQuestions(quizDefinition.getQuestions().size());
                    quizBaseDto.setTotalTime(quizDefinition.getQuestions().stream()
                            .mapToInt(Question::getTimeToAnswerInSeconds).sum());
                    return quizBaseDto;
                })
                .collect(Collectors.toList());

        return QuizPage.builder().pageNumber(quizDefinitionPage.getPageable().getPageNumber())
                .pageSize(quizDefinitionPage.getPageable().getPageSize())
                .totalElements(quizDefinitionPage.getTotalElements())
                .quizzes(quizBaseDtos).build();
    }

    public QuizGetDto getQuizById(Long id) {
        return quizDefinitionRepository.findById(id).map(quizDefinition -> {
            QuizGetDto quizGetDto = new ModelMapper().map(quizDefinition, QuizGetDto.class);
            quizGetDto.setNumberOfQuestions(quizDefinition.getQuestions().size());
            quizGetDto.setTotalTime(quizDefinition.getQuestions().stream()
                    .mapToInt(Question::getTimeToAnswerInSeconds).sum());
            return quizGetDto;
        }).orElseGet(QuizGetDto::new);
    }

    public List<QuizCategoryDto> getCategories() {
        return Arrays.stream(QuizCategory.values()).map(quizCategory ->
                QuizCategoryDto.builder().category(quizCategory.name()).build())
                .collect(Collectors.toList());
    }
}
