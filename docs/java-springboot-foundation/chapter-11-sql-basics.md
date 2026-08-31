# 第 11 章 数据库基础和 SQL 语法

## 本章目标

- 掌握表、行、列、主键、约束和常见 MySQL 类型。
- 读懂 CREATE TABLE、SELECT、INSERT、UPDATE、DELETE、JOIN 和分页 SQL。
- 理解事务、索引、EXPLAIN 和参数安全。

> 本章是独立学习章节。建议先阅读本章，再完成文末练习，并回到项目中寻找对应代码。


> 本章专门讲数据库语法。实际项目建表脚本位于 `/Users/wuhaoyu/IdeaProjects/user-demo/sql/schema.sql`，本章示例用于理解语法，不代替项目设计。

### 11.1 表、行、列和主键

可以先这样理解：

- 表：一类数据的集合；
- 行：一条具体记录；
- 列：记录的一个属性；
- 主键：唯一标识一行数据。

当前 `users` 表的概念关系：

```text
一行 users 记录
    ↔
一个 User 对象
```

### 11.2 常见数据类型

| MySQL 类型 | Java 常见类型 | 说明 |
|---|---|---|
| `BIGINT` | `Long` / `long` | 编号、较大整数 |
| `INT` | `Integer` / `int` | 普通整数 |
| `VARCHAR(n)` | `String` | 有长度限制的字符串 |
| `DATETIME` | `LocalDateTime` | 日期和时间 |
| `TINYINT` | `Boolean` 或整数 | 状态值，需统一约定 |
| `DECIMAL` | `BigDecimal` | 精确小数和金额 |

### 11.3 建表 `CREATE TABLE`

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    age INT NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

逐项理解：

- `PRIMARY KEY`：主键；
- `AUTO_INCREMENT`：由数据库生成递增编号；
- `NOT NULL`：不允许为空；
- `UNIQUE`：值不能重复；
- `DEFAULT`：没有显式传值时使用默认值。

### 11.4 新增 `INSERT`

```sql
INSERT INTO users (name, age, email)
VALUES ('阿西', 28, 'user@example.com');
```

写 SQL 前先明确：

```text
往哪张表写？
写哪些列？
每一列的值从哪里来？
哪些列由数据库自动生成？
```

### 11.5 查询 `SELECT`

```sql
SELECT id, name, age, email
FROM users;
```

按条件查询：

```sql
SELECT id, name, age, email
FROM users
WHERE id = 1;
```

查询邮箱：

```sql
SELECT id, name, age, email
FROM users
WHERE email = 'user@example.com';
```

### 11.6 `WHERE` 常用条件

| 写法 | 含义 |
|---|---|
| `=` | 等于 |
| `<>` | 不等于 |
| `>` / `<` | 大于 / 小于 |
| `>=` / `<=` | 大于等于 / 小于等于 |
| `IN (...)` | 在多个值中匹配 |
| `LIKE` | 模糊匹配 |
| `IS NULL` | 判断为空 |
| `AND` | 同时满足 |
| `OR` | 满足其一 |

示例：

```sql
SELECT id, name, age, email
FROM users
WHERE age >= 18
  AND name LIKE '%阿%';
```

### 11.7 修改 `UPDATE`

```sql
UPDATE users
SET name = '新名字',
    age = 29
WHERE id = 1;
```

修改语句最重要的检查点：

```text
WHERE 条件是否准确？
会影响一行、几行，还是所有行？
```

### 11.8 删除 `DELETE`

```sql
DELETE FROM users
WHERE id = 1;
```

删除前先确认查询条件。业务上是否真的采用物理删除，要结合模块需求、审计和数据恢复要求决定。

### 11.9 排序 `ORDER BY`

```sql
SELECT id, name, age, email
FROM users
ORDER BY id DESC;
```

- `ASC`：升序；
- `DESC`：降序。

分页查询通常需要稳定排序，否则同一页数据可能在不同请求中发生变化。

### 11.10 分页 `LIMIT` 和 `OFFSET`

```sql
SELECT id, name, age, email
FROM users
ORDER BY id
LIMIT 10 OFFSET 20;
```

含义：

```text
跳过 20 条
取出 10 条
```

前端页码和 SQL 偏移量的换算：

```text
offset = (page - 1) * size
```

### 11.11 聚合函数和总数

```sql
SELECT COUNT(*)
FROM users;
```

常见聚合函数：

| 函数 | 作用 |
|---|---|
| `COUNT` | 统计数量 |
| `SUM` | 求和 |
| `AVG` | 平均值 |
| `MAX` | 最大值 |
| `MIN` | 最小值 |

分组：

```sql
SELECT status, COUNT(*)
FROM tasks
GROUP BY status;
```

### 11.12 `JOIN`

当数据分布在多张表中，需要通过关联字段组合查询：

```sql
SELECT p.id, p.name, u.name AS owner_name
FROM projects p
JOIN users u ON u.id = p.owner_id
WHERE p.id = 1;
```

阅读 JOIN 时先找：

1. 主表是哪张；
2. 关联表是哪张；
3. 两张表通过哪些字段关联；
4. 需要返回哪张表的字段；
5. 是否需要 `LEFT JOIN` 保留没有关联记录的主表数据。

### 11.13 事务 SQL

```sql
START TRANSACTION;

-- 多条相关写操作

COMMIT;
```

发生错误时：

```sql
ROLLBACK;
```

在 Spring 项目中通常不手动在业务代码中执行这些语句，而是通过 Service 上的 `@Transactional` 让 Spring 管理事务边界。

### 11.14 索引和 `EXPLAIN`

索引帮助数据库更快定位数据，但会增加写入和维护成本：

```sql
CREATE INDEX idx_tasks_project_status
ON tasks (project_id, status);
```

使用执行计划：

```sql
EXPLAIN
SELECT id, title
FROM tasks
WHERE project_id = 1
  AND status = 'TODO';
```

设计索引前先问：

- 哪条查询频繁？
- WHERE、JOIN、ORDER BY 使用了哪些列？
- 联合索引的字段顺序为什么这样安排？
- 是否真的通过 `EXPLAIN` 验证过？

### 11.15 SQL 和参数安全

不要把客户端原始输入直接拼进 SQL：

```text
错误方向：把字符串直接拼接到 SQL
正确方向：使用参数绑定
```

MyBatis 中优先使用 `#{}`，让参数作为值绑定，而不是把文本直接拼成 SQL。

---

上一章：[`第 10 章`](chapter-10-validation-dto-vo-exception.md)

下一章：[`第 12 章`](chapter-12-mybatis-mapping.md)
返回目录：[`README.md`](README.md)
