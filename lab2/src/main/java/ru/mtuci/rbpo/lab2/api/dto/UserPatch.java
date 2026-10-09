package ru.mtuci.rbpo.lab2.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;


@Schema(description = "Поля пользователя для изменения, отсутствующие поля не меняются")
public record UserPatch(

        @Schema(description = "Логин", example = "ivan")
        @Pattern(regexp = "^[a-zA-Z0-9_.-]{3,32}$",
                message = "допустимы латинские буквы, цифры и символы _ . - , длина от 3 до 32") String username,

        @Schema(description = "Имя и фамилия", example = "Иван Петров")
        @Pattern(regexp = "\\S+", message = "значение не должно быть пустым") String fullName,

        @Schema(description = "Адрес электронной почты", example = "ivan.petrov@example.com")
        @Email(message = "ожидается адрес электронной почты") String email) {
}
