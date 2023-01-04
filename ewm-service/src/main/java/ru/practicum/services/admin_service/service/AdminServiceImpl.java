package ru.practicum.services.admin_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.State;
import ru.practicum.event.dto.AdminUpdateEventRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.location.model.Location;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.exception.category_exception.CantDeleteCategoryException;
import ru.practicum.exception.category_exception.CategoryAlreadyExistsException;
import ru.practicum.exception.category_exception.CategoryNotFoundException;
import ru.practicum.exception.compilation.CompilationNutFoundException;
import ru.practicum.exception.event_exception.EventNotFoundException;
import ru.practicum.exception.location_exception.LocationNotFoundException;
import ru.practicum.exception.user_exception.UserAlreadyExistsException;
import ru.practicum.exception.user_exception.UserNotFound;
import ru.practicum.services.admin_service.repository.AdminCategoryRepository;
import ru.practicum.services.admin_service.repository.AdminCompilationRepository;
import ru.practicum.services.admin_service.repository.AdminUsersRepository;
import ru.practicum.services.private_service.repository.PrivateEventsRepository;
import ru.practicum.services.private_service.repository.PrivateLocationRepository;
import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminUsersRepository usersRepository;
    private final AdminCategoryRepository categoryRepository;
    private final PrivateEventsRepository eventsRepository;
    private final PrivateLocationRepository locationRepository;
    private final AdminCompilationRepository compilationRepository;

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        List<UserDto> result;
        if (ids.size() == 1 && ids.get(0) == -1) {
            result = usersRepository.findAll(PageRequest.of(from, size, Sort.by("id").ascending())).stream()
                    .map(UserMapper::mapToUserDto).collect(Collectors.toList());
        } else {
            result = usersRepository.findAllByIdIn(ids,
                            PageRequest.of(from, size, Sort.by("id").ascending())).stream()
                    .map(UserMapper::mapToUserDto).collect(Collectors.toList());
        }
        return result;
    }

    @Override
    @Transactional
    public UserDto addUser(NewUserDto newUserDto) {
        User user;
        if (usersRepository.findByName(newUserDto.getName()).isEmpty()) {
            user = usersRepository.save(UserMapper.mapToUserFromNewUserDto(newUserDto));
        } else {
            throw new UserAlreadyExistsException("Данное имя занято.");
        }
        return UserMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public String delete(long userId) {
        usersRepository.findById(userId).orElseThrow(() ->
                new UserNotFound("Пользователь с id: " + userId + " не найден"));
        usersRepository.deleteById(userId);
        return "Пользователь с Id: " + userId + " удален";
    }

    @Override
    @Transactional
    public CategoryDto changeCategory(CategoryDto categoryDto) {
        Category category = checkCategory(categoryDto.getId());
        if (categoryRepository.findByName(categoryDto.getName()).isEmpty()) {
            category.setName(categoryDto.getName());
        } else {
            throw new CategoryAlreadyExistsException("Новая категория уже имеется в списке.");
        }
        return CategoryMapper.mapToCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto addCategory(CategoryDto categoryDto) {
        Category category;
        if (categoryRepository.findByName(categoryDto.getName()).isEmpty()) {
            category = categoryRepository.save(CategoryMapper.mapToCategory(categoryDto));
        } else {
            throw new CategoryAlreadyExistsException("Новая категория уже имеется в списке.");
        }
        return CategoryMapper.mapToCategoryDto(category);
    }

    @Override
    @Transactional
    public String deleteCategory(long catId) {
        checkCategory(catId);
        List<Event> events = eventsRepository.findAllByCategoryId(catId);
        if (events.size() != 0) {
            throw new CantDeleteCategoryException("Удаляемая категория присутствует в событиях: " + events);
        } else {
            categoryRepository.deleteById(catId);
            return "Категория удалена";
        }
    }

    @Override
    public List<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories, String rangeStart,
                                        String rangeEnd, int from, int size) {
        LocalDateTime startDate;
        LocalDateTime endDate;

        List<Event> events = eventsRepository.findAllByUserIdIn(users, PageRequest.of(from, size, Sort.by("id")
                        .ascending())).stream().collect(Collectors.toList());

        if (events.size() == 0) {
            return List.of();
        }

        if (rangeStart != null) {
            startDate = LocalDateTime.parse(rangeStart, dateTimeFormatter);
        } else {
            startDate = LocalDateTime.now();
        }

        if (rangeEnd != null) {
            endDate = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
        } else {
            endDate = LocalDateTime.MAX;
        }

        List<EventFullDto> result = events.stream()
                .filter(e -> states.contains(e.getState().name())
                && categories.contains(e.getCategory().getId())
                && e.getEventDate().isAfter(startDate)
                && e.getEventDate().isBefore(endDate))
                .map(EventMapper::mapToEventFullDtoFromEvent)
                .collect(Collectors.toList());
        return result;
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(long eventId, AdminUpdateEventRequest adminUpdateEventRequest) {
        Location location;
        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("ивент не найден"));
        event.setAnnotation(adminUpdateEventRequest.getAnnotation());
        Category category = checkCategory(adminUpdateEventRequest.getCategory());
        event.setCategory(category);
        event.setDescription(adminUpdateEventRequest.getDescription());
        event.setEventDate(adminUpdateEventRequest.getEventDate());
        if (adminUpdateEventRequest.getLocation() != null) {
            location = locationRepository.findByLatAndLon(adminUpdateEventRequest.getLocation().getLat(),
                    adminUpdateEventRequest.getLocation().getLon()).orElseThrow(() ->
                    new LocationNotFoundException("Данная локация не найдена"));
            event.setLocation(location);
        }
        event.setPaid(adminUpdateEventRequest.isPaid());
        event.setParticipantLimit(adminUpdateEventRequest.getParticipantLimit());
        event.setRequestModeration(adminUpdateEventRequest.isRequestModeration());
        event.setTitle(adminUpdateEventRequest.getTitle());
        return EventMapper.mapToEventFullDtoFromEvent(event);
    }

    @Override
    @Transactional
    public EventFullDto publishEvent(long eventId) {
        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("ивент не найден"));
        event.setState(State.PUBLISHED);
        return EventMapper.mapToEventFullDtoFromEvent(event);
    }

    @Override
    @Transactional
    public EventFullDto rejectEvent(long eventId) {
        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("ивент не найден"));
        event.setState(State.CANCELED);
        return EventMapper.mapToEventFullDtoFromEvent(event);
    }

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        List<Event> eventList = eventsRepository.findAllById(newCompilationDto.getEvents());
        Compilation compilation = CompilationMapper.mapToCompilationFromNewCompilationDto(newCompilationDto);
        compilation.setEvents(eventList);
        return CompilationMapper.mapToCompilationDtoFromCompilation(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public String deleteCompilationById(long complId) {
        Compilation compilation = checkCompilation(complId);
        compilationRepository.deleteById(complId);
        return "Подборка удалена";
    }

    @Override
    @Transactional
    public String deleteEventFromCompilation(long complId, long eventId) {
        Compilation compilation = checkCompilation(complId);
        List<Event> eventList = compilation.getEvents().stream()
                .filter(event -> event.getId() != eventId)
                .collect(Collectors.toList());
        compilation.setEvents(eventList);
        compilationRepository.save(compilation);
        return "ивент удален из подборки";
    }

    @Override
    @Transactional
    public CompilationDto addEventIntoCompilation(long complId, long eventId) {
        Compilation compilation = checkCompilation(complId);
        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("ивент не найден"));
        compilation.getEvents().add(event);
        return CompilationMapper.mapToCompilationDtoFromCompilation(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public CompilationDto removeCompilationFromMainPage(long complId) {
        Compilation compilation = checkCompilation(complId);
        compilation.setPinned(false);
        return CompilationMapper.mapToCompilationDtoFromCompilation(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public CompilationDto addCompilationOnMainPage(long complId) {
        Compilation compilation = checkCompilation(complId);
        compilation.setPinned(true);
        return CompilationMapper.mapToCompilationDtoFromCompilation(compilationRepository.save(compilation));
    }

    private Category checkCategory(long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new CategoryNotFoundException("Категория с id: " + categoryId + " не найдена"));
        return category;
    }

    private Compilation checkCompilation(long complId) {
        Compilation compilation = compilationRepository.findById(complId)
                .orElseThrow(() -> new CompilationNutFoundException("Подборка не найдена"));
        return compilation;
    }

}
