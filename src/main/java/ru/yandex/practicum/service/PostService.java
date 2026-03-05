package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;

import ru.yandex.practicum.model.PostDTO;
import ru.yandex.practicum.repository.PostRepository;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostDTO findById(Long id) {
        return postRepository.findById(id);
    }

    public PostDTO save(PostDTO post) {
        return postRepository.save(post);
    }

    public PostDTO update(PostDTO post) {
        return postRepository.update(post);
    }

    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }

    public int incLikesCount(Long id) {
        return postRepository.incLikesCount(id);
    }
}
