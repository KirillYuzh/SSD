package ru.mtuci.rbpo.lab4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.rbpo.lab4.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByOrderById();

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
