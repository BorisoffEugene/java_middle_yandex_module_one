package ru.yandex.practicum.repository;

import ru.yandex.practicum.model.PostDTO;
import ru.yandex.practicum.model.PostList;

import java.util.List;

public interface PostRepository {
    PostList findByParams(String titleSearch, List<String> tags, int pageNumber, int pageSize);
    int findPostsCount(String titleSearch, List<String> tags);
    PostDTO findById(Long id);
    PostDTO save(PostDTO post);
    PostDTO update(PostDTO post);
    void deleteById(Long id);
    int incLikesCount(Long id);
    void updateImage(Long id, byte[] image);
    byte[] findImageById(Long id);
}
