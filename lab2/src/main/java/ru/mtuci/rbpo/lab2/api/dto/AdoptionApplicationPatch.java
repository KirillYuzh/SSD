package ru.mtuci.rbpo.lab2.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.mtuci.rbpo.lab2.domain.AdoptionStatus;

/**
 * Частичное обновление заявки. 
 * Животное и заявитель после создания не меняются:
 * поданная заявка относится к конкретной паре "животное - заявитель"
 */
@Schema(description = "Новый статус заявки")
public record AdoptionApplicationPatch(

        @Schema(description = "Статус заявки", example = "APPROVED")
        AdoptionStatus status) {
}
