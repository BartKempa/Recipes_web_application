insert into
    users(email, first_name, last_name, nick_name, age, password)
values
    ('admin@mail.com', 'Bartek', 'Kowalski', 'BigAdmin', 40, '{noop}adminpass'),
    ('user@mail.com',  'Janek', 'Janecki', 'BigJohn', 28, '{noop}userpass'),
    ('editor@mail.com',  'Daga', 'Szczepankowa', 'Dagula', 35, '{noop}editorpass');

insert into
    user_role(name, description)
values
    ('ADMIN', 'pełne uprawnienia'),
    ('USER', 'podstawowe uprawnienia, możliwość oddawania głosów i komentarzy'),
    ('EDITOR', 'podstawowe uprawnienia + możliwość zarządzania treściami');

