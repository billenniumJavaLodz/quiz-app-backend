package billennium.quizapp.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity(name = "result_details")
@Data
public class ResultDetails extends BaseModel {

    @Column(name = "asked_question")
    private String question;

    @Column(name = "given_answer")
    private String givenAnswer;

    @ManyToOne
    @JsonIgnore
    private ExecutingQuiz executingQuiz;

}
