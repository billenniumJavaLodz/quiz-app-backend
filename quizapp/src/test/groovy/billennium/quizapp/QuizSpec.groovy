package billennium.quizapp

import billennium.quizapp.controller.QuizController
import billennium.quizapp.entity.*
import billennium.quizapp.repository.*
import billennium.quizapp.resource.quiz.AnswerDto
import billennium.quizapp.resource.quiz.QuestionDto
import billennium.quizapp.resource.quiz.QuizDefinitionDto
import billennium.quizapp.utils.JsonTestUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static billennium.quizapp.controller.ControllerConstants.QUIZ
import static billennium.quizapp.controller.ControllerConstants.SLASH
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class QuizSpec extends Specification {

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

    def candidate

    def model;

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

        def userQuiz = QuizExecuted.builder()
                .quiz(quiz)
                .build()

        candidate = candidateRepository.save(Candidate.builder()
                .email("billenet@billennum.com")
                .quizExecuted(userQuiz)
                .build())

        def answerDto = AnswerDto.builder()
                .id(1)
                .text("Testowa Odpowiedz")
                .build()

        def questionDto = QuestionDto.builder()
                .id(1l)
                .text("Testowe pytanie")
                .answers(Collections.singletonList(answerDto))
                .build()

        model = QuizDefinitionDto.builder().
                id(1l)
                .numberOfQuestions(1)
                .actualQuestion(0)
                .question(questionDto)
                .build()
    }

    def cleanup() {
        candidateRepository.deleteAll()
        quizExecutedRepository.deleteAll()
        quizDefinitionRepository.deleteAll()
        questionRepository.deleteAll()
        answerRepository.deleteAll()
    }

    def "when Get in user/uuid/quiz then response status 200 with content"() {
        given: "A Candidate Entity and response excepting model"
        candidate
        model

        when:
        def response = mockMvc.perform(get(QUIZ + SLASH + candidate.id.toString()))
        then:
        response.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JsonTestUtil.asJsonString(model)))
    }

    def "when Get with bad uuid  in user/uuid/quiz then response status 200 with default content"() {
        given: "A Candidate Entity and response excepting model"
        candidate
        model

        when:
        def response = mockMvc.perform(get(QUIZ + SLASH + UUID.randomUUID()))
        then:
        response.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JsonTestUtil.asJsonString(QuizDefinitionDto.builder().build())))
    }
}