package ru.mtuci.rbpo.lab3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.rbpo.lab3.domain.AdoptionApplication;
import ru.mtuci.rbpo.lab3.domain.AdoptionStatus;

import java.util.Collection;
import java.util.List;

public interface AdoptionApplicationRepository extends JpaRepository<AdoptionApplication, Long> {

    List<AdoptionApplication> findAllByOrderById();

    List<AdoptionApplication> findByAnimalIdOrderById(Long animalId);

    List<AdoptionApplication> findByApplicantIdOrderById(Long applicantId);

    List<AdoptionApplication> findByAnimalIdAndStatusOrderById(Long animalId, AdoptionStatus status);

    List<AdoptionApplication> findByAnimalIdAndStatusInOrderById(Long animalId, Collection<AdoptionStatus> statuses);

    boolean existsByAnimalIdAndApplicantId(Long animalId, Long applicantId);
}
