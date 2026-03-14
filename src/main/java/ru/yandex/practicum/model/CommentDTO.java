package ru.yandex.practicum.model;

public class CommentDTO {
    private Long id;
    private String text;
    private Long postId;

    public CommentDTO() {
    }

    public CommentDTO(Long id, String text, Long postId) {
        this.id = id;
        this.text = text;
        this.postId = postId;
    }

    public CommentDTO(String text, Long postId) {
        this.id = 0L;
        this.text = text;
        this.postId = postId;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Long getPostId() {
        return postId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
}
