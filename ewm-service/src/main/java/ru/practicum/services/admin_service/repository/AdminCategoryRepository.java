package ru.practicum.services.admin_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.category.model.Category;

import java.util.Optional;

public interface AdminCategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

}
