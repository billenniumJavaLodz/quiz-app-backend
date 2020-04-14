package billennium.quizapp

import billennium.quizapp.controller.QuizController
import billennium.quizapp.entity.*
import billennium.quizapp.repository.*
import billennium.quizapp.resource.AnswerDto
import billennium.quizapp.resource.QuestionDto
import billennium.quizapp.resource.QuizDefinitionDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static billennium.quizapp.controller.ControllerConstants.*
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
    private QuestionRepository questionRepository

    @Autowired
    private QuizDefinitionRepository quizDefinitionRepository

    @Autowired
    private QuizExecutedRepository quizExecutedRepository

    def candidate

    def model;

    def setup() {

        def answer = answerRepository.save([
                text         : "Testowa Odpowiedz",
                correctAnswer: true
        ] as Answer)

        def question = questionRepository.save([
                text   : "Testowe pytanie",
                answers: Collections.emptyList()
        ] as Question)
        question.setAnswers(Collections.singletonList(answer))
        questionRepository.save(question)

        def quiz = quizDefinitionRepository.save([
                title: "Java zesatw 1"
        ] as QuizDefinition)
        quiz.questions = Collections.singletonList(question)
        quizDefinitionRepository.save(quiz)

        def userQuiz = quizExecutedRepository.save([
                quiz: quiz
        ] as QuizExecuted)
        quizExecutedRepository.save(userQuiz)

        candidate = candidateRepository.save([
                email       : "billenet@billennium.com",
                quizExecuted: userQuiz
        ] as Candidate)
        candidateRepository.save(candidate)

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

    def "when Get in user/uuid/quiz/0 then response status 200 with content"() {
        given: "A Candidate Entity and response excepting model"
        candidate
        model

        when:
        def response = mockMvc.perform(get(USER + SLASH + candidate.id.toString() + QUIZ ))
        then:
        response.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JsonTestUtil.asJsonString(model)))
    }
}
