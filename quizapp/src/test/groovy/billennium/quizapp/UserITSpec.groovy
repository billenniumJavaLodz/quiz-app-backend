package billennium.quizapp

import billennium.quizapp.entity.User
import billennium.quizapp.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.test.web.servlet.MockMvc
import static billennium.quizapp.controller.ControllerConstants.USER
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import spock.lang.Specification

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserITSpec extends Specification {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository


    def "Insert a User Entity and retrieve the resulting String via GET /user/1"() {
        given: "A User Entity"
        userRepository.save([
                name: "newUser"
        ] as User)

        when:
        def response = mockMvc.perform(get(USER + "/1"))
        then:
        response.andExpect(status().isOk())
    }
}
