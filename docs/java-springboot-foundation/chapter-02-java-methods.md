# 第 2 章 Java 方法（重点）

## 本章目标

- 能够拆解 Java 方法签名，理解参数、返回值和 void。
- 掌握构造方法、this、重载、static 和泛型方法。
- 能够根据 Controller、Service、Mapper 的职责设计方法。

> 本章是独立学习章节。建议先阅读本章，再完成文末练习，并回到项目中寻找对应代码。


> 这是本手册的重点章节。建议至少阅读两遍，并结合当前 `UserService`、`UserController` 和 `ApiResponse` 练习。

### 2.1 方法的基本结构

```java
访问修饰符 返回类型 方法名(参数列表) {
    // 方法体
    return 返回值;
}
```

当前项目中的方法：

```java
public UserVo findById(long id) {
    User user = userMapper.findById(id);
    return convertToVo(user);
}
```

拆开看：

| 部分 | 示例 | 含义 |
|---|---|---|
| 访问修饰符 | `public` | 哪些代码可以调用 |
| 返回类型 | `UserVo` | 方法完成后交出的结果类型 |
| 方法名 | `findById` | 方法表达的动作 |
| 参数 | `long id` | 调用者必须提供的数据 |
| 方法体 | `{ ... }` | 真正执行的步骤 |
| `return` | `return ...` | 返回结果并结束当前方法 |

### 2.2 阅读方法签名的顺序

看到下面的方法：

```java
public PageResult<UserVo> findPage(UserPageQueryRequest request)
```

按这个顺序阅读：

1. `public`：外部类可以调用；
2. `PageResult<UserVo>`：返回分页结果，列表元素是 `UserVo`；
3. `findPage`：方法要执行分页查询；
4. `UserPageQueryRequest request`：需要一个分页请求对象；
5. 没有 `throws`：签名没有额外声明受检异常。

### 2.3 `void` 和 `return`

没有业务返回值时使用 `void`：

```java
public void deleteUser(long id) {
    userMapper.deleteUser(id);
}
```

`void` 方法不能返回一个业务值，但可以通过 `throw` 表示失败：

```java
public void deleteUser(long id) {
    if (/* 用户不存在 */) {
        throw new BusinessException(404, "用户不存在");
    }

    userMapper.deleteUser(id);
}
```

上面条件中的具体查询由 Service 负责，示例只展示方法控制流。

### 2.4 参数是什么

参数是方法和调用者之间的输入契约：

```java
public UserVo findById(long id)
```

表示：

```text
调用 findById 时，必须提供一个 long 类型的 id
方法完成后，返回一个 UserVo
```

参数可以是：

- 基本类型：`long id`、`int size`；
- 字符串：`String email`；
- 对象：`CreateUserRequest request`；
- 集合：`List<User> users`；
- 泛型对象：`PageResult<UserVo>`。

### 2.5 Java 的值传递

Java 始终是值传递。传入对象时，传递的是对象引用的副本：

```text
调用者持有 User 引用 A
    ↓
方法接收到引用 A 的副本
    ↓
方法可以通过这份引用修改同一个 User 对象
    ↓
但方法参数重新指向另一个对象，不会改变调用者的变量指向
```

这也是 Service 可以创建 `User`、设置属性、再交给 Mapper 保存的原因。

### 2.6 方法调用和调用链

```java
UserVo user = userService.findById(id);
```

可以拆成：

```text
Controller 调用 userService 的 findById
    ↓
Service 调用 userMapper 的 findById
    ↓
Mapper 执行 XML 中对应的 SQL
    ↓
Service 把 User 转换为 UserVo
    ↓
Controller 把 UserVo 包装成响应
```

方法调用链就是后端分层执行的具体表现。

### 2.7 构造方法

构造方法用于创建对象：

```java
public class User {
    private Long id;
    private String name;

    public User() {
    }

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
```

特点：

- 名称必须和类名相同；
- 没有返回类型；
- 使用 `new` 创建对象时调用；
- 可以重载多个构造方法。

```java
User emptyUser = new User();
User user = new User(1L, "阿西");
```

### 2.8 `this`

```java
public void setName(String name) {
    this.name = name;
}
```

- `this.name`：当前对象的字段；
- `name`：方法参数；
- `this`：当前对象本身。

### 2.9 方法重载

同一个类中，方法名相同、参数列表不同，可以构成重载：

```java
public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<>(200, "success", data);
}

public static <T> ApiResponse<T> ok() {
    return new ApiResponse<>(200, "success", null);
}
```

下面这种不算重载：

```text
只改变返回类型，参数列表完全相同
```

### 2.10 实例方法和静态方法

实例方法依赖对象：

```java
userService.findById(id);
```

静态方法属于类：

```java
ApiResponse.ok(user);
```

判断方法是否应该是 `static`：

```text
是否需要当前对象的字段或注入依赖？
    是 → 通常是实例方法
    否 → 可以考虑 static 工具方法
```

`UserService.findById` 需要 `userMapper`，所以是实例方法；`ApiResponse.ok` 只负责创建响应对象，可以设计成静态方法。

### 2.11 泛型方法

当前项目中的：

```java
public static <T> ApiResponse<T> ok(T data)
```

从左到右看：

- 第一个 `<T>`：声明方法有一个类型参数；
- `ApiResponse<T>`：返回值使用这个类型参数；
- `T data`：参数也使用这个类型参数。

调用时，编译器可以推导 `T`：

```java
ApiResponse<UserVo> response = ApiResponse.ok(userVo);
ApiResponse<Boolean> response = ApiResponse.ok(true);
```

### 2.12 方法引用和 Lambda

当前 Service 中的转换：

```java
List<UserVo> userVoList = userList.stream()
        .map(this::convertToVo)
        .toList();
```

读法：

```text
把 userList 变成流
    ↓
每个 User 都调用 convertToVo
    ↓
收集成 List<UserVo>
```

`this::convertToVo` 是方法引用。初学时先能用普通循环写出相同逻辑，再学习 Stream：

```java
List<UserVo> userVoList = new ArrayList<>();
for (User user : userList) {
    userVoList.add(convertToVo(user));
}
```

### 2.13 常见方法设计错误

| 问题 | 结果 | 思考方向 |
|---|---|---|
| 方法名过于宽泛，如 `handle()` | 调用者不知道它做什么 | 用动作和对象命名 |
| 一个方法同时查库、校验、拼响应 | 难以测试和复用 | 拆分层次和职责 |
| 返回 `Object` | 类型信息丢失 | 返回明确类型或泛型类型 |
| 用特殊字符串表示失败 | 容易被误当成正常结果 | 抛出明确异常 |
| 为了少写方法把所有逻辑塞进 Controller | 业务无法复用 | 把业务流程放到 Service |

### 2.14 方法练习

1. 解释 `public UserVo findById(long id)` 每一部分的作用；
2. 写一个接收 `List<User>`、返回 `List<UserVo>` 的方法；
3. 写一个接收 `String email`、返回 `UserVo` 的方法；
4. 解释 `ApiResponse.ok(true)` 中的 `T` 是什么；
5. 用普通循环实现一次，再用 Stream 实现一次；
6. 说明 `UserController` 的方法为什么不应该直接写 SQL。

---

上一章：[`第 1 章`](chapter-01-java-types.md)

下一章：[`第 3 章`](chapter-03-java-oop-generics.md)
返回目录：[`README.md`](README.md)
