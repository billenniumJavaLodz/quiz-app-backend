package billennium.quizapp.entity;


import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
abstract class BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

}
