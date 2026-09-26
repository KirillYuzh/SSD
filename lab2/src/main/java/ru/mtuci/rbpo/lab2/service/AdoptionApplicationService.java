package ru.mtuci.rbpo.lab2.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mtuci.rbpo.lab2.api.dto.AdoptionApplicationPatch;
import ru.mtuci.rbpo.lab2.api.dto.AdoptionApplicationRequest;
import ru.mtuci.rbpo.lab2.domain.AdoptionApplication;
import ru.mtuci.rbpo.lab2.domain.AdoptionStatus;
import ru.mtuci.rbpo.lab2.domain.Animal;
import ru.mtuci.rbpo.lab2.domain.AnimalHandoverRecord;
import ru.mtuci.rbpo.lab2.domain.AnimalStatus;
import ru.mtuci.rbpo.lab2.domain.HandoverStatus;
import ru.mtuci.rbpo.lab2.exception.ConflictException;
import ru.mtuci.rbpo.lab2.exception.NotFoundException;
import ru.mtuci.rbpo.lab2.repository.AdoptionApplicationRepository;
import ru.mtuci.rbpo.lab2.repository.AnimalHandoverRecordRepository;

import java.time.Instant;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdoptionApplicationService {

    private final AdoptionApplicationRepository adoptionApplicationRepository;
    private final AnimalHandoverRecordRepository handoverRecordRepository;
    private final AnimalService animalService;
    private final UserService userService;

    public AdoptionApplicationService(AdoptionApplicationRepository adoptionApplicationRepository,
                                     AnimalHandoverRecordRepository handoverRecordRepository,
                                     AnimalService animalService,
                                     UserService userService) {
        this.adoptionApplicationRepository = adoptionApplicationRepository;
        this.handoverRecordRepository = handoverRecordRepository;
        this.animalService = animalService;
        this.userService = userService;
    }

    public List<AdoptionApplication> findAll() {
        return adoptionApplicationRepository.findAllByOrderById();
    }

    public AdoptionApplication findById(long id) {
        return adoptionApplicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Заявка с id " + id + " не найдена"));
    }

    @Transactional
    public AdoptionApplication create(AdoptionApplicationRequest request) {
        Animal animal = animalService.findById(request.animalId());
        userService.findById(request.applicantId());
        if (animal.getStatus() != AnimalStatus.AVAILABLE) {
            throw new ConflictException("Животное с id " + animal.getId()
                    + " недоступно для усыновления, текущий статус: " + animal.getStatus());
        }

        AdoptionStatus status = request.status() != null ? request.status() : AdoptionStatus.PENDING;
        AdoptionApplication saved = adoptionApplicationRepository.save(new AdoptionApplication(animal.getId(),
                request.applicantId(), status, Instant.now()));
        applyAnimalStatus(animal, status);
        return saved;
    }

    @Transactional
    public AdoptionApplication update(long id, AdoptionApplicationPatch patch) {
        AdoptionApplication application = findById(id);
        AdoptionStatus status = patch.status();
        if (status == null || status == application.getStatus()) {
            return application;
        }
        if (handoverRecordRepository.findByApplicationIdAndStatus(id, HandoverStatus.CONFIRMED).isPresent()) {
            throw new ConflictException("Заявка с id " + id + " подтверждена актом передачи, её статус изменить нельзя");
        }
        application.setStatus(status);
        applyAnimalStatus(animalService.findById(application.getAnimalId()), status);
        return application;
    }

    @Transactional
    public void delete(long id) {
        AdoptionApplication application = findById(id);
        List<AnimalHandoverRecord> records = handoverRecordRepository.findByApplicationIdOrderById(id);
        if (!records.isEmpty()) {
            throw new ConflictException("По заявке с id " + id + " оформлен акт передачи "
                    + records.stream().map(record -> String.valueOf(record.getId())).toList()
                    + ", удаление оставило бы недействительные ссылки");
        }
        adoptionApplicationRepository.deleteById(id);
        applyAnimalStatus(animalService.findById(application.getAnimalId()), AdoptionStatus.CANCELLED);
    }

    /**
     * Активная заявка закрепляет животное за семьёй, закрытая освобождает его.
     */
    private void applyAnimalStatus(Animal animal, AdoptionStatus applicationStatus) {
        AnimalStatus target = switch (applicationStatus) {
            case PENDING, APPROVED -> AnimalStatus.RESERVED;
            case REJECTED, CANCELLED -> AnimalStatus.AVAILABLE;
        };
        animal.setStatus(target);
    }
}
