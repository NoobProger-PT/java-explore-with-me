package ru.practicum.services.admin_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.Optional;

public interface AdminUsersRepository extends JpaRepository<User, Long> {

    Page<User> findAllByIdIn(List<Long> ids, Pageable pageable);

    Optional<User> findByName(String name);
}