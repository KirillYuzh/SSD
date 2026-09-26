package ru.mtuci.rbpo.lab2.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.mtuci.rbpo.lab2.domain.HandoverStatus;

/**
 * Частичное обновление акта передачи. Меняется только статус: даты подготовки
 * и подтверждения проставляются сервером и в запросе не передаются.
 */
@Schema(description = "Новый статус акта передачи")
public record AnimalHandoverRecordPatch(

        @Schema(description = "Статус акта передачи", example = "CONFIRMED")
        HandoverStatus status) {
}
