package billennium.quizapp.service;

import billennium.quizapp.entity.Answer;
import billennium.quizapp.entity.Candidate;
import billennium.quizapp.entity.Question;
import billennium.quizapp.entity.QuizDefinition;
import billennium.quizapp.entity.QuizExecuted;
import billennium.quizapp.repository.CandidateRepository;
import billennium.quizapp.resource.AnswerDto;
import billennium.quizapp.resource.QuestionDto;
import billennium.quizapp.resource.QuizDefinitionDto;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class QuizService {
    private final CandidateRepository candidateRepository;

    public QuizDefinitionDto getQuiz(String userId, Integer questionNumber) {
        Optional<Candidate> candidate = candidateRepository.getCandidateWithQuiz(UUID.fromString(userId));
        return candidate.map(value -> mapToQuizDefinitionModel(value, questionNumber)).orElseGet(QuizDefinitionDto::new);
    }

    private QuizDefinitionDto mapToQuizDefinitionModel(Candidate candidate, Integer questionNumber) {
        QuizExecuted quizExecuted = candidate.getQuizExecuted();
        QuizDefinition quiz = quizExecuted.getQuiz();
        Question question = quiz.getQuestions().get(questionNumber);
        List<Answer> answers = question.getAnswers();

        List<AnswerDto> answerDtoList = answers.stream().map(answer ->
                new ModelMapper().map(answer, AnswerDto.class)
        ).collect(Collectors.toList());

        QuestionDto questionModel = new ModelMapper().map(question, QuestionDto.class);
        questionModel.setAnswers(answerDtoList);

        QuizDefinitionDto quizDefinitionModel = new ModelMapper().map(quizExecuted, QuizDefinitionDto.class);
        quizDefinitionModel.setQuestion(questionModel);
        quizDefinitionModel.setNumberOfQuestions(quiz.getQuestions().size());
        quizDefinitionModel.setActualQuestion(questionNumber);

        return quizDefinitionModel;
    }
}
