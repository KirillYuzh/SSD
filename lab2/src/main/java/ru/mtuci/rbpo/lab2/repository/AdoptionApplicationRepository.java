package ru.mtuci.rbpo.lab2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.rbpo.lab2.domain.AdoptionApplication;

import java.util.List;

public interface AdoptionApplicationRepository extends JpaRepository<AdoptionApplication, Long> {

    List<AdoptionApplication> findAllByOrderById();

    List<AdoptionApplication> findByAnimalIdOrderById(Long animalId);

    List<AdoptionApplication> findByApplicantIdOrderById(Long applicantId);
}
