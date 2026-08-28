ALTER TABLE activity_logs
    DROP COLUMN type;

ALTER TABLE activity_logs
ADD COLUMN activity_time TIMESTAMP;