package billennium.quizapp

import billennium.quizapp.controller.QuizController
import billennium.quizapp.entity.*
import billennium.quizapp.repository.*
import billennium.quizapp.resource.candidate.CandidateResultDto
import billennium.quizapp.utils.JsonTestUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static billennium.quizapp.controller.ControllerConstants.RESULT
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

        def candidate = candidateRepository.save(Candidate.builder()
                .email("billenet@billennum.com")
                .quizExecuted(userQuiz)
                .build())

        resultDto = Arrays.asList(CandidateResultDto.builder()
                .totalPoints(result.totalQuestions)
                .scoredPoints(result.correctQuestions)
                .quizTitle(quiz.title)
                .id(candidate.id)
                .email(candidate.email)
                .build())

    }

    def "when get /result then resposne 200 with content"() {
        given: "CandidateResultDto"
        resultDto
        when:
        def response = mockMvc.perform(get(RESULT))
        then:
        response.andExpect(status().is2xxSuccessful()).andExpect(content().json(JsonTestUtil.asJsonString(resultDto)))
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
