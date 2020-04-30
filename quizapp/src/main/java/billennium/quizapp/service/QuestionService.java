package billennium.quizapp.service;

import billennium.quizapp.entity.Answer;
import billennium.quizapp.entity.Question;
import billennium.quizapp.repository.AnswerRepository;
import billennium.quizapp.repository.QuestionRepository;
import billennium.quizapp.resource.question.QuestionBaseDto;
import billennium.quizapp.resource.question.QuestionDto;
import billennium.quizapp.resource.question.QuestionToSaveDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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

    public List<QuestionBaseDto> getQuestions() {
        return questionRepository.findAll().stream()
                .map(question -> new ModelMapper().map(question, QuestionBaseDto.class))
                .collect(Collectors.toList());
    }

    public QuestionDto getQuestionById(Long id) {
        return questionRepository.findById(id).map(question -> new ModelMapper().map(question, QuestionDto.class))
                .orElse(QuestionDto.builder().build());
    }
}
