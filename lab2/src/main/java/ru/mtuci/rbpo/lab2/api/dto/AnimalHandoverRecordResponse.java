package ru.mtuci.rbpo.lab2.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.mtuci.rbpo.lab2.domain.HandoverStatus;

import java.time.LocalDate;

@Schema(description = "Акт передачи животного семье")
public record AnimalHandoverRecordResponse(

        @Schema(description = "Идентификатор", example = "1") Long id,

        @Schema(description = "Заявка, по которой оформлена передача", example = "1") Long applicationId,

        @Schema(description = "Дата подготовки акта, проставляется сервером", example = "2026-09-26")
        LocalDate preparationDate,

        @Schema(description = "Дата подтверждения акта, проставляется сервером при подтверждении",
                example = "2026-09-26", nullable = true)
        LocalDate confirmationDate,

        @Schema(description = "Статус акта передачи", example = "PREPARED") HandoverStatus status) {
}
