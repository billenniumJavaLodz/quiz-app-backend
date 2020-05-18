package billennium.quizapp.controller;

import billennium.quizapp.resource.question.QuestionGetDto;
import billennium.quizapp.resource.question.QuestionPageDto;
import billennium.quizapp.resource.question.QuestionToSaveDto;
import billennium.quizapp.resource.question.QuestionToUpdateDto;
import billennium.quizapp.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
    public QuestionPageDto getQuestions(@RequestParam Integer pageSize, @RequestParam Integer pageNumber) {
        return questionService.getQuestions(pageSize, pageNumber);
    }

    @GetMapping(ID_PARAM)
    public QuestionGetDto getQuestion(@PathVariable Long id) {
        return questionService.getQuestionById(id);
    }

    @DeleteMapping(ID_PARAM)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestionById(id);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateQuestion(@RequestBody QuestionToUpdateDto question) {
        questionService.updateQuestion(question);
    }
}
