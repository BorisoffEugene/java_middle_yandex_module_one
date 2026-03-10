package ru.yandex.practicum.integration.db;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import ru.yandex.practicum.configuration.DataSourceConfiguration;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.repository.JdbcCommentRepository;
import ru.yandex.practicum.repository.SQLRequests;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig({
        DataSourceConfiguration.class,
        JdbcCommentRepository.class
})
@TestPropertySource({
        "classpath:test_application.properties",
        "classpath:postgres_post.properties",
        "classpath:postgres_comment.properties"
})
public class JdbcCommentRepositoryTest {
    @Value("${post.deleteAll}")
    private String SQL_POST_DELETE_ALL;
    @Value("${post.save}")
    private String SQL_POST_SAVE;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void beforeEach() {
        // очищаем все данные
        jdbcTemplate.execute(SQL_POST_DELETE_ALL);

    }

    @Test
    void test() {
        assertEquals(2, 2);
    }
}
