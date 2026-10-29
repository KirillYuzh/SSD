package ru.mtuci.rbpo.lab3.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mtuci.rbpo.lab3.api.dto.EnclosurePatch;
import ru.mtuci.rbpo.lab3.api.dto.EnclosureRequest;
import ru.mtuci.rbpo.lab3.domain.Enclosure;
import ru.mtuci.rbpo.lab3.exception.ConflictException;
import ru.mtuci.rbpo.lab3.exception.NotFoundException;
import ru.mtuci.rbpo.lab3.repository.AnimalRepository;
import ru.mtuci.rbpo.lab3.repository.EnclosureRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EnclosureService {

    private final EnclosureRepository enclosureRepository;
    private final AnimalRepository animalRepository;

    public EnclosureService(EnclosureRepository enclosureRepository, AnimalRepository animalRepository) {
        this.enclosureRepository = enclosureRepository;
        this.animalRepository = animalRepository;
    }

    public List<Enclosure> findAll() {
        return enclosureRepository.findAllByOrderById();
    }

    public Enclosure findById(long id) {
        return enclosureRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вольер с id " + id + " не найден"));
    }

    @Transactional
    public Enclosure create(EnclosureRequest request) {
        return enclosureRepository.save(
                new Enclosure(request.name().trim(), request.allowedSpecies(), request.capacity()));
    }

    @Transactional
    public Enclosure update(long id, EnclosurePatch patch) {
        Enclosure enclosure = findById(id);
        if (patch.capacity() != null) {
            long occupied = animalRepository.countByEnclosureId(id);
            if (patch.capacity() < occupied) {
                throw new ConflictException("В вольере с id " + id + " уже размещено " + occupied
                        + " животных, вместимость не может быть меньше");
            }
            enclosure.setCapacity(patch.capacity());
        }
        if (patch.name() != null) {
            enclosure.setName(patch.name().trim());
        }
        if (patch.allowedSpecies() != null) {
            if (animalRepository.findByEnclosureId(id).stream()
                    .anyMatch(animal -> !patch.allowedSpecies().contains(animal.getSpecies()))) {
                throw new ConflictException("В вольере с id " + id
                        + " размещены животные видов, которые не входят в новый список");
            }
            enclosure.setAllowedSpecies(patch.allowedSpecies());
        }
        return enclosure;
    }

    @Transactional
    public void delete(long id) {
        findById(id);
        if (animalRepository.existsByEnclosureId(id)) {
            throw new ConflictException("В вольере с id " + id
                    + " размещены животные, удаление оставило бы недействительные ссылки");
        }
        enclosureRepository.deleteById(id);
    }
}
