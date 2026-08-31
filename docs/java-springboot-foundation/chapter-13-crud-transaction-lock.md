# 第 13 章 CRUD、分页、事务、索引和乐观锁

## 本章目标

- 理解 CRUD 请求在 Controller、Service、Mapper 和数据库之间的职责分配。
- 掌握分页、唯一约束、事务和联合索引的基本思路。
- 理解 version 字段和乐观锁冲突。

> 本章是独立学习章节。建议先阅读本章，再完成文末练习，并回到项目中寻找对应代码。

### 13.1 CRUD 和分层

一个新增请求的职责分配：

```text
Controller：接收 DTO，触发校验，调用 Service
    ↓
Service：判断业务规则，构造 Entity，组织操作
    ↓
Mapper：执行 INSERT
    ↓
数据库：保存记录并生成主键
    ↓
Service：转换结果
    ↓
Controller：返回 VO 或统一响应
```

### 13.2 分页的两条 SQL

分页通常需要：

```sql
-- 当前页数据
SELECT id, name, age, email
FROM users
ORDER BY id
LIMIT #{limit} OFFSET #{offset};

-- 符合条件的总数量
SELECT COUNT(*)
FROM users;
```

原因：

- 列表 SQL 只取当前页；
- `COUNT(*)` 给前端计算总页数；
- 两者职责不同，不能用当前页数量代替总数。

### 13.3 Service 查重和数据库唯一约束

只在 Service 先查重存在并发竞态：

```text
请求 A 查询：邮箱不存在
请求 B 查询：邮箱不存在
请求 A 插入
请求 B 插入
```

因此：

- Service 查重：提供清晰、友好的业务提示；
- 数据库唯一约束：保证并发场景下的数据一致性。

### 13.4 事务边界

创建项目和负责人成员记录属于一个业务动作：

```text
insert project
    +
insert project_member
    =
一个完整业务事务
```

把事务放在 Service，是因为 Service 能看见一次业务动作包含哪些 Mapper 调用。

### 13.5 乐观锁

任务表可以增加 `version` 字段：

```text
读取任务时得到 version = 3
    ↓
更新时要求 id 匹配且 version = 3
    ↓
更新成功后 version 变成 4
```

概念 SQL：

```sql
UPDATE tasks
SET title = #{title},
    version = version + 1
WHERE id = #{id}
  AND version = #{version};
```

如果受影响行数为 0，说明记录已经被其他请求修改，应返回 409 冲突。

### 13.6 索引和查询设计

索引不是“字段越多越好”。设计时从查询出发：

```text
业务查询
    ↓
WHERE / JOIN / ORDER BY 使用的字段
    ↓
评估选择性和字段顺序
    ↓
创建索引
    ↓
EXPLAIN 验证
```

### 13.7 CRUD 验收矩阵

| 场景 | 预期 |
|---|---|
| 新增合法用户 | 成功写入数据库 |
| 重复邮箱 | 409 |
| 非法邮箱 | 400 |
| 查询存在用户 | 返回 VO |
| 查询不存在用户 | 404 |
| 修改用户 | 数据发生变化 |
| 删除用户 | 后续查询不到 |
| 分页查询 | 列表、总数、页码正确 |

---

上一章：[`第 12 章`](chapter-12-mybatis-mapping.md)

下一章：[`第 14 章`](chapter-14-testing-debugging.md)
返回目录：[`README.md`](README.md)
