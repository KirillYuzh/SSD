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
import org.springframework.security.core.Authentication;
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
import ru.mtuci.rbpo.lab4.api.dto.AdoptionApplicationPatch;
import ru.mtuci.rbpo.lab4.api.dto.AdoptionApplicationRequest;
import ru.mtuci.rbpo.lab4.api.dto.AdoptionApplicationResponse;
import ru.mtuci.rbpo.lab4.domain.AdoptionApplication;
import ru.mtuci.rbpo.lab4.service.AdoptionApplicationService;

@RestController
@RequestMapping("/api/adoption-applications")
@Tag(
    name = "Заявки на усыновление",
    description = "Заявки: животное, заявитель и статус"
)
public class AdoptionApplicationController {

    private final AdoptionApplicationService adoptionApplicationService;

    public AdoptionApplicationController(
        AdoptionApplicationService adoptionApplicationService
    ) {
        this.adoptionApplicationService = adoptionApplicationService;
    }

    @GetMapping
    @Operation(
        operationId = "listAdoptionApplications",
        summary = "Список заявок"
    )
    @PreAuthorize("hasRole('STAFF')")
    public List<AdoptionApplicationResponse> findAll() {
        return adoptionApplicationService
            .findAll()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @GetMapping("/{id}")
    @Operation(
        operationId = "getAdoptionApplication",
        summary = "Заявка по идентификатору"
    )
    @PreAuthorize(
        "hasRole('STAFF') or @adoptionApplicationService.isApplicant(#id, authentication.name)"
    )
    public AdoptionApplicationResponse findById(@PathVariable long id) {
        return toResponse(adoptionApplicationService.findById(id));
    }

    @PostMapping
    @Operation(
        operationId = "createAdoptionApplication",
        summary = "Подать заявку на усыновление"
    )
    @PreAuthorize("isAuthenticated()")
    @ApiResponse(
        responseCode = "201",
        description = "Ресурс создан",
        headers = @Header(
            name = "Location",
            description = "Адрес созданного ресурса"
        )
    )
    public ResponseEntity<AdoptionApplicationResponse> create(
        @Valid @RequestBody AdoptionApplicationRequest request,
        Authentication authentication
    ) {
        AdoptionApplication created = adoptionApplicationService.create(
            request,
            authentication.getName()
        );
        return ResponseEntity.created(locationOf(created.getId())).body(
            toResponse(created)
        );
    }

    @PatchMapping("/{id}")
    @Operation(
        operationId = "updateAdoptionApplication",
        summary = "Изменить мотивацию заявки"
    )
    @PreAuthorize(
        "@adoptionApplicationService.isApplicant(#id, authentication.name)"
    )
    public AdoptionApplicationResponse update(
        @PathVariable long id,
        @Valid @RequestBody AdoptionApplicationPatch patch
    ) {
        return toResponse(adoptionApplicationService.update(id, patch));
    }

    @PostMapping("/{id}/approval")
    @Operation(
        operationId = "approveAdoptionApplication",
        summary = "Одобрить заявку и отклонить конкурирующие заявки на это животное"
    )
    @PreAuthorize("hasRole('STAFF')")
    public AdoptionApplicationResponse approve(@PathVariable long id) {
        return toResponse(adoptionApplicationService.approve(id));
    }

    @PostMapping("/{id}/rejection")
    @Operation(
        operationId = "rejectAdoptionApplication",
        summary = "Отклонить заявку"
    )
    @PreAuthorize("hasRole('STAFF')")
    public AdoptionApplicationResponse reject(@PathVariable long id) {
        return toResponse(adoptionApplicationService.reject(id));
    }

    @PostMapping("/{id}/withdrawal")
    @Operation(
        operationId = "withdrawAdoptionApplication",
        summary = "Отозвать заявку заявителем"
    )
    @PreAuthorize(
        "@adoptionApplicationService.isApplicant(#id, authentication.name)"
    )
    public AdoptionApplicationResponse withdraw(@PathVariable long id) {
        return toResponse(adoptionApplicationService.withdraw(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        operationId = "deleteAdoptionApplication",
        summary = "Удалить заявку"
    )
    @PreAuthorize("hasRole('STAFF')")
    public void delete(@PathVariable long id) {
        adoptionApplicationService.delete(id);
    }

    private AdoptionApplicationResponse toResponse(
        AdoptionApplication application
    ) {
        return new AdoptionApplicationResponse(
            application.getId(),
            application.getAnimalId(),
            application.getApplicantId(),
            application.getMotivation(),
            application.getStatus(),
            application.getSubmittedAt()
        );
    }

    private static URI locationOf(long id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(id)
            .toUri();
    }
}
