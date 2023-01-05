package ru.practicum.services.public_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.exception.compilation.CompilationNutFoundException;
import ru.practicum.services.admin_service.repository.AdminCompilationRepository;
import ru.practicum.services.public_service.service.PublicCompilationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicCompilationServiceImpl implements PublicCompilationService {

    private final AdminCompilationRepository compilationRepository;

    @Override
    public List<CompilationDto> getCompilations(boolean pinned, int from, int size) {
        List<CompilationDto> result;
        if (pinned) {
            result = compilationRepository.findAllByPinned(true,
                            PageRequest.of(from, size, Sort.by("id").ascending())).stream()
                    .map(CompilationMapper::mapToCompilationDtoFromCompilation)
                    .collect(Collectors.toList());
        } else {
            result = compilationRepository.findAll(
                            PageRequest.of(from, size, Sort.by("id").ascending())).stream()
                    .map(CompilationMapper::mapToCompilationDtoFromCompilation)
                    .collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public CompilationDto getCompilationById(long complId) {
        Compilation compilation = compilationRepository.findById(complId).orElseThrow(
                () -> new CompilationNutFoundException("Такой подборки не существует"));
        return CompilationMapper.mapToCompilationDtoFromCompilation(compilation);
    }
}
