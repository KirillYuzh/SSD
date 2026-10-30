package ru.mtuci.rbpo.lab4.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import ru.mtuci.rbpo.lab4.api.dto.EnclosurePatch;
import ru.mtuci.rbpo.lab4.api.dto.EnclosureRequest;
import ru.mtuci.rbpo.lab4.api.dto.EnclosureResponse;
import ru.mtuci.rbpo.lab4.domain.Enclosure;
import ru.mtuci.rbpo.lab4.service.EnclosureService;

@RestController
@RequestMapping("/api/enclosures")
@Tag(
    name = "Вольеры",
    description = "Вольеры приюта: допустимые виды животных и вместимость"
)
public class EnclosureController {

    private final EnclosureService enclosureService;

    public EnclosureController(EnclosureService enclosureService) {
        this.enclosureService = enclosureService;
    }

    @GetMapping
    @Operation(operationId = "listEnclosures", summary = "Список вольеров")
    @PreAuthorize("isAuthenticated()")
    public List<EnclosureResponse> findAll() {
        return enclosureService
            .findAll()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @GetMapping("/{id}")
    @Operation(
        operationId = "getEnclosure",
        summary = "Вольер по идентификатору"
    )
    @PreAuthorize("isAuthenticated()")
    public EnclosureResponse findById(@PathVariable long id) {
        return toResponse(enclosureService.findById(id));
    }

    @PostMapping
    @Operation(operationId = "createEnclosure", summary = "Добавить вольер")
    @PreAuthorize("hasRole('STAFF')")
    @ApiResponse(
        responseCode = "201",
        description = "Ресурс создан",
        headers = @Header(
            name = "Location",
            description = "Адрес созданного ресурса"
        )
    )
    public ResponseEntity<EnclosureResponse> create(
        @Valid @RequestBody EnclosureRequest request
    ) {
        Enclosure created = enclosureService.create(request);
        return ResponseEntity.created(locationOf(created.getId())).body(
            toResponse(created)
        );
    }

    @PatchMapping("/{id}")
    @Operation(
        operationId = "updateEnclosure",
        summary = "Частично обновить вольер"
    )
    @PreAuthorize("hasRole('STAFF')")
    public EnclosureResponse update(
        @PathVariable long id,
        @Valid @RequestBody EnclosurePatch patch
    ) {
        return toResponse(enclosureService.update(id, patch));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(operationId = "deleteEnclosure", summary = "Удалить вольер")
    @PreAuthorize("hasRole('STAFF')")
    public void delete(@PathVariable long id) {
        enclosureService.delete(id);
    }

    private EnclosureResponse toResponse(Enclosure enclosure) {
        return new EnclosureResponse(
            enclosure.getId(),
            enclosure.getName(),
            enclosure.getAllowedSpecies(),
            enclosure.getCapacity()
        );
    }

    private static URI locationOf(long id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(id)
            .toUri();
    }
}
