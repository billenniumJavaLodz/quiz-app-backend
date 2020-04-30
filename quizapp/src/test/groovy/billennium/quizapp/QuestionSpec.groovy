package billennium.quizapp

import billennium.quizapp.entity.Answer
import billennium.quizapp.entity.Question
import billennium.quizapp.repository.AnswerRepository
import billennium.quizapp.repository.QuestionRepository
import billennium.quizapp.resource.answer.AnswerDto
import billennium.quizapp.resource.answer.AnswerToSaveDto
import billennium.quizapp.resource.question.QuestionBaseDto
import billennium.quizapp.resource.question.QuestionDto
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
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

    def questionBaseDto

    def questionToSaveDto

    def questionDto

    def question

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

        questionBaseDto = QuestionBaseDto.builder().id(question.id).text(question.text).build()

        def answerDto = AnswerDto.builder()
                .text(answer.text)
                .id(answer.id).build()

        questionDto = QuestionDto.builder()
                .id(question.id)
                .text(question.text)
                .timeToAnswer(question.timeToAnswerInSeconds)
                .answers(Arrays.asList(answerDto))
                .build()

        def firstAnswerToSave = AnswerToSaveDto.builder().text("Test1").correctAnswer(false).build()
        def secondAnswerToSave = AnswerToSaveDto.builder().text("Test").correctAnswer(true).build()
        questionToSaveDto = QuestionToSaveDto.builder()
                .answers(Arrays.asList(firstAnswerToSave, secondAnswerToSave))
                .text("TestQuestion2")
                .build()
    }

    def cleanup() {
        questionRepository.deleteAll()
        answerRepository.deleteAll()
    }

    def " when get via /question then return all question"() {
        given:
        questionBaseDto
        when:
        def response = mockMvc.perform(get(QUESTION))
        then:
        response.andExpect(status().is2xxSuccessful())
                .andExpect(content().json(JsonTestUtil.asJsonString(Arrays.asList(questionBaseDto))))
    }

    def "when post /question then return 200 without content"() {
        given:
        questionToSaveDto
        when:
        def response = mockMvc.perform(post(QUESTION)
                .contentType(MediaType.APPLICATION_JSON).content(JsonTestUtil.asJsonString(questionToSaveDto)))
        then:
        response.andExpect(status().is2xxSuccessful())
        questionRepository.findAll().size() == 2
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
}
