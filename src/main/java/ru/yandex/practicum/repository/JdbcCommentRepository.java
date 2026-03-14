package ru.yandex.practicum.repository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
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
    private final JdbcTemplate jdbcTemplate;

    public JdbcCommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<CommentDTO> findAll(Long postId) {
        return jdbcTemplate.query(
                "select c.id, c.text, c.post_id from blog.comments c where c.post_id = ? order by c.id desc",
                (rs, rowNum) -> new CommentDTO(
                        rs.getLong("id"),
                        rs.getString("text"),
                        rs.getLong("post_id")
                ),
                postId
        );
    }

    @Override
    public CommentDTO findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(
                    "select c.id, c.text, c.post_id from blog.comments c where c.id = ?",
                    (rs, rowNum) -> new CommentDTO(
                            rs.getLong("id"),
                            rs.getString("text"),
                            rs.getLong("post_id")
                    ),
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public CommentDTO save(CommentDTO comment) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement("insert into blog.comments (text, post_id) values(?, ?)", Statement.RETURN_GENERATED_KEYS);
                        ps.setString(1, comment.getText());
                        ps.setLong(2, comment.getPostId());
                        return ps;
                    },
                    keyHolder
            );
        } catch (DataIntegrityViolationException e) {
            return null;
        }

        if (keyHolder.getKeys() == null) return null;

        comment.setId((Long)keyHolder.getKeys().get("id"));
        return comment;
    }

    @Override
    public CommentDTO update(CommentDTO comment) {
        int rowsNumber = jdbcTemplate.update("update blog.comments set text = ? where id = ?", comment.getText(), comment.getId());
        return rowsNumber == 0 ? null : comment;
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("delete from blog.comments where id = ?", id);
    }
}
