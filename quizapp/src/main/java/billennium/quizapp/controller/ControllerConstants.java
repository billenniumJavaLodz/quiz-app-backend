package billennium.quizapp.controller;

public class ControllerConstants {

    public static final String USER = "/user";
    public static final String ID_PARAM = "/{id}";
    public static final String CANDIDATE = "/candidate";
    public static final String EMAIL = "/email";
    public static final String EMAIL_PARAM = "/{email}";
    public static final String QUIZ = "/quiz";
    public static final String SLASH = "/";

    private ControllerConstants() {
        throw new IllegalStateException("Constants class");
    }
}
