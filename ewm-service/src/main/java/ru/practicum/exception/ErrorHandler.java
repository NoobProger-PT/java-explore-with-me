package ru.practicum.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.category_exception.CantDeleteCategoryException;
import ru.practicum.exception.category_exception.CategoryAlreadyExistsException;
import ru.practicum.exception.category_exception.CategoryNotFoundException;
import ru.practicum.exception.compilation.CompilationNutFoundException;
import ru.practicum.exception.event_exception.EventNotFoundException;
import ru.practicum.exception.event_exception.PublishedEventException;
import ru.practicum.exception.event_exception.WrongEventDateException;
import ru.practicum.exception.location_exception.LocationNotFoundException;
import ru.practicum.exception.participation_exception.InvalidParticipationException;
import ru.practicum.exception.participation_exception.ParticipationAlreadyExistsException;
import ru.practicum.exception.participation_exception.ParticipationNotFoundException;
import ru.practicum.exception.user_exception.InvalidUserException;
import ru.practicum.exception.user_exception.UserAlreadyExistsException;
import ru.practicum.exception.user_exception.UserNotFound;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> invalidUser(final UserNotFound e) {
        log.info("Пользователь не найден. {}", e.getMessage());
        String exceptionName = e.getClass().getName();
        return new ResponseEntity<>(new ErrorResponse(List.of(exceptionName), e.getMessage(),
                "Пользователь не найден.", "NOT_FOUND",
                LocalDateTime.now()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> invalidEventDate(final WrongEventDateException e) {
        log.info("Некоректная дата создаваемого события. {}", e.getMessage());
        String exceptionName = e.getClass().getName();
        return new ResponseEntity<>(new ErrorResponse(List.of(exceptionName), e.getMessage(),
                "Некоректная дата создаваемого события.", "BAD_REQUEST", LocalDateTime.now()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> eventNotFound(final EventNotFoundException e) {
        log.info("Событие не найдено. {}", e.getMessage());
        String exceptionName = e.getClass().getName();
        return new ResponseEntity<>(new ErrorResponse(List.of(exceptionName), e.getMessage(),
                "Событие не найдено.", "NOT_FOUND", LocalDateTime.now()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> eventCantUpdate(final PublishedEventException e) {
        log.info("Ошибка при обновлении события. {}", e.getMessage());
        String exceptionName = e.getClass().getName();
        return new ResponseEntity<>(new ErrorResponse(List.of(exceptionName), e.getMessage(),
                "Ошибка при обновлении события.", "BAD_REQUEST", LocalDateTime.now()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> categoryNotFound(final CategoryNotFoundException e) {
        log.info("Категория не найдена. {}", e.getMessage());
        String exceptionName = e.getClass().getName();
        return new ResponseEntity<>(new ErrorResponse(List.of(exceptionName), e.getMessage(),
                "Категория не найдена.", "NOT_FOUND", LocalDateTime.now()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> categoryAlreadyExists(final CategoryAlreadyExistsException e) {
        log.info("Категория уже существует. {}", e.getMessage());
        String exceptionName = e.getClass().getName();
        return new ResponseEntity<>(new ErrorResponse(List.of(exceptionName), e.getMessage(),
                "Такая категория уже имеется в списке", "CONFLICT", LocalDateTime.now()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> invalidUser(final InvalidUserException e) {
        log.info("Некоректные данные пользователя. {}", e.getMessage());
        String exceptionName = e.getClass().getName();
        return new ResponseEntity<>(new ErrorResponse(List.of(exceptionName), e.getMessage(),
                "Некоректные данные пользователя.", "BAD_REQUEST", LocalDateTime.now()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> userExists(final UserAlreadyExistsException e) {
        log.info("Име пользователя занято. {}", e.getMessage());
        String exceptionName = e.getClass().getName();
        return new ResponseEntity<>(new ErrorResponse(List.of(exceptionName), e.getMessage(),
                "Некоректные данные пользователя.", "CONFLICT", LocalDateTime.now()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> participationNotFound(final ParticipationNotFoundException e) {
        log.info("Участие не найдено. {}", e.getMessage());
        String exceptionName = e.getClass().getName();
        return new ResponseEntity<>(new ErrorResponse(List.of(exceptionName), e.getMessage(),
                "Участие не найдено.", "NOT_FOUND", LocalDateTime.now()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> participationAlreadyExists(final ParticipationAlreadyExistsException e) {
        log.info("Участие уже создано. {}", e.getMessage());
        String exceptionName = e.getClass().getName();
        return new ResponseEntity<>(new ErrorResponse(List.of(exceptionName), e.getMessage(),
                "Участие уже создано.", "CONFLICT", LocalDateTime.now()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> invalidParticipation(final InvalidParticipationException e) {
        log.info("Некоректные данные участия. {}", e.getMessage());
        String exceptionName = e.getClass().getName();
        return new ResponseEntity<>(new ErrorResponse(List.of(exceptionName), e.getMessage(),
                "Некоректные данные участия.", "BAD_REQUEST", LocalDateTime.now()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> cantDeleteCategory(final CantDeleteCategoryException e) {
        log.info("Удаление категории невозможно. {}", e.getMessage());
        String exceptionName = e.getClass().getName();
        return new ResponseEntity<>(new ErrorResponse(List.of(exceptionName), e.getMessage(),
                "Удаляемая категория используется в событии. Удалить невозможно.", "CONFLICT",
                LocalDateTime.now()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> locationNotFound(final LocationNotFoundException e) {
        log.info("Локация не найдена. {}", e.getMessage());
        String exceptionName = e.getClass().getName();
        return new ResponseEntity<>(new ErrorResponse(List.of(exceptionName), e.getMessage(),
                "Локация не найдена.", "NOT_FOUND", LocalDateTime.now()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> compilationNotFound(final CompilationNutFoundException e) {
        log.info("Подборка не найдена. {}", e.getMessage());
        String exceptionName = e.getClass().getName();
        return new ResponseEntity<>(new ErrorResponse(List.of(exceptionName), e.getMessage(),
                "Подборка не найдена.", "NOT_FOUND", LocalDateTime.now()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> badValid(MethodArgumentNotValidException e) {
        log.info("Неверно заполнены данные. {}", e.getMessage());
        String exceptionName = e.getClass().getName();
        return new ResponseEntity<>(new ErrorResponse(List.of(exceptionName), e.getMessage(),
                "Неверно заполнены данные.", "BAD_REQUEST", LocalDateTime.now()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> serverException(MissingServletRequestParameterException e) {
        log.info("Ошибка переданных данных. {}", e.getMessage());
        String exceptionName = e.getClass().getName();
        return new ResponseEntity<>(new ErrorResponse(List.of(exceptionName), e.getMessage(),
                "Неверно переданы данные по пути.", "BAD_REQUEST", LocalDateTime.now()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> emailAlreadyExists(DataIntegrityViolationException e) {
        log.info("Почта уже занята. {}", e.getMessage());
        String exceptionName = e.getClass().getName();
        return new ResponseEntity<>(new ErrorResponse(List.of(exceptionName), e.getMessage(),
                "Почта занята.", "CONFLICT", LocalDateTime.now()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> serverException(Throwable e) {
        log.info("Ошибка на сервере. {}", e.getMessage());
        String exceptionName = e.getClass().getName();
        return new ResponseEntity<>(new ErrorResponse(List.of(exceptionName), e.getMessage(),
                "Ошибка на сервере.", "INTERNAL_SERVER_ERROR", LocalDateTime.now()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Getter
    @AllArgsConstructor
    static class ErrorResponse {
        private final List errors;
        private final String message;
        private final String reason;
        private final String status;
        private final LocalDateTime timestamp;
    }
}
