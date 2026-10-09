package ru.mtuci.rbpo.lab2.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.util.Set;


@Schema(description = "Поля вольера для изменения, отсутствующие поля не меняются")
public record EnclosurePatch(

        @Schema(description = "Название вольера", example = "Вольер для собак")
        @Pattern(regexp = "\\S+", message = "значение не должно быть пустым") String name,

        @Schema(description = "Виды животных, которых допустимо размещать", example = "[\"Собака\", \"Кошка\"]")
        Set<@Pattern(regexp = "\\S+", message = "значение не должно быть пустым") String> allowedSpecies,

        @Schema(description = "Вместимость вольера", example = "6")
        @Positive Integer capacity) {
}
