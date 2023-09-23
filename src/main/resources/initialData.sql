INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
INSERT INTO users(email, password, username) VALUES ('admin@mail.com', '$2a$12$W5KqZpjA9eFsN0IfpAGcwuSEstYQlEyiDSx4p5byMp1/sL1S3rcqW', 'admin');
INSERT INTO users(email, password, username) VALUES ('user@mail.com', '$2a$12$2kbPfMpUq9SUiyWdCqw/bORYivH.fg5OszFl3Ho9isEbqgbPvs1Nm', 'user');
INSERT INTO user_roles(user_id, role_id) VALUES (1, 1);
INSERT INTO user_roles(user_id, role_id) VALUES (1, 2);
INSERT INTO user_roles(user_id, role_id) VALUES (2, 1);