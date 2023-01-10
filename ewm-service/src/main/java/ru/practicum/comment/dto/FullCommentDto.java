package ru.practicum.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

public class FullCommentDto {
    private String text;
    private String authorName;
    private User author;
    private Event event;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime commentDate;
    private int likes;
    private int dislikes;
}
