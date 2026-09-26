package ru.mtuci.rbpo.lab2.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Заявка, по которой готовится акт передачи")
public record AnimalHandoverRecordRequest(

        @Schema(description = "Идентификатор одобренной заявки", example = "1")
        @NotNull(message = "значение обязательно")
        @Positive(message = "идентификатор должен быть положительным числом") Long applicationId) {
}
