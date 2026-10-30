package ru.mtuci.rbpo.lab4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.rbpo.lab4.domain.Animal;

import java.util.List;

public interface AnimalRepository extends JpaRepository<Animal, Long> {

    List<Animal> findAllByOrderById();

    List<Animal> findByEnclosureId(Long enclosureId);

    long countByEnclosureId(Long enclosureId);

    boolean existsByEnclosureId(Long enclosureId);
}
