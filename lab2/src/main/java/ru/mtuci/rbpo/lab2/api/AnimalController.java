package ru.mtuci.rbpo.lab2.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.mtuci.rbpo.lab2.api.dto.AnimalPatch;
import ru.mtuci.rbpo.lab2.api.dto.AnimalRequest;
import ru.mtuci.rbpo.lab2.api.dto.AnimalResponse;
import ru.mtuci.rbpo.lab2.domain.Animal;
import ru.mtuci.rbpo.lab2.service.AnimalService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/animals")
@Tag(name = "Животные", description = "Животные приюта: кличка, вид, возраст, статус размещения")
public class AnimalController {

    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @GetMapping
    @Operation(operationId = "listAnimals", summary = "Список животных")
    public List<AnimalResponse> findAll() {
        return animalService.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getAnimal", summary = "Животное по идентификатору")
    public AnimalResponse findById(@PathVariable long id) {
        return toResponse(animalService.findById(id));
    }

    @PostMapping
    @Operation(operationId = "createAnimal", summary = "Добавить животное")
    @ApiResponse(responseCode = "201", description = "Ресурс создан",
        headers = @Header(name = "Location", description = "Адрес созданного ресурса"))
    public ResponseEntity<AnimalResponse> create(@Valid @RequestBody AnimalRequest request) {
        Animal created = animalService.create(request);
        return ResponseEntity
                .created(locationOf(created.getId()))
                .body(toResponse(created));
    }

    @PatchMapping("/{id}")
    @Operation(operationId = "updateAnimal", summary = "Частично обновить животное")
    public AnimalResponse update(@PathVariable long id, @Valid @RequestBody AnimalPatch patch) {
        return toResponse(animalService.update(id, patch));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(operationId = "deleteAnimal", summary = "Удалить животное")
    public void delete(@PathVariable long id) {
        animalService.delete(id);
    }

    private AnimalResponse toResponse(Animal animal) {
        return new AnimalResponse(animal.getId(), animal.getName(), animal.getSpecies(), animal.getAge(),
                animal.getStatus());
    }

    private static URI locationOf(long id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
