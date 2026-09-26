package ru.mtuci.rbpo.lab2.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import ru.mtuci.rbpo.lab2.domain.AnimalStatus;

@Schema(description = "Новое животное приюта")
public record AnimalRequest(

        @Schema(description = "Кличка", example = "Бобик")
        @NotBlank(message = "значение не должно быть пустым") String name,

        @Schema(description = "Вид животного", example = "Собака")
        @NotBlank(message = "значение не должно быть пустым") String species,

        @Schema(description = "Возраст в полных годах", example = "3")
        @NotNull(message = "значение обязательно")
        @PositiveOrZero(message = "значение не должно быть отрицательным") Integer age,

        @Schema(description = "Статус размещения, по умолчанию AVAILABLE", example = "AVAILABLE")
        AnimalStatus status) {
}
