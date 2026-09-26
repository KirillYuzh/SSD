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
import ru.mtuci.rbpo.lab2.api.dto.AdoptionApplicationPatch;
import ru.mtuci.rbpo.lab2.api.dto.AdoptionApplicationRequest;
import ru.mtuci.rbpo.lab2.api.dto.AdoptionApplicationResponse;
import ru.mtuci.rbpo.lab2.domain.AdoptionApplication;
import ru.mtuci.rbpo.lab2.service.AdoptionApplicationService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/adoption-applications")
@Tag(name = "Заявки на усыновление", description = "Заявки: животное, заявитель и статус")
public class AdoptionApplicationController {

    private final AdoptionApplicationService adoptionApplicationService;

    public AdoptionApplicationController(AdoptionApplicationService adoptionApplicationService) {
        this.adoptionApplicationService = adoptionApplicationService;
    }

    @GetMapping
    @Operation(operationId = "listAdoptionApplications", summary = "Список заявок")
    public List<AdoptionApplicationResponse> findAll() {
        return adoptionApplicationService.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getAdoptionApplication", summary = "Заявка по идентификатору")
    public AdoptionApplicationResponse findById(@PathVariable long id) {
        return toResponse(adoptionApplicationService.findById(id));
    }

    @PostMapping
    @Operation(operationId = "createAdoptionApplication", summary = "Подать заявку на усыновление")
    @ApiResponse(responseCode = "201", description = "Ресурс создан",
        headers = @Header(name = "Location", description = "Адрес созданного ресурса"))
    public ResponseEntity<AdoptionApplicationResponse> create(@Valid @RequestBody AdoptionApplicationRequest request) {
        AdoptionApplication created = adoptionApplicationService.create(request);
        return ResponseEntity
                .created(locationOf(created.getId()))
                .body(toResponse(created));
    }

    @PatchMapping("/{id}")
    @Operation(operationId = "updateAdoptionApplication", summary = "Изменить статус заявки")
    public AdoptionApplicationResponse update(@PathVariable long id,
                                              @Valid @RequestBody AdoptionApplicationPatch patch) {
        return toResponse(adoptionApplicationService.update(id, patch));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(operationId = "deleteAdoptionApplication", summary = "Удалить заявку")
    public void delete(@PathVariable long id) {
        adoptionApplicationService.delete(id);
    }

    private AdoptionApplicationResponse toResponse(AdoptionApplication application) {
        return new AdoptionApplicationResponse(application.getId(), application.getAnimalId(),
                application.getApplicantId(), application.getStatus(), application.getSubmittedAt());
    }

    private static URI locationOf(long id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
