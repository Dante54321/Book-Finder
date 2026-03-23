CREATE TABLE reading_progress (
                                  reading_progress_id BIGSERIAL PRIMARY KEY,
                                  user_id BIGINT NOT NULL,
                                  book_id BIGINT NOT NULL,
                                  status VARCHAR(50) NOT NULL,
                                  started_at TIMESTAMP NOT NULL,
                                  last_read_at TIMESTAMP NOT NULL,

                                  CONSTRAINT fk_reading_progress_user
                                      FOREIGN KEY (user_id) REFERENCES users(user_id),

                                  CONSTRAINT fk_reading_progress_book
                                      FOREIGN KEY (book_id) REFERENCES books(book_id),

                                  CONSTRAINT uk_reading_progress_user_book
                                      UNIQUE (user_id, book_id)
);

CREATE INDEX idx_reading_progress_user_status
    ON reading_progress(user_id, status);

CREATE INDEX idx_reading_progress_last_read_at
    ON reading_progress(last_read_at);