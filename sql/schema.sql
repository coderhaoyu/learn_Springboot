CREATE TABLE users
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name       VARCHAR(50) NOT NULL COMMENT '姓名',
    age        INT COMMENT '年龄',
    email      VARCHAR(100) UNIQUE COMMENT '邮箱',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
-- 3. 插入初始测试数据
INSERT INTO users (name, age, email)
VALUES ('阿西', 38, 'axi@example.com'),
       ('阿光', 83, 'aguang@example.com');