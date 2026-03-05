package ru.yandex.practicum.repository;

import ru.yandex.practicum.model.PostDTO;

public interface PostRepository {
    // search todo
    PostDTO findById(Long id);
    PostDTO save(PostDTO post);
    PostDTO update(PostDTO post);
    void deleteById(Long id);
    int incLikesCount(Long id);
}
