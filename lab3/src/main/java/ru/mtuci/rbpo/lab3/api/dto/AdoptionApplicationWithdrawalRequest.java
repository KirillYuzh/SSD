package ru.mtuci.rbpo.lab3.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Заявитель, отзывающий свою заявку")
public record AdoptionApplicationWithdrawalRequest(

        @Schema(description = "Заявитель, подавший заявку", example = "1")
        @NotNull(message = "значение обязательно")
        @Positive(message = "идентификатор должен быть положительным числом") Long applicantId) {
}
