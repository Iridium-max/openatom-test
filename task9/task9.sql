-- =========================
-- Task9 数据库基础
-- 在线图书商城数据库
-- =========================


-- 1. 创建数据库
CREATE DATABASE task9_db;

-- 使用数据库
USE task9_db;


-- =========================
-- 一、创建基础表
-- =========================

-- 用户表
CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE
);


-- 图书表
CREATE TABLE books (
                       book_id INT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(100) NOT NULL,
                       category VARCHAR(50) NOT NULL,
                       price DECIMAL(10, 2) NOT NULL,
                       stock INT NOT NULL
);


-- 订单主表
CREATE TABLE orders (
                        order_id INT AUTO_INCREMENT PRIMARY KEY,
                        user_id INT NOT NULL,
                        order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                        total_amount DECIMAL(10, 2) NOT NULL,
                        FOREIGN KEY (user_id) REFERENCES users(user_id)
);


-- =========================
-- Task1：创建订单明细表
-- =========================

CREATE TABLE order_items (
                             item_id INT AUTO_INCREMENT PRIMARY KEY,
                             order_id INT NOT NULL,
                             book_id INT NOT NULL,
                             quantity INT NOT NULL CHECK (quantity > 0),
                             buy_price DECIMAL(10, 2) NOT NULL,

                             FOREIGN KEY (order_id) REFERENCES orders(order_id),
                             FOREIGN KEY (book_id) REFERENCES books(book_id)
);


-- =========================
-- 插入测试数据
-- =========================

-- 用户数据
INSERT INTO users (username)
VALUES
    ('Alice'),
    ('Bob'),
    ('Charlie');


-- 图书数据
INSERT INTO books (title, category, price, stock)
VALUES
    ('C语言', 'Tech', 50.00, 100),
    ('数据结构', 'Tech', 60.00, 80),
    ('微积分', 'Math', 45.00, 60),
    ('线性代数', 'Math', 55.00, 50),
    ('Java', 'Tech', 70.00, 40);


-- 订单数据
INSERT INTO orders (user_id, total_amount)
VALUES
    (1, 170.00),
    (2, 115.00),
    (3, 70.00);


-- 订单明细数据
INSERT INTO order_items (order_id, book_id, quantity, buy_price)
VALUES
    (1, 1, 1, 50.00),
    (1, 2, 2, 60.00),
    (2, 3, 1, 45.00),
    (2, 5, 1, 70.00),
    (3, 5, 1, 70.00);


-- =========================
-- Task2
-- 查询购买至少3本Tech类图书的用户
-- =========================

SELECT
    users.username,
    SUM(order_items.quantity) AS total_tech_books,
    SUM(order_items.quantity * order_items.buy_price) AS total_spent
FROM users
         JOIN orders
              ON users.user_id = orders.user_id
         JOIN order_items
              ON orders.order_id = order_items.order_id
         JOIN books
              ON order_items.book_id = books.book_id
WHERE books.category = 'Tech'
GROUP BY users.username
HAVING SUM(order_items.quantity) >= 3;


-- =========================
-- Task3
-- 查询每个分类中价格最高的图书
-- =========================

SELECT
    b.category,
    b.title,
    b.price
FROM books b
         JOIN (
    SELECT
        category,
        MAX(price) AS max_price
    FROM books
    GROUP BY category
) m
              ON b.category = m.category
                  AND b.price = m.max_price;