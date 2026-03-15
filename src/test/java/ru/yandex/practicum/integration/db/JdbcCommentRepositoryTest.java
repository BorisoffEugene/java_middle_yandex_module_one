package ru.yandex.practicum.integration.db;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import ru.yandex.practicum.model.CommentDTO;
import ru.yandex.practicum.model.PostDTO;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.repository.PostRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@Disabled
@DisplayName("Интеграционное (база данных) тестирование комментариев")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class JdbcCommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;

    private PostDTO post;
    private CommentDTO comment1;
    private CommentDTO comment2;

    @BeforeEach
    void beforeEach() {
        // очищаем все данные
        postRepository.deleteAll();

        // добавляем один пост
        post = postRepository.save(new PostDTO("Название поста", "Текст поста", List.of("tag1", "tag2")));

        // добавляем к нему пару комментариев
        comment1 = commentRepository.save(new CommentDTO("Комментарий 1", post.getId()));
        comment2 = commentRepository.save(new CommentDTO("Комментарий 2", post.getId()));
    }

    @Test
    @DisplayName("Получение комментариев поста (комментарии существуют)")
    void testFindAll_Success() {
        List<CommentDTO> comments = commentRepository.findAll(post.getId());

        assertNotNull(comments, "Комментарии должены существовать");
        assertEquals(2, comments.size(), "Количество комментариев должно быть 2");

        assertEquals(comment2.getId(), comments.getFirst().getId(), String.format("ID должен быть: %d", comment2.getId()));
        assertEquals(comment2.getText(), comments.getFirst().getText(), String.format("Текст должен быть: %s", comment2.getText()));
        assertEquals(post.getId(), comments.getFirst().getPostId(), String.format("ID поста должен быть: %d", post.getId()));
    }

    @Test
    @DisplayName("Получение комментариев поста (комментарии не существуют)")
    void testFindAll_NotFound() {
        List<CommentDTO> comments = commentRepository.findAll(-1L);
        assertEquals(0, comments.size(), "Количество комментариев должно быть 0");
    }

    @Test
    @DisplayName("Получение комментария поста (комментарий существует)")
    void testFindById_Success() {
        CommentDTO commentDTO = commentRepository.findById(comment1.getId());

        assertNotNull(commentDTO, "Комментарий должен существовать");
        assertEquals(comment1.getId(), commentDTO.getId(), String.format("ID должен быть: %d", comment1.getId()));
        assertEquals(comment1.getText(), commentDTO.getText(), String.format("Текст должен быть: %s", comment1.getText()));
        assertEquals(comment1.getPostId(), commentDTO.getPostId(), String.format("ID поста должен быть: %d", comment1.getPostId()));
    }

    @Test
    @DisplayName("Получение комментария поста (комментарий не существует)")
    void testFindById_NotFound() {
        CommentDTO commentDTO = commentRepository.findById(-1L);
        assertNull(commentDTO, "Комментария не должно существовать");
    }

    @Test
    @DisplayName("Добавление комментария к посту (пост существует)")
    void testSave_Success() {
        String text = "Комментарий 3";
        Long postId = post.getId();

        CommentDTO commentDTO = commentRepository.save(new CommentDTO(text, postId));

        assertNotNull(commentDTO, "Комментарий должен существовать");
        assertEquals(text, commentDTO.getText(), String.format("Текст должен быть: %s", text));
        assertEquals(postId, commentDTO.getPostId(), String.format("ID поста должен быть: %d", postId));
    }

    @Test
    @DisplayName("Добавление комментария к посту (пост не существует)")
    void testSave_NotFound() {
        String text = "Тестовый комментарий";
        Long postId = -1L;

        CommentDTO commentDTO = commentRepository.save(new CommentDTO(text, postId));
        assertNull(commentDTO, "Комментария не должно существовать");
    }

    @Test
    @DisplayName("Редактирование комментария к посту (комментарий существует)")
    void testUpdate_Success() {
        Long id = comment1.getId();
        String text = "Изменили комментарий 1";
        Long postId = post.getId();

        CommentDTO commentDTO = commentRepository.update(new CommentDTO(id, text, postId));

        assertNotNull(commentDTO, "Комментарий должен существовать");
        assertEquals(id, commentDTO.getId(), String.format("ID должен быть: %d", id));
        assertEquals(text, commentDTO.getText(), String.format("Текст должен быть: %s", text));
        assertEquals(postId, commentDTO.getPostId(), String.format("ID поста должен быть: %d", postId));
    }

    @Test
    @DisplayName("Редактирование комментария к посту (пост / комментарий не существует)")
    void testUpdate_NotFound() {
        String text = "Тестовый комментарий";
        Long postId = -1L;

        CommentDTO commentDTO = commentRepository.update(new CommentDTO(text, postId));
        assertNull(commentDTO, "Комментария не должно существовать");
    }

    @Test
    @DisplayName("Удаление комментария")
    void testDeleteById() {
        commentRepository.deleteById(comment1.getId());
        List<CommentDTO> comments = commentRepository.findAll(post.getId());
        assertEquals(1, comments.size(), "Количество комментариев должно быть 1");
        assertTrue(comments.stream().noneMatch(c -> c.getId().equals(comment1.getId())), String.format("Не должно быть комментария с ID: %d", comment1.getId()));
    }
}
