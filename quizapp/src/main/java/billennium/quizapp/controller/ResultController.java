package billennium.quizapp.controller;

import billennium.quizapp.resource.candidate.CandidateResultDto;
import billennium.quizapp.service.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static billennium.quizapp.controller.ControllerConstants.RESULT;

@RestController
@RequiredArgsConstructor
@RequestMapping(RESULT)
public class ResultController {
    private final ResultService resultService;

    @GetMapping
    public List<CandidateResultDto> getResults() {
        return resultService.getCandidatesResults();
    }
}
