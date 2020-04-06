package billennium.quizapp.controller;

import billennium.quizapp.exception.UserException;
import billennium.quizapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import static billennium.quizapp.controller.ControllerConstants.ID_PARAM;
import static billennium.quizapp.controller.ControllerConstants.USER;


@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping(USER+ID_PARAM)
    @ResponseStatus(HttpStatus.OK)
    public String getUser(@PathVariable("id") Long id) {
        return userRepository.findById(id).orElseThrow(UserException::new).getName();
    }
}
