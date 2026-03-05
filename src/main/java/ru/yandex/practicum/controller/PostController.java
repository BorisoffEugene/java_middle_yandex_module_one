package ru.yandex.practicum.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.PostDTO;
import ru.yandex.practicum.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public PostDTO findById(@PathVariable("id") Long id) {
        return postService.findById(id);
    }

    @PostMapping("/{id}")
    public PostDTO save(@RequestBody PostDTO post) {
        return postService.save(post);
    }

    @PutMapping("/{id}")
    public PostDTO update(@RequestBody PostDTO post) {
        return postService.update(post);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        postService.deleteById(id);
    }

    @PostMapping("{id}/likes")
    public int incLikesCount(@PathVariable("id") Long id) {
        return postService.incLikesCount(id);
    }
}
