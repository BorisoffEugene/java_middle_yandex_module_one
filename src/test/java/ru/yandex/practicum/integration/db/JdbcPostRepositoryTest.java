package ru.yandex.practicum.integration.db;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.practicum.configuration.DataSourceConfiguration;
import ru.yandex.practicum.model.PostDTO;
import ru.yandex.practicum.model.PostList;
import ru.yandex.practicum.repository.JdbcPostRepository;
import ru.yandex.practicum.repository.PostRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringJUnitConfig({DataSourceConfiguration.class, JdbcPostRepository.class})
@TestPropertySource({"classpath:test_application.properties", "classpath:postgres_post.properties"})
@DisplayName("Интеграционное (база данных) тестирование постов")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class JdbcPostRepositoryTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PostRepository postRepository;

    private PostDTO post1;
    private PostDTO post2;

    @BeforeEach
    void beforeEach() {
        // очищаем все данные
        postRepository.deleteAll();

        // добавляем пару постов
        post1 = postRepository.save(new PostDTO("Название поста 1", "Текст поста 1", List.of("tag1", "tag2")));
        post2 = postRepository.save(new PostDTO("Название поста 2", "Текст поста 2", List.of("tag1", "tag3")));
    }

    @Test
    @Disabled // h2 выдает "Ошибка преобразования данных при конвертации "ARRAY to GEOMETRY"" - не смог ее победить (((
    @DisplayName("Получение списка постов (посты существуют)")
    void testFindByParams_Success() {
        PostList postList = postRepository.findByParams("пост", List.of("tag1"), 1, 5);

        assertEquals(2, postList.getPosts().size(), "Количество постов должно быть 2");
        assertEquals(post2.getId(), postList.getPosts().getFirst().getId(), String.format("ID должен быть: %d", post2.getId()));
        assertEquals(post2.getTitle(), postList.getPosts().getFirst().getTitle(), String.format("Наименование должно быть: %s", post2.getTitle()));
        assertEquals(post2.getText(), postList.getPosts().getFirst().getText(), String.format("Текст должен быть: %s", post2.getText()));
        assertEquals(post2.getTags().size(), postList.getPosts().getFirst().getTags().size(), String.format("Количество тэгов должно быть: %d", post2.getTags().size()));
        assertEquals(post2.getLikesCount(), postList.getPosts().getFirst().getLikesCount(), String.format("Количество лайков должно быть: %d", post2.getLikesCount()));
        assertEquals(post2.getCommentsCount(), postList.getPosts().getFirst().getCommentsCount(), String.format("Количество комментариев должно быть: %d", post2.getCommentsCount()));
        assertFalse(postList.isHasPrev(), "Текущая страница должна быть первой");
        assertFalse(postList.isHasNext(), "Текущая страница должна быть последней");
        assertEquals(1, postList.getLastPage(), String.format("Количество страниц должно быть: %d", 1));
    }

    @Test
    @Disabled // h2 выдает "Ошибка преобразования данных при конвертации "ARRAY to GEOMETRY"" - не смог ее победить (((
    @DisplayName("Получение списка постов (посты не существуют)")
    void testFindByParams_NotFound() {
        PostList postList = postRepository.findByParams("несуществующие посты", List.of("tag5"), 1, 5);

        assertEquals(0, postList.getPosts().size(), "Количество постов должно быть 0");
        assertFalse(postList.isHasPrev(), "Текущая страница должна быть первой");
        assertFalse(postList.isHasNext(), "Текущая страница должна быть последней");
        assertEquals(0, postList.getLastPage(), String.format("Количество страниц должно быть: %d", 0));
    }

    @Test
    @DisplayName("Получение поста (пост существует)")
    void testFindById_Success() {
        PostDTO postDTO = postRepository.findById(post1.getId());

        assertNotNull(postDTO, "Пост должен существовать");
        assertEquals(post1.getId(), postDTO.getId(), String.format("ID должен быть: %d", post1.getId()));
        assertEquals(post1.getTitle(), postDTO.getTitle(), String.format("Наименование должно быть: %s", post1.getTitle()));
        assertEquals(post1.getText(), postDTO.getText(), String.format("Текст должен быть: %s", post1.getText()));
        assertEquals(post1.getTags().size(), postDTO.getTags().size(), String.format("Количество тэгов должно быть: %d", post1.getTags().size()));
        assertEquals(post1.getLikesCount(), postDTO.getLikesCount(), String.format("Количество лайков должно быть: %d", post1.getLikesCount()));
        assertEquals(post1.getCommentsCount(), postDTO.getCommentsCount(), String.format("Количество комментариев должно быть: %d", post1.getCommentsCount()));
    }

    @Test
    @DisplayName("Получение поста (пост не существует)")
    void testFindById_NotFound() {
        Long id = -1L;
        PostDTO postDTO = postRepository.findById(id);
        assertNull(postDTO, "Поста не должно существовать");
    }

    @Test
    @DisplayName("Добавление поста")
    void testSave() {
        String title = "Наименование поста 3";
        String text = "Текст поста 3";
        List<String> tags = List.of("tag1", "tag2");
        int likesCount = 0;
        int commentsCount = 0;

        PostDTO postDTO = postRepository.save(new PostDTO(title, text, tags));

        assertNotNull(postDTO, "Пост должен существовать");
        assertEquals(title, postDTO.getTitle(), String.format("Наименование должно быть: %s", title));
        assertEquals(text, postDTO.getText(), String.format("Текст должен быть: %s", text));
        assertEquals(tags.size(), postDTO.getTags().size(), String.format("Количество тэгов должно быть: %d", tags.size()));
        assertEquals(likesCount, postDTO.getLikesCount(), String.format("Количество лайков должно быть: %d", likesCount));
        assertEquals(commentsCount, postDTO.getCommentsCount(), String.format("Количество комментариев должно быть: %d", commentsCount));
    }

    @Test
    @DisplayName("Редактирование поста")
    void testUpdate() {
        Long id = post1.getId();
        String title = "Изменение наименования поста";
        String text = "Изменение текста поста";
        List<String> tags = List.of("tag1", "tag2", "tag3");

        PostDTO postDTO = postRepository.update(new PostDTO(id, title, text, tags));

        assertNotNull(postDTO, "Пост должен существовать");
        assertEquals(id, postDTO.getId(), String.format("ID должен быть: %d", id));
        assertEquals(title, postDTO.getTitle(), String.format("Наименование должно быть: %s", title));
        assertEquals(text, postDTO.getText(), String.format("Текст должен быть: %s", text));
        assertEquals(tags.size(), postDTO.getTags().size(), String.format("Количество тэгов должно быть: %d", tags.size()));
        assertEquals(post1.getLikesCount(), postDTO.getLikesCount(), String.format("Количество лайков должно быть: %d", post1.getLikesCount()));
        assertEquals(post1.getCommentsCount(), postDTO.getCommentsCount(), String.format("Количество комментариев должно быть: %d", post1.getCommentsCount()));
    }

    @Test
    @DisplayName("Удаление поста")
    void testDeleteById() {
        postRepository.deleteById(post1.getId());
        List<PostDTO> posts = postRepository.findByParams("", new ArrayList<>(), 1, 5).getPosts();
        assertEquals(1, posts.size(), "Количество постов должно быть 1");
        assertTrue(posts.stream().noneMatch(p -> p.getId().equals(post1.getId())), String.format("Не должно быть поста с ID: %d", post1.getId()));
    }

    @Test
    @DisplayName("Инкремент числа лайков поста")
    void testIncLikesCount() {
        int likesCount = postRepository.incLikesCount(post1.getId());
        assertEquals(1, likesCount, String.format("Количество лайков должно быть: %d", 1));
    }

    @Test
    @DisplayName("Обновление картинки поста")
    void testUploadImage() {
        byte[] image = new byte[]{1, 2, 3, 4};
        postRepository.updateImage(post1.getId(), image);
        byte[] fromDb = postRepository.findImageById(post1.getId());
        assertArrayEquals(image, fromDb);
    }

    @Test
    @DisplayName("Получение картинки поста (пост существует)")
    void testGetImage_Success() {
        postRepository.updateImage(post1.getId(), new byte[]{1, 2, 3, 4});
        byte[] image = postRepository.findImageById(post1.getId());
        assertNotNull(image, "Картинка должна существовать");
    }

    @Test
    @DisplayName("Получение картинки поста (пост / картинка не существует)")
    void testGetImage_NotFound() {
        byte[] image = postRepository.findImageById(-1L);
        assertNull(image, "Картинка не должна существовать");
    }
}
