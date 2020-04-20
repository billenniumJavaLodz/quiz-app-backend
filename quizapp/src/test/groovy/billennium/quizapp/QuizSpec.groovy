package billennium.quizapp

import billennium.quizapp.controller.QuizController
import billennium.quizapp.entity.*
import billennium.quizapp.repository.*
import billennium.quizapp.resource.quiz.AnswerDto
import billennium.quizapp.resource.quiz.AnswersDto
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
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

    @Autowired
    private ResultRepository resultRepository


    def candidate

    def responseModelWithFirstQuestion

    def responseModelEndQuiz

    def defaultAnswerModelPost

    def answerModelPost

    def setup() {

        def answer = Answer.builder()
                .text("Testowa Odpowiedz")
                .correctAnswer(true)
                .build()

        def question = Question.builder()
                .text("Testowe pytanie")
                .timeToAnswerInSeconds(3)
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
                .id(answer.id)
                .text("Testowa Odpowiedz")
                .build()

        def questionDto = QuestionDto.builder()
                .id(question.id)
                .text("Testowe pytanie")
                .answers(Collections.singletonList(answerDto))
                .timeToAnswer(3)
                .build()

        responseModelWithFirstQuestion = QuizDefinitionDto.builder().
                id(userQuiz.id)
                .numberOfQuestions(1)
                .actualQuestion(1)
                .question(questionDto)
                .build()

        responseModelEndQuiz = QuizDefinitionDto.builder().build()

        defaultAnswerModelPost = AnswersDto.builder()
                .answerId(null)
                .questionId(null)
                .build()

        answerModelPost = AnswersDto.builder()
                .questionId(question.id)
                .answerId(answer.id)
                .build()
    }

    def cleanup() {
        candidateRepository.deleteAll()
        quizExecutedRepository.deleteAll()
        resultRepository.deleteAll()
        quizDefinitionRepository.deleteAll()
        questionRepository.deleteAll()
        answerRepository.deleteAll()
    }

    def "solving quiz in post quiz/uuid"() {
        given: "A Candidate Entity, default post body request,  excepting response body with first question, post body request with answer and default body response"
        candidate
        defaultAnswerModelPost
        responseModelWithFirstQuestion
        answerModelPost
        responseModelEndQuiz

        when:
        def firstResponse = mockMvc.perform(post(QUIZ + SLASH + candidate.id.toString())
                .contentType(MediaType.APPLICATION_JSON).content(JsonTestUtil.asJsonString(defaultAnswerModelPost)))
        def secondResponse = mockMvc.perform(post(QUIZ + SLASH + candidate.id.toString())
                .contentType(MediaType.APPLICATION_JSON).content(JsonTestUtil.asJsonString(answerModelPost)))
        def result = resultRepository.findById(1l)
        then:
        firstResponse.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JsonTestUtil.asJsonString(responseModelWithFirstQuestion)))
        secondResponse.andExpect(status().is2xxSuccessful()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JsonTestUtil.asJsonString(responseModelEndQuiz)))
        result.get().totalQuestions == 1
        result.get().correctQuestions == 1
    }

    def "solving quiz with answer after resolve time  in post quiz/uuid"() {
        given: "A Candidate Entity, default post body request,  excepting response body with first question, post body request with answer and default body response"
        candidate
        defaultAnswerModelPost
        responseModelWithFirstQuestion
        answerModelPost
        responseModelEndQuiz

        when:
        mockMvc.perform(post(QUIZ + SLASH + candidate.id.toString())
                .contentType(MediaType.APPLICATION_JSON).content(JsonTestUtil.asJsonString(defaultAnswerModelPost)))
        Thread.sleep(7000)
        mockMvc.perform(post(QUIZ + SLASH + candidate.id.toString())
                .contentType(MediaType.APPLICATION_JSON).content(JsonTestUtil.asJsonString(answerModelPost)))
        def result = resultRepository.findById(2l)
        then:
        result.get().totalQuestions == 1
        result.get().correctQuestions == 0
    }

}
