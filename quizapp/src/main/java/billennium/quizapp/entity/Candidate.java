package billennium.quizapp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Candidate {

    @Id
    @Type(type = "pg-uuid")
    private UUID id;

    private String email;

    @OneToOne(fetch = FetchType.LAZY)
    private QuizExecuted quizExecuted;

}
