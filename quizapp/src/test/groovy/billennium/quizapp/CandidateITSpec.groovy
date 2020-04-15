package billennium.quizapp

import billennium.quizapp.entity.Candidate
import billennium.quizapp.entity.QuizDefinition
import billennium.quizapp.repository.CandidateRepository
import billennium.quizapp.repository.QuizDefinitionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static billennium.quizapp.controller.ControllerConstants.CANDIDATE
import static billennium.quizapp.controller.ControllerConstants.SAVE_CANDIDATE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class CandidateITSpec extends Specification {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private QuizDefinitionRepository quizDefinitionRepository;

    def setup() {
        candidateRepository.save(Candidate.builder()
                .email("billenet@billennium.com")
                .build())

        quizDefinitionRepository.save(QuizDefinition.builder()
                .title("BIG-DATA")
                .questions(Collections.emptyList())
                .build())

    }

    def "Insert a Candidate Entity and retrieve the resulting String via GET /candidate/email"() {
        when:
        def response = mockMvc.perform(get(CANDIDATE + "/billenet@billennium.com"))
        then:
        response.andExpect(status().isOk())
    }

    def 'should return 201 code (created) when trying to save record'() {
        given:
        Map request = [
                email: 'john.wayne@gmail.com'
        ]

        when: 'try to save candidate'
        def response = mockMvc.perform(post(SAVE_CANDIDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonTestUtil.asJsonString(request)))

        then:
        response.andExpect(status().isCreated())
        candidateRepository.findAll().size() == 2
    }

    def cleanup() {
        candidateRepository.deleteAll();
    }
}
