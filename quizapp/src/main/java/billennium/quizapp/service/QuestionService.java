package billennium.quizapp.service;

import billennium.quizapp.entity.Answer;
import billennium.quizapp.entity.Question;
import billennium.quizapp.entity.QuizDefinition;
import billennium.quizapp.repository.AnswerRepository;
import billennium.quizapp.repository.QuestionRepository;
import billennium.quizapp.resource.question.QuestionGetDto;
import billennium.quizapp.resource.question.QuestionPageDto;
import billennium.quizapp.resource.question.QuestionToSaveDto;
import billennium.quizapp.resource.question.QuestionToUpdateDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Transactional
    public Long saveQuestion(QuestionToSaveDto questionToSaveDto) {

        List<Answer> answers = questionToSaveDto.getAnswers().stream()
                .map(answerToSaveDto -> new ModelMapper().map(answerToSaveDto, Answer.class))
                .collect(Collectors.toList());

        checkAnswersExisting(answers);

        Question question = new ModelMapper().map(questionToSaveDto, Question.class);
        question.setTimeToAnswerInSeconds(questionToSaveDto.getTimeToAnswer());
        question.setAnswers(Collections.emptyList());
        question.setImage(questionToSaveDto.getImage());
        questionRepository.save(question);

        question.setAnswers(answers);
        questionRepository.save(question);
        return question.getId();
    }

    private void checkAnswersExisting(List<Answer> answers) {
        answers.forEach(answer -> {
            Optional<Answer> answerFromDb = answerRepository
                    .findByTextIgnoreCaseAndCorrectAnswer(answer.getText(), answer.getCorrectAnswer());
            answerFromDb.ifPresent(answerToUpdate -> {
                answer.setId(answerToUpdate.getId());
                answer.setQuestion(answerToUpdate.getQuestion());
            });
        });
    }

    public QuestionPageDto getQuestions(Integer pageSize, Integer pageNumber) {

        Page<Question> questionPage = questionRepository.findAll(PageRequest.of(pageNumber, pageSize));
        List<QuestionGetDto> questionList = questionPage.getContent().stream()
                .map(question -> new ModelMapper().map(question, QuestionGetDto.class))
                .collect(Collectors.toList());

        return QuestionPageDto.builder()
                .pageNumber(questionPage.getPageable().getPageNumber())
                .pageSize(questionPage.getPageable().getPageSize())
                .totalElements(questionPage.getTotalElements())
                .questions(questionList).build();
    }

    public QuestionGetDto getQuestionById(Long id) {
        return questionRepository.findById(id).map(question -> new ModelMapper().map(question, QuestionGetDto.class))
                .orElse(QuestionGetDto.builder().build());
    }

    public void deleteQuestionById(Long id) {
        Optional<Question> quizDefinition = questionRepository.findById(id);
        if (quizDefinition.isPresent()) {
            if (quizDefinition.get().getQuiz().isEmpty()) {
                questionRepository.delete(quizDefinition.get());
            } else {
                String quizzes = quizDefinition.get().getQuiz().stream()
                        .map(QuizDefinition::getTitle).collect(Collectors.toList()).toString();
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Nie można usunąć pytania, ponieważ należy ono do quziu/ów: " + quizzes);
            }
        }
    }

    public void updateQuestion(QuestionToUpdateDto questionToUpdate) {
        questionRepository.findById(questionToUpdate.getId()).ifPresent(question -> {
            List<Answer> answers = questionToUpdate.getAnswers().stream()
                    .map(answerToUpdateDto -> new ModelMapper().map(answerToUpdateDto, Answer.class))
                    .collect(Collectors.toList());

            checkAnswersExisting(answers);
            question.setAnswers(answers);
            question.setText(questionToUpdate.getText());
            question.setImage(questionToUpdate.getImage());
            question.setTimeToAnswerInSeconds(questionToUpdate.getTimeToAnswer());
            questionRepository.save(question);
        });
    }
}
