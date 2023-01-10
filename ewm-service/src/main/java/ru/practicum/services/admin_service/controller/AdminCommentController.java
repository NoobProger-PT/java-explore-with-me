package ru.practicum.services.admin_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
