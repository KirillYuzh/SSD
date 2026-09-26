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
import ru.mtuci.rbpo.lab2.api.dto.AnimalHandoverRecordPatch;
import ru.mtuci.rbpo.lab2.api.dto.AnimalHandoverRecordRequest;
import ru.mtuci.rbpo.lab2.api.dto.AnimalHandoverRecordResponse;
import ru.mtuci.rbpo.lab2.domain.AnimalHandoverRecord;
import ru.mtuci.rbpo.lab2.service.AnimalHandoverRecordService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/animal-handover-records")
@Tag(name = "Акты передачи", description = "Акты передачи животного семье: даты подготовки и подтверждения, статус")
public class AnimalHandoverRecordController {

    private final AnimalHandoverRecordService handoverRecordService;

    public AnimalHandoverRecordController(AnimalHandoverRecordService handoverRecordService) {
        this.handoverRecordService = handoverRecordService;
    }

    @GetMapping
    @Operation(operationId = "listHandoverRecords", summary = "Список актов передачи")
    public List<AnimalHandoverRecordResponse> findAll() {
        return handoverRecordService.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getHandoverRecord", summary = "Акт передачи по идентификатору")
    public AnimalHandoverRecordResponse findById(@PathVariable long id) {
        return toResponse(handoverRecordService.findById(id));
    }

    @PostMapping
    @Operation(operationId = "createHandoverRecord", summary = "Подготовить акт передачи")
    @ApiResponse(responseCode = "201", description = "Ресурс создан",
        headers = @Header(name = "Location", description = "Адрес созданного ресурса"))
    public ResponseEntity<AnimalHandoverRecordResponse> create(
            @Valid @RequestBody AnimalHandoverRecordRequest request) {
        AnimalHandoverRecord created = handoverRecordService.create(request);
        return ResponseEntity
                .created(locationOf(created.getId()))
                .body(toResponse(created));
    }

    @PatchMapping("/{id}")
    @Operation(operationId = "updateHandoverRecord", summary = "Изменить статус акта передачи")
    public AnimalHandoverRecordResponse update(@PathVariable long id,
                                               @Valid @RequestBody AnimalHandoverRecordPatch patch) {
        return toResponse(handoverRecordService.update(id, patch));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(operationId = "deleteHandoverRecord", summary = "Удалить акт передачи")
    public void delete(@PathVariable long id) {
        handoverRecordService.delete(id);
    }

    private AnimalHandoverRecordResponse toResponse(AnimalHandoverRecord record) {
        return new AnimalHandoverRecordResponse(record.getId(), record.getApplicationId(),
                record.getPreparationDate(), record.getConfirmationDate(), record.getStatus());
    }

    private static URI locationOf(long id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
