package ru.practicum.services.public_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.dto.ShortCommentDto;
import ru.practicum.services.public_service.service.PublicCommentService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/public")
public class PublicCommentController {

    private final PublicCommentService service;

    @GetMapping("/comment/event/{eventId}")
    public List<ShortCommentDto> getAllByEvent(@PathVariable @Positive Long eventId) {
        log.info("Получение всех комментов определенного ивента.");
        return service.getAllComments(eventId);
    }
}
