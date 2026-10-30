package ru.mtuci.rbpo.lab3.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mtuci.rbpo.lab3.domain.Enclosure;

import java.util.List;
import java.util.Optional;

public interface EnclosureRepository extends JpaRepository<Enclosure, Long> {

    List<Enclosure> findAllByOrderById();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select enclosure from Enclosure enclosure where enclosure.id = :id")
    Optional<Enclosure> findByIdForUpdate(@Param("id") Long id);
}
