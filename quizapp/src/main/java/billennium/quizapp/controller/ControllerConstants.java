package billennium.quizapp.controller;

public class ControllerConstants {

    public static final String RESULT = "/result";
    public static final String ID_PARAM = "/{id}";
    public static final String CANDIDATE = "/candidate";
    public static final String EMAIL = "/email";
    public static final String EMAIL_PARAM = "/{email}";
    public static final String QUIZ = "/quiz";
    public static final String STOP_QUIZ = "/stop";
    public static final String QUESTION = "/question";
    public static final String SLASH = "/";
    public static final String CATEGORY = "/category";

    private ControllerConstants() {
        throw new IllegalStateException("Constants class");
    }
}
