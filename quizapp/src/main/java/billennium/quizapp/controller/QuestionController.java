package billennium.quizapp.controller;

import billennium.quizapp.resource.question.QuestionBaseDto;
import billennium.quizapp.resource.question.QuestionDto;
import billennium.quizapp.resource.question.QuestionToSaveDto;
import billennium.quizapp.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static billennium.quizapp.controller.ControllerConstants.ID_PARAM;
import static billennium.quizapp.controller.ControllerConstants.QUESTION;

@RestController
@RequiredArgsConstructor
@RequestMapping(QUESTION)
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping
    public Long saveQuestion(@RequestBody QuestionToSaveDto questionToSaveDto) {
        return questionService.saveQuestion(questionToSaveDto);
    }

    @GetMapping
    public List<QuestionBaseDto> getQuestions() {
        return questionService.getQuestions();
    }

    @GetMapping(ID_PARAM)
    public QuestionDto getQuestion(@PathVariable Long id) {
        return questionService.getQuestionById(id);
    }
}
