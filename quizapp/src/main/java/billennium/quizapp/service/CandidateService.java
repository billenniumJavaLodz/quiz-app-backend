package billennium.quizapp.service;

import billennium.quizapp.entity.Candidate;
import billennium.quizapp.entity.QuizExecuted;
import billennium.quizapp.entity.QuizStatus;
import billennium.quizapp.exception.CandidateException;
import billennium.quizapp.projection.CandidateWithQuizStatusView;
import billennium.quizapp.repository.CandidateRepository;
import billennium.quizapp.repository.QuizDefinitionRepository;
import billennium.quizapp.resource.candidate.CandidateDto;
import billennium.quizapp.resource.candidate.CandidateToSaveDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;
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

    public UUID save(CandidateToSaveDto candidateToSaveDto) {
        return candidateRepository.save(
                Candidate.builder()
                        .email(candidateToSaveDto.getEmail())
                        .quizExecuted(QuizExecuted.builder()
                                .quiz(quizDefinitionRepository.findById(candidateToSaveDto.getQuizId()).get())
                                .quizStatus(QuizStatus.READY)
                                .build())
                        .build()
        ).getId();
    }

    public CandidateDto getById(UUID id) {
        Optional<CandidateWithQuizStatusView> queryResult = candidateRepository.findByIdWithQuizStatus(id);
        if (queryResult.isPresent()) {
            CandidateDto candidateDto = new ModelMapper().map(queryResult.get().getCandidate(), CandidateDto.class);
            candidateDto.setQuizStatus(queryResult.get().getQuizStatus());
            return candidateDto;
        } else {
            return new CandidateDto();
        }
    }
}
