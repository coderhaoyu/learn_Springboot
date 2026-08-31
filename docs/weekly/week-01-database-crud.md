# 第 1 周：用户数据库 CRUD

> 建议投入：15–18 小时
> 对应总计划：[PLAN.md](../../PLAN.md)
> 产品设计：[我们的冒险](../couple-challenge-design.md)

## 当前目标

把当前用户模块作为整个应用的后端基础，确认一条完整的数据访问链路：

```text
HTTP 请求 → UserController → UserService → UserMapper → MySQL → JSON
```

本周不开发情侣、挑战和前端功能，只把用户新增、详情、分页、修改、删除做稳定。

## 当前已有基础

重点阅读和检查：

- `src/main/java/com/example/userdemo/controller/UserController.java`；
- `src/main/java/com/example/userdemo/service/UserService.java`；
- `src/main/java/com/example/userdemo/mapper/UserMapper.java`；
- `src/main/resources/mapper/UserMapper.xml`；
- `src/main/java/com/example/userdemo/entity/User.java`；
- `sql/schema.sql`；
- `src/main/resources/application.properties`。

## 本周完成标准

- [ ] `users` 表结构、主键、唯一约束和时间字段能够解释；
- [ ] 用户新增、详情、分页、修改、删除全部访问 MySQL；
- [ ] 应用重启后数据仍然存在；
- [ ] Mapper 接口和 XML 的 namespace、方法 id、参数名称匹配；
- [ ] 能解释自增主键如何回填到 Java 对象；
- [ ] 能解释 `#{}` 参数如何进入 SQL；
- [ ] Controller 不直接调用 Mapper；
- [ ] 只验证本周涉及的用户文件和数据库脚本。

## 编写步骤

### 第 1 步：确认内存版和数据库版的差异

1. 画出用户详情请求的数据流；
2. 说明重启应用为什么会丢失内存数据；
3. 记录数据库持久化带来的变化；
4. 检查每个接口的 HTTP 方法、路径参数和请求体。

### 第 2 步：设计并检查 `users` 表

先自己写出字段清单，再确认：

- 哪个字段是主键；
- 邮箱为什么需要唯一约束；
- 哪些字段允许为空；
- 时间由数据库还是 Java 维护；
- 查询和修改最常用的条件是什么。

### 第 3 步：完成最小查询闭环

先只验证“按 id 查询”：

```text
Controller 接收 id
    ↓
Service 调用 Mapper
    ↓
Mapper 找到 XML 中的 SELECT
    ↓
MySQL 返回一行
    ↓
结果映射成 User
    ↓
Service 转成 UserVo
```

### 第 4 步：逐个迁移剩余操作

建议顺序：

1. 列表和分页；
2. 新增；
3. 修改；
4. 删除。

每完成一个操作都立即验证：

```text
准备数据 → 发送请求 → 查看响应 → 查询数据库 → 重启应用后再次查询
```

## 验证清单

| 场景 | 检查内容 |
|---|---|
| 新增用户 | 数据库出现新记录，主键正确生成 |
| 查询详情 | 返回记录与数据库一致 |
| 分页查询 | `LIMIT`、`OFFSET` 和总数正确 |
| 修改用户 | 只有目标 id 的记录发生变化 |
| 删除用户 | 删除后再次查询得到不存在结果 |
| 重启应用 | 已有数据仍然存在 |
| 重复邮箱 | 为第 2 周的业务冲突处理准备数据 |

## 本周暂不处理

- 注册和登录；
- 密码摘要；
- JWT；
- 情侣绑定；
- 挑战和打卡；
- 前端工程；
- 复杂并发控制。

## 周末复盘问题

1. `@PathVariable` 和 `@RequestBody` 分别从请求的哪里取得数据？
2. Mapper 接口没有手写实现类，为什么仍然可以注入？
3. XML 中的 `namespace` 和接口全限定名有什么关系？
4. 查询结果为什么先映射成 Entity，再转换为 VO？
5. 为什么分页通常需要列表查询和总数查询两条 SQL？
6. 数据库唯一约束和 Service 查重分别解决什么问题？
