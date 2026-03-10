package ru.yandex.practicum.modular;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void testFindByParams() {
        //return postRepository.findByParams(titleSearch, tags, pageNumber, pageSize);
    }

    @Test
    void testFindById() {
        //return postRepository.findById(id);
    }

    @Test
    void testSave() {
        //return postRepository.save(post);
    }

    @Test
    void testUpdate() {
        //return postRepository.update(post);
    }

    @Test
    void testDeleteById() {
        //postRepository.deleteById(id);
    }

    @Test
    void testIncLikesCount() {
        //return postRepository.incLikesCount(id);
    }

    @Test
    void testUploadImage() {
        //postRepository.updateImage(id, image);
    }

    @Test
    void testGetImage() {
        //return postRepository.findImageById(id);
    }
}
