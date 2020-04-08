package billennium.quizapp.entity;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.Calendar;
import java.util.UUID;

@Entity
@Data
public class Candidate {

    @Id
    @Type(type = "pg-uuid")
    private UUID id;

    @Column(name = "email")
    private String email;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private Calendar createdDate;

    @OneToOne
    private ExecutingQuiz executingQuiz;

}
