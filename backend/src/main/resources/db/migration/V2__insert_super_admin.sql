INSERT INTO users (
    name,
    email,
    password,
    role,
    enabled,
    account_locked,
    failed_attempts,
    created_at,
    updated_at
)
VALUES (
           'Admin',
           'aathi9211@gmail.com',
           '$2a$10$tOjfoUpp0RwDuTiJW2dHJeXlYeaiMxhPIDrPg9TPn4/SVTmgEoil.',
           'SUPER_ADMIN',
           true,
           false,
           0,
           NOW(),
           NOW()
       );