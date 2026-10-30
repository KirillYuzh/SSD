package ru.mtuci.rbpo.lab4.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.mtuci.rbpo.lab4.domain.AdoptionStatus;

import java.time.Instant;

@Schema(description = "Заявка на усыновление")
public record AdoptionApplicationResponse(

        @Schema(description = "Идентификатор", example = "1") Long id,

        @Schema(description = "Животное, на которое подана заявка", example = "1") Long animalId,

        @Schema(description = "Заявитель", example = "1") Long applicantId,

        @Schema(description = "Мотивация заявителя", example = "Есть дом с участком", nullable = true)
        String motivation,

        @Schema(description = "Статус заявки", example = "PENDING") AdoptionStatus status,

        @Schema(description = "Дата и время подачи заявки, проставляются сервером",
                example = "2026-09-26T07:07:27.613832Z")
        Instant submittedAt) {
}
