insert into
    users(email, first_name, last_name, nick_name, age, password, password_reset_token)
values
    ('admin@mail.com', 'Bartek', 'Kowalski', 'BigAdmin', 40, '{noop}adminpass', 'be7dd3e6-d9b7-49ba-a2ee-3eeafa175156'),
    ('user@mail.com',  'Janek', 'Janecki', 'BigJohn', 28, '{noop}Userpass123!@#', 'be7dd3e6-d9b7-49ba-a2ee-3eeafa175157'),
    ('bartekkempa@gmail.com',  'Daga', 'Szczepankowa', 'Dagula', 35, '{noop}userpass', 'be7dd3e6-d9b7-49ba-a2ee-3eeafa175158');

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
    (3, 2);