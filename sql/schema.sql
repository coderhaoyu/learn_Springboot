CREATE TABLE users
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name       VARCHAR(50) NOT NULL COMMENT '姓名',
    age        INT COMMENT '年龄',
    email      VARCHAR(100) UNIQUE COMMENT '邮箱',
    password   VARCHAR(255) NOT NULL DEFAULT '' COMMENT '密码哈希',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

#测试数据
INSERT INTO users (name, age, email) VALUES
('张伟', 25, 'zhangwei@example.com'),
('王芳', 28, 'wangfang@example.com'),
('李强', 32, 'liqiang@example.com'),
('赵敏', 22, 'zhaomin@example.com'),
('刘洋', 30, 'liuyang@example.com'),
('陈杰', 27, 'chenjie@example.com'),
('杨光', 35, 'yangguang@example.com'),
('黄磊', 40, 'huanglei@example.com'),
('周涛', 29, 'zhoutao@example.com'),
('吴艳', 24, 'wuyan@example.com'),
('徐勇', 33, 'xuyong@example.com'),
('孙丽', 26, 'sunli@example.com'),
('朱峰', 31, 'zhufeng@example.com'),
('马超', 23, 'machao@example.com'),
('胡斌', 38, 'hubin@example.com'),
('郭林', 29, 'guolin@example.com'),
('何静', 21, 'hejing@example.com'),
('高翔', 34, 'gaoxiang@example.com'),
('罗浩', 26, 'luohao@example.com'),
('郑洁', 28, 'zhengjie@example.com'),
('梁栋', 36, 'liangdong@example.com'),
('谢宇', 25, 'xieyu@example.com'),
('宋涛', 30, 'songtao@example.com'),
('唐甜', 22, 'tangtian@example.com'),
('许杰', 27, 'xujie@example.com'),
('邓军', 45, 'dengjun@example.com'),
('冯伟', 31, 'fengwei@example.com'),
('韩梅', 24, 'hanmei@example.com'),
('曹磊', 33, 'caolei@example.com'),
('曾敏', 29, 'zengmin@example.com');