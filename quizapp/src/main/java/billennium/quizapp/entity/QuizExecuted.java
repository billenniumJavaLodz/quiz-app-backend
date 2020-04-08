package billennium.quizapp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizExecuted extends BaseModel {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private QuizDefinition quiz;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id")
    private Result result;

    @OneToMany
    @JoinColumn(name = "result_details_id")
    private List<ResultDetails> resultDetails;

    @Enumerated(EnumType.STRING)
    private QuizStatus quizStatus;

}
