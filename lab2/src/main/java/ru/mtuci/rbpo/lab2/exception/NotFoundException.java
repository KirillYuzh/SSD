package ru.mtuci.rbpo.lab2.exception;

/**
 * Запрошенный ресурс не найден. Преобразуется в ответ со статусом 404.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
