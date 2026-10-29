package ru.mtuci.rbpo.lab3.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mtuci.rbpo.lab3.api.dto.AnimalHandoverRecordPatch;
import ru.mtuci.rbpo.lab3.api.dto.AnimalHandoverRecordRequest;
import ru.mtuci.rbpo.lab3.domain.AdoptionApplication;
import ru.mtuci.rbpo.lab3.domain.AdoptionStatus;
import ru.mtuci.rbpo.lab3.domain.AnimalHandoverRecord;
import ru.mtuci.rbpo.lab3.domain.HandoverStatus;
import ru.mtuci.rbpo.lab3.exception.ConflictException;
import ru.mtuci.rbpo.lab3.exception.NotFoundException;
import ru.mtuci.rbpo.lab3.repository.AnimalHandoverRecordRepository;

@Service
@Transactional(readOnly = true)
public class AnimalHandoverRecordService {

    private final AnimalHandoverRecordRepository handoverRecordRepository;
    private final AdoptionApplicationService adoptionApplicationService;
    private final AnimalService animalService;

    public AnimalHandoverRecordService(
        AnimalHandoverRecordRepository handoverRecordRepository,
        AdoptionApplicationService adoptionApplicationService,
        AnimalService animalService
    ) {
        this.handoverRecordRepository = handoverRecordRepository;
        this.adoptionApplicationService = adoptionApplicationService;
        this.animalService = animalService;
    }

    public List<AnimalHandoverRecord> findAll() {
        return handoverRecordRepository.findAllByOrderById();
    }

    public AnimalHandoverRecord findById(long id) {
        return handoverRecordRepository
            .findById(id)
            .orElseThrow(() ->
                new NotFoundException("Акт передачи с id " + id + " не найден")
            );
    }

    @Transactional
    public AnimalHandoverRecord create(AnimalHandoverRecordRequest request) {
        AdoptionApplication application = adoptionApplicationService.findById(
            request.applicationId()
        );
        if (application.getStatus() != AdoptionStatus.APPROVED) {
            throw new ConflictException(
                "Акт передачи можно подготовить только по одобренной заявке, текущий статус: " +
                    application.getStatus()
            );
        }
        if (
            !handoverRecordRepository
                .findByApplicationIdOrderById(application.getId())
                .isEmpty()
        ) {
            throw new ConflictException(
                "По заявке с id " +
                    application.getId() +
                    " акт передачи уже оформлен"
            );
        }
        return handoverRecordRepository.save(
            new AnimalHandoverRecord(
                application.getId(),
                LocalDate.now(),
                null,
                HandoverStatus.PREPARED
            )
        );
    }

    @Transactional
    public AnimalHandoverRecord update(
        long id,
        AnimalHandoverRecordPatch patch
    ) {
        AnimalHandoverRecord record = findById(id);
        HandoverStatus status = patch.status();
        if (status == null || status == record.getStatus()) {
            return record;
        }
        if (status != HandoverStatus.CONFIRMED) {
            throw new ConflictException(
                "Через обновление акт можно только подтвердить, текущий статус: " +
                    record.getStatus()
            );
        }
        if (record.getStatus() != HandoverStatus.PREPARED) {
            throw new ConflictException(
                "Подтвердить можно только подготовленный акт, текущий статус: " +
                    record.getStatus()
            );
        }
        AdoptionApplication application = adoptionApplicationService.findById(
            record.getApplicationId()
        );
        record.setStatus(HandoverStatus.CONFIRMED);
        record.setConfirmationDate(LocalDate.now());
        animalService.handOver(application.getAnimalId());
        return record;
    }

    /**
     * Возврат животного в приют
     * акт переходит в RETURNED, заявка отменяется и животное снова доступно для усыновления
     */
    @Transactional
    public AnimalHandoverRecord returnToShelter(long id) {
        AnimalHandoverRecord record = findById(id);
        if (record.getStatus() != HandoverStatus.CONFIRMED) {
            throw new ConflictException(
                "Вернуть животное можно только по подтверждённому акту, текущий статус: " +
                    record.getStatus()
            );
        }
        AdoptionApplication application = adoptionApplicationService.findById(
            record.getApplicationId()
        );
        record.setStatus(HandoverStatus.RETURNED);
        application.setStatus(AdoptionStatus.CANCELLED);
        animalService.returnToShelter(application.getAnimalId());
        return record;
    }

    @Transactional
    public void delete(long id) {
        AnimalHandoverRecord record = findById(id);
        if (record.getStatus() != HandoverStatus.PREPARED) {
            throw new ConflictException(
                "Удалить можно только подготовленный акт, текущий статус: " +
                    record.getStatus()
            );
        }
        handoverRecordRepository.deleteById(id);
    }
}
