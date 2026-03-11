package ru.yandex.practicum.model;

import java.util.List;

public class PostList {
    private List<PostDTO> posts;
    private boolean hasPrev;
    private boolean hasNext;
    private int lastPage;

    public PostList(List<PostDTO> posts) {
        this.posts = posts;
    }

    public List<PostDTO> getPosts() {
        return posts;
    }

    public boolean isHasPrev() {
        return hasPrev;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setPosts(List<PostDTO> posts) {
        this.posts = posts;
    }

    public void setHasPrev(boolean hasPrev) {
        this.hasPrev = hasPrev;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }
}
