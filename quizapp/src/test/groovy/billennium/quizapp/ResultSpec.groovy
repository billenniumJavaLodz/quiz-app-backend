package billennium.quizapp

import billennium.quizapp.controller.QuizController
import billennium.quizapp.entity.*
import billennium.quizapp.repository.*
import billennium.quizapp.resource.candidate.CandidateResultDto
import billennium.quizapp.resource.quiz.QuizResultPage
import billennium.quizapp.utils.JsonTestUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static billennium.quizapp.controller.ControllerConstants.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class ResultSpec extends Specification {
    @Autowired
    private MockMvc mockMvc
    @Autowired
    private QuizController quizController

    @Autowired
    private CandidateRepository candidateRepository

    @Autowired
    private AnswerRepository answerRepository

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizDefinitionRepository quizDefinitionRepository

    @Autowired
    private QuizExecutedRepository quizExecutedRepository

    @Autowired
    private ResultRepository resultRepository;

    def resultDto

    def candidate

    def quizResultPage

    def quizId;

    def setup() {

        def answer = Answer.builder()
                .text("Testowa Odpowiedz")
                .correctAnswer(true)
                .build()

        def question = Question.builder()
                .text("Testowe pytanie")
                .answers(Arrays.asList(answer))
                .build()

        def quiz = QuizDefinition.builder()
                .title("Java zestaw 1")
                .questions(Arrays.asList(question))
                .build()

        def result = Result.builder()
                .totalQuestions(1)
                .correctQuestions(1)
                .build()

        def userQuiz = QuizExecuted.builder()
                .quiz(quiz)
                .result(result)
                .quizStatus(QuizStatus.DONE)
                .build()

        candidate = candidateRepository.save(Candidate.builder()
                .email("billenet@billennum.com")
                .quizExecuted(userQuiz)
                .build())

        resultDto = CandidateResultDto.builder()
                .totalPoints(result.totalQuestions)
                .scoredPoints(result.correctQuestions)
                .scoreInPercentage(100l)
                .quizTitle(quiz.title)
                .id(candidate.id)
                .email(candidate.email)
                .build()

        def candidateResult = CandidateResultDto.builder()
                .quizTitle(quiz.title)
                .email(candidate.email)
                .id(candidate.id)
                .totalPoints(userQuiz.result.totalQuestions)
                .scoredPoints(userQuiz.result.correctQuestions)
                .scoreInPercentage(100l)
                .build()

        quizResultPage = QuizResultPage.builder()
                .pageNumber(0)
                .pageSize(1)
                .totalElements(1)
                .candidateResults(Arrays.asList(candidateResult))
                .build()

        quizId = quiz.id;

    }

    def "when get /result then resposne 200 with content"() {
        given: "CandidateResultDto"
        resultDto
        when:
        def response = mockMvc.perform(get(RESULT))
        then:
        response.andExpect(status().is2xxSuccessful()).andExpect(content().json(JsonTestUtil.asJsonString(Arrays.asList(resultDto))))
    }

    def "when get result/uuid then response content with 200 http status"() {
        given:
        resultDto
        def uuid = candidateRepository.findByEmail("billenet@billennum.com")
        when:
        def response = mockMvc.perform(get(RESULT + SLASH + uuid.get().id))
        then:
        response.andExpect(status().is2xxSuccessful()).andExpect(content().json(JsonTestUtil.asJsonString(resultDto)))
    }

    def " when get result/quiz/{id} with pageable paramas then return page of r quiz result"() {
        given:
        quizResultPage
        quizId
        when:
        def response = mockMvc.perform(get(RESULT + QUIZ + SLASH + quizId).param("pageSize", "1")
                .param("pageNumber", "0"))
        then:
        response.andExpect(status().is2xxSuccessful()).andExpect(content().json(JsonTestUtil.asJsonString(quizResultPage)))
    }

    def cleanup() {
        candidateRepository.deleteAll()
        quizExecutedRepository.deleteAll()
        resultRepository.deleteAll()
        quizDefinitionRepository.deleteAll()
        questionRepository.deleteAll()
        answerRepository.deleteAll()
    }
}
