package djavidmustafaev.io.financetracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDto {
    private Long id;

    @Size(max = 100, message = "Название должно быть не больше 100 символов")
    private String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private CategoryDto category;

    private Long categoryId;

    @NotNull(message = "Сумма не может быть пустой")
    @Positive(message = "Сумма должна быть положительным числом")
    private BigDecimal amount;

    @Size(max = 255, message = "Описание не может быть длиннее 255 символов")
    private String description;

    @NotNull(message = "Дата не может быть пустой")
    private LocalDate date;
}
