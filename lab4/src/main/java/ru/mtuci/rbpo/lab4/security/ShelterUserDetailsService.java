package ru.mtuci.rbpo.lab4.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.mtuci.rbpo.lab4.domain.User;
import ru.mtuci.rbpo.lab4.domain.UserStatus;
import ru.mtuci.rbpo.lab4.repository.UserRepository;

@Service
public class ShelterUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public ShelterUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Заблокированная учётная запись возвращается как отключённая,
     * поэтому поставщик аутентификации отклоняет вход без раскрытия причины.
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository
            .findByUsername(username)
            .orElseThrow(() ->
                new UsernameNotFoundException("Учётная запись не найдена")
            );
        return org.springframework.security.core.userdetails.User.withUsername(
            user.getUsername()
        )
            .password(user.getPassword())
            .roles(user.getRole().name())
            .disabled(user.getStatus() != UserStatus.ACTIVE)
            .build();
    }
}
