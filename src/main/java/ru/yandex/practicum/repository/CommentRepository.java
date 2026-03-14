package ru.yandex.practicum.repository;

import java.util.List;

import ru.yandex.practicum.model.CommentDTO;

public interface CommentRepository {
    List<CommentDTO> findAll(Long postId);
    CommentDTO findById(Long id);
    CommentDTO save(CommentDTO comment);
    CommentDTO update(CommentDTO comment);
    void deleteById(Long id);
}
