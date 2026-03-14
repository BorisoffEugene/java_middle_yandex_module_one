package ru.yandex.practicum.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ru.yandex.practicum.model.PostDTO;
import ru.yandex.practicum.model.PostList;
import ru.yandex.practicum.service.PostService;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public PostList findByParams(@RequestParam("search") String search, @RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize) {
        return postService.findByParams(search, pageNumber, pageSize);
    }

    @GetMapping("/{id}")
    public PostDTO findById(@PathVariable("id") Long id) {
        return postService.findById(id);
    }

    @PostMapping
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

    @PostMapping("/{id}/likes")
    public int incLikesCount(@PathVariable("id") Long id) {
        return postService.incLikesCount(id);
    }

    @PutMapping("/{id}/image")
    public void uploadImage(@PathVariable("id") Long id, @RequestParam("image") MultipartFile file) throws Exception {
        postService.uploadImage(id, file.getBytes());
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Long id) {
        byte[] bytes = postService.getImage(id);
        return ResponseEntity.ok().body(bytes);
    }
}
