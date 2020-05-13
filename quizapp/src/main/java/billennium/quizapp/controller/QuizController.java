package billennium.quizapp.controller;

import billennium.quizapp.resource.answer.AnswersDto;
import billennium.quizapp.resource.quiz.QuizDefinitionDto;
import billennium.quizapp.resource.quiz.QuizEndDto;
import billennium.quizapp.resource.quiz.QuizGetDto;
import billennium.quizapp.resource.quiz.QuizPage;
import billennium.quizapp.resource.quiz.QuizToSaveDto;
import billennium.quizapp.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static billennium.quizapp.controller.ControllerConstants.ID_PARAM;
import static billennium.quizapp.controller.ControllerConstants.QUIZ;
import static billennium.quizapp.controller.ControllerConstants.STOP_QUIZ;

@RestController
@RequiredArgsConstructor
@RequestMapping(QUIZ)
public class QuizController {

    private final QuizService quizService;

    @PostMapping(ID_PARAM)
    public QuizDefinitionDto playQuiz(@PathVariable String id, @RequestBody AnswersDto answersDto) {
        return quizService.begin(id, answersDto);
    }

    @PostMapping(STOP_QUIZ)
    public void stopQuiz(@RequestBody QuizEndDto stopQuizDto) {
        quizService.stopQuiz(stopQuizDto);
    }

    @PostMapping
    public Long addQuiz(@RequestBody QuizToSaveDto quizToSaveDto) {
        return quizService.addQuiz(quizToSaveDto);
    }

    @GetMapping
    public QuizPage getQuizzes(@RequestParam Integer pageSize, @RequestParam Integer pageNumber) {
        return quizService.getQuizzes(pageSize, pageNumber);
    }

    @GetMapping(ID_PARAM)
    public QuizGetDto getQuiz(@PathVariable Long id) {
        return quizService.getQuizById(id);
    }
}
