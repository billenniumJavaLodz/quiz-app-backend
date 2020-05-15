package billennium.quizapp

import billennium.quizapp.entity.Answer
import billennium.quizapp.entity.Question
import billennium.quizapp.entity.QuizDefinition
import billennium.quizapp.repository.AnswerRepository
import billennium.quizapp.repository.QuestionRepository
import billennium.quizapp.repository.QuizDefinitionRepository
import billennium.quizapp.resource.answer.AnswerGetDto
import billennium.quizapp.resource.answer.AnswerToSaveDto
import billennium.quizapp.resource.question.QuestionGetDto
import billennium.quizapp.resource.question.QuestionPageDto
import billennium.quizapp.resource.question.QuestionToSaveDto
import billennium.quizapp.utils.JsonTestUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static billennium.quizapp.controller.ControllerConstants.QUESTION
import static billennium.quizapp.controller.ControllerConstants.SLASH
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class QuestionSpec extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    AnswerRepository answerRepository

    @Autowired
    QuizDefinitionRepository quizDefinitionRepository;

    def questionPageDto

    def questionToSaveDto

    def questionDto

    def question

    def questionInQuiz

    def setup() {
        def answer = answerRepository.save(Answer.builder()
                .text("Test")
                .correctAnswer(true)
                .build())
        question = questionRepository.save(Question.builder()
                .text("TestQuestion")
                .answers(Collections.emptyList())
                .build())
        question.answers = Arrays.asList(answer)
        questionRepository.save(question)

        def answerDto = AnswerGetDto.builder()
                .text(answer.text)
                .id(answer.id)
                .correctAnswer(true)
                .build()

        questionDto = QuestionGetDto.builder()
                .id(question.id)
                .text(question.text)
                .timeToAnswer(question.timeToAnswerInSeconds)
                .answers(Arrays.asList(answerDto))
                .build()

        questionPageDto = QuestionPageDto.builder()
                .pageNumber(0)
                .pageSize(1)
                .totalElements(2).questions(Arrays.asList(questionDto)).build()

        def firstAnswerToSave = AnswerToSaveDto.builder().text("Test1").correctAnswer(false).build()
        def secondAnswerToSave = AnswerToSaveDto.builder().text("Test").correctAnswer(true).build()
        questionToSaveDto = QuestionToSaveDto.builder()
                .answers(Arrays.asList(firstAnswerToSave, secondAnswerToSave))
                .text("TestQuestion2")
                .build()


        questionInQuiz = questionRepository.save(Question.builder()
                .text("TestQuestion1")
                .answers(Collections.emptyList())
                .build())
        question.answers = Arrays.asList(answer)
        questionRepository.save(questionInQuiz)

        def quiz = quizDefinitionRepository.save(QuizDefinition.builder()
                .title("TestQuiz")
                .questions(Collections.emptyList()).build())
        quiz.questions = Arrays.asList(questionInQuiz)
        quizDefinitionRepository.save(quiz)


    }

    def cleanup() {
        quizDefinitionRepository.deleteAll()
        questionRepository.deleteAll()
        answerRepository.deleteAll()
    }

    def " when get via /question then return all question"() {
        given:
        questionPageDto
        when:
        def response = mockMvc.perform(get(QUESTION)
                .param("pageSize", "1")
                .param("pageNumber", "0"))
        then:
        response.andExpect(status().is2xxSuccessful())
                .andExpect(content().json(JsonTestUtil.asJsonString(questionPageDto)))
    }

    def "when post /question then return 200 without content"() {
        given:
        questionToSaveDto
        when:
        def response = mockMvc.perform(post(QUESTION)
                .contentType(MediaType.APPLICATION_JSON).content(JsonTestUtil.asJsonString(questionToSaveDto)))
        then:
        response.andExpect(status().is2xxSuccessful())
        questionRepository.findAll().size() == 3
        answerRepository.findAll().size() == 2
    }

    def "get question/id then return questionDto"() {
        given:
        question
        questionDto
        when:
        def response = mockMvc.perform(get(QUESTION + SLASH + question.id))
        then:
        response.andExpect(content().json(JsonTestUtil.asJsonString(questionDto)))
    }

    def "when delete question/id then return http status 204"() {
        given:
        question
        when:
        def response = mockMvc.perform(delete(QUESTION + SLASH + question.id))
        then:
        response.andExpect(status().is2xxSuccessful())
    }

    def "when delete question/id with not existing id then return delete status false"() {
        given:
        questionInQuiz
        when:
        def response = mockMvc.perform(delete(QUESTION + SLASH + questionInQuiz.id))
        then:
        response.andExpect(status().is4xxClientError())
    }
}
