package ru.mtuci.rbpo.lab4.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.mtuci.rbpo.lab4.domain.HandoverStatus;

@Schema(description = "Новый статус акта передачи")
public record AnimalHandoverRecordPatch(
    @Schema(description = "Статус акта передачи", example = "CONFIRMED")
    HandoverStatus status
) {}
