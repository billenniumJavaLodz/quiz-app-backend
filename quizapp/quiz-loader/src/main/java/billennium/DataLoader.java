package billennium;

import billennium.quizapp.entity.Answer;
import billennium.quizapp.entity.Question;
import billennium.quizapp.entity.QuizDefinition;
import billennium.quizapp.repository.QuizDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final QuizDefinitionRepository quizDefinitionRepository;

    @Override
    public void run(ApplicationArguments args) {
        QuizDefinition quizDefinition = QuizDefinition.builder()
                .title("JAVA")
                .questions(createListQuestions())
                .build();
        quizDefinitionRepository.save(quizDefinition);
    }

    private List<Question> createListQuestions() {
        Question question1 = Question.builder()
                .text("Java to:")
                .answers(createListAnswersForQuestion("Język programowania", "JVM", "C++"))
                .build();
        Question question2 = Question.builder()
                .text("JVM TO")
                .answers(createListAnswersForQuestion("JRE", "JDK", "JAVA VIRTUAL MACHINE"))
                .build();
        Question question3 = Question.builder()
                .text("Co to jest konstruktor domyślny?")
                .answers(createListAnswersForQuestion("Jest tworzony jeśli nie zdefiniowano jawnie konstruktora w klasie",
                        "Jest tworzony tylko wtedy kiedy zostanie jawnie zdefiniowany w kodzie",
                        "Konstruktor zdefiniowany ze słowem kluczowym default"))
                .build();
        Question question4 = Question.builder()
                .text("Czy można zmienić istniejący String?")
                .answers(createListAnswersForQuestion("Tylko za pomocą StringBuilder-a", "NIE", "Tak," +
                        " jak każdy inny obiekt"))
                .build();
        Question question5 = Question.builder()
                .text("Czy klasa może dziedziczyć kilka interfejsów?")
                .answers(createListAnswersForQuestion("Tylko wtedy jak interfejsy roszerzają jeden wspólny interfejs",
                        "NIE", "TAK"))
                .build();

        return Arrays.asList(question1, question2, question3, question4, question5);
    }

    private List<Answer> createListAnswersForQuestion(String... answers) {
        return Arrays.stream(answers).map(
                answer ->
                        Answer.builder()
                                .text(answer)
                                .correctAnswer(setCorrectAnswers(answer))
                                .build()
        ).collect(Collectors.toList());
    }

    boolean setCorrectAnswers(String answer) {
        List<String> correctAnswers = new ArrayList<>();
        correctAnswers.add("Język programowania");
        correctAnswers.add("JAVA VIRTUAL MACHINE");
        correctAnswers.add("Jest tworzony jeśli nie zdefiniowano jawnie konstruktora w klasie");
        correctAnswers.add("NIE");
        correctAnswers.add("TAK");

        return correctAnswers.contains(answer);
    }
}
