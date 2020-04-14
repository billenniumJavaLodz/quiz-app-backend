package billennium.quizapp

import billennium.quizapp.entity.Candidate
import billennium.quizapp.entity.QuizDefinition
import billennium.quizapp.entity.QuizExecuted
import billennium.quizapp.repository.CandidateRepository
import billennium.quizapp.repository.QuizDefinitionRepository
import billennium.quizapp.repository.QuizExecutedRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static billennium.quizapp.controller.ControllerConstants.CANDIDATE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class CandidateITSpec extends Specification {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    QuizExecutedRepository quizExecutedRepository;
    @Autowired
    QuizDefinitionRepository quizDefinitionRepository;

    def setup() {

        def quiz = quizDefinitionRepository.save(
                QuizDefinition.builder()
                        .title("Zestaw 1")
                        .build())

        quizDefinitionRepository.save(quiz)

        def userQuiz = quizExecutedRepository.save(QuizExecuted.builder()
                .quiz(quiz).build())
        quizExecutedRepository.save(userQuiz)

        candidateRepository.save(Candidate.builder()
                .email("billenet@billennium.com")
                .quizExecuted(userQuiz)
                .build())
    }

    def "Insert a Candidate Entity and retrieve the resulting String via GET /candidate/email"() {
        when:
        def response = mockMvc.perform(get(CANDIDATE + "/billenet@billennium.com"))
        then:
        response.andExpect(status().isOk())
    }

    def cleanup() {
        candidateRepository.deleteAll();
        quizExecutedRepository.deleteAll()
        quizDefinitionRepository.deleteAll()
    }
}
