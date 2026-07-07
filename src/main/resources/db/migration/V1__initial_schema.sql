CREATE TABLE password_reset_token
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    token       VARCHAR(255)          NOT NULL,
    expiry_date datetime              NOT NULL,
    created_at  datetime              NOT NULL,
    user_id     BIGINT                NULL,
    CONSTRAINT pk_password_reset_token PRIMARY KEY (id)
);

CREATE TABLE refresh_token
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    token       VARCHAR(255)          NOT NULL,
    expiry_date datetime              NOT NULL,
    created_at  datetime              NULL,
    revoked     BIT(1)                NOT NULL,
    user_id     BIGINT                NOT NULL,
    CONSTRAINT pk_refresh_token PRIMARY KEY (id)
);

CREATE TABLE users
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    name            VARCHAR(255)          NOT NULL,
    email           VARCHAR(255)          NOT NULL,
    password        VARCHAR(255)          NOT NULL,
    `role`          VARCHAR(255)          NOT NULL,
    enabled         BIT(1)                NOT NULL,
    account_locked  BIT(1)                NOT NULL,
    failed_attempts INT                   NOT NULL,
    created_at      datetime              NOT NULL,
    updated_at      datetime              NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE verification_token
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    token       VARCHAR(255)          NOT NULL,
    expiry_date datetime              NOT NULL,
    created_at  datetime              NOT NULL,
    user_id     BIGINT                NULL,
    CONSTRAINT pk_verification_token PRIMARY KEY (id)
);

ALTER TABLE password_reset_token
    ADD CONSTRAINT UK_password_reset_token UNIQUE (token);

ALTER TABLE password_reset_token
    ADD CONSTRAINT UK_password_reset_token_user UNIQUE (user_id);

ALTER TABLE refresh_token
    ADD CONSTRAINT UK_refresh_token UNIQUE (token);

ALTER TABLE users
    ADD CONSTRAINT UK_users_email UNIQUE (email);

ALTER TABLE verification_token
    ADD CONSTRAINT UK_verification_token UNIQUE (token);

ALTER TABLE verification_token
    ADD CONSTRAINT UK_verification_token_user UNIQUE (user_id);

CREATE INDEX idx_refresh_token ON refresh_token (token);

ALTER TABLE password_reset_token
    ADD CONSTRAINT FK_PASSWORD_RESET_TOKEN_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE refresh_token
    ADD CONSTRAINT FK_REFRESH_TOKEN_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_refresh_token_user ON refresh_token (user_id);

ALTER TABLE verification_token
    ADD CONSTRAINT FK_VERIFICATION_TOKEN_USER FOREIGN KEY (user_id) REFERENCES users (id);