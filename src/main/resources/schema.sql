-- Удаляем таблицы, если они существуют, с каскадным удалением всех связей
DROP TABLE IF EXISTS rating_user CASCADE;
DROP TABLE IF EXISTS rating CASCADE;
DROP TABLE IF EXISTS quote CASCADE;
DROP TABLE IF EXISTS usr CASCADE;

-- Удаляем последовательности, если они существуют
DROP SEQUENCE IF EXISTS rating_seq;
DROP SEQUENCE IF EXISTS quote_seq;
DROP SEQUENCE IF EXISTS usr_seq;

-- Создаем последовательности для автоинкремента
CREATE SEQUENCE quote_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE rating_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE usr_seq START WITH 1 INCREMENT BY 50;

-- Создаем таблицу для цитат
CREATE TABLE quote (
                       id BIGINT NOT NULL,
                       text VARCHAR(255),
                       author_id BIGINT,
                       creation_date_time TIMESTAMP,
                       last_update_time TIMESTAMP,
                       rating BIGINT,
                       PRIMARY KEY (id)
);


CREATE TABLE rating (
                        id BIGINT NOT NULL,
                        quote_id BIGINT,
                        rating BIGINT,
                        update_time TIMESTAMP,
                        PRIMARY KEY (id),
                        CONSTRAINT FK_rating_quote FOREIGN KEY (quote_id) REFERENCES quote(id) ON DELETE CASCADE
);

CREATE TABLE usr (
                     id BIGINT NOT NULL,
                     username VARCHAR(255),
                     password VARCHAR(255),
                     email VARCHAR(255),
                     creation_date_time TIMESTAMP,
                     PRIMARY KEY (id)
);

CREATE TABLE rating_user (
                             rating_id BIGINT NOT NULL,
                             user_id BIGINT NOT NULL,
                             PRIMARY KEY (rating_id, user_id),
                             CONSTRAINT FK_rating_user_rating FOREIGN KEY (rating_id) REFERENCES rating(id) ON DELETE CASCADE,
                             CONSTRAINT FK_rating_user_user FOREIGN KEY (user_id) REFERENCES usr(id) ON DELETE CASCADE
);


-- ALTER TABLE quote
--     ADD CONSTRAINT FK_quote_author FOREIGN KEY (author_id) REFERENCES usr(id);
--

-- ALTER TABLE rating
--     ADD CONSTRAINT FK_rating_quote FOREIGN KEY (quote_id) REFERENCES quote(id) ON DELETE CASCADE;
