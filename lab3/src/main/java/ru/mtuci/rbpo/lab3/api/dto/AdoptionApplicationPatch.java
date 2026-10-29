package ru.mtuci.rbpo.lab3.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Новая мотивация заявки")
public record AdoptionApplicationPatch(
    @Schema(
        description = "Мотивация заявителя",
        example = "Есть дом с участком и опыт содержания собак"
    )
    @Size(max = 500, message = "длина не должна превышать 500 символов")
    String motivation
) {}
