package ru.mtuci.rbpo.lab2.exception;

/**
 * Операция нарушает состояние ресурсов: дубликат, недопустимый переход статуса
 * или удаление объекта, на который ссылаются другие. Преобразуется в ответ со статусом 409.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
