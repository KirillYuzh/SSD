package ru.mtuci.rbpo.lab4.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "CSRF-токен для изменяющих запросов")
public record CsrfResponse(

        @Schema(description = "Заголовок для передачи токена", example = "X-XSRF-TOKEN") String headerName,

        @Schema(description = "Параметр формы для передачи токена", example = "_csrf") String parameterName,

        @Schema(description = "Значение токена") String token) {
}
