-- Roles
INSERT INTO roles (role_name)
VALUES ('ROLE_USER')
    ON CONFLICT (role_name) DO NOTHING;

INSERT INTO roles (role_name)
VALUES ('ROLE_ADMIN')
    ON CONFLICT (role_name) DO NOTHING;


-- Genres
INSERT INTO genres (genre_name)
VALUES
    ('Fantasy'),
    ('Science Fiction'),
    ('Mystery'),
    ('Thriller'),
    ('Romance'),
    ('Horror'),
    ('Historical Fiction'),
    ('Non-Fiction'),
    ('Biography'),
    ('Young Adult'),
    ('Dystopian'),
    ('Adventure'),
    ('Crime'),
    ('Drama'),
    ('Self-Help')
    ON CONFLICT (genre_name) DO NOTHING;