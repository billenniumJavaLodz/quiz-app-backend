package billennium.quizapp.controller;

public class ControllerConstants {

    public static final String USER = "/user";
    public static final String ID_PARAM = "/{id}";
    public static final String CANDIDATE = "/candidate";
    public static final String SAVE_CANDIDATE = "/saveCandidate";
    public static final String EMAIL_PARAM = "/{email}";

    private ControllerConstants() {
        throw new IllegalStateException("Constants class");
    }
}
