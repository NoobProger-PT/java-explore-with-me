package ru.practicum.services.admin_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.FullCommentDto;
import ru.practicum.services.admin_service.service.AdminCommentService;

import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
@Slf4j
public class AdminCommentController {

    private final AdminCommentService service;

    @DeleteMapping("/comment/{commentId}")
    public String delete(@PathVariable @Positive Long commentId) {
        log.info("Удаление коммента администратором.");
        return service.deleteComment(commentId);
    }

    @GetMapping("/comment/{commentId}")
    public FullCommentDto getById(@PathVariable @Positive Long commentId) {
        log.info("Получение полной информации о комменте.");
        return service.getById(commentId);
    }
}
