package billennium.quizapp.service;

import billennium.quizapp.projection.CandidateResultView;
import billennium.quizapp.repository.CandidateRepository;
import billennium.quizapp.resource.candidate.CandidateResultDto;
import billennium.quizapp.resource.quiz.QuizResultPage;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final CandidateRepository candidateRepository;

    public List<CandidateResultDto> getCandidatesResults() {

        return mapToListOfCandidateResultDto(candidateRepository.findAllCandidateWithResult());
    }

    private List<CandidateResultDto> mapToListOfCandidateResultDto(List<CandidateResultView> allCandidateWithResult) {
        return allCandidateWithResult.stream().map(result -> {
            CandidateResultDto resultDto = new CandidateResultDto();
            new ModelMapper().map(result, resultDto);
            resultDto.setTotalPoints(result.getQuizResult().getTotalQuestions());
            resultDto.setScoredPoints(result.getQuizResult().getCorrectQuestions());
            resultDto.setScoreInPercentage(Math.round(
                    ((double) result.getQuizResult().getCorrectQuestions() /
                            (double) result.getQuizResult().getTotalQuestions()) * 100
            ));
            return resultDto;
        }).collect(Collectors.toList());
    }

    public CandidateResultDto getCandidateResultDtoById(UUID id) {
        Optional<CandidateResultView> candidateResultView = candidateRepository.findCandidateWithResult(id);
        if (candidateResultView.isPresent()) {
            CandidateResultDto candidateResultDto = new ModelMapper()
                    .map(candidateResultView.get(), CandidateResultDto.class);
            candidateResultDto.setScoredPoints(candidateResultView.get().getQuizResult().getCorrectQuestions());
            candidateResultDto.setTotalPoints(candidateResultView.get().getQuizResult().getTotalQuestions());
            candidateResultDto.setScoreInPercentage(Math.round(
                    ((double) candidateResultView.get().getQuizResult().getCorrectQuestions() /
                            (double) candidateResultView.get().getQuizResult().getTotalQuestions()) * 100
            ));
            return candidateResultDto;
        } else {
            return CandidateResultDto.builder().build();
        }
    }

    public QuizResultPage getQuizResults(Long quizId, Integer pageSize, Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("correct").descending());

        Page<CandidateResultView> candidates = candidateRepository.findAllQuizResult(pageable, quizId);

        return QuizResultPage.builder()
                .totalElements(candidates.getTotalElements())
                .pageSize(pageSize)
                .pageNumber(pageNumber)
                .candidateResults(mapToListOfCandidateResultDto(candidates.getContent()))
                .build();
    }
}
