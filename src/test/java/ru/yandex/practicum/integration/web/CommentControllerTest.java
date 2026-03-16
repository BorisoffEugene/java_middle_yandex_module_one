package ru.yandex.practicum.integration.web;

import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.yandex.practicum.model.CommentDTO;
import ru.yandex.practicum.model.PostDTO;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.repository.PostRepository;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DisplayName("Интеграционное (веб) тестирование комментариев")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;
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
    void testFindAll_Success() throws Exception {
        mockMvc.perform(get("/api/posts/{postId}/comments", comment1.getPostId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].id").value(comment1.getId()))
                .andExpect(jsonPath("$[1].text").value(comment1.getText()))
                .andExpect(jsonPath("$[1].postId").value(comment1.getPostId()));
    }

    @Test
    @DisplayName("Получение комментариев поста (комментарии не существуют)")
    void testFindAll_NotFound() throws Exception {
        mockMvc.perform(get("/api/posts/{postId}/comments", -1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Получение комментария поста (комментарий существует)")
    void testFindById_Success() throws Exception {
        mockMvc.perform(get("/api/posts/{postId}/comments/{id}", comment1.getPostId(), comment1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(comment1.getId()))
                .andExpect(jsonPath("$.text").value(comment1.getText()))
                .andExpect(jsonPath("$.postId").value(comment1.getPostId()));
    }

    @Test
    @DisplayName("Получение комментария поста (комментарий не существует)")
    void testFindById_NotFound() throws Exception {
        mockMvc.perform(get("/api/posts/{postId}/comments/{id}", comment1.getPostId(), -1L))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Добавление комментария к посту (пост существует)")
    void testSave_Success() throws Exception {
        String text = "Комментарий 3";
        Long postId = post.getId();
        String json = String.format("""
            {
                "text": "%s",
                "postId": %d
            }
            """, text, postId);

        mockMvc.perform(post("/api/posts/{postId}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value(text))
                .andExpect(jsonPath("$.postId").value(postId));

        mockMvc.perform(get("/api/posts/{postId}/comments", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @DisplayName("Добавление комментария к посту (пост не существует)")
    void testSave_NotFound() throws Exception {
        String text = "Комментарий 3";
        Long postId = -1L;
        String json = String.format("""
            {
                "text": "%s",
                "postId": %d
            }
            """, text, postId);

        mockMvc.perform(post("/api/posts/{postId}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(content().string(""));

        mockMvc.perform(get("/api/posts/{postId}/comments", post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Редактирование комментария к посту (комментарий существует)")
    void testUpdate_Success() throws Exception {
        Long id = comment1.getId();
        String text = "Изменили комментарий 1";
        Long postId = post.getId();
        String json = String.format("""
            {
                "id": %d,
                "text": "%s",
                "postId": %d
            }
            """, id, text, postId);

        mockMvc.perform(put("/api/posts/{postId}/comments/{id}", postId, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.text").value(text))
                .andExpect(jsonPath("$.postId").value(postId));

        mockMvc.perform(get("/api/posts/{postId}/comments", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Редактирование комментария к посту (пост / комментарий не существует)")
    void testUpdate_NotFound() throws Exception {
        Long id = -1L;
        String text = "Тестовый комментарий";
        Long postId = post.getId();

        String json = String.format("""
            {
                "id": %d,
                "text": "%s",
                "postId": %d
            }
            """, id, text, postId);

        mockMvc.perform(put("/api/posts/{postId}/comments/{id}", postId, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(content().string(""));

        mockMvc.perform(get("/api/posts/{postId}/comments", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Удаление комментария")
    void testDeleteById() throws Exception {
        mockMvc.perform(delete("/api/posts/{postId}/comments/{id}", comment1.getPostId(), comment1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/posts/{postId}/comments", comment1.getPostId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
