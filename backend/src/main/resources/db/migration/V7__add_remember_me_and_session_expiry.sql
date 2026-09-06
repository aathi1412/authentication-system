ALTER TABLE refresh_token
    ADD remember_me BIT(1) NOT NULL;

ALTER TABLE refresh_token
    ADD session_expiry_date datetime(6) NOT NULL;