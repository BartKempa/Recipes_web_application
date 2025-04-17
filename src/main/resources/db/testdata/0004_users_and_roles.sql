insert into
    users(email, first_name, last_name, nick_name, age, password)
values
    ('admin@mail.com', 'Bartek', 'Kowalski', 'BigAdmin', 40, '{noop}adminpass'),
    ('user@mail.com',  'Janek', 'Janecki', 'BigJohn', 28, '{noop}Userpass123!@#'),
    ('daga@mail.com',  'Daga', 'Szczepankowa', 'Dagula', 35, '{noop}userpass');

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