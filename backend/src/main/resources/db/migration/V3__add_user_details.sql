ALTER TABLE users
    ADD bio VARCHAR(255) NULL;

ALTER TABLE users
    ADD phone VARCHAR(255) NULL;

ALTER TABLE users
    ADD profile_image VARCHAR(255) NULL;

CREATE INDEX idx_refresh_token ON refresh_token (token);