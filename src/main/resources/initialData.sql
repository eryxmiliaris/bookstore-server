SET @coverImagePath = 'D:\\bookstore\\src\\main\\resources\\cover.jpg';

SET @bookFilePathEpub = 'D:\\bookstore\\src\\main\\resources\\book.epub';
SET @previewFilePathEpub = 'D:\\bookstore\\src\\main\\resources\\preview.epub';

SET @bookFilePathMp3 = 'D:\\bookstore\\src\\main\\resources\\book.mp3';
SET @previewFilePathMp3 = 'D:\\bookstore\\src\\main\\resources\\preview.mp3';

-- Roles
INSERT INTO `roles`
(`id`, `name`)
VALUES
(1, 'ROLE_USER'),
(2, 'ROLE_ADMIN');

-- Default users (login/password: [admin/admin, usersub/user, usernosub/user])
INSERT INTO `users`
(`id`, `email`, `password`, `username`, `birth_date`, `has_active_subscription`, `active_subscription_end_date`)
VALUES
(1, 'admin@admin.com', '$2a$12$W5KqZpjA9eFsN0IfpAGcwuSEstYQlEyiDSx4p5byMp1/sL1S3rcqW', 'admin', '1854-12-21', false, null),
(2, 'usersub@test.com', '$2a$12$2kbPfMpUq9SUiyWdCqw/bORYivH.fg5OszFl3Ho9isEbqgbPvs1Nm', 'usersub', '1995-03-20', true, '2030-01-01'),
(3, 'usernosub@test.com', '$2a$12$2kbPfMpUq9SUiyWdCqw/bORYivH.fg5OszFl3Ho9isEbqgbPvs1Nm', 'usernosub', '1995-03-20', false, null);

-- Assign roles to users
INSERT INTO `user_roles`
(user_id, role_id)
VALUES (1, 1),
(1, 2),
(2, 1),
(3, 1);

-- Shipping methods
INSERT INTO `shipping_methods`
(`duration_days`, `name`, `price`)
VALUES
(3, "Post service ABC", 12.99),
(2, "Post service XYZ", 16.99);

-- Addresses
INSERT INTO `bookstore`.`addresses`
(`id`, `city`, `name`, `phone_number`, `postal_code`, `street`, `user_name`, `user_surname`, `user_id`)
VALUES
(1, 'Admin City', 'Admin\'s house', '123123123', '20-501', 'Admin Street 12', 'Admin', 'Adminius', 1),
(2, 'Subscriber City', 'Subscriber\'s house', '123123123', '20-501', 'Subscriber Street 12', 'Subscriber', 'Subscriberus', 2),
(3, 'User City', 'User\'s house', '123123123', '20-501', 'User Street 12', 'User', 'Userius', 3);

-- Carts
INSERT INTO `carts`
(`user_id`, `has_paper_books`, `has_promo_code`, `payment_id`, `payment_redirect_url`, `payment_status`, `total_price`, `total_price_with_promo_code`, `address_id`, `promo_code_id`, `shipping_method_id`)
VALUES
(1, false, false, null, null, null, 0, 0, null, null, null),
(2, false, false, null, null, null, 0, 0, null, null, null),
(3, false, false, null, null, null, 0, 0, null, null, null);

-- Books
INSERT INTO `books`
(`id`, `author`, `description`, `num_of_reviews`, `publication_date`, `rating`, `title`)
VALUES
(1, 'Jane Austen', 'A classic novel about love and social class.', 0, '1813-01-28', 0, 'Pride and Prejudice'),
(2, 'Frank Herbert', 'Epic science fiction set in a desert world.', 0, '1965-08-01', 0, 'Dune'),
(3, 'Stieg Larsson', 'A gripping mystery involving a journalist and a hacker.', 0, '2005-08-01', 0, 'The Girl with the Dragon Tattoo'),
(4, 'Harper Lee', 'A poignant exploration of racial injustice in the American South.', 0, '1960-07-11', 0, 'To Kill a Mockingbird'),
(5, 'George Orwell', 'A dystopian classic exploring the dangers of totalitarianism.', 0, '1949-06-08', 0, '1984'),
(6, 'J.R.R. Tolkien', 'A fantasy adventure about a hobbit on a quest.', 0, '1937-09-21', 0, 'The Hobbit'),
(7, 'Gillian Flynn', 'A psychological thriller about a woman\'s mysterious disappearance.', 0, '2012-05-24', 0, 'Gone Girl'),
(8, 'Dan Brown', 'A gripping mystery involving symbols, codes, and conspiracy.', 0, '2003-03-18', 0, 'The Da Vinci Code'),
(9, 'Siddhartha Mukherjee', 'An intimate history of the gene and its impact on medicine and society.', 0, '2016-05-17', 0, 'The Gene: An Intimate History'),
(10, 'Stephen R. Covey', 'A guide to personal and professional effectiveness.', 0, '1989-08-15', 0, 'The 7 Habits of Highly Effective People');

-- Categories
INSERT INTO `categories`
(`id`, `category_name`)
VALUES
(1, 'Classic Literature'),
(2, 'Science Fiction'),
(3, 'Thriller'),
(5, 'Romance'),
(7, 'Science'),
(10, 'Motivational');

-- Assign books to categories
INSERT INTO `category_books`
(`book_id`, `category_id`)
VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 1),
(5, 2),
(6, 2),
(9, 7),
(4, 5),
(5, 3),
(6, 3),
(9, 5),
(7, 3),
(7, 5),
(8, 5),
(8, 3),
(10, 10);

-- Paper books
INSERT INTO `paper_books`
(`paper_book_id`, `cover_image_path`, `cover_type`, `discount_amount`, `discount_end_date`, `discount_percentage`, `has_discount`, `is_available`, `is_hidden`, `isbn`, `num_of_pages`, `price`, `price_with_discount`, `publisher`, `book_id`)
VALUES
(1, @coverImagePath, 'HARDCOVER', 2.00, '2023-12-31', 10, 1, 1, 0, '9781234567890', 400, 19.99, 17.99, 'Classic Books Publishing', 1),
(2, @coverImagePath, 'PAPERBACK', null, null, null, 0, 1, 0, '9780987654321', 600, 24.99, 24.99, 'Sci-Fi Press', 2),
(3, @coverImagePath, 'HARDCOVER', 3.00, '2023-12-31', 15, 1, 1, 0, '9780876543210', 450, 21.99, 18.99, 'Mystery Publications', 3),
(11, @coverImagePath, 'HARDCOVER', null, null, null, 0, 0, 0, '9781234567891', 200, 9.99, 9.99, 'New Books Publishing', 1),
(12, @coverImagePath, 'PAPERBACK', null, null, null, 0, 0, 0, '9781234567892', 250, 11.99, 11.99, 'Unavailable Books Co.', 2),
(13, @coverImagePath, 'HARDCOVER', null, null, null, 0, 0, 0, '9781234567893', 180, 8.99, 8.99, 'New Books Publishing', 3),
(4, @coverImagePath, 'PAPERBACK', null, null, null, 0, 1, 0, '9780111122223', 350, 12.99, 12.99, 'Classic Books Publishing', 4),
(5, @coverImagePath, 'HARDCOVER', null, null, null, 0, 1, 0, '9780333344445', 300, 15.99, 15.99, 'Dystopian Books Inc.', 5),
(6, @coverImagePath, 'PAPERBACK', null, null, null, 0, 1, 0, '9780555566667', 400, 17.99, 17.99, 'Fantasy Publications', 6),
(7, @coverImagePath, 'PAPERBACK', null, null, null, 0, 1, 0, '9780987654322', 400, 14.99, 14.99, 'Mystery Publications', 7),
(8, @coverImagePath, 'HARDCOVER', null, null, null, 0, 1, 0, '9780876543211', 450, 21.99, 21.99, 'Mystery Publications', 8),
(9, @coverImagePath, 'HARDCOVER', null, null, null, 0, 1, 0, '9780999988887', 500, 24.99, 24.99, 'Scientific Books Co.', 9),
(10, @coverImagePath, 'PAPERBACK', null, null, null, 0, 1, 0, '9780765432109', 300, 14.99, 14.99, 'Self-Help Books Co.', 10);

-- Ebooks
INSERT INTO `ebooks`
(`book_id`, `book_path`, `cover_image_path`, `discount_amount`, `discount_end_date`, `discount_percentage`, `has_discount`, `is_hidden`, `num_of_pages`, `preview_path`, `price`, `price_with_discount`, `publisher`)
VALUES
(2, @bookFilePathEpub, @coverImagePath, 1.50, '2023-12-31', 5, 1, 0, 600, @previewFilePathEpub, 12.99, 11.49, 'Sci-Fi Press'),
(3, @bookFilePathEpub, @coverImagePath, 3.00, '2023-12-31', 15, 1, 0, 450, @previewFilePathEpub, 14.99, 12.99, 'Mystery Publications'),
(5, @bookFilePathEpub, @coverImagePath, null, null, null, 0, 0, 300, @previewFilePathEpub, 10.99, 10.99, 'Dystopian Books Inc.'),
(6, @bookFilePathEpub, @coverImagePath, null, null, null, 0, 0, 400, @previewFilePathEpub, 9.99, 9.99, 'Fantasy Publications'),
(7, @bookFilePathEpub, @coverImagePath, null, null, null, 0, 0, 400, @previewFilePathEpub, 11.99, 11.99, 'Mystery Publications'),
(9, @bookFilePathEpub, @coverImagePath, null, null, null, 0, 0, 500, @previewFilePathEpub, 15.99, 15.99, 'Scientific Books Co.');

-- Audiobooks
INSERT INTO `audiobooks`
(`book_id`, `book_path`, `cover_image_path`, `discount_amount`, `discount_end_date`, `discount_percentage`, `duration_seconds`, `has_discount`, `is_hidden`, `narrator`, `preview_path`, `price`, `price_with_discount`, `publisher`)
VALUES
(1, @bookFilePathMp3, @coverImagePath, 2.00, '2023-12-31', 10, 18000, 1, 0, 'Emma Thompson', @previewFilePathMp3, 24.99, 22.49, 'Classic Books Publishing'),
(4, @bookFilePathMp3, @coverImagePath, 1.00, '2023-12-31', 5, 15000, 1, 0, 'Sissy Spacek', @previewFilePathMp3, 18.99, 17.49, 'Classic Books Publishing'),
(6, @bookFilePathMp3, @coverImagePath, null, null, null, 12000, 0, 0, 'Andy Serkis', @previewFilePathMp3, 21.99, 21.99, 'Fantasy Publications'),
(8, @bookFilePathMp3, @coverImagePath, null, null, null, 20000, 0, 0, 'Tom Hanks', @previewFilePathMp3, 27.99, 27.99, 'Mystery Publications'),
(10, @bookFilePathMp3, @coverImagePath, null, null, null, 30000, 0, 0, 'Stephen R. Covey', @previewFilePathMp3, 19.99, 19.99, 'Self-Help Books Co.'),
(3, @bookFilePathMp3, @coverImagePath, 3.00, '2023-12-31', 15, 21000, 1, 0, 'Simon Vance', @previewFilePathMp3, 23.99, 21.49, 'Mystery Publications');

-- Subscriptions
INSERT INTO `subscriptions`
(`id`, `description`, `duration_days`, `name`, `price`)
VALUES
(1, 'Ideal for those who enjoy trying out new titles and want the flexibility to adjust their reading choices frequently.', 30, 'Monthly Subscription', 39.99),
(2, 'Perfect for readers committed to exploring a diverse range of genres over an extended period while enjoying a cost-effective plan.', 180, 'Half-Year Subscription', 199.99),
(3, 'Our best value option for the avid reader, granting a full year of uninterrupted access to our entire library at a significant discount.', 365, 'Annual Subscription', 399.99);
