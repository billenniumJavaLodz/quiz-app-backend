package billennium.quizapp


import billennium.quizapp.entity.Candidate
import billennium.quizapp.entity.QuizDefinition
import billennium.quizapp.repository.CandidateRepository
import billennium.quizapp.repository.QuizDefinitionRepository
import billennium.quizapp.repository.QuizExecutedRepository
import billennium.quizapp.resource.candidate.CandidateDto
import billennium.quizapp.utils.JsonTestUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static billennium.quizapp.controller.ControllerConstants.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class CandidateITSpec extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private CandidateRepository candidateRepository

    @Autowired
    private QuizExecutedRepository quizExecutedRepository

    @Autowired
    private QuizDefinitionRepository quizDefinitionRepository

    def candidateDto

    def setup() {
        def candidate = candidateRepository.save(Candidate.builder()
                .email("billenet@billennium.com")
                .build())

        quizDefinitionRepository.save(QuizDefinition.builder()
                .title("BIG-DATA")
                .questions(Collections.emptyList())
                .build())

        candidateDto = CandidateDto.builder()
                .email(candidate.email)
                .id(candidate.id.toString())
                .build()
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
        def response = mockMvc.perform(post(CANDIDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonTestUtil.asJsonString(request)))

        then:
        response.andExpect(status().isCreated())
        candidateRepository.findAll().size() == 2
    }

    def "when Get in getCandidate/uuid then response status 200 with content"() {
        given:
        candidateDto
        def uuid = candidateRepository.findByEmail("billenet@billennium.com")

        when:
        def response = mockMvc.perform(get(CANDIDATE + SLASH + uuid.get().id.toString() + EMAIL))

        then:
        response.andExpect(status().is2xxSuccessful())
                .andExpect(content().json(JsonTestUtil.asJsonString(candidateDto)))
    }

    def "when Get in getCandidate/uuid with bad uuid then response status 200 with default content"() {
        given:
        def uuid = UUID.randomUUID()

        when:
        def response = mockMvc.perform(get(CANDIDATE + SLASH + uuid + EMAIL))

        then:
        response.andExpect(status().is2xxSuccessful())
                .andExpect(content().json(JsonTestUtil.asJsonString(CandidateDto.builder().build())))
    }

    def cleanup() {
        candidateRepository.deleteAll();
        quizExecutedRepository.deleteAll()
        quizDefinitionRepository.deleteAll()
    }
}
