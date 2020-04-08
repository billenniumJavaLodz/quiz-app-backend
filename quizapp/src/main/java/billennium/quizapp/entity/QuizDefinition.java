package billennium.quizapp.entity;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity(name = "quiz_definition")
@Data
public class QuizDefinition extends BaseModel {

    @Size(min = 2, max = 100, message = "The name must be between 2 and 100 messages.")
    @NotNull(message = "Please provide a name")
    private String title;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<Question> questions;
}
