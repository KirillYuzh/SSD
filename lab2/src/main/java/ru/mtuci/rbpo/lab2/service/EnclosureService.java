package ru.mtuci.rbpo.lab2.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mtuci.rbpo.lab2.api.dto.EnclosurePatch;
import ru.mtuci.rbpo.lab2.api.dto.EnclosureRequest;
import ru.mtuci.rbpo.lab2.domain.Enclosure;
import ru.mtuci.rbpo.lab2.exception.NotFoundException;
import ru.mtuci.rbpo.lab2.repository.EnclosureRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EnclosureService {

    private final EnclosureRepository enclosureRepository;

    public EnclosureService(EnclosureRepository enclosureRepository) {
        this.enclosureRepository = enclosureRepository;
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
        if (patch.name() != null) {
            enclosure.setName(patch.name().trim());
        }
        if (patch.allowedSpecies() != null) {
            enclosure.setAllowedSpecies(patch.allowedSpecies());
        }
        if (patch.capacity() != null) {
            enclosure.setCapacity(patch.capacity());
        }
        return enclosure;
    }

    @Transactional
    public void delete(long id) {
        findById(id);
        enclosureRepository.deleteById(id);
    }
}
