package billennium.quizapp.resource.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class BasePage {
    private Long totalElements;
    private Integer pageNumber;
    private Integer pageSize;
}
