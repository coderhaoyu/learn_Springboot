# 第 9 章 Spring MVC 与 HTTP 请求

## 本章目标

- 理解 HTTP 请求如何进入 Controller。
- 掌握路由、路径参数、查询参数、模型属性和 JSON 请求体。
- 能够为 CRUD 接口选择合适的 HTTP 方法和状态码。

> 本章是独立学习章节。建议先阅读本章，再完成文末练习，并回到项目中寻找对应代码。

### 9.1 Controller 的职责

Controller 负责：

1. 声明路径和 HTTP 方法；
2. 接收路径参数、查询参数和请求体；
3. 触发参数格式校验；
4. 调用 Service；
5. 返回响应对象。

Controller 不负责：

- 直接调用 Mapper；
- 查邮箱是否重复；
- 手动拼接 SQL；
- 编写复杂权限流程；
- 在每个接口重复处理异常。

### 9.2 路由映射注解

| 注解 | HTTP 方法 | 当前用途 |
|---|---|---|
| `@RequestMapping` | 可定义公共路径和其他匹配条件 | 类级别 `/users` |
| `@GetMapping` | GET | 列表、详情、查询 |
| `@PostMapping` | POST | 新增 |
| `@PutMapping` | PUT | 整体修改 |
| `@PatchMapping` | PATCH | 局部修改，后续状态流转可使用 |
| `@DeleteMapping` | DELETE | 删除 |

类级别和方法级别路径会拼接：

```text
@RequestMapping("/users")
    + @GetMapping("/{id}")
    = GET /users/{id}
```

### 9.3 `@PathVariable`

从 URL 路径中取值：

```java
@GetMapping("/{id}")
public ApiResponse<UserVo> getUserInfoById(@PathVariable long id) {
    // id 来自 /users/123 中的 123
    return ApiResponse.ok(userService.findById(id));
}
```

适合资源编号：用户 id、情侣关系 id、挑战 id、打卡 id。

### 9.4 `@RequestParam`

从查询字符串中取值：

```java
@GetMapping("/by-email")
public ApiResponse<UserVo> getUserInfoByEmail(@RequestParam String email) {
    return ApiResponse.ok(userService.getUserInfoByEmail(email));
}
```

对应请求：

```text
GET /users/by-email?email=user@example.com
```

适合筛选条件、分页参数、搜索关键词。

### 9.5 `@ModelAttribute`

复杂对象可以从查询参数或表单字段绑定：

```java
@GetMapping
public PageResult<UserVo> list(
        @Valid @ModelAttribute UserPageQueryRequest request) {
    return userService.findPage(request);
}
```

当前项目省略了 `@ModelAttribute`：

```java
public PageResult<UserVo> list(@Valid UserPageQueryRequest request)
```

Spring MVC 对这类复杂参数有默认的模型属性绑定规则。学习时要知道：`page` 和 `size` 来自查询参数，不是 JSON 请求体。

### 9.6 `@RequestBody`

从 JSON 请求体转换成 Java 对象：

```java
@PostMapping
public ApiResponse<Boolean> addUser(
        @Valid @RequestBody CreateUserRequest request) {
    userService.addUser(request);
    return ApiResponse.ok(true);
}
```

数据流：

```text
JSON 请求体
    ↓
消息转换器反序列化
    ↓
CreateUserRequest
    ↓
Controller 方法参数
```

### 9.7 `@RestController`

`@RestController` 可以理解为：

```text
@Controller + @ResponseBody
```

方法返回的 Java 对象会被序列化成 JSON，而不是被当成视图名称。

### 9.8 HTTP 方法和状态码

| 方法 | 典型语义 |
|---|---|
| GET | 查询资源，不应以修改数据为主要目的 |
| POST | 创建资源或提交动作 |
| PUT | 整体替换或修改资源 |
| PATCH | 局部修改 |
| DELETE | 删除资源 |

| 状态码 | 当前项目中的场景 |
|---:|---|
| 200 | 查询、修改、删除成功 |
| 201 | 创建成功，后续可按接口契约使用 |
| 400 | 参数格式或校验失败 |
| 401 | 未通过身份认证 |
| 403 | 已认证但没有权限 |
| 404 | 资源不存在 |
| 409 | 与当前数据状态冲突，例如邮箱重复 |
| 500 | 未预期的系统错误 |

---

上一章：[`第 8 章`](chapter-08-spring-boot-annotations-config.md)

下一章：[`第 10 章`](chapter-10-validation-dto-vo-exception.md)
返回目录：[`README.md`](README.md)
