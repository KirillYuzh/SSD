package ru.mtuci.rbpo.lab2.domain;

/**
 * Статус акта передачи животного.
 */
public enum HandoverStatus {

    /** Акт подготовлен приютом, дата подтверждения ещё не наступила. */
    PREPARED,

    /** Акт подтверждён семьёй, животное передано. */
    CONFIRMED,

    /** Подготовка акта отменена. */
    CANCELLED
}
