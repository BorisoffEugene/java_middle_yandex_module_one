package ru.yandex.practicum.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.model.CommentDTO;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class JdbcCommentRepository implements CommentRepository{
    @Value("${comment.findAll}")
    private String SQL_COMMENT_FIND_ALL;
    @Value("${comment.findById}")
    private String SQL_COMMENT_FIND_BY_ID;
    @Value("${comment.save}")
    private String SQL_COMMENT_SAVE;
    @Value("${comment.update}")
    private String SQL_COMMENT_UPDATE;
    @Value("${comment.deleteById}")
    private String SQL_COMMENT_DELETE_BY_ID;

    private final JdbcTemplate jdbcTemplate;

    public JdbcCommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<CommentDTO> findAll(Long postId) {
        return jdbcTemplate.query(
                SQL_COMMENT_FIND_ALL,
                (rs, rowNum) -> new CommentDTO(
                        rs.getLong("id"),
                        rs.getString("text"),
                        rs.getLong("post_id")
                ),
                postId
        );
    }

    @Override
    public CommentDTO findById(Long postId, Long id) {
        return jdbcTemplate.queryForObject(
                SQL_COMMENT_FIND_BY_ID,
                (rs, rowNum) -> new CommentDTO(
                        rs.getLong("id"),
                        rs.getString("text"),
                        rs.getLong("post_id")
                ),
                id
        );
    }

    @Override
    public CommentDTO save(CommentDTO comment) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(SQL_COMMENT_SAVE, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, comment.getText());
                    ps.setLong(2, comment.getPostId());
                    return ps;
                },
                keyHolder
        );

        comment.setId((Long)keyHolder.getKeys().get("id"));
        return comment;
    }

    @Override
    public CommentDTO update(CommentDTO comment) {
        jdbcTemplate.update(SQL_COMMENT_UPDATE, comment.getText(), comment.getId());
        return comment;
    }

    @Override
    public void deleteById(Long postId, Long id) {
        jdbcTemplate.update(SQL_COMMENT_DELETE_BY_ID, id);
    }
}
