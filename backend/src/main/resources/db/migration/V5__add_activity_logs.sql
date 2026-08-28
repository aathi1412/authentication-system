CREATE TABLE activity_logs
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    user_id       BIGINT                NOT NULL,
    type          VARCHAR(255)          NOT NULL,
    title         VARCHAR(255)          NOT NULL,
    `description` VARCHAR(255)          NOT NULL,
    category      VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_activitylogs PRIMARY KEY (id)
);

ALTER TABLE activity_logs
    ADD CONSTRAINT FK_ACTIVITYLOGS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);