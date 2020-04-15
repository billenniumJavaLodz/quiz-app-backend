package billennium.quizapp.service;

import billennium.quizapp.entity.Candidate;
import billennium.quizapp.entity.QuizExecuted;
import billennium.quizapp.exception.CandidateException;
import billennium.quizapp.exception.QuizDefinitionException;
import billennium.quizapp.repository.CandidateRepository;
import billennium.quizapp.repository.QuizDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final QuizDefinitionRepository quizDefinitionRepository;

    public String findByEmail(String email) {
        return candidateRepository.findByEmail(email)
                .map(Candidate::getId)
                .map(UUID::toString)
                .orElseThrow(CandidateException::new);
    }

    public Candidate save(String email) {
        return candidateRepository.save(
                Candidate.builder()
                        .email(email)
                        .quizExecuted(QuizExecuted.builder()
                                //TODO The quiz selection is temporary and requires expansion
                                .quiz(quizDefinitionRepository.findAll().stream().findAny().orElseThrow(QuizDefinitionException::new))
                                .build())
                        .build()
        );
    }
}
