package billennium.quizapp.entity;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;


@Entity(name = "executing_quiz")
@Data
public class ExecutingQuiz extends BaseModel {

    @OneToOne
    private Candidate candidateId;

    @OneToOne
    @JoinColumn(name = "quiz_id")
    private QuizDefinition quiz;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "result_id")
    private Result result;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "result_details_id")
    private List<ResultDetails> resultDetails;

    @Enumerated(EnumType.STRING)
    private QuizStatus quizStatus;

}
