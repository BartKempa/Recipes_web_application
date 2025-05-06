insert into
    users(email, first_name, last_name, nick_name, age, password, email_verified, email_verification_token, email_verification_token_expiry)
values
    ('admin@mail.com', 'Bartek', 'Kowalski', 'BigAdmin', 40, '{noop}adminpass', 1, null, null),
    ('user@mail.com',  'Janek', 'Janecki', 'BigJohn', 28, '{noop}Userpass123!@#', 1, null, null),
    ('daga@mail.com',  'Daga', 'Szczepankowa', 'Dagula', 35, '{noop}userpass', 1, null, null),
    ('test@mail.com',  'Jan', 'Janecki', 'Jaenczek', 35, '{noop}janpass18$', 0, '28be22c6-c750-4e13-987a-f31963ae9f07', '2025-05-05 23:45:34.598405');

insert into
    user_role(name, description)
values
    ('ADMIN', 'pełne uprawnienia'),
    ('USER', 'podstawowe uprawnienia, możliwość oddawania głosów i komentarzy');

insert into
    user_roles(user_id, role_id)
values
    (1, 1),
    (2, 2),
    (3, 2),
    (4, 2);