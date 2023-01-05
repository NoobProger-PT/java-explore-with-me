package ru.practicum.services.private_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.participation.dto.ParticipationRequestDto;
import ru.practicum.services.private_service.service.PrivateParticipationService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
@Validated
public class PrivateParticipationController {

    private final PrivateParticipationService service;

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipationByEventId(@PathVariable @Positive Long eventId,
                                                                   @PathVariable @Positive Long userId) {
        log.info("Get participation of event id: {}", eventId);
        return service.getParticipation(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getParticipationByUserId(@PathVariable @Positive Long userId) {
        log.info("Get all participation of user with id: {}", userId);
        return service.getParticipationByUserId(userId);
    }

    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto addParticipation(@PathVariable @Positive Long userId,
                                                    @RequestParam @Positive Long eventId) {
        log.info("Add new participation");
        return service.addParticipation(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmReq(@PathVariable @Positive Long eventId,
                                              @PathVariable @Positive Long userId,
                                              @PathVariable @Positive Long reqId) {
        log.info("Confirm participation with id: {}", reqId);
        return service.confirmParticipation(userId, eventId, reqId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectReq(@PathVariable @Positive Long eventId,
                                             @PathVariable @Positive Long userId,
                                             @PathVariable @Positive Long reqId) {
        log.info("Reject participation with id: {}", reqId);
        return service.rejectParticipation(userId, eventId, reqId);
    }

    @PatchMapping("/{userId}/requests/{requestsId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable @Positive Long userId,
                                                 @PathVariable @Positive Long requestsId) {
        log.info("Cancel participation with id: {}", requestsId);
        return service.cancelRequest(userId, requestsId);
    }
}
