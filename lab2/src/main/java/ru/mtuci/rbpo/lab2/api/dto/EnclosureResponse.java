package ru.mtuci.rbpo.lab2.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "Вольер приюта")
public record EnclosureResponse(

        @Schema(description = "Идентификатор", example = "1") Long id,

        @Schema(description = "Название вольера", example = "Вольер для собак") String name,

        @Schema(description = "Виды животных, которых допустимо размещать", example = "[\"Собака\"]")
        Set<String> allowedSpecies,

        @Schema(description = "Вместимость вольера", example = "4") int capacity) {
}
