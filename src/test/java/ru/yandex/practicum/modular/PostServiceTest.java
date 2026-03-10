package ru.yandex.practicum.modular;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ru.yandex.practicum.model.CommentDTO;
import ru.yandex.practicum.model.PostDTO;
import ru.yandex.practicum.model.PostList;
import ru.yandex.practicum.repository.PostRepository;
import ru.yandex.practicum.service.PostService;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульное тестирование постов")
public class PostServiceTest {
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("Получение списка постов (посты существуют)")
    void testFindByParams_Success() {
        int postsCount = 2;
        int pageNumber = 1;
        int pageSize = 5;
        String titleSearch = "пост";
        List<String> tags = List.of("tag1");

        List<PostDTO> posts = List.of(
            new PostDTO(1L, "Наименование поста 1", "Текст поста 1", List.of("tag1", "tag2"), 0, 0),
            new PostDTO(2L, "Наименование поста 2", "Текст поста 2", List.of("tag1", "tag3"), 0, 0)
        );

        PostList mockPostList = new PostList(posts);
        mockPostList.setHasPrev(pageNumber > 1);
        mockPostList.setLastPage(Math.ceilDiv(postsCount, pageSize));
        mockPostList.setHasNext(pageNumber < mockPostList.getLastPage());

        when(postRepository.findByParams(titleSearch, tags, pageNumber, pageSize)).thenReturn(mockPostList);
        PostList postList = postRepository.findByParams(titleSearch, tags, pageNumber, pageSize);

        assertEquals(2, postList.getPosts().size(), "Количество постов должно быть 2");
        assertEquals(1L, postList.getPosts().getFirst().getId(), String.format("ID должен быть: %d", 1L));
        assertEquals("Наименование поста 1", postList.getPosts().getFirst().getTitle(), String.format("Наименование должно быть: %s", "Наименование поста 1"));
        assertEquals("Текст поста 1", postList.getPosts().getFirst().getText(), String.format("Текст должен быть: %s", "Текст поста 1"));
        assertEquals(2, postList.getPosts().getFirst().getTags().size(), String.format("Количество тэгов должно быть: %d", 2));
        assertEquals(0, postList.getPosts().getFirst().getLikesCount(), String.format("Количество лайков должно быть: %d", 0));
        assertEquals(0, postList.getPosts().getFirst().getCommentsCount(), String.format("Количество комментариев должно быть: %d", 0));
        assertFalse(postList.isHasPrev(), "Текущая страница должна быть первой");
        assertFalse(postList.isHasNext(), "Текущая страница должна быть последней");
        assertEquals(1, postList.getLastPage(), String.format("Количество страниц должно быть: %d", 1));
    }

    @Test
    @DisplayName("Получение списка постов (посты не существуют)")
    void testFindByParams_NotFound() {
        int postsCount = 0;
        int pageNumber = 1;
        int pageSize = 5;
        String titleSearch = "несуществующие посты";
        List<String> tags = List.of("tag5");

        List<PostDTO> posts = new ArrayList<>();

        PostList mockPostList = new PostList(posts);
        mockPostList.setHasPrev(pageNumber > 1);
        mockPostList.setLastPage(Math.ceilDiv(postsCount, pageSize));
        mockPostList.setHasNext(pageNumber < mockPostList.getLastPage());

        when(postRepository.findByParams(titleSearch, tags, pageNumber, pageSize)).thenReturn(mockPostList);
        PostList postList = postRepository.findByParams(titleSearch, tags, pageNumber, pageSize);

        assertEquals(0, postList.getPosts().size(), "Количество постов должно быть 0");
        assertFalse(postList.isHasPrev(), "Текущая страница должна быть первой");
        assertFalse(postList.isHasNext(), "Текущая страница должна быть последней");
        assertEquals(0, postList.getLastPage(), String.format("Количество страниц должно быть: %d", 0));
    }

    @Test
    @DisplayName("Получение поста (пост существует)")
    void testFindById_Success() {
        Long id = 1L;
        String title = "Наименование поста";
        String text = "Текст поста";
        List<String> tags = List.of("tag1", "tag2");
        int likesCount = 0;
        int commentsCount = 0;

        when(postRepository.findById(id)).thenReturn(new PostDTO(id, title, text, tags, likesCount, commentsCount));
        PostDTO postDTO = postRepository.findById(id);

        assertNotNull(postDTO, "Пост должен существовать");
        assertEquals(id, postDTO.getId(), String.format("ID должен быть: %d", id));
        assertEquals(title, postDTO.getTitle(), String.format("Наименование должно быть: %s", title));
        assertEquals(text, postDTO.getText(), String.format("Текст должен быть: %s", text));
        assertEquals(tags.size(), postDTO.getTags().size(), String.format("Количество тэгов должно быть: %d", tags.size()));
        assertEquals(likesCount, postDTO.getLikesCount(), String.format("Количество лайков должно быть: %d", likesCount));
        assertEquals(commentsCount, postDTO.getCommentsCount(), String.format("Количество комментариев должно быть: %d", commentsCount));
    }

    @Test
    @DisplayName("Получение поста (пост не существует)")
    void testFindById_NotFound() {
        Long id = -1L;
        when(postRepository.findById(id)).thenReturn(null);
        PostDTO postDTO = postRepository.findById(id);
        assertNull(postDTO, "Поста не должно существовать");
    }

    @Test
    @DisplayName("Добавление поста")
    void testSave() {
        Long id = 1L;
        String title = "Наименование поста";
        String text = "Текст поста";
        List<String> tags = List.of("tag1", "tag2");
        int likesCount = 0;
        int commentsCount = 0;

        when(postRepository.save(any(PostDTO.class))).thenReturn(new PostDTO(id, title, text, tags, likesCount, commentsCount));
        PostDTO postDTO = postRepository.save(new PostDTO(id, title, text, tags, likesCount, commentsCount));

        assertNotNull(postDTO, "Пост должен существовать");
        assertEquals(id, postDTO.getId(), String.format("ID должен быть: %d", id));
        assertEquals(title, postDTO.getTitle(), String.format("Наименование должно быть: %s", title));
        assertEquals(text, postDTO.getText(), String.format("Текст должен быть: %s", text));
        assertEquals(tags.size(), postDTO.getTags().size(), String.format("Количество тэгов должно быть: %d", tags.size()));
        assertEquals(likesCount, postDTO.getLikesCount(), String.format("Количество лайков должно быть: %d", likesCount));
        assertEquals(commentsCount, postDTO.getCommentsCount(), String.format("Количество комментариев должно быть: %d", commentsCount));
    }

    @Test
    @DisplayName("Редактирование поста")
    void testUpdate() {
        Long id = 1L;
        String title = "Наименование поста";
        String text = "Текст поста";
        List<String> tags = List.of("tag1", "tag2");
        int likesCount = 0;
        int commentsCount = 0;

        when(postRepository.update(any(PostDTO.class))).thenReturn(new PostDTO(id, title, text, tags, likesCount, commentsCount));
        PostDTO postDTO = postRepository.update(new PostDTO(id, title, text, tags, likesCount, commentsCount));

        assertNotNull(postDTO, "Пост должен существовать");
        assertEquals(id, postDTO.getId(), String.format("ID должен быть: %d", id));
        assertEquals(title, postDTO.getTitle(), String.format("Наименование должно быть: %s", title));
        assertEquals(text, postDTO.getText(), String.format("Текст должен быть: %s", text));
        assertEquals(tags.size(), postDTO.getTags().size(), String.format("Количество тэгов должно быть: %d", tags.size()));
        assertEquals(likesCount, postDTO.getLikesCount(), String.format("Количество лайков должно быть: %d", likesCount));
        assertEquals(commentsCount, postDTO.getCommentsCount(), String.format("Количество комментариев должно быть: %d", commentsCount));
    }

    @Test
    @DisplayName("Удаление поста")
    void testDeleteById() {
        Long id = 1L;
        doNothing().when(postRepository).deleteById(id);
        postService.deleteById(id);
        verify(postRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Инкремент числа лайков поста")
    void testIncLikesCount() {
        Long id = 1L;
        int mockLikesCount = 1;
        when(postRepository.incLikesCount(id)).thenReturn(mockLikesCount);
        int likesCount = postService.incLikesCount(id);
        assertEquals(mockLikesCount, likesCount, String.format("Количество лайков должно быть: %d", mockLikesCount));
    }

    @Test
    @DisplayName("Обновление картинки поста")
    void testUploadImage() {
        Long id = 1L;
        byte[] image = new byte[10];
        doNothing().when(postRepository).updateImage(id, image);
        postService.uploadImage(id, image);
        verify(postRepository, times(1)).updateImage(id, image);
    }

    @Test
    @DisplayName("Получение картинки поста (пост существует)")
    void testGetImage_Success() {
        Long id = 1L;
        when(postRepository.findImageById(id)).thenReturn(new byte[10]);
        byte[] image = postRepository.findImageById(id);
        assertNotNull(image, "Картинка должна существовать");
    }

    @Test
    @DisplayName("Получение картинки поста (пост / картинка не существует)")
    void testGetImage_NotFound() {
        Long id = -1L;
        when(postRepository.findImageById(id)).thenReturn(null);
        byte[] image = postRepository.findImageById(id);
        assertNull(image, "Картинка не должна существовать");
    }
}
