package ru.mtuci.rbpo.lab4.service;

import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mtuci.rbpo.lab4.api.dto.RegistrationRequest;
import ru.mtuci.rbpo.lab4.api.dto.UserPatch;
import ru.mtuci.rbpo.lab4.domain.AdoptionApplication;
import ru.mtuci.rbpo.lab4.domain.User;
import ru.mtuci.rbpo.lab4.domain.UserRole;
import ru.mtuci.rbpo.lab4.domain.UserStatus;
import ru.mtuci.rbpo.lab4.exception.ConflictException;
import ru.mtuci.rbpo.lab4.exception.NotFoundException;
import ru.mtuci.rbpo.lab4.repository.AdoptionApplicationRepository;
import ru.mtuci.rbpo.lab4.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final AdoptionApplicationRepository adoptionApplicationRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
        UserRepository userRepository,
        AdoptionApplicationRepository adoptionApplicationRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.adoptionApplicationRepository = adoptionApplicationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAllByOrderById();
    }

    public User findById(long id) {
        return userRepository
            .findById(id)
            .orElseThrow(() ->
                new NotFoundException("Пользователь с id " + id + " не найден")
            );
    }

    public User findByUsername(String username) {
        return userRepository
            .findByUsername(username)
            .orElseThrow(() ->
                new NotFoundException("Пользователь " + username + " не найден")
            );
    }

    public boolean isCurrentUser(long id, String username) {
        return userRepository
            .findById(id)
            .map(user -> user.getUsername().equals(username))
            .orElse(false);
    }

    /**
     * Учётная запись создаётся в роли заявителя и в активном состоянии: роль и
     * состояние задаёт сервер, поэтому регистрация не даёт повышенных привилегий.
     */
    @Transactional
    public User register(RegistrationRequest request) {
        String username = request.username().trim();
        String email = request.email().trim();
        requireUniqueUsername(username, null);
        requireUniqueEmail(email, null);
        return userRepository.save(
            new User(
                username,
                request.fullName().trim(),
                email,
                passwordEncoder.encode(request.password()),
                UserRole.ADOPTER,
                UserStatus.ACTIVE
            )
        );
    }

    @Transactional
    public User update(long id, UserPatch patch) {
        User user = findById(id);
        String username =
            patch.username() != null
                ? patch.username().trim()
                : user.getUsername();
        String email =
            patch.email() != null ? patch.email().trim() : user.getEmail();
        requireUniqueUsername(username, id);
        requireUniqueEmail(email, id);
        if (patch.username() != null) {
            user.setUsername(username);
        }
        if (patch.fullName() != null) {
            user.setFullName(patch.fullName().trim());
        }
        if (patch.email() != null) {
            user.setEmail(email);
        }
        return user;
    }

    @Transactional
    public User changeStatus(long id, UserStatus status) {
        User user = findById(id);
        user.setStatus(status);
        return user;
    }

    @Transactional
    public void delete(long id) {
        findById(id);
        List<AdoptionApplication> applications =
            adoptionApplicationRepository.findByApplicantIdOrderById(id);
        if (!applications.isEmpty()) {
            throw new ConflictException(
                "Пользователь с id " +
                    id +
                    " является заявителем в заявках " +
                    applications
                        .stream()
                        .map(application -> String.valueOf(application.getId()))
                        .toList() +
                    ", удаление оставило бы недействительные ссылки"
            );
        }
        userRepository.deleteById(id);
    }

    private void requireUniqueUsername(String username, Long currentId) {
        userRepository
            .findByUsername(username)
            .filter(existing -> !existing.getId().equals(currentId))
            .ifPresent(existing -> {
                throw new ConflictException(
                    "Логин " +
                        username +
                        " уже занят пользователем с id " +
                        existing.getId()
                );
            });
    }

    private void requireUniqueEmail(String email, Long currentId) {
        userRepository
            .findByEmail(email)
            .filter(existing -> !existing.getId().equals(currentId))
            .ifPresent(existing -> {
                throw new ConflictException(
                    "Адрес " +
                        email +
                        " уже используется пользователем с id " +
                        existing.getId()
                );
            });
    }
}
