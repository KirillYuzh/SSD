package ru.mtuci.rbpo.lab4.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mtuci.rbpo.lab4.api.dto.AnimalPatch;
import ru.mtuci.rbpo.lab4.api.dto.AnimalRequest;
import ru.mtuci.rbpo.lab4.domain.AdoptionApplication;
import ru.mtuci.rbpo.lab4.domain.Animal;
import ru.mtuci.rbpo.lab4.domain.AnimalStatus;
import ru.mtuci.rbpo.lab4.domain.Enclosure;
import ru.mtuci.rbpo.lab4.exception.ConflictException;
import ru.mtuci.rbpo.lab4.exception.NotFoundException;
import ru.mtuci.rbpo.lab4.repository.AdoptionApplicationRepository;
import ru.mtuci.rbpo.lab4.repository.AnimalRepository;
import ru.mtuci.rbpo.lab4.repository.EnclosureRepository;

@Service
@Transactional(readOnly = true)
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final AdoptionApplicationRepository adoptionApplicationRepository;
    private final EnclosureRepository enclosureRepository;

    public AnimalService(
        AnimalRepository animalRepository,
        AdoptionApplicationRepository adoptionApplicationRepository,
        EnclosureRepository enclosureRepository
    ) {
        this.animalRepository = animalRepository;
        this.adoptionApplicationRepository = adoptionApplicationRepository;
        this.enclosureRepository = enclosureRepository;
    }

    public List<Animal> findAll() {
        return animalRepository.findAllByOrderById();
    }

    public Animal findById(long id) {
        return animalRepository
            .findById(id)
            .orElseThrow(() ->
                new NotFoundException("Животное с id " + id + " не найдено")
            );
    }

    @Transactional
    public Animal create(AnimalRequest request) {
        return animalRepository.save(
            new Animal(
                request.name().trim(),
                request.species().trim(),
                request.age(),
                AnimalStatus.AVAILABLE
            )
        );
    }

    @Transactional
    public Animal update(long id, AnimalPatch patch) {
        Animal animal = findById(id);
        if (patch.species() != null) {
            String species = patch.species().trim();
            requireSpeciesAllowedInEnclosure(animal, species);
            animal.setSpecies(species);
        }
        if (patch.name() != null) {
            animal.setName(patch.name().trim());
        }
        if (patch.age() != null) {
            animal.setAge(patch.age());
        }
        return animal;
    }

    /**
     * Размещает животное в вольере
     * вид должен быть допустим и свободные места в наличии
     */
    @Transactional
    public Animal placeInEnclosure(long id, long enclosureId) {
        Animal animal = findById(id);
        if (animal.getStatus() == AnimalStatus.HANDED_OVER) {
            throw new ConflictException(
                "Животное с id " +
                    id +
                    " передано семье, его нельзя разместить в вольере"
            );
        }
        Enclosure enclosure = enclosureRepository
            .findByIdForUpdate(enclosureId)
            .orElseThrow(() ->
                new NotFoundException(
                    "Вольер с id " + enclosureId + " не найден"
                )
            );
        if (!enclosure.getAllowedSpecies().contains(animal.getSpecies())) {
            throw new ConflictException(
                "Вольер с id " +
                    enclosureId +
                    " не предназначен для вида " +
                    animal.getSpecies()
            );
        }
        if (
            animal.getEnclosureId() != null &&
            animal.getEnclosureId() == enclosureId
        ) {
            return animal;
        }
        long occupied = animalRepository.countByEnclosureId(enclosureId);
        if (occupied >= enclosure.getCapacity()) {
            throw new ConflictException(
                "Вольер с id " +
                    enclosureId +
                    " заполнен: занято " +
                    occupied +
                    " мест из " +
                    enclosure.getCapacity()
            );
        }
        animal.setEnclosureId(enclosureId);
        return animal;
    }

    /**
     * Освобождает животное при передаче семье
     */
    @Transactional
    public Animal handOver(long id) {
        Animal animal = findById(id);
        animal.setStatus(AnimalStatus.HANDED_OVER);
        animal.setEnclosureId(null);
        return animal;
    }

    @Transactional
    public Animal returnToShelter(long id) {
        Animal animal = findById(id);
        if (animal.getStatus() != AnimalStatus.HANDED_OVER) {
            throw new ConflictException(
                "Животное с id " + id + " не передано семье"
            );
        }
        animal.setStatus(AnimalStatus.AVAILABLE);
        animal.setEnclosureId(null);
        return animal;
    }

    /**
     * Меняет статус размещения
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
        List<AdoptionApplication> applications =
            adoptionApplicationRepository.findByAnimalIdOrderById(id);
        if (!applications.isEmpty()) {
            throw new ConflictException(
                "Животное с id " +
                    id +
                    " используется в заявках " +
                    applications
                        .stream()
                        .map(application -> String.valueOf(application.getId()))
                        .toList() +
                    ", удаление оставило бы недействительные ссылки"
            );
        }
        animalRepository.deleteById(id);
    }

    private void requireSpeciesAllowedInEnclosure(
        Animal animal,
        String species
    ) {
        if (
            animal.getEnclosureId() == null ||
            animal.getSpecies().equals(species)
        ) {
            return;
        }
        Enclosure enclosure = enclosureRepository
            .findById(animal.getEnclosureId())
            .orElseThrow(() ->
                new NotFoundException(
                    "Вольер с id " + animal.getEnclosureId() + " не найден"
                )
            );
        if (!enclosure.getAllowedSpecies().contains(species)) {
            throw new ConflictException(
                "Вольер с id " +
                    enclosure.getId() +
                    " не предназначен для вида " +
                    species
            );
        }
    }
}
