-- Roles
INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
-- Default users (login/password: [user/user, admin/admin])
INSERT INTO users(email, password, username) VALUES ('admin@mail.com', '$2a$12$W5KqZpjA9eFsN0IfpAGcwuSEstYQlEyiDSx4p5byMp1/sL1S3rcqW', 'admin');
INSERT INTO users(email, password, username) VALUES ('user@mail.com', '$2a$12$2kbPfMpUq9SUiyWdCqw/bORYivH.fg5OszFl3Ho9isEbqgbPvs1Nm', 'user');
-- Set roles to users
INSERT INTO user_roles(user_id, role_id) VALUES (1, 1);
INSERT INTO user_roles(user_id, role_id) VALUES (1, 2);
INSERT INTO user_roles(user_id, role_id) VALUES (2, 1);
-- Initial data for categories
INSERT INTO `bookstore`.`categories` (`category_name`) VALUES
("Science Fiction"),
("Fantasy"),
("Horror"),
("Mystery"),
("Adventure"),
("Romance"),
("Thriller"),
("Historical Fiction"),
("Biography"),
("Self-Help"),
("Cooking"),
("Travel"),
("Science"),
("Poetry"),
("Classic Literature"),
("Young Adult"),
("Children's"),
("Graphic Novels"),
("Non-Fiction"),
("Comics");
-- Initial data for books
INSERT INTO `bookstore`.`books`
(`author`, `title`, `description`, `publication_date`, `rating`, `hidden`)
VALUES
("Stephen King", "The Gunslinger", "The Gunslinger is the first book in The Dark Tower series. It introduces the enigmatic Roland Deschain and his quest to reach the Dark Tower. On his journey, he encounters strange characters and a mysterious world.", '1982-06-10', 8.4, false),
("Stephen King", "The Drawing of the Three", "The Drawing of the Three is the second book in The Dark Tower series. Roland continues his quest, drawing companions from different worlds to aid him in his journey to the Dark Tower.", '1987-02-07', 9.0, false),
("Stephen King", "The Waste Lands", "The Waste Lands is the third book in The Dark Tower series. Roland and his companions face new challenges as they move closer to the Dark Tower, including encountering a sentient monorail.", '1991-08-15', 8.8, false),
("Stephen King", "Wizard and Glass", "Wizard and Glass is the fourth book in The Dark Tower series. In this installment, Roland recounts a tale from his youth, a tragic love story set in the town of Hambry.", '1997-11-04', 9.2, false),
("Stephen King", "The Wolves of the Calla", "The Wolves of the Calla is the fifth book in The Dark Tower series. Roland and his group must protect a small town from marauding wolves, while facing new mysteries and challenges.", '2003-11-04', 9.0, false),
("Stephen King", "Song of Susannah", "Song of Susannah is the sixth book in The Dark Tower series. The group faces a crisis as Susannah's pregnancy progresses, and a malevolent force threatens their quest.", '2004-06-08', 8.8, false),
("Stephen King", "The Dark Tower", "The Dark Tower is the seventh and final book in The Dark Tower series. Roland and his companions reach the Dark Tower itself, where the ultimate battle for the fate of the multiverse unfolds.", '2004-09-21', 9.4, false),
("Stephen King", "The Wind Through the Keyhole", "The Wind Through the Keyhole is a supplementary novel in The Dark Tower series. It takes place between books four and five and tells a story from Roland's past.", '2012-04-24', 8.6, false),
("J.K. Rowling", "Harry Potter and the Philosopher's Stone", "Harry Potter and the Philosopher's Stone is the first book in the Harry Potter series. It follows the young wizard Harry Potter as he discovers his magical abilities and starts his journey at Hogwarts School of Witchcraft and Wizardry.", '1997-06-26', 9.3, false),
("J.K. Rowling", "Harry Potter and the Chamber of Secrets", "Harry Potter and the Chamber of Secrets is the second book in the Harry Potter series. Harry and his friends investigate mysterious attacks at Hogwarts, uncovering dark secrets along the way.", '1998-07-02', 9.2, false),
("J.K. Rowling", "Harry Potter and the Prisoner of Azkaban", "Harry Potter and the Prisoner of Azkaban is the third book in the Harry Potter series. Harry learns about the dark past of Sirius Black while dealing with the dangers of the wizarding world.", '1999-07-08', 9.5, false),
("Terry Pratchett", "The Color of Magic", "The Color of Magic is the first book in the Discworld series. It follows the misadventures of Rincewind, a hapless wizard, and Twoflower, a tourist, as they journey through a fantastical world filled with strange creatures and magic.", '1983-11-24', 8.2, false),
("Terry Pratchett", "Mort", "Mort is the fourth book in the Discworld series. It tells the story of Mort, a young man who becomes an apprentice to Death and must navigate the responsibilities and consequences of his new role.", '1987-08-06', 8.9, false),
("Terry Pratchett", "Guards! Guards!", "Guards! Guards! is the eighth book in the Discworld series. It introduces the Night Watch, a group of misfit city guards, as they attempt to thwart a plot to overthrow the city with the help of a dragon.", '1989-11-14', 9.1, false);
-- Initial data for category_books relation
-- Books by Stephen King
INSERT INTO `bookstore`.`category_books` (`book_id`, `category_id`) VALUES
(1, 2),  -- Fantasy (The Gunslinger)
(1, 3),  -- Horror (The Gunslinger)
(2, 2),  -- Fantasy (The Drawing of the Three)
(2, 3),  -- Horror (The Drawing of the Three)
(3, 2),  -- Fantasy (The Waste Lands)
(3, 3),  -- Horror (The Waste Lands)
(4, 2),  -- Fantasy (Wizard and Glass)
(4, 3),  -- Horror (Wizard and Glass)
(5, 2),  -- Fantasy (The Wolves of the Calla)
(5, 3),  -- Horror (The Wolves of the Calla)
(6, 2),  -- Fantasy (Song of Susannah)
(6, 3),  -- Horror (Song of Susannah)
(7, 2),  -- Fantasy (The Dark Tower)
(7, 3),  -- Horror (The Dark Tower)
(8, 2);  -- Fantasy (The Wind Through the Keyhole)
-- Books by J.K. Rowling
INSERT INTO `bookstore`.`category_books` (`book_id`, `category_id`) VALUES
(9, 2),  -- Fantasy (Harry Potter and the Philosopher's Stone)
(9, 6),  -- Romance (Harry Potter and the Philosopher's Stone)
(10, 2), -- Fantasy (Harry Potter and the Chamber of Secrets)
(10, 6), -- Romance (Harry Potter and the Chamber of Secrets)
(11, 2), -- Fantasy (Harry Potter and the Prisoner of Azkaban)
(11, 6); -- Romance (Harry Potter and the Prisoner of Azkaban)
-- Books by Terry Pratchett
INSERT INTO `bookstore`.`category_books` (`book_id`, `category_id`) VALUES
(12, 2), -- Fantasy (The Color of Magic)
(13, 2), -- Fantasy (Mort)
(14, 2), -- Fantasy (Guards! Guards!)
(14, 7); -- Adventure (Guards! Guards!)
-- Initial data for paper books
INSERT INTO `bookstore`.`paper_books`
(`cover_image_url`, `cover_type`, `is_available`, `isbn`, `num_of_pages`, `price`, `publisher`, `book_id`)
VALUES
('', 'HARDCOVER', true, '9781982127802', 300, 25.99, 'Penguin Random House', 1),
('', 'PAPERBACK', true, '9781982127819', 400, 15.99, 'Scholastic', 2),
('', 'HARDCOVER', true, '9781982127826', 350, 28.99, 'HarperCollins', 3),
('', 'PAPERBACK', true, '9781982127871', 350, 18.99, 'Simon & Schuster', 8),
('', 'HARDCOVER', true, '9781982127888', 320, 29.99, 'Penguin Random House', 9),
('', 'PAPERBACK', true, '9781982127918', 480, 20.99, 'Scholastic', 12),
('', 'PAPERBACK', true, '9781982127932', 430, 19.99, 'Scholastic', 14),
('', 'PAPERBACK', true, '9781982127949', 250, 12.99, 'Simon & Schuster', 4),
('', 'HARDCOVER', true, '9781982127956', 320, 29.99, 'HarperCollins', 5);
-- Initial data for ebooks
INSERT INTO `bookstore`.`ebooks`
(`book_id`, `cover_image_url`, `price`, `publisher`, `num_of_pages`, `download_link`)
VALUES
(1, '', 9.99, 'Penguin Random House', 300, ''),
(2, '', 8.49, 'Scholastic', 400, ''),
(3, '', 7.99, 'HarperCollins', 250, ''),
(4, '', 6.99, 'Simon & Schuster', 350, ''),
(6, '', 4.99, 'HarperCollins', 200, ''),
(13, '', 10.99, 'HarperCollins', 380, ''),
(5, '', 5.99, 'Penguin Random House', 420, '');
-- Initial data for audiobooks
INSERT INTO `bookstore`.`audio_books`
(`book_id`, `cover_image_url`, `price`, `publisher`, `duration_seconds`, `narrator`, `download_link`)
VALUES
(1, '', 19.99, 'Penguin Random House', 7200, 'John Smith', ''),
(2, '', 18.49, 'Scholastic', 8400, 'Emily Johnson', ''),
(3, '', 17.99, 'HarperCollins', 6000, 'Michael Williams', ''),
(4, '', 16.99, 'Simon & Schuster', 7800, 'Sarah Davis', ''),
(7, '', 19.99, 'Simon & Schuster', 8300, 'Sarah Davis', ''),
(10, '', 14.49, 'ABC Books', 7200, 'Laura Johnson', ''),
(11, '', 12.99, 'XYZ Publishing', 9000, 'Michael Clark', ''),
(5, '', 15.99, 'Penguin Random House', 6600, 'David Miller', '');

