package djavidmustafaev.io.financetracker.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private Long id;

    @Size(max = 20, message = "Имя должна быть не больше 20 символов")
    private String name;
}
