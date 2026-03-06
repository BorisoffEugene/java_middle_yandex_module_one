package ru.yandex.practicum.repository;

import ru.yandex.practicum.model.PostDTO;
import ru.yandex.practicum.model.PostList;

public interface PostRepository {
    PostList findByParams(String search, int pageNumber, int pageSize);
    PostDTO findById(Long id);
    PostDTO save(PostDTO post);
    PostDTO update(PostDTO post);
    void deleteById(Long id);
    int incLikesCount(Long id);
    void updateImage(Long id, byte[] image);
    byte[] findImageById(Long id);
}
