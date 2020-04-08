package billennium.quizapp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result extends BaseModel {

    private Integer totalQuestions;

    private Integer correctQuestions;

}
