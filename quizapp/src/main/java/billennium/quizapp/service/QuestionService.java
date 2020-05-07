package billennium.quizapp.service;

import billennium.quizapp.entity.Answer;
import billennium.quizapp.entity.Question;
import billennium.quizapp.repository.AnswerRepository;
import billennium.quizapp.repository.QuestionRepository;
import billennium.quizapp.resource.question.QuestionGetDto;
import billennium.quizapp.resource.question.QuestionPageDto;
import billennium.quizapp.resource.question.QuestionToSaveDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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
}
