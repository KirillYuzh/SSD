package ru.mtuci.rbpo.lab2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.rbpo.lab2.domain.AnimalHandoverRecord;
import ru.mtuci.rbpo.lab2.domain.HandoverStatus;

import java.util.List;
import java.util.Optional;

public interface AnimalHandoverRecordRepository extends JpaRepository<AnimalHandoverRecord, Long> {

    List<AnimalHandoverRecord> findAllByOrderById();

    List<AnimalHandoverRecord> findByApplicationIdOrderById(Long applicationId);

    Optional<AnimalHandoverRecord> findByApplicationIdAndStatus(Long applicationId, HandoverStatus status);
}
