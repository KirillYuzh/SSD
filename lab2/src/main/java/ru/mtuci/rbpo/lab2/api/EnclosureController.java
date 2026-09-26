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
import ru.mtuci.rbpo.lab2.api.dto.EnclosurePatch;
import ru.mtuci.rbpo.lab2.api.dto.EnclosureRequest;
import ru.mtuci.rbpo.lab2.api.dto.EnclosureResponse;
import ru.mtuci.rbpo.lab2.domain.Enclosure;
import ru.mtuci.rbpo.lab2.service.EnclosureService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/enclosures")
@Tag(name = "Вольеры", description = "Вольеры приюта: допустимые виды животных и вместимость")
public class EnclosureController {

    private final EnclosureService enclosureService;

    public EnclosureController(EnclosureService enclosureService) {
        this.enclosureService = enclosureService;
    }

    @GetMapping
    @Operation(operationId = "listEnclosures", summary = "Список вольеров")
    public List<EnclosureResponse> findAll() {
        return enclosureService.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getEnclosure", summary = "Вольер по идентификатору")
    public EnclosureResponse findById(@PathVariable long id) {
        return toResponse(enclosureService.findById(id));
    }

    @PostMapping
    @Operation(operationId = "createEnclosure", summary = "Добавить вольер")
    @ApiResponse(responseCode = "201", description = "Ресурс создан",
        headers = @Header(name = "Location", description = "Адрес созданного ресурса"))
    public ResponseEntity<EnclosureResponse> create(@Valid @RequestBody EnclosureRequest request) {
        Enclosure created = enclosureService.create(request);
        return ResponseEntity
                .created(locationOf(created.getId()))
                .body(toResponse(created));
    }

    @PatchMapping("/{id}")
    @Operation(operationId = "updateEnclosure", summary = "Частично обновить вольер")
    public EnclosureResponse update(@PathVariable long id, @Valid @RequestBody EnclosurePatch patch) {
        return toResponse(enclosureService.update(id, patch));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(operationId = "deleteEnclosure", summary = "Удалить вольер")
    public void delete(@PathVariable long id) {
        enclosureService.delete(id);
    }

    private EnclosureResponse toResponse(Enclosure enclosure) {
        return new EnclosureResponse(enclosure.getId(), enclosure.getName(), enclosure.getAllowedSpecies(),
                enclosure.getCapacity());
    }

    private static URI locationOf(long id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
