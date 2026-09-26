package ru.mtuci.rbpo.lab2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.rbpo.lab2.domain.Animal;

import java.util.List;

public interface AnimalRepository extends JpaRepository<Animal, Long> {

    List<Animal> findAllByOrderById();
}
