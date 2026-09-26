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
import ru.mtuci.rbpo.lab2.api.dto.UserPatch;
import ru.mtuci.rbpo.lab2.api.dto.UserRequest;
import ru.mtuci.rbpo.lab2.api.dto.UserResponse;
import ru.mtuci.rbpo.lab2.domain.User;
import ru.mtuci.rbpo.lab2.service.UserService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Пользователи", description = "Пользователи системы, выступающие заявителями усыновления")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(operationId = "listUsers", summary = "Список пользователей")
    public List<UserResponse> findAll() {
        return userService.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getUser", summary = "Пользователь по идентификатору")
    public UserResponse findById(@PathVariable long id) {
        return toResponse(userService.findById(id));
    }

    @PostMapping
    @Operation(operationId = "createUser", summary = "Зарегистрировать пользователя")
    @ApiResponse(responseCode = "201", description = "Ресурс создан",
        headers = @Header(name = "Location", description = "Адрес созданного ресурса"))
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        User created = userService.create(request);
        return ResponseEntity
                .created(locationOf(created.getId()))
                .body(toResponse(created));
    }

    @PatchMapping("/{id}")
    @Operation(operationId = "updateUser", summary = "Частично обновить пользователя")
    public UserResponse update(@PathVariable long id, @Valid @RequestBody UserPatch patch) {
        return toResponse(userService.update(id, patch));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(operationId = "deleteUser", summary = "Удалить пользователя")
    public void delete(@PathVariable long id) {
        userService.delete(id);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getFullName(), user.getEmail());
    }

    private static URI locationOf(long id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
