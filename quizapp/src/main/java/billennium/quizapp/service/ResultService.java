package billennium.quizapp.service;

import billennium.quizapp.projection.CandidateResultView;
import billennium.quizapp.repository.CandidateRepository;
import billennium.quizapp.resource.candidate.CandidateResultDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
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
            return resultDto;
        }).collect(Collectors.toList());
    }
}
