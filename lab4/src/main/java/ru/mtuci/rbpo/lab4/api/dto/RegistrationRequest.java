package ru.mtuci.rbpo.lab4.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Новая учётная запись заявителя")
public record RegistrationRequest(

        @Schema(description = "Логин", example = "ivan")
        @NotBlank(message = "значение не должно быть пустым")
        @Pattern(regexp = "^[a-zA-Z0-9_.-]{3,32}$",
                message = "допустимы латинские буквы, цифры и символы _ . - , длина от 3 до 32") String username,

        @Schema(description = "Имя и фамилия", example = "Иван Петров")
        @NotBlank(message = "значение не должно быть пустым") String fullName,

        @Schema(description = "Адрес электронной почты", example = "ivan@example.com")
        @NotBlank(message = "значение не должно быть пустым")
        @Email(message = "ожидается адрес электронной почты") String email,

        @Schema(description = "Пароль", example = "Ivan12345!")
        @NotBlank(message = "значение не должно быть пустым")
        @Size(min = 10, max = 72, message = "длина пароля от 10 до 72 символов")
        @Pattern(regexp = "^(?=.*\\p{L})(?=.*\\d).+$", message = "пароль должен содержать буквы и цифры")
        String password) {
}
