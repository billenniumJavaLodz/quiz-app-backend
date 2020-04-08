package billennium.quizapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Question extends BaseModel {

    @NotNull(message = "Question text not provided")
    @Column(columnDefinition = "TEXT")
    private String text;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(
            name = "questions_answers",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "answer_id")
    )
    private List<Answer> answers;

    @ManyToMany(mappedBy = "questions")
    @JsonIgnore
    private List<QuizDefinition> quiz;

    @OneToOne
    private Answer correctAnswer;
}
