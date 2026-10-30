package ru.mtuci.rbpo.lab4.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.mtuci.rbpo.lab4.domain.UserRole;
import ru.mtuci.rbpo.lab4.domain.UserStatus;

@Schema(description = "Пользователь системы")
public record UserResponse(
    @Schema(description = "Идентификатор", example = "1") Long id,

    @Schema(description = "Логин", example = "ivan") String username,

    @Schema(description = "Имя и фамилия", example = "Иван Петров")
    String fullName,

    @Schema(
        description = "Адрес электронной почты",
        example = "ivan@example.com"
    )
    String email,

    @Schema(description = "Роль пользователя", example = "ADOPTER")
    UserRole role,

    @Schema(description = "Состояние учётной записи", example = "ACTIVE")
    UserStatus status
) {}
