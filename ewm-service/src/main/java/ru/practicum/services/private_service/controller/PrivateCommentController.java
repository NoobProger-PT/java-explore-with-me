package ru.practicum.services.private_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.ShortCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.services.private_service.service.PrivateCommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/comments")
@Slf4j
@Validated
public class PrivateCommentController {

    private final PrivateCommentService service;

    @GetMapping("/event/{eventId}/user/{userId}")
    public ShortCommentDto get(@PathVariable @Positive Long eventId,
                               @PathVariable @Positive Long userId) {
        log.info("Получение коммента пользователя к посту.");
        return service.get(userId, eventId);
    }

    @PostMapping("/event/{eventId}/user/{userId}")
    public ShortCommentDto add(@PathVariable @Positive Long eventId,
                               @PathVariable @Positive Long userId,
                               @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Добавление нового коммента.");
        return service.add(userId, eventId, newCommentDto);
    }

    @DeleteMapping("/event/{eventId}/user/{userId}/comment/{commentId}")
    public String delete(@PathVariable @Positive Long eventId,
                         @PathVariable @Positive Long userId,
                         @PathVariable @Positive Long commentId) {
        log.info("Удаление коммента пользователем.");
        return service.delete(userId, eventId, commentId);
    }

    @PatchMapping("/event/{eventId}/user/{userId}")
    public ShortCommentDto changeComment(@PathVariable @Positive Long eventId,
                                         @PathVariable @Positive Long userId,
                                         @RequestBody @Valid UpdateCommentDto updateCommentDto) {
        log.info("Редактирование коммента пользователем.");
        return service.update(userId, eventId, updateCommentDto);
    }

    @PostMapping("/event/{eventId}/user/{userId}/comment/{commentId}/like")
    public ShortCommentDto addLike(@PathVariable @Positive Long eventId,
                                   @PathVariable @Positive Long userId,
                                   @PathVariable @Positive Long commentId) {
        log.info("Добавление лайка комменту.");
        return service.addLike(userId, eventId, commentId);
    }

    @PostMapping("/event/{eventId}/user/{userId}/comment/{commentId}/dislike")
    public ShortCommentDto addDislike(@PathVariable @Positive Long eventId,
                                   @PathVariable @Positive Long userId,
                                   @PathVariable @Positive Long commentId) {
        log.info("Добавление дислайка комменту.");
        return service.addDislike(userId, eventId, commentId);
    }
}
