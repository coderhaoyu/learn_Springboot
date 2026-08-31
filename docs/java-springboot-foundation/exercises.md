# 第一次学习任务

## 任务 1：拆解用户详情接口

请自己填写：

```text
请求：GET /users/{id}

Controller 方法：
输入：
输出：

Service 方法：
输入：
输出：

Mapper 方法：
输入：
输出：

XML SQL：
查询哪张表：
查询哪些字段：
WHERE 条件：

异常场景：
响应对象：
```

## 任务 2：解释新增用户请求

回答：

1. JSON 如何变成 `CreateUserRequest`？
2. `@NotBlank`、`@Email`、`@Min` 分别检查什么？
3. `User` 对象在哪里构造？
4. `#{name}`、`#{age}`、`#{email}` 如何得到值？
5. 数据库如何生成主键？
6. 重复邮箱由哪几层共同保护？

## 任务 3：专门练习方法

暂时不写 Controller，只写普通 Java 类：

```text
定义 User 类
    ↓
定义 UserVo 类
    ↓
定义一个转换方法 User → UserVo
    ↓
定义一个按 id 查询的方法
    ↓
查询不到时抛出 BusinessException
```

完成后，对照当前 `/Users/wuhaoyu/IdeaProjects/user-demo/src/main/java/com/example/userdemo/service/UserService.java`，找出相同点和不同点。

---
