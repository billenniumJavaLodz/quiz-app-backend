package billennium.quizapp.controller;


import billennium.quizapp.entity.Candidate;
import billennium.quizapp.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static billennium.quizapp.controller.ControllerConstants.CANDIDATE;
import static billennium.quizapp.controller.ControllerConstants.EMAIL_PARAM;
import static billennium.quizapp.controller.ControllerConstants.SAVE_CANDIDATE;


@RestController
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @GetMapping(CANDIDATE + EMAIL_PARAM)
    public String getUserId(@PathVariable("email") String email) {
        return candidateService.findByEmail(email);
    }

    @PostMapping(path = SAVE_CANDIDATE)
    @ResponseStatus(HttpStatus.CREATED)
    public Candidate saveUser(@RequestBody String email) {
        return candidateService.save(email);
    }
}
