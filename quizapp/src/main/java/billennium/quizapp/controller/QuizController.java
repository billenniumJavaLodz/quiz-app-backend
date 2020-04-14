package billennium.quizapp.controller;

import billennium.quizapp.resource.QuizDefinitionDto;
import billennium.quizapp.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static billennium.quizapp.controller.ControllerConstants.*;

@RestController
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;



    @GetMapping(USER + ID_PARAM + QUIZ)
    public QuizDefinitionDto getQuizFirstQuestion(@PathVariable(name = "id") String id) {
        return quizService.getQuiz(id, QUIZ_FIRST_QUESTION_NUMBER);
    }

}
