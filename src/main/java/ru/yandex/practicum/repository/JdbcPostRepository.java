package ru.yandex.practicum.repository;

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
    private final JdbcTemplate jdbcTemplate;

    private JdbcPostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Array convertTags(List<String> tags) {
        try {
            return jdbcTemplate.getDataSource().getConnection().createArrayOf("varchar", tags.toArray(new String[0]));
        } catch (SQLException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> convertTags(Array tags) {
        Object[] objArray;

        try {
            objArray = (Object[])tags.getArray();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String[] strArray = Arrays.copyOf(objArray, objArray.length, String[].class);
        return Arrays.asList(strArray);
    }

    @Override
    public PostList findByParams(String titleSearch, List<String> tags, int pageNumber, int pageSize) {
        int postsCount = findPostsCount(titleSearch, tags);
        List<PostDTO> posts;

        if (postsCount > 0) {
            posts = jdbcTemplate.query(
                    "select p.id, p.title, case when length(p.text) > 128 then substr(p.text, 1, 128)||'...' else p.text end text, p.tags, p.likes_count, count(c.id) comments_count from blog.posts p left join blog.comments c on c.post_id = p.id where p.title ilike '%'||?||'%' and (cardinality(?) = 0 or p.tags && ?) group by p.id, p.title, p.text, p.tags, p.likes_count order by id desc limit ? offset ?",
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
        return jdbcTemplate.queryForObject(
                "select count(p.id) posts_count from blog.posts p where p.title ilike '%'||?||'%' and (cardinality(?) = 0 or p.tags && ?)",
                Integer.class,
                titleSearch, convertTags(tags), convertTags(tags)
        );
    }

    @Override
    public PostDTO findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(
                    "select p.id, p.title, p.text, p.tags, p.likes_count, count(c.id) comments_count from blog.posts p left join blog.comments c on c.post_id = p.id where p.id = ? group by p.id, p.title, p.text, p.tags, p.likes_count",
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
                    PreparedStatement ps = connection.prepareStatement("insert into blog.posts (title, text, tags) values (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
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
        jdbcTemplate.update("update blog.posts set title = ?, text = ?, tags = ? where id = ?", post.getTitle(), post.getText(), convertTags(post.getTags()), post.getId());
        return findById(post.getId());
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("delete from blog.posts where id = ?", id);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.execute("delete from blog.posts");
    }

    @Override
    public int incLikesCount(Long id) {
        jdbcTemplate.update("update blog.posts set likes_count = likes_count + 1 where id = ?", id);
        return findById(id).getLikesCount();
    }

    @Override
    public void updateImage(Long id, byte[] image) {
        jdbcTemplate.update("update blog.posts set image = ? where id = ?", image, id);
    }

    @Override
    public byte[] findImageById(Long id) {
        return jdbcTemplate.query(
                "select p.image from blog.posts p where p.id = ?",
                preparedStatement -> preparedStatement.setLong(1, id),
                resultSet -> resultSet.next() ? resultSet.getBytes("image") : null
        );
    }
}
