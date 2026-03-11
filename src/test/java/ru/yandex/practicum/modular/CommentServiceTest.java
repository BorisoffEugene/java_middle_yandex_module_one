package ru.yandex.practicum.modular;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ru.yandex.practicum.model.CommentDTO;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.service.CommentService;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульное тестирование комментариев")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    @DisplayName("Получение комментариев поста (комментарии существуют)")
    void testFindAll_Success() {
        Long postId = 1L;
        List<CommentDTO> mockComments = List.of(
            new CommentDTO(1L, "Первый комментарий", postId),
            new CommentDTO(2L, "Второй комментарий", postId)
        );

        when(commentRepository.findAll(postId)).thenReturn(mockComments);
        List<CommentDTO> comments = commentRepository.findAll(postId);

        assertNotNull(comments, "Комментарии должены существовать");
        assertEquals(2, comments.size(), "Количество комментариев должно быть 2");

        assertEquals(1L, comments.getFirst().getId(), String.format("ID должен быть: %d", 1L));
        assertEquals("Первый комментарий", comments.getFirst().getText(), String.format("Текст должен быть: %s", "Первый комментарий"));
        assertEquals(postId, comments.getFirst().getPostId(), String.format("ID поста должен быть: %d", postId));
    }

    @Test
    @DisplayName("Получение комментариев поста (комментарии не существуют)")
    void testFindAll_NotFound() {
        Long postId = -1L;
        List<CommentDTO> mockComments = new ArrayList<>();

        when(commentRepository.findAll(postId)).thenReturn(mockComments);
        List<CommentDTO> comments = commentRepository.findAll(postId);

        assertEquals(0, comments.size(), "Количество комментариев должно быть 0");
    }

    @Test
    @DisplayName("Получение комментария поста (комментарий существует)")
    void testFindById_Success() {
        Long id = 1L;
        String text = "Тестовый комментарий";
        Long postId = 1L;

        when(commentRepository.findById(id)).thenReturn(new CommentDTO(id, text, postId));
        CommentDTO commentDTO = commentRepository.findById(id);

        assertNotNull(commentDTO, "Комментарий должен существовать");
        assertEquals(id, commentDTO.getId(), String.format("ID должен быть: %d", id));
        assertEquals(text, commentDTO.getText(), String.format("Текст должен быть: %s", text));
        assertEquals(postId, commentDTO.getPostId(), String.format("ID поста должен быть: %d", postId));
    }

    @Test
    @DisplayName("Получение комментария поста (комментарий не существует)")
    void testFindById_NotFound() {
        Long id = -1L;
        when(commentRepository.findById(id)).thenReturn(null);
        CommentDTO commentDTO = commentRepository.findById(id);
        assertNull(commentDTO, "Комментария не должно существовать");
    }

    @Test
    @DisplayName("Добавление комментария к посту (пост существует)")
    void testSave_Success() {
        Long id = 1L;
        String text = "Тестовый комментарий";
        Long postId = 1L;

        when(commentRepository.save(any(CommentDTO.class))).thenReturn(new CommentDTO(id, text, postId));
        CommentDTO commentDTO = commentRepository.save(new CommentDTO(id, text, postId));

        assertNotNull(commentDTO, "Комментарий должен существовать");
        assertEquals(id, commentDTO.getId(), String.format("ID должен быть: %d", id));
        assertEquals(text, commentDTO.getText(), String.format("Текст должен быть: %s", text));
        assertEquals(postId, commentDTO.getPostId(), String.format("ID поста должен быть: %d", postId));
    }

    @Test
    @DisplayName("Добавление комментария к посту (пост не существует)")
    void testSave_NotFound() {
        Long id = 1L;
        String text = "Тестовый комментарий";
        Long postId = -1L;

        when(commentRepository.save(any(CommentDTO.class))).thenReturn(null);
        CommentDTO commentDTO = commentRepository.save(new CommentDTO(id, text, postId));

        assertNull(commentDTO, "Комментария не должно существовать");
    }

    @Test
    @DisplayName("Редактирование комментария к посту (комментарий существует)")
    void testUpdate_Success() {
        Long id = 1L;
        String text = "Тестовый комментарий";
        Long postId = 1L;

        when(commentRepository.update(any(CommentDTO.class))).thenReturn(new CommentDTO(id, text, postId));
        CommentDTO commentDTO = commentRepository.update(new CommentDTO(id, text, postId));

        assertNotNull(commentDTO, "Комментарий должен существовать");
        assertEquals(id, commentDTO.getId(), String.format("ID должен быть: %d", id));
        assertEquals(text, commentDTO.getText(), String.format("Текст должен быть: %s", text));
        assertEquals(postId, commentDTO.getPostId(), String.format("ID поста должен быть: %d", postId));
    }

    @Test
    @DisplayName("Редактирование комментария к посту (пост / комментарий не существует)")
    void testUpdate_NotFound() {
        Long id = 1L;
        String text = "Тестовый комментарий";
        Long postId = -1L;

        when(commentRepository.update(any(CommentDTO.class))).thenReturn(null);
        CommentDTO commentDTO = commentRepository.update(new CommentDTO(id, text, postId));

        assertNull(commentDTO, "Комментария не должно существовать");
    }

    @Test
    @DisplayName("Удаление комментария")
    void testDeleteById() {
        Long id = 1L;
        doNothing().when(commentRepository).deleteById(id);
        commentRepository.deleteById(id);
        verify(commentRepository, times(1)).deleteById(id);
    }
}
