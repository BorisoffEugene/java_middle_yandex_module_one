package ru.yandex.practicum.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({
        "classpath:postgres_comment.properties",
        "classpath:postgres_post.properties"
})
public class PropertySourceConfiguration {}
