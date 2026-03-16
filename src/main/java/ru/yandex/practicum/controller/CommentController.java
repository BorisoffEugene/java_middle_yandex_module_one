package ru.yandex.practicum.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import ru.yandex.practicum.model.CommentDTO;
import ru.yandex.practicum.service.CommentService;

@RestController
@RequestMapping({"api/posts/{postId}/comments", "api/posts/undefined/comments"})
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<CommentDTO> comments(@PathVariable(name = "postId", required = false) Long postId) {
        if (postId == null) return new ArrayList<>();

        return commentService.findAll(postId);
    }

    @GetMapping("/{id}")
    public CommentDTO findById(@PathVariable("id") Long id) {
        return commentService.findById(id);
    }

    @PostMapping
    public CommentDTO save(@RequestBody CommentDTO comment) {
        return commentService.save(comment);
    }

    @PutMapping("/{id}")
    public CommentDTO update(@RequestBody CommentDTO comment) {
        return commentService.update(comment);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        commentService.deleteById(id);
    }
}
