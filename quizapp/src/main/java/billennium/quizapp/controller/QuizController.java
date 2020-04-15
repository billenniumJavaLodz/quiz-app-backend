package billennium.quizapp.controller;

import billennium.quizapp.resource.quiz.QuizDefinitionDto;
import billennium.quizapp.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static billennium.quizapp.controller.ControllerConstants.ID_PARAM;
import static billennium.quizapp.controller.ControllerConstants.QUIZ;

@RestController
@RequiredArgsConstructor
@RequestMapping(QUIZ)
public class QuizController {

    private final QuizService quizService;

    @GetMapping(ID_PARAM)
    public QuizDefinitionDto getQuizFirstQuestion(@PathVariable String id) {
        return quizService.getFirstQuizQuestion(id);
    }
}