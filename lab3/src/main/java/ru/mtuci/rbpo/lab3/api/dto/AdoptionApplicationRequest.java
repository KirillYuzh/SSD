package ru.mtuci.rbpo.lab3.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Новая заявка на усыновление")
public record AdoptionApplicationRequest(

        @Schema(description = "Животное, на которое подана заявка", example = "1")
        @NotNull(message = "значение обязательно")
        @Positive(message = "идентификатор должен быть положительным числом") Long animalId,

        @Schema(description = "Заявитель", example = "1")
        @NotNull(message = "значение обязательно")
        @Positive(message = "идентификатор должен быть положительным числом") Long applicantId,

        @Schema(description = "Мотивация заявителя", example = "Есть дом с участком")
        @Size(max = 500, message = "длина не должна превышать 500 символов") String motivation) {
}
