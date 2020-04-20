package billennium.quizapp.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultDetails extends BaseModel {

    private Long questionId;

    private Long givenAnswerId;

    private LocalDateTime timeToResolve;

    private LocalDateTime timeOfAnswer;

    private boolean isAnswerInTime;
}
