package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;

import ru.yandex.practicum.model.PostDTO;
import ru.yandex.practicum.model.PostList;
import ru.yandex.practicum.repository.PostRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;

    private String parseSearch(String search, List<String> tags) {
        String[] arrSearch = search.split(" ");
        StringBuilder sb = new StringBuilder();

        for (String s : arrSearch) {
            s = s.trim();

            if (s.isEmpty())
                continue;
            else if (s.charAt(0) == '#')
                tags.add(s.substring(1));
            else if (sb.isEmpty())
                sb.append(s);
            else
                sb.append(" ").append(s);
        }

        return sb.toString();
    }

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostList findByParams(String search, int pageNumber, int pageSize) {
        List<String> tags = new ArrayList<>();
        String titleSearch = parseSearch(search, tags);
        pageNumber =Math.max(1, pageNumber);
        pageSize = Math.max(1, pageSize);

        return postRepository.findByParams(titleSearch, tags, pageNumber, pageSize);
    }

    public PostDTO findById(Long id) {
        return postRepository.findById(id);
    }

    public PostDTO save(PostDTO post) {
        return postRepository.save(post);
    }

    public PostDTO update(PostDTO post) {
        return postRepository.update(post);
    }

    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }

    public int incLikesCount(Long id) {
        return postRepository.incLikesCount(id);
    }

    public void uploadImage(Long id, byte[] image) {
        postRepository.updateImage(id, image);
    }

    public byte[] getImage(Long id) {
        return postRepository.findImageById(id);
    }

}
