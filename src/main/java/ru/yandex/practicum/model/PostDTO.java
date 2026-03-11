package ru.yandex.practicum.model;

import java.util.List;

public class PostDTO {
    private Long id;
    private String title;
    private String text;
    private List<String> tags;
    private int likesCount;
    private int commentsCount;

    public PostDTO() {
    }

    public PostDTO(Long id, String title, String text, List<String> tags, int likesCount, int commentsCount) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.tags = tags;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
    }

    public PostDTO(String title, String text, List<String> tags) {
        this.id = 0L;
        this.title = title;
        this.text = text;
        this.tags = tags;
        this.likesCount = 0;
        this.commentsCount = 0;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public List<String> getTags() {
        return tags;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }
}
