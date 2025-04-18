CREATE TYPE period AS ENUM ('DAILY', 'WEEKLY', 'MONTHLY', 'ALL_TIME');
CREATE TYPE value_type AS ENUM ('SCORE', 'REVIEW_SCORE_SUM', 'LIKE_COUNT', 'COMMENT_COUNT');
CREATE TYPE key_type AS ENUM ('USER', 'BOOK', 'REVIEW');

CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP           NOT NULL,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN             NOT NULL DEFAULT FALSE,
    email      VARCHAR(255) UNIQUE NOT NULL,
    nickname   VARCHAR(20)         NOT NULL,
    password   VARCHAR(20)         NOT NULL
);

CREATE TABLE books
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMP     NOT NULL,
    updated_at   TIMESTAMP,
    is_deleted   BOOLEAN       NOT NULL DEFAULT FALSE,
    title        VARCHAR(255)  NOT NULL,
    author       VARCHAR(255)  NOT NULL,
    description  TEXT          NOT NULL,
    publisher    VARCHAR(255)  NOT NULL,
    publish_date DATE          NOT NULL,
    isbn         VARCHAR(255) UNIQUE,
    thumbnailUrl TEXT,
    review_count INTEGER       NOT NULL DEFAULT 0,
    rating       DECIMAL(2, 1) NOT NULL
);

CREATE TABLE reviews
(
    id            UUID PRIMARY KEY,
    created_at    TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP,
    is_deleted    BOOLEAN   NOT NULL DEFAULT FALSE,
    user_id       UUID      NOT NULL,
    book_id       UUID      NOT NULL,
    content       TEXT      NOT NULL,
    rating        INTEGER   NOT NULL,
    like_count    INTEGER   NOT NULL,
    comment_count INTEGER   NOT NULL,
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES
        users (id) ON DELETE CASCADE,
    CONSTRAINT fk_review_book FOREIGN KEY (book_id) REFERENCES
        books (id) ON DELETE CASCADE
);

CREATE TABLE comments
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN   NOT NULL DEFAULT FALSE,
    user_id    UUID      NOT NULL,
    review_id  UUID      NOT NULL,
    content    TEXT      NOT NULL,
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES
        users (id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_review FOREIGN KEY (review_id) REFERENCES
        reviews (id) ON DELETE CASCADE
);

CREATE TABLE notifications
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP,
    user_id    UUID         NOT NULL,
    review_id  UUID         NOT NULL,
    content    VARCHAR(255) NOT NULL,
    confirmed  BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE likes
(
    id         UUID PRIMARY KEY,
    user_id    UUID NOT NULL,
    review_id  UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_DATE,
    CONSTRAINT unique_review_like UNIQUE (user_id, review_id)
);

CREATE TABLE dashboards
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP     NOT NULL,
    updated_at TIMESTAMP,
    key        UUID          NOT NULL,
    key_type   key_type      NOT NULL,
    period     period       NOT NULL,
    value      DECIMAL(5, 2) NOT NULL,
    value_type value_type    NOT NULL
);

CREATE INDEX idx_books_is_deleted_title ON books (is_deleted, title);
CREATE INDEX idx_reviews_is_deleted_created_at ON reviews (is_deleted, created_at);
CREATE INDEX idx_reviews_is_deleted_rating ON reviews (is_deleted, rating);