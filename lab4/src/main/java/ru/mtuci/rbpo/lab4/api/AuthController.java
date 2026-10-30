package ru.mtuci.rbpo.lab4.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.mtuci.rbpo.lab4.api.dto.CsrfResponse;
import ru.mtuci.rbpo.lab4.api.dto.RegistrationRequest;
import ru.mtuci.rbpo.lab4.api.dto.UserResponse;
import ru.mtuci.rbpo.lab4.domain.User;
import ru.mtuci.rbpo.lab4.service.UserService;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Аутентификация", description = "Регистрация, CSRF-токен и текущая учётная запись")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/csrf")
    @SecurityRequirements
    @Operation(operationId = "getCsrfToken", summary = "Получить CSRF-токен")
    public CsrfResponse csrf(CsrfToken csrfToken) {
        return new CsrfResponse(csrfToken.getHeaderName(), csrfToken.getParameterName(), csrfToken.getToken());
    }

    @PostMapping("/register")
    @SecurityRequirements
    @Operation(operationId = "register", summary = "Зарегистрировать заявителя")
    @ApiResponse(responseCode = "201", description = "Ресурс создан",
        headers = @Header(name = "Location", description = "Адрес созданного ресурса"))
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegistrationRequest request) {
        User created = userService.register(request);
        return ResponseEntity
                .created(locationOf(created.getId()))
                .body(toResponse(created));
    }

    @GetMapping("/me")
    @Operation(operationId = "currentUser", summary = "Текущая учётная запись")
    public UserResponse current(Authentication authentication) {
        return toResponse(userService.findByUsername(authentication.getName()));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getFullName(), user.getEmail(),
                user.getRole(), user.getStatus());
    }

    private static URI locationOf(long id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
