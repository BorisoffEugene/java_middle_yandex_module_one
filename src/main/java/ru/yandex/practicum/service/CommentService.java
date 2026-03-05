package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import java.util.List;

import ru.yandex.practicum.model.CommentDTO;
import ru.yandex.practicum.repository.CommentRepository;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<CommentDTO> findAll(Long postId) {
        return commentRepository.findAll(postId);
    }

    public CommentDTO findById(Long postId, Long id) {
        return commentRepository.findById(postId, id);
    }

    public CommentDTO save(CommentDTO comment) {
        return commentRepository.save(comment);
    }

    public CommentDTO update(CommentDTO comment) {
        return commentRepository.update(comment);
    }

    public void deleteById(Long postId, Long id) {
        commentRepository.deleteById(postId, id);
    }
}
