package billennium.quizapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
@Data
public class Answer extends BaseModel {

    @Size(min = 1, max = 50, message = "The answer should be less than 50 characters")
    @NotNull(message = "No answer text provided.")
    private String text;

    @ManyToOne
    @JoinColumn(name = "question_id")
    @JsonIgnore
    private Question question;

    @Type(type = "true_false")
    @NotNull
    private Boolean correctAnswer;

}