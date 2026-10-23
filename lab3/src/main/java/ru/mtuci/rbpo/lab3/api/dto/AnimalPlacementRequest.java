package ru.mtuci.rbpo.lab3.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Вольер для размещения животного")
public record AnimalPlacementRequest(

        @Schema(description = "Вольер, в который размещается животное", example = "1")
        @NotNull(message = "значение обязательно")
        @Positive(message = "идентификатор должен быть положительным числом") Long enclosureId) {
}
