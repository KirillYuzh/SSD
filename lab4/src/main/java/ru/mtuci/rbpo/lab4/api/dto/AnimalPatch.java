package ru.mtuci.rbpo.lab4.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(
    description = "Поля животного для изменения, отсутствующие поля не меняются"
)
public record AnimalPatch(
    @Schema(description = "Кличка", example = "Бобик")
    @Pattern(regexp = "\\S+", message = "значение не должно быть пустым")
    String name,

    @Schema(description = "Вид животного", example = "Собака")
    @Pattern(regexp = "\\S+", message = "значение не должно быть пустым")
    String species,

    @Schema(description = "Возраст в полных годах", example = "4")
    @PositiveOrZero
    Integer age
) {}
