package ru.mtuci.rbpo.lab2.domain;

/**
 * Статус заявки на усыновление.
 */
public enum AdoptionStatus {

    /** Заявка подана и ожидает рассмотрения. */
    PENDING,

    /** Заявка одобрена, можно готовить акт передачи. */
    APPROVED,

    /** Заявка отклонена. */
    REJECTED,

    /** Заявка отозвана заявителем. */
    CANCELLED
}
