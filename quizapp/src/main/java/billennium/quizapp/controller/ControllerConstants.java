package billennium.quizapp.controller;

public class ControllerConstants {

    public static final String USER = "/user";
    public static final String ID_PARAM = "/{id}";

    public static final String QUIZ = "/quiz";
    public static final Integer QUIZ_FIRST_QUESTION_NUMBER = 0;


    public static final String SLASH = "/";

    private ControllerConstants() {
        throw new IllegalStateException("Constants class");
    }
}
