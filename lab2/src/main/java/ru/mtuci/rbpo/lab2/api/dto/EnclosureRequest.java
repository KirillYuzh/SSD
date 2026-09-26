package ru.mtuci.rbpo.lab2.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Set;

@Schema(description = "Новый вольер")
public record EnclosureRequest(

        @Schema(description = "Название вольера", example = "Вольер для собак")
        @NotBlank(message = "значение не должно быть пустым") String name,

        @Schema(description = "Виды животных, которых допустимо размещать", example = "[\"Собака\"]")
        @NotEmpty(message = "список видов не должен быть пустым")
        Set<@NotBlank(message = "значение не должно быть пустым") String> allowedSpecies,

        @Schema(description = "Вместимость вольера", example = "4")
        @NotNull(message = "значение обязательно")
        @Positive(message = "вместимость должна быть положительным числом") Integer capacity) {
}
