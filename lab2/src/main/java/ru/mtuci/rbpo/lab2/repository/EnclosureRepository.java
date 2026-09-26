package ru.mtuci.rbpo.lab2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.rbpo.lab2.domain.Enclosure;

import java.util.List;

public interface EnclosureRepository extends JpaRepository<Enclosure, Long> {

    List<Enclosure> findAllByOrderById();
}
