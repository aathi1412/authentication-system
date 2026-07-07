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
           'SuperAdmin',
           'super@admin.com',
           '$2a$10$2DqHoWviJCBtgnrNQYcdoe0wYjw1R1DbC644lDM8fxbX1lj9/vpjq',
           'SUPER_ADMIN',
           true,
           false,
           0,
           NOW(),
           NOW()
       );