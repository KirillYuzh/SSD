package ru.mtuci.rbpo.lab2.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import ru.mtuci.rbpo.lab2.domain.AnimalStatus;

/**
 * Частичное обновление животного
 */
@Schema(description = "Поля животного для изменения, отсутствующие поля не меняются")
public record AnimalPatch(

        @Schema(description = "Кличка", example = "Бобик")
        @Pattern(regexp = "\\S+", message = "значение не должно быть пустым") String name,

        @Schema(description = "Вид животного", example = "Собака")
        @Pattern(regexp = "\\S+", message = "значение не должно быть пустым") String species,

        @Schema(description = "Возраст в полных годах", example = "4")
        @PositiveOrZero Integer age,

        @Schema(description = "Статус размещения", example = "AVAILABLE")
        AnimalStatus status) {
}
