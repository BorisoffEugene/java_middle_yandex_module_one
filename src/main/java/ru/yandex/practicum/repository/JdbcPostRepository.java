package ru.yandex.practicum.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.model.PostDTO;
import ru.yandex.practicum.model.PostList;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class JdbcPostRepository implements PostRepository{
    @Value("${post.findByParams}")
    private String SQL_POST_FIND_BY_PARAMS;
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

    @Override
    public PostList findByParams(String search, int pageNumber, int pageSize) {
        return null; //todo
    }

    @Override
    public PostDTO findById(Long id) {
        return jdbcTemplate.queryForObject(
                SQL_POST_FIND_BY_ID,
                (rs, rowNum) -> new PostDTO(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("text"),
                        (List<String>) rs.getArray("tags"),
                        rs.getInt("likes_count"),
                        rs.getInt("comments_count")
                ),
                id
        );
    }

    @Override
    public PostDTO save(PostDTO post) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(SQL_POST_SAVE, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, post.getTitle());
                    ps.setString(2, post.getText());
                    ps.setArray(3, connection.createArrayOf("VARCHAR", post.getTags().toArray(new String[0])));
                    return ps;
                },
                keyHolder
        );

        Long id = (Long)keyHolder.getKeys().get("id");
        return findById(id);
    }

    @Override
    public PostDTO update(PostDTO post) {
        jdbcTemplate.update(SQL_POST_UPDATE, post.getTitle(), post.getText(), post.getTags(), post.getId());
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

        return (int)keyHolder.getKeys().get("likes_count");
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
