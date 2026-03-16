-- схема
create schema if not exists blog;

-- таблица posts
create table if not exists blog.posts(
	id bigserial primary key,
    title varchar not null,
    text varchar not null,
    tags varchar array,
    likes_count integer not null default 0,
    image bytea
);

comment on table blog.posts is 'Информация о постах блога';
comment on column blog.posts.id is 'ID записи';
comment on column blog.posts.title is 'Название';
comment on column blog.posts.text is 'Текст';
comment on column blog.posts.tags is 'Тэги';
comment on column blog.posts.likes_count is 'Количество лайков';
comment on column blog.posts.image is 'Картинка';

create index if not exists idx_posts_title on blog.posts (title);
create index if not exists idx_posts_tags on blog.posts (tags);

-- таблица comments
create table if not exists blog.comments(
	id bigserial primary key,
    text varchar not null,
	post_id bigint not null references blog.posts(id) on delete cascade
);

comment on table blog.comments is 'Информация о комментариях к постам блога';
comment on column blog.comments.id is 'ID записи';
comment on column blog.comments.text is 'Текст';
comment on column blog.comments.post_id is 'ID поста (blog.posts.id)';

create index if not exists idx_comments_post_id on blog.comments (post_id);