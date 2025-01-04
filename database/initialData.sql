SET @coverImagePath = 'D:\\bookstore\\database\\cover.jpg';

SET @bookFilePathEpub = 'D:\\bookstore\\database\\book.epub';
SET @previewFilePathEpub = 'D:\\bookstore\\database\\preview.epub';

SET @bookFilePathMp3 = 'D:\\bookstore\\database\\book.mp3';
SET @previewFilePathMp3 = 'D:\\bookstore\\database\\preview.mp3';

-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema bookstore
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `bookstore` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `bookstore` ;

-- -----------------------------------------------------
-- Table `bookstore`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookstore`.`users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `active_subscription_end_date` DATE NULL DEFAULT NULL,
  `birth_date` DATE NOT NULL,
  `email` VARCHAR(320) NULL DEFAULT NULL,
  `has_active_subscription` BIT(1) NOT NULL,
  `password` VARCHAR(255) NULL DEFAULT NULL,
  `reset_token` VARCHAR(255) NULL DEFAULT NULL,
  `username` VARCHAR(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UKr43af9ap4edm43mmtq01oddj6` (`username` ASC) VISIBLE,
  UNIQUE INDEX `UK6dotkott2kjsp8vw4d0m25fb7` (`email` ASC) VISIBLE,
  UNIQUE INDEX `UKkpeyao30ym7l5vf8wsterwase` (`reset_token` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bookstore`.`addresses`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookstore`.`addresses` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `city` VARCHAR(100) NULL DEFAULT NULL,
  `name` VARCHAR(255) NULL DEFAULT NULL,
  `phone_number` VARCHAR(255) NULL DEFAULT NULL,
  `postal_code` VARCHAR(255) NULL DEFAULT NULL,
  `street` VARCHAR(100) NULL DEFAULT NULL,
  `user_name` VARCHAR(100) NULL DEFAULT NULL,
  `user_surname` VARCHAR(100) NULL DEFAULT NULL,
  `user_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK1fa36y2oqhao3wgg2rw1pi459` (`user_id` ASC) VISIBLE,
  CONSTRAINT `FK1fa36y2oqhao3wgg2rw1pi459`
    FOREIGN KEY (`user_id`)
    REFERENCES `bookstore`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bookstore`.`books`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookstore`.`books` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `author` VARCHAR(100) NULL DEFAULT NULL,
  `description` TEXT NULL DEFAULT NULL,
  `num_of_reviews` INT NOT NULL,
  `popularity_score` DOUBLE NULL DEFAULT NULL,
  `publication_date` DATE NOT NULL,
  `rating` DECIMAL(4,2) NOT NULL,
  `title` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bookstore`.`audiobooks`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookstore`.`audiobooks` (
  `book_id` BIGINT NOT NULL,
  `book_path` VARCHAR(255) NULL DEFAULT NULL,
  `cover_image_path` VARCHAR(255) NULL DEFAULT NULL,
  `discount_amount` DECIMAL(6,2) NULL DEFAULT NULL,
  `discount_end_date` DATETIME(6) NULL DEFAULT NULL,
  `discount_percentage` INT NULL DEFAULT NULL,
  `duration_seconds` INT NOT NULL,
  `has_discount` BIT(1) NOT NULL,
  `is_hidden` BIT(1) NOT NULL,
  `narrator` VARCHAR(100) NULL DEFAULT NULL,
  `preview_path` VARCHAR(255) NULL DEFAULT NULL,
  `price` DECIMAL(6,2) NOT NULL,
  `price_with_discount` DECIMAL(6,2) NULL DEFAULT NULL,
  `publisher` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`book_id`),
  CONSTRAINT `FKo8fo18hyygmlbb81wbe1m7a5n`
    FOREIGN KEY (`book_id`)
    REFERENCES `bookstore`.`books` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bookstore`.`collections`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookstore`.`collections` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL DEFAULT NULL,
  `user_id` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKn7pdedyqaiddr0uxdj603my7d` (`user_id` ASC) VISIBLE,
  CONSTRAINT `FKn7pdedyqaiddr0uxdj603my7d`
    FOREIGN KEY (`user_id`)
    REFERENCES `bookstore`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bookstore`.`library_items`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookstore`.`library_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `added_date` DATE NOT NULL,
  `book_type` VARCHAR(255) NULL DEFAULT NULL,
  `is_subscription_item` BIT(1) NOT NULL,
  `last_position` VARCHAR(255) NOT NULL,
  `book_id` BIGINT NULL DEFAULT NULL,
  `collection_id` BIGINT NULL DEFAULT NULL,
  `user_id` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKaiw6n7a2x1wlm6vesbsd5yq9v` (`book_id` ASC) VISIBLE,
  INDEX `FK1d93v0amtuwnsh1tnv3m0p94j` (`collection_id` ASC) VISIBLE,
  INDEX `FK8rw5wurjq0es405abufpvwhlr` (`user_id` ASC) VISIBLE,
  CONSTRAINT `FK1d93v0amtuwnsh1tnv3m0p94j`
    FOREIGN KEY (`collection_id`)
    REFERENCES `bookstore`.`collections` (`id`),
  CONSTRAINT `FK8rw5wurjq0es405abufpvwhlr`
    FOREIGN KEY (`user_id`)
    REFERENCES `bookstore`.`users` (`id`),
  CONSTRAINT `FKaiw6n7a2x1wlm6vesbsd5yq9v`
    FOREIGN KEY (`book_id`)
    REFERENCES `bookstore`.`books` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bookstore`.`book_notes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookstore`.`book_notes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NULL DEFAULT NULL,
  `position` VARCHAR(255) NULL DEFAULT NULL,
  `text` VARCHAR(300) NULL DEFAULT NULL,
  `library_item_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKivirhsgvmo9nfwtoddaai9jpf` (`library_item_id` ASC) VISIBLE,
  CONSTRAINT `FKivirhsgvmo9nfwtoddaai9jpf`
    FOREIGN KEY (`library_item_id`)
    REFERENCES `bookstore`.`library_items` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bookstore`.`promo_codes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookstore`.`promo_codes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(255) NULL DEFAULT NULL,
  `end_date` DATETIME(6) NOT NULL,
  `is_active` BIT(1) NOT NULL,
  `percentage` INT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bookstore`.`shipping_methods`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookstore`.`shipping_methods` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `duration_days` INT NOT NULL,
  `name` VARCHAR(255) NULL DEFAULT NULL,
  `price` DECIMAL(38,2) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bookstore`.`carts`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookstore`.`carts` (
  `user_id` BIGINT NOT NULL,
  `has_paper_books` BIT(1) NOT NULL,
  `has_promo_code` BIT(1) NOT NULL,
  `payment_id` VARCHAR(255) NULL DEFAULT NULL,
  `payment_redirect_url` VARCHAR(255) NULL DEFAULT NULL,
  `payment_status` VARCHAR(255) NULL DEFAULT NULL,
  `total_price` DECIMAL(10,2) NOT NULL,
  `total_price_with_promo_code` DECIMAL(38,2) NULL DEFAULT NULL,
  `address_id` BIGINT NULL DEFAULT NULL,
  `promo_code_id` BIGINT NULL DEFAULT NULL,
  `shipping_method_id` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `UK_7s9eocyv1gbis6sesxvbt7oom` (`address_id` ASC) VISIBLE,
  INDEX `FK1wuwehaw4gc1qtsws7sikdmtw` (`promo_code_id` ASC) VISIBLE,
  INDEX `FKdcmjt88knwgv32xfijn9eldqy` (`shipping_method_id` ASC) VISIBLE,
  CONSTRAINT `FK1wuwehaw4gc1qtsws7sikdmtw`
    FOREIGN KEY (`promo_code_id`)
    REFERENCES `bookstore`.`promo_codes` (`id`),
  CONSTRAINT `FKb5o626f86h46m4s7ms6ginnop`
    FOREIGN KEY (`user_id`)
    REFERENCES `bookstore`.`users` (`id`),
  CONSTRAINT `FKdcmjt88knwgv32xfijn9eldqy`
    FOREIGN KEY (`shipping_method_id`)
    REFERENCES `bookstore`.`shipping_methods` (`id`),
  CONSTRAINT `FKtg1eewdsmejxk3bb6nx16o2oc`
    FOREIGN KEY (`address_id`)
    REFERENCES `bookstore`.`addresses` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bookstore`.`cart_items`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookstore`.`cart_items` (
  `cart_item_id` BIGINT NOT NULL AUTO_INCREMENT,
  `book_type` VARCHAR(255) NULL DEFAULT NULL,
  `has_discount` BIT(1) NOT NULL,
  `paper_book_id` BIGINT NULL DEFAULT NULL,
  `price` DECIMAL(6,2) NOT NULL,
  `price_with_discount` DECIMAL(6,2) NOT NULL,
  `quantity` INT NULL DEFAULT NULL,
  `total_price` DECIMAL(8,2) NOT NULL,
  `book_id` BIGINT NOT NULL,
  `cart_id` BIGINT NOT NULL,
  PRIMARY KEY (`cart_item_id`),
  INDEX `FKhiu1jw80o45wfiw5tgok1xpkl` (`book_id` ASC) VISIBLE,
  INDEX `FKpcttvuq4mxppo8sxggjtn5i2c` (`cart_id` ASC) VISIBLE,
  CONSTRAINT `FKhiu1jw80o45wfiw5tgok1xpkl`
    FOREIGN KEY (`book_id`)
    REFERENCES `bookstore`.`books` (`id`),
  CONSTRAINT `FKpcttvuq4mxppo8sxggjtn5i2c`
    FOREIGN KEY (`cart_id`)
    REFERENCES `bookstore`.`carts` (`user_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bookstore`.`categories`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookstore`.`categories` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `category_name` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UKeeyxi4vom9nrwcce0gddyo3rn` (`category_name` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bookstore`.`category_books`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookstore`.`category_books` (
  `book_id` BIGINT NOT NULL,
  `category_id` BIGINT NOT NULL,
  PRIMARY KEY (`book_id`, `category_id`),
  INDEX `FK7xof6yq8ylo66f6hemiw6im62` (`category_id` ASC) VISIBLE,
  CONSTRAINT `FK7xof6yq8ylo66f6hemiw6im62`
    FOREIGN KEY (`category_id`)
    REFERENCES `bookstore`.`categories` (`id`),
  CONSTRAINT `FKbgkdf6es4p7sbmt89v4egcjap`
    FOREIGN KEY (`book_id`)
    REFERENCES `bookstore`.`books` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bookstore`.`ebooks`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookstore`.`ebooks` (
  `book_id` BIGINT NOT NULL,
  `book_path` VARCHAR(255) NULL DEFAULT NULL,
  `cover_image_path` VARCHAR(255) NULL DEFAULT NULL,
  `discount_amount` DECIMAL(6,2) NULL DEFAULT NULL,
  `discount_end_date` DATETIME(6) NULL DEFAULT NULL,
  `discount_percentage` INT NULL DEFAULT NULL,
  `has_discount` BIT(1) NOT NULL,
  `is_hidden` BIT(1) NOT NULL,
  `num_of_pages` INT NOT NULL,
  `preview_path` VARCHAR(255) NULL DEFAULT NULL,
  `price` DECIMAL(6,2) NOT NULL,
  `price_with_discount` DECIMAL(6,2) NOT NULL,
  `publisher` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`book_id`),
  CONSTRAINT `FK5cohnl00svuog22wpa05pavl`
    FOREIGN KEY (`book_id`)
    REFERENCES `bookstore`.`books` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bookstore`.`orders`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookstore`.`orders` (
  `order_id` BIGINT NOT NULL AUTO_INCREMENT,
  `address_location` VARCHAR(255) NULL DEFAULT NULL,
  `address_phone_number` VARCHAR(255) NULL DEFAULT NULL,
  `address_user_full_name` VARCHAR(255) NULL DEFAULT NULL,
  `cart_price` DECIMAL(10,2) NOT NULL,
  `has_paper_books` BIT(1) NOT NULL,
  `order_date` DATE NOT NULL,
  `order_status` VARCHAR(255) NULL DEFAULT NULL,
  `payment_id` VARCHAR(255) NULL DEFAULT NULL,
  `shipping_date` DATE NULL DEFAULT NULL,
  `shipping_price` DECIMAL(5,2) NULL DEFAULT NULL,
  `total_price` DECIMAL(10,2) NOT NULL,
  `user_id` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  INDEX `FK32ql8ubntj5uh44ph9659tiih` (`user_id` ASC) VISIBLE,
  CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih`
    FOREIGN KEY (`user_id`)
    REFERENCES `bookstore`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bookstore`.`order_items`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookstore`.`order_items` (
  `order_item_id` BIGINT NOT NULL AUTO_INCREMENT,
  `book_type` VARCHAR(255) NULL DEFAULT NULL,
  `paper_book_id` BIGINT NULL DEFAULT NULL,
  `price` DECIMAL(6,2) NOT NULL,
  `quantity` INT NULL DEFAULT NULL,
  `total_price` DECIMAL(8,2) NOT NULL,
  `book_id` BIGINT NULL DEFAULT NULL,
  `order_id` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`order_item_id`),
  INDEX `FKi4ptndslo2pyfp9r1x0eulh9g` (`book_id` ASC) VISIBLE,
  INDEX `FKbioxgbv59vetrxe0ejfubep1w` (`order_id` ASC) VISIBLE,
  CONSTRAINT `FKbioxgbv59vetrxe0ejfubep1w`
    FOREIGN KEY (`order_id`)
    REFERENCES `bookstore`.`orders` (`order_id`),
  CONSTRAINT `FKi4ptndslo2pyfp9r1x0eulh9g`
    FOREIGN KEY (`book_id`)
    REFERENCES `bookstore`.`books` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bookstore`.`paper_books`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookstore`.`paper_books` (
  `paper_book_id` BIGINT NOT NULL AUTO_INCREMENT,
  `cover_image_path` VARCHAR(255) NULL DEFAULT NULL,
  `cover_type` ENUM('HARDCOVER', 'PAPERBACK') NOT NULL,
  `discount_amount` DECIMAL(6,2) NULL DEFAULT NULL,
  `discount_end_date` DATETIME(6) NULL DEFAULT NULL,
  `discount_percentage` INT NULL DEFAULT NULL,
  `has_discount` BIT(1) NOT NULL,
  `is_available` BIT(1) NOT NULL,
  `is_hidden` BIT(1) NOT NULL,
  `isbn` VARCHAR(13) NULL DEFAULT NULL,
  `num_of_pages` INT NOT NULL,
  `price` DECIMAL(6,2) NOT NULL,
  `price_with_discount` DECIMAL(6,2) NOT NULL,
  `publisher` VARCHAR(100) NULL DEFAULT NULL,
  `book_id` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`paper_book_id`),
  INDEX `FKdocvxsktfwacjhnmawt96p819` (`book_id` ASC) VISIBLE,
  CONSTRAINT `FKdocvxsktfwacjhnmawt96p819`
    FOREIGN KEY (`book_id`)
    REFERENCES `bookstore`.`books` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bookstore`.`recommended_books`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookstore`.`recommended_books` (
  `user_id` BIGINT NOT NULL,
  `book_id` BIGINT NOT NULL,
  INDEX `FK47w852tvsvttl4598q9xo2ab0` (`book_id` ASC) VISIBLE,
  INDEX `FKe22f7u3icqj9hpvywx4b3gmxj` (`user_id` ASC) VISIBLE,
  CONSTRAINT `FK47w852tvsvttl4598q9xo2ab0`
    FOREIGN KEY (`book_id`)
    REFERENCES `bookstore`.`books` (`id`),
  CONSTRAINT `FKe22f7u3icqj9hpvywx4b3gmxj`
    FOREIGN KEY (`user_id`)
    REFERENCES `bookstore`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bookstore`.`reviews`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookstore`.`reviews` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `publication_date` DATETIME(6) NOT NULL,
  `rating` DECIMAL(4,2) NOT NULL,
  `text` TEXT NULL DEFAULT NULL,
  `book_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK6a9k6xvev80se5rreqvuqr7f9` (`book_id` ASC) VISIBLE,
  INDEX `FKcgy7qjc1r99dp117y9en6lxye` (`user_id` ASC) VISIBLE,
  CONSTRAINT `FK6a9k6xvev80se5rreqvuqr7f9`
    FOREIGN KEY (`book_id`)
    REFERENCES `bookstore`.`books` (`id`),
  CONSTRAINT `FKcgy7qjc1r99dp117y9en6lxye`
    FOREIGN KEY (`user_id`)
    REFERENCES `bookstore`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bookstore`.`roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookstore`.`roles` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` ENUM('ROLE_ADMIN', 'ROLE_USER') NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bookstore`.`subscriptions`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookstore`.`subscriptions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `description` VARCHAR(255) NULL DEFAULT NULL,
  `duration_days` INT NULL DEFAULT NULL,
  `name` VARCHAR(255) NULL DEFAULT NULL,
  `price` DECIMAL(6,2) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bookstore`.`subscription_orders`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookstore`.`subscription_orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `end_date` DATE NULL DEFAULT NULL,
  `payment_id` VARCHAR(255) NULL DEFAULT NULL,
  `payment_redirect_url` VARCHAR(255) NULL DEFAULT NULL,
  `payment_status` VARCHAR(255) NULL DEFAULT NULL,
  `start_date` DATE NULL DEFAULT NULL,
  `subscription_id` BIGINT NULL DEFAULT NULL,
  `user_id` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKa6hllc9mrl5d27426q473kp2q` (`subscription_id` ASC) VISIBLE,
  INDEX `FK8tun062s02o7f6jdj5dhl4uxm` (`user_id` ASC) VISIBLE,
  CONSTRAINT `FK8tun062s02o7f6jdj5dhl4uxm`
    FOREIGN KEY (`user_id`)
    REFERENCES `bookstore`.`users` (`id`),
  CONSTRAINT `FKa6hllc9mrl5d27426q473kp2q`
    FOREIGN KEY (`subscription_id`)
    REFERENCES `bookstore`.`subscriptions` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bookstore`.`used_promo_codes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookstore`.`used_promo_codes` (
  `user_id` BIGINT NOT NULL,
  `promo_code_id` BIGINT NOT NULL,
  INDEX `FKo8brxwnftm89i3yjpiy3j1d0s` (`promo_code_id` ASC) VISIBLE,
  INDEX `FKcu8a8bm68jrj865f9bkfdlv9a` (`user_id` ASC) VISIBLE,
  CONSTRAINT `FKcu8a8bm68jrj865f9bkfdlv9a`
    FOREIGN KEY (`user_id`)
    REFERENCES `bookstore`.`users` (`id`),
  CONSTRAINT `FKo8brxwnftm89i3yjpiy3j1d0s`
    FOREIGN KEY (`promo_code_id`)
    REFERENCES `bookstore`.`promo_codes` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bookstore`.`user_roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookstore`.`user_roles` (
  `user_id` BIGINT NOT NULL,
  `role_id` INT NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`),
  INDEX `FKh8ciramu9cc9q3qcqiv4ue8a6` (`role_id` ASC) VISIBLE,
  CONSTRAINT `FKh8ciramu9cc9q3qcqiv4ue8a6`
    FOREIGN KEY (`role_id`)
    REFERENCES `bookstore`.`roles` (`id`),
  CONSTRAINT `FKhfh9dx7w3ubf1co1vdev94g3f`
    FOREIGN KEY (`user_id`)
    REFERENCES `bookstore`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `bookstore`.`wishlists`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookstore`.`wishlists` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `book_type` VARCHAR(255) NULL DEFAULT NULL,
  `paper_book_id` BIGINT NULL DEFAULT NULL,
  `book_id` BIGINT NULL DEFAULT NULL,
  `user_id` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKo69vdp48axn9ovctfp0jrlgum` (`book_id` ASC) VISIBLE,
  INDEX `FK330pyw2el06fn5g28ypyljt16` (`user_id` ASC) VISIBLE,
  CONSTRAINT `FK330pyw2el06fn5g28ypyljt16`
    FOREIGN KEY (`user_id`)
    REFERENCES `bookstore`.`users` (`id`),
  CONSTRAINT `FKo69vdp48axn9ovctfp0jrlgum`
    FOREIGN KEY (`book_id`)
    REFERENCES `bookstore`.`books` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

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
