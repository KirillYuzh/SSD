package ru.mtuci.rbpo.lab2.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Тело ответа с ошибкой")
public record ApiError(

        @Schema(description = "Момент формирования ответа", example = "2026-09-26T07:07:27.667885Z")
        Instant timestamp,

        @Schema(description = "Код ответа", example = "409") int status,

        @Schema(description = "Причина, соответствующая коду", example = "Conflict") String error,

        @Schema(description = "Описание причины ошибки") String message,

        @Schema(description = "Путь запроса", example = "/api/animals/1") String path) {
}
