package ru.practicum.explore.services.admin_service.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.compilation.model.Compilation;

import java.util.List;

public interface AdminCompilationRepository extends JpaRepository<Compilation, Long> {

    List<Compilation> findAllByPinned(boolean pinned, PageRequest pageRequest);

}
