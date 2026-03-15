package ru.yandex.practicum.integration.web;

import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ru.yandex.practicum.model.PostDTO;
import ru.yandex.practicum.repository.PostRepository;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Disabled
@DisplayName("Интеграционное (веб) тестирование постов")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class PostControllerTest {
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private PostRepository postRepository;

    private MockMvc mockMvc;
    private PostDTO post1;
    private PostDTO post2;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        // очищаем все данные
        postRepository.deleteAll();

        // добавляем пару постов
        post1 = postRepository.save(new PostDTO("Название поста 1", "Текст поста 1", List.of("tag1", "tag2")));
        post2 = postRepository.save(new PostDTO("Название поста 2", "Текст поста 2", List.of("tag1", "tag3")));
    }

    @Test
    @DisplayName("Получение списка постов (посты существуют)")
    void testFindByParams_Success() throws Exception {
        mockMvc.perform(get("/posts?search=пост&pageNumber=1&pageSize=5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.posts", hasSize(2)))
                .andExpect(jsonPath("$.posts[0].id").value(post2.getId()))
                .andExpect(jsonPath("$.posts[0].title").value(post2.getTitle()))
                .andExpect(jsonPath("$.posts[0].text").value(post2.getText()))
                .andExpect(jsonPath("$.posts[0].tags", hasSize(post2.getTags().size())))
                .andExpect(jsonPath("$.posts[0].likesCount").value(post2.getLikesCount()))
                .andExpect(jsonPath("$.posts[0].commentsCount").value(post2.getCommentsCount()))
                .andExpect(jsonPath("$.hasPrev").value(false))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.lastPage").value(1));
    }

    @Test
    @DisplayName("Получение списка постов (посты не существуют)")
    void testFindByParams_NotFound() throws Exception {
        mockMvc.perform(get("/posts?search=несуществующий пост&pageNumber=1&pageSize=5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.posts", hasSize(0)));
    }

    @Test
    @DisplayName("Получение поста (пост существует)")
    void testFindById_Success() throws Exception {
        mockMvc.perform(get("/posts/{id}", post1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(post1.getId()))
                .andExpect(jsonPath("$.title").value(post1.getTitle()))
                .andExpect(jsonPath("$.text").value(post1.getText()))
                .andExpect(jsonPath("$.tags", hasSize(post1.getTags().size())))
                .andExpect(jsonPath("$.likesCount").value(post1.getLikesCount()))
                .andExpect(jsonPath("$.commentsCount").value(post1.getCommentsCount()));
    }

    @Test
    @DisplayName("Получение поста (пост не существует)")
    void testFindById_NotFound() throws Exception {
        mockMvc.perform(get("/posts/{id}", -1L))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Добавление поста")
    void testSave() throws Exception {
        String title = "Наименование поста 3";
        String text = "Текст поста 3";
        List<String> tags = List.of("tag1", "tag2");
        int likesCount = 0;
        int commentsCount = 0;


        String json = String.format("""
                {
                    "title": "%s",
                    "text": "%s",
                    "tags": ["%s", "%s"]
                }
                """, title, text, tags.getFirst(), tags.getLast());

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.text").value(text))
                .andExpect(jsonPath("$.tags", hasSize(tags.size())))
                .andExpect(jsonPath("$.likesCount").value(likesCount))
                .andExpect(jsonPath("$.commentsCount").value(commentsCount));

        mockMvc.perform(get("/posts?search=&pageNumber=1&pageSize=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts", hasSize(3)));
    }

    @Test
    @DisplayName("Редактирование поста")
    void testUpdate() throws Exception {
        Long id = post1.getId();
        String title = "Изменение наименования поста";
        String text = "Изменение текста поста";
        List<String> tags = List.of("tag1", "tag2");

        String json = String.format("""
                {
                    "id": %d,
                    "title": "%s",
                    "text": "%s",
                    "tags": ["%s", "%s"]
                }
                """, id, title, text, tags.getFirst(), tags.getLast());

        mockMvc.perform(put("/posts/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.text").value(text))
                .andExpect(jsonPath("$.tags", hasSize(tags.size())))
                .andExpect(jsonPath("$.likesCount").value(post1.getLikesCount()))
                .andExpect(jsonPath("$.commentsCount").value(post1.getCommentsCount()));

        mockMvc.perform(get("/posts?search=&pageNumber=1&pageSize=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts", hasSize(2)));
    }

    @Test
    @DisplayName("Удаление поста")
    void testDeleteById() throws Exception {
        mockMvc.perform(delete("/posts/{id}", post1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/posts?search=&pageNumber=1&pageSize=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts", hasSize(1)));
    }

    @Test
    @DisplayName("Инкремент числа лайков поста")
    void testIncLikesCount() throws Exception {
        mockMvc.perform(post("/posts/{id}/likes", post1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    @DisplayName("Обновление / Получение картинки поста (пост существует)")
    void testGetImage_Success() throws Exception {
        byte[] image = new byte[]{(byte) 137, 80, 78, 71};
        MockMultipartFile file = new MockMultipartFile("image", "image.png", MediaType.IMAGE_PNG_VALUE, image);

        RequestPostProcessor putMethodProcessor = request -> {
            request.setMethod("PUT");
            return request;
        };

        mockMvc.perform(multipart("/posts/{id}/image", post1.getId())
                        .file(file)
                        .with(putMethodProcessor)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk());

        mockMvc.perform(get("/posts/{id}/image", post1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().bytes(image));
    }

    @Test
    @DisplayName("Получение картинки поста (пост / картинка не существует)")
    void testGetImage_NotFound() throws Exception {
        mockMvc.perform(get("/posts/{id}/image", -1L))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }
}
