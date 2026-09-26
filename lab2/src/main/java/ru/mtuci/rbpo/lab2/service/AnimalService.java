package ru.mtuci.rbpo.lab2.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mtuci.rbpo.lab2.api.dto.AnimalPatch;
import ru.mtuci.rbpo.lab2.api.dto.AnimalRequest;
import ru.mtuci.rbpo.lab2.domain.AdoptionApplication;
import ru.mtuci.rbpo.lab2.domain.Animal;
import ru.mtuci.rbpo.lab2.domain.AnimalStatus;
import ru.mtuci.rbpo.lab2.exception.ConflictException;
import ru.mtuci.rbpo.lab2.exception.NotFoundException;
import ru.mtuci.rbpo.lab2.repository.AdoptionApplicationRepository;
import ru.mtuci.rbpo.lab2.repository.AnimalRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final AdoptionApplicationRepository adoptionApplicationRepository;

    public AnimalService(AnimalRepository animalRepository,
                         AdoptionApplicationRepository adoptionApplicationRepository) {
        this.animalRepository = animalRepository;
        this.adoptionApplicationRepository = adoptionApplicationRepository;
    }

    public List<Animal> findAll() {
        return animalRepository.findAllByOrderById();
    }

    public Animal findById(long id) {
        return animalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Животное с id " + id + " не найдено"));
    }

    @Transactional
    public Animal create(AnimalRequest request) {
        AnimalStatus status = request.status() != null ? request.status() : AnimalStatus.AVAILABLE;
        return animalRepository.save(
                new Animal(request.name().trim(), request.species().trim(), request.age(), status));
    }

    @Transactional
    public Animal update(long id, AnimalPatch patch) {
        Animal animal = findById(id);
        if (patch.status() != null && patch.status() != animal.getStatus()
                && !adoptionApplicationRepository.findByAnimalIdOrderById(id).isEmpty()) {
            throw new ConflictException("Статус размещения животного с id " + id
                    + " меняется автоматически по заявкам, измените статус заявки или акт передачи");
        }
        if (patch.name() != null) {
            animal.setName(patch.name().trim());
        }
        if (patch.species() != null) {
            animal.setSpecies(patch.species().trim());
        }
        if (patch.age() != null) {
            animal.setAge(patch.age());
        }
        if (patch.status() != null) {
            animal.setStatus(patch.status());
        }
        return animal;
    }

    /**
     * Меняет статус размещения по решению сервисного слоя — при обработке заявок и актов передачи.
     */
    @Transactional
    public Animal changeStatus(long id, AnimalStatus status) {
        Animal animal = findById(id);
        animal.setStatus(status);
        return animal;
    }

    @Transactional
    public void delete(long id) {
        findById(id);
        List<AdoptionApplication> applications = adoptionApplicationRepository.findByAnimalIdOrderById(id);
        if (!applications.isEmpty()) {
            throw new ConflictException("Животное с id " + id + " используется в заявках "
                    + applications.stream().map(application -> String.valueOf(application.getId())).toList()
                    + ", удаление оставило бы недействительные ссылки");
        }
        animalRepository.deleteById(id);
    }
}
