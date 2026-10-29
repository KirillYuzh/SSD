package ru.mtuci.rbpo.lab3.service;

import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mtuci.rbpo.lab3.api.dto.AdoptionApplicationPatch;
import ru.mtuci.rbpo.lab3.api.dto.AdoptionApplicationRequest;
import ru.mtuci.rbpo.lab3.domain.AdoptionApplication;
import ru.mtuci.rbpo.lab3.domain.AdoptionStatus;
import ru.mtuci.rbpo.lab3.domain.Animal;
import ru.mtuci.rbpo.lab3.domain.AnimalHandoverRecord;
import ru.mtuci.rbpo.lab3.domain.AnimalStatus;
import ru.mtuci.rbpo.lab3.exception.ConflictException;
import ru.mtuci.rbpo.lab3.exception.NotFoundException;
import ru.mtuci.rbpo.lab3.repository.AdoptionApplicationRepository;
import ru.mtuci.rbpo.lab3.repository.AnimalHandoverRecordRepository;

@Service
@Transactional(readOnly = true)
public class AdoptionApplicationService {

    private final AdoptionApplicationRepository adoptionApplicationRepository;
    private final AnimalHandoverRecordRepository handoverRecordRepository;
    private final AnimalService animalService;
    private final UserService userService;

    public AdoptionApplicationService(
        AdoptionApplicationRepository adoptionApplicationRepository,
        AnimalHandoverRecordRepository handoverRecordRepository,
        AnimalService animalService,
        UserService userService
    ) {
        this.adoptionApplicationRepository = adoptionApplicationRepository;
        this.handoverRecordRepository = handoverRecordRepository;
        this.animalService = animalService;
        this.userService = userService;
    }

    public List<AdoptionApplication> findAll() {
        return adoptionApplicationRepository.findAllByOrderById();
    }

    public AdoptionApplication findById(long id) {
        return adoptionApplicationRepository
            .findById(id)
            .orElseThrow(() ->
                new NotFoundException("Заявка с id " + id + " не найдена")
            );
    }

    @Transactional
    public AdoptionApplication create(AdoptionApplicationRequest request) {
        Animal animal = animalService.findById(request.animalId());
        userService.findById(request.applicantId());
        if (animal.getStatus() == AnimalStatus.HANDED_OVER) {
            throw new ConflictException(
                "Животное с id " + animal.getId() + " уже передано семье"
            );
        }
        if (
            adoptionApplicationRepository.existsByAnimalIdAndApplicantId(
                request.animalId(),
                request.applicantId()
            )
        ) {
            throw new ConflictException(
                "Заявитель с id " +
                    request.applicantId() +
                    " уже подал заявку на животное с id " +
                    request.animalId()
            );
        }
        AdoptionApplication saved = adoptionApplicationRepository.save(
            new AdoptionApplication(
                animal.getId(),
                request.applicantId(),
                trimToNull(request.motivation()),
                AdoptionStatus.PENDING,
                Instant.now()
            )
        );
        refreshAnimalStatus(animal);
        return saved;
    }

    @Transactional
    public AdoptionApplication update(long id, AdoptionApplicationPatch patch) {
        AdoptionApplication application = findById(id);
        if (patch.motivation() == null) {
            return application;
        }
        if (application.getStatus() != AdoptionStatus.PENDING) {
            throw new ConflictException(
                "Заявку с id " +
                    id +
                    " можно изменить только в статусе PENDING, текущий статус: " +
                    application.getStatus()
            );
        }
        application.setMotivation(trimToNull(patch.motivation()));
        return application;
    }

    /**
     * Одобрение заявки
     * животное закрепляется за заявителем, конкурирующие заявки на это животное отклоняются
     */
    @Transactional
    public AdoptionApplication approve(long id) {
        AdoptionApplication application = findById(id);
        if (application.getStatus() != AdoptionStatus.PENDING) {
            throw new ConflictException(
                "Одобрить можно только заявку в статусе PENDING, текущий статус: " +
                    application.getStatus()
            );
        }
        Animal animal = animalService.findById(application.getAnimalId());
        if (animal.getStatus() == AnimalStatus.HANDED_OVER) {
            throw new ConflictException(
                "Животное с id " + animal.getId() + " уже передано семье"
            );
        }
        application.setStatus(AdoptionStatus.APPROVED);
        adoptionApplicationRepository
            .findByAnimalIdAndStatusOrderById(
                animal.getId(),
                AdoptionStatus.PENDING
            )
            .forEach(other -> other.setStatus(AdoptionStatus.REJECTED));
        refreshAnimalStatus(animal);
        return application;
    }

    @Transactional
    public AdoptionApplication reject(long id) {
        AdoptionApplication application = findById(id);
        if (application.getStatus() != AdoptionStatus.PENDING) {
            throw new ConflictException(
                "Отклонить можно только заявку в статусе PENDING, текущий статус: " +
                    application.getStatus()
            );
        }
        application.setStatus(AdoptionStatus.REJECTED);
        refreshAnimalStatus(animalService.findById(application.getAnimalId()));
        return application;
    }

    /**
     * Отзыв заявки заявителем
     * отозвать может только автор заявки и только пока по ней не оформлен акт передачи.
     */
    @Transactional
    public AdoptionApplication withdraw(long id, long applicantId) {
        AdoptionApplication application = findById(id);
        if (!application.getApplicantId().equals(applicantId)) {
            throw new ConflictException(
                "Заявка с id " + id + " подана другим пользователем"
            );
        }
        if (
            application.getStatus() != AdoptionStatus.PENDING &&
            application.getStatus() != AdoptionStatus.APPROVED
        ) {
            throw new ConflictException(
                "Отозвать можно только заявку в статусе PENDING или APPROVED, текущий статус: " +
                    application.getStatus()
            );
        }
        if (
            !handoverRecordRepository.findByApplicationIdOrderById(id).isEmpty()
        ) {
            throw new ConflictException(
                "По заявке с id " +
                    id +
                    " оформлен акт передачи, заявку нельзя отозвать"
            );
        }
        application.setStatus(AdoptionStatus.CANCELLED);
        refreshAnimalStatus(animalService.findById(application.getAnimalId()));
        return application;
    }

    @Transactional
    public void delete(long id) {
        AdoptionApplication application = findById(id);
        List<AnimalHandoverRecord> records =
            handoverRecordRepository.findByApplicationIdOrderById(id);
        if (!records.isEmpty()) {
            throw new ConflictException(
                "По заявке с id " +
                    id +
                    " оформлен акт передачи " +
                    records
                        .stream()
                        .map(record -> String.valueOf(record.getId()))
                        .toList() +
                    ", удаление оставило бы недействительные ссылки"
            );
        }
        adoptionApplicationRepository.deleteById(id);
        refreshAnimalStatus(animalService.findById(application.getAnimalId()));
    }

    /**
     * Активная заявка закрепляет животное за семьёй,
     * отсутствие активных заявок освобождает его.
     */
    private void refreshAnimalStatus(Animal animal) {
        if (animal.getStatus() == AnimalStatus.HANDED_OVER) {
            return;
        }
        boolean active = !adoptionApplicationRepository
            .findByAnimalIdAndStatusInOrderById(
                animal.getId(),
                List.of(AdoptionStatus.PENDING, AdoptionStatus.APPROVED)
            )
            .isEmpty();
        animal.setStatus(
            active ? AnimalStatus.RESERVED : AnimalStatus.AVAILABLE
        );
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
