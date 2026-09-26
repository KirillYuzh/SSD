package ru.mtuci.rbpo.lab2.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mtuci.rbpo.lab2.api.dto.AnimalHandoverRecordPatch;
import ru.mtuci.rbpo.lab2.api.dto.AnimalHandoverRecordRequest;
import ru.mtuci.rbpo.lab2.domain.AdoptionApplication;
import ru.mtuci.rbpo.lab2.domain.AdoptionStatus;
import ru.mtuci.rbpo.lab2.domain.AnimalHandoverRecord;
import ru.mtuci.rbpo.lab2.domain.AnimalStatus;
import ru.mtuci.rbpo.lab2.domain.HandoverStatus;
import ru.mtuci.rbpo.lab2.exception.ConflictException;
import ru.mtuci.rbpo.lab2.exception.NotFoundException;
import ru.mtuci.rbpo.lab2.repository.AnimalHandoverRecordRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AnimalHandoverRecordService {

    private final AnimalHandoverRecordRepository handoverRecordRepository;
    private final AdoptionApplicationService adoptionApplicationService;
    private final AnimalService animalService;

    public AnimalHandoverRecordService(AnimalHandoverRecordRepository handoverRecordRepository,
                                       AdoptionApplicationService adoptionApplicationService,
                                       AnimalService animalService) {
        this.handoverRecordRepository = handoverRecordRepository;
        this.adoptionApplicationService = adoptionApplicationService;
        this.animalService = animalService;
    }

    public List<AnimalHandoverRecord> findAll() {
        return handoverRecordRepository.findAllByOrderById();
    }

    public AnimalHandoverRecord findById(long id) {
        return handoverRecordRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Акт передачи с id " + id + " не найден"));
    }

    /**
     * Акт создаётся в статусе PREPARED с датой подготовки, которую проставляет сервер.
     * Подтверждение оформляется отдельным обновлением, поэтому дата подтверждения
     * совпадает с датой подтверждения, а не с датой подготовки.
     */
    @Transactional
    public AnimalHandoverRecord create(AnimalHandoverRecordRequest request) {
        AdoptionApplication application = adoptionApplicationService.findById(request.applicationId());
        if (application.getStatus() != AdoptionStatus.APPROVED) {
            throw new ConflictException("Акт передачи можно подготовить только по одобренной заявке, текущий статус: "
                    + application.getStatus());
        }
        if (!handoverRecordRepository.findByApplicationIdOrderById(application.getId()).isEmpty()) {
            throw new ConflictException("По заявке с id " + application.getId() + " акт передачи уже оформлен");
        }
        return handoverRecordRepository.save(new AnimalHandoverRecord(application.getId(), LocalDate.now(),
                null, HandoverStatus.PREPARED));
    }

    @Transactional
    public AnimalHandoverRecord update(long id, AnimalHandoverRecordPatch patch) {
        AnimalHandoverRecord record = findById(id);
        HandoverStatus status = patch.status();
        if (status == null || status == record.getStatus()) {
            return record;
        }
        if (record.getStatus() == HandoverStatus.CONFIRMED) {
            throw new ConflictException("Акт с id " + id + " уже подтверждён, его статус изменить нельзя");
        }

        record.setStatus(status);
        if (status == HandoverStatus.CONFIRMED) {
            record.setConfirmationDate(LocalDate.now());
            AdoptionApplication application = adoptionApplicationService.findById(record.getApplicationId());
            animalService.changeStatus(application.getAnimalId(), AnimalStatus.HANDED_OVER);
        }
        return record;
    }

    @Transactional
    public void delete(long id) {
        AnimalHandoverRecord record = findById(id);
        if (record.getStatus() == HandoverStatus.CONFIRMED) {
            throw new ConflictException("Подтверждённый акт с id " + id + " удалить нельзя");
        }
        handoverRecordRepository.deleteById(id);
    }
}
