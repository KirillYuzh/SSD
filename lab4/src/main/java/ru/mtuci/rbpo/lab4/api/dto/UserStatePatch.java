package ru.mtuci.rbpo.lab4.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import ru.mtuci.rbpo.lab4.domain.UserStatus;

@Schema(description = "Новое состояние учётной записи")
public record UserStatePatch(

        @Schema(description = "Состояние учётной записи", example = "BLOCKED")
        @NotNull(message = "значение обязательно") UserStatus status) {
}
