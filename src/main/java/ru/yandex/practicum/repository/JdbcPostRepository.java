package ru.yandex.practicum.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.model.PostDTO;
import ru.yandex.practicum.model.PostList;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class JdbcPostRepository implements PostRepository{
    @Value("${post.findByParams}")
    private String SQL_POST_FIND_BY_PARAMS;
    @Value("${post.findPostsCount}")
    private String SQL_POST_FIND_POSTS_COUNT;
    @Value("${post.findById}")
    private String SQL_POST_FIND_BY_ID;
    @Value("${post.save}")
    private String SQL_POST_SAVE;
    @Value("${post.update}")
    private String SQL_POST_UPDATE;
    @Value("${post.deleteById}")
    private String SQL_POST_DELETE_BY_ID;
    @Value("${post.incLikesCount}")
    private String SQL_POST_INC_LIKES_COUNT;
    @Value("${post.updateImage}")
    private String SQL_POST_UPDATE_IMAGE;
    @Value("${post.findImageById}")
    private String SQL_POST_FIND_IMAGE_BY_ID;

    private final JdbcTemplate jdbcTemplate;

    public JdbcPostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Array convertTags(List<String> tags) {
        try {
            return jdbcTemplate.getDataSource().getConnection().createArrayOf("text", tags.toArray(new String[0]));
        } catch (SQLException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> convertTags(Array tags) {
        try {
            return Arrays.asList((String[])tags.getArray());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PostList findByParams(String titleSearch, List<String> tags, int pageNumber, int pageSize) {
        int postsCount = findPostsCount(titleSearch, tags);
        List<PostDTO> posts;

        if (postsCount > 0) {
            posts = jdbcTemplate.query(
                    SQL_POST_FIND_BY_PARAMS,
                    (rs, rowNum) -> new PostDTO(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("text"),
                            convertTags(rs.getArray("tags")),
                            rs.getInt("likes_count"),
                            rs.getInt("comments_count")
                    ),
                    titleSearch, convertTags(tags), convertTags(tags), pageSize, (pageNumber - 1) * pageSize
                );
        } else
            posts = new ArrayList<>();

        PostList postList = new PostList(posts);
        postList.setHasPrev(pageNumber > 1);
        postList.setLastPage(Math.ceilDiv(postsCount, pageSize));
        postList.setHasNext(pageNumber < postList.getLastPage());

        return postList;
    }

    @Override
    public int findPostsCount(String titleSearch, List<String> tags) {
        return jdbcTemplate.queryForObject(SQL_POST_FIND_POSTS_COUNT, Integer.class, titleSearch, convertTags(tags), convertTags(tags));
    }

    @Override
    public PostDTO findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(
                    SQL_POST_FIND_BY_ID,
                    (rs, rowNum) -> new PostDTO(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("text"),
                            convertTags(rs.getArray("tags")),
                            rs.getInt("likes_count"),
                            rs.getInt("comments_count")
                    ),
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public PostDTO save(PostDTO post) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(SQL_POST_SAVE, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, post.getTitle());
                    ps.setString(2, post.getText());
                    ps.setArray(3, convertTags(post.getTags()));
                    return ps;
                },
                keyHolder
        );

        if (keyHolder.getKeys() == null) return null;

        Long id = (Long)keyHolder.getKeys().getOrDefault("id", 0);
        return findById(id);
    }

    @Override
    public PostDTO update(PostDTO post) {
        jdbcTemplate.update(SQL_POST_UPDATE, post.getTitle(), post.getText(), convertTags(post.getTags()), post.getId());
        return findById(post.getId());
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(SQL_POST_DELETE_BY_ID, id);
    }

    @Override
    public int incLikesCount(Long id) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(SQL_POST_INC_LIKES_COUNT, Statement.RETURN_GENERATED_KEYS);
                    ps.setLong(1, id);
                    return ps;
                },
                keyHolder
        );

        if (keyHolder.getKeys() == null) return 0;

        return (int)keyHolder.getKeys().getOrDefault("likes_count", 0);
    }

    @Override
    public void updateImage(Long id, byte[] image) {
        jdbcTemplate.update(SQL_POST_UPDATE_IMAGE, image, id);
    }

    @Override
    public byte[] findImageById(Long id) {
        return jdbcTemplate.query(
                SQL_POST_FIND_IMAGE_BY_ID,
                preparedStatement -> preparedStatement.setLong(1, id),
                resultSet -> resultSet.next() ? resultSet.getBytes("image") : null
        );
    }
}
