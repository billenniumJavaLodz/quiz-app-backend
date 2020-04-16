package billennium.quizapp.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultDetails extends BaseModel {

    private Long questionId;

    private Long givenAnswerId;

}
