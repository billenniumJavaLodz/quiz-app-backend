package billennium.quizapp

import billennium.quizapp.controller.QuizController
import billennium.quizapp.entity.*
import billennium.quizapp.repository.*
import billennium.quizapp.resource.answer.AnswerDto
import billennium.quizapp.resource.answer.AnswersDto
import billennium.quizapp.resource.question.QuestionBaseDto
import billennium.quizapp.resource.question.QuestionDto
import billennium.quizapp.resource.quiz.*
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

    @Autowired
    private ResultRepository resultRepository


    def candidate

    def responseModelWithFirstQuestion

    def responseModelEndQuiz

    def defaultAnswerModelPost

    def answerModelPost

    def quizEndDto

    long quizExecutedId

    def questionBaseDto

    def quizToSaveDto

    def quizPage

    def setup() {

        def answer = answerRepository.save(Answer.builder()
                .text("Testowa Odpowiedz")
                .correctAnswer(true)
                .build())

        def question = questionRepository.save(Question.builder()
                .text("Testowe pytanie")
                .timeToAnswerInSeconds(3)
                .answers(Collections.emptyList())
                .build())
        question.answers = Arrays.asList(answer)
        questionRepository.save(question)

        def quiz = quizDefinitionRepository.save(QuizDefinition.builder()
                .title("Java zestaw 1")
                .questions(Collections.emptyList())
                .build())
        quiz.questions = Arrays.asList(question)
        quizDefinitionRepository.save(quiz)

        def userQuiz = quizExecutedRepository.save(QuizExecuted.builder()
                .quizStatus(QuizStatus.READY)
                .build()
        )
        userQuiz.quiz = quiz
        quizExecutedRepository.save(userQuiz)

        candidate = candidateRepository.save(Candidate.builder()
                .email("billenet@billennum.com")
                .build())
        candidate.quizExecuted = userQuiz
        candidateRepository.save(candidate)

        def answerDto = AnswerDto.builder()
                .id(answer.id)
                .text(answer.text)
                .build()

        def questionDto = QuestionDto.builder()
                .id(question.id)
                .text(question.text)
                .answers(Collections.singletonList(answerDto))
                .timeToAnswer(question.timeToAnswerInSeconds)
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

        quizEndDto = QuizEndDto.builder()
                .quizId(candidate.quizExecuted.id)
                .build()

        quizExecutedId = userQuiz.id

        questionBaseDto = QuestionBaseDto.builder()
                .id(question.id)
                .build()

        quizToSaveDto = QuizToSaveDto.builder()
                .title("Test1")
                .questions(Arrays.asList(questionBaseDto))
                .build()


        def quizBaseDto = QuizBaseDto.builder()
                .id(quiz.id)
                .title(quiz.title)
                .totalTime(question.timeToAnswerInSeconds)
                .numberOfQuestions(quiz.questions.size())
                .build()

        quizPage = QuizPage.builder()
                .totalElements(1)
                .pageSize(20)
                .pageNumber(0)
                .quizzes(Arrays.asList(quizBaseDto))
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
        def result = resultRepository.findAll().get(0)
        then:
        firstResponse.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JsonTestUtil.asJsonString(responseModelWithFirstQuestion)))
        secondResponse.andExpect(status().is2xxSuccessful()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(JsonTestUtil.asJsonString(responseModelEndQuiz)))
        result.totalQuestions == 1
        result.correctQuestions == 1
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
        def result = resultRepository.findAll().get(0)
        then:
        result.totalQuestions == 1
        result.correctQuestions == 0
    }


    def "when post to quiz/stop quiz status change status to done"() {
        given:
        candidate
        quizEndDto
        when:
        def response = mockMvc.perform(post(QUIZ + STOP_QUIZ).contentType(MediaType.APPLICATION_JSON)
                .content(JsonTestUtil.asJsonString(quizEndDto)))
        then:
        response.andExpect(status().is2xxSuccessful())
        quizExecutedRepository.findById(quizExecutedId).get().getQuizStatus().equals(QuizStatus.DONE)
    }

    def "when post to /quiz then save new quiz to db"() {
        given:
        quizToSaveDto
        when:
        def response = mockMvc.perform(post(QUIZ)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonTestUtil.asJsonString(quizToSaveDto)))
        then:
        response.andExpect(status().is2xxSuccessful())
    }

    def "when get to /quiz with params then return page of quizzes"() {
        given:
        quizPage
        when:
        def response = mockMvc.perform(get(QUIZ).param("pageSize", "20")
                .param("pageNumber", "0"))
        then:
        response.andExpect(status().is2xxSuccessful())
                .andExpect(content().json(JsonTestUtil.asJsonString(quizPage)))
    }
}
