package billennium.quizapp.service;

import billennium.quizapp.entity.Candidate;
import billennium.quizapp.exception.CandidateException;
import billennium.quizapp.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;

    public String findByEmail(String email) {
        return candidateRepository.findByEmail(email)
                .map(Candidate::getId)
                .map(UUID::toString)
                .orElseThrow(CandidateException::new);
    }

}
