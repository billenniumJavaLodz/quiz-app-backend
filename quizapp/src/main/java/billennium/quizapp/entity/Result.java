package billennium.quizapp.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;


@Entity
@Data
public class Result extends BaseModel {

    @Column(name = "total_question")
    private Integer totalQuestions;

    @Column(name = "sum_of_correct_answers")
    private Integer correctQuestions;

}
