package ru.mtuci.rbpo.lab2.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Новый пользователь системы")
public record UserRequest(

        @Schema(description = "Логин", example = "ivan")
        @NotBlank(message = "значение не должно быть пустым")
        @Pattern(regexp = "^[a-zA-Z0-9_.-]{3,32}$",
                message = "допустимы латинские буквы, цифры и символы _ . - , длина от 3 до 32") String username,

        @Schema(description = "Имя и фамилия", example = "Иван Петров")
        @NotBlank(message = "значение не должно быть пустым") String fullName,

        @Schema(description = "Адрес электронной почты", example = "ivan@example.com")
        @NotBlank(message = "значение не должно быть пустым")
        @Email(message = "ожидается адрес электронной почты") String email) {
}
