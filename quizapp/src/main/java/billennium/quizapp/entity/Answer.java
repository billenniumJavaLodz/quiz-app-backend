package billennium.quizapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Answer extends BaseModel {

    @NotNull(message = "No answer text provided.")
    @Column(columnDefinition = "TEXT")
    private String text;

    @ManyToMany(mappedBy = "answers")
    @JsonIgnore
    private List<Question> question;

    @Type(type = "true_false")
    private Boolean correctAnswer;
}