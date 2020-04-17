package billennium.quizapp.controller;


import billennium.quizapp.entity.Candidate;
import billennium.quizapp.resource.candidate.CandidateDto;
import billennium.quizapp.resource.candidate.CandidateEmailDto;
import billennium.quizapp.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static billennium.quizapp.controller.ControllerConstants.CANDIDATE;
import static billennium.quizapp.controller.ControllerConstants.EMAIL;
import static billennium.quizapp.controller.ControllerConstants.EMAIL_PARAM;
import static billennium.quizapp.controller.ControllerConstants.ID_PARAM;


@RestController
@RequiredArgsConstructor
@RequestMapping(CANDIDATE)
public class CandidateController {

    private final CandidateService candidateService;

    @GetMapping(EMAIL_PARAM)
    public String getCandidateId(@PathVariable("email") String email) {
        return candidateService.findByEmail(email);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Candidate saveCandidate(@RequestBody CandidateEmailDto candidateEmailDto) {
        return candidateService.save(candidateEmailDto.getEmail());
    }

    @GetMapping(ID_PARAM + EMAIL)
    public CandidateDto getCandidate(@PathVariable UUID id) {
        return candidateService.getById(id);
    }


}
