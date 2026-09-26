package ru.mtuci.rbpo.lab2.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.mtuci.rbpo.lab2.domain.AdoptionStatus;

@Schema(description = "Новая заявка на усыновление")
public record AdoptionApplicationRequest(

        @Schema(description = "Животное, на которое подана заявка", example = "1")
        @NotNull(message = "значение обязательно")
        @Positive(message = "идентификатор должен быть положительным числом") Long animalId,

        @Schema(description = "Заявитель", example = "1")
        @NotNull(message = "значение обязательно")
        @Positive(message = "идентификатор должен быть положительным числом") Long applicantId,

        @Schema(description = "Статус заявки, по умолчанию PENDING", example = "PENDING")
        AdoptionStatus status) {
}
