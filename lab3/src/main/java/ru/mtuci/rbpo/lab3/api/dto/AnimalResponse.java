package ru.mtuci.rbpo.lab3.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.mtuci.rbpo.lab3.domain.AnimalStatus;

@Schema(description = "Животное приюта")
public record AnimalResponse(

        @Schema(description = "Идентификатор", example = "1") Long id,

        @Schema(description = "Кличка", example = "Бобик") String name,

        @Schema(description = "Вид животного", example = "Собака") String species,

        @Schema(description = "Возраст в полных годах", example = "3") int age,

        @Schema(description = "Статус размещения", example = "AVAILABLE") AnimalStatus status,

        @Schema(description = "Вольер, в котором находится животное", example = "1", nullable = true)
        Long enclosureId) {
}
