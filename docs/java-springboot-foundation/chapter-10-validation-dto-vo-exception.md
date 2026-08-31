# 第 10 章 校验、DTO、VO、异常与响应

## 本章目标

- 区分 DTO、Entity、VO 的边界和数据流向。
- 掌握 Bean Validation 和常见校验注解。
- 理解全局异常处理和统一响应结构。

> 本章是独立学习章节。建议先阅读本章，再完成文末练习，并回到项目中寻找对应代码。

### 10.1 DTO、Entity、VO

#### DTO：请求边界

例如 `CreateUserRequest`：

- 接收前端允许提交的字段；
- 承载输入格式校验；
- 防止客户端提交不应该修改的内部字段。

#### Entity：持久化对象

例如 `User`：

- 对应数据库表字段；
- 在 Service 和 Mapper 之间传递；
- 不默认作为接口响应对象。

#### VO：输出边界

例如 `UserVo`：

- 控制对外返回字段；
- 隐藏内部字段；
- 可以根据页面需要调整结构。

数据流：

```text
请求 JSON → DTO → Entity → SQL
SQL 结果 → Entity → VO → 响应 JSON
```

### 10.2 Bean Validation 注解

| 注解 | 含义 |
|---|---|
| `@NotBlank` | 字符串不能为 null、空字符串或全空白 |
| `@NotNull` | 值不能为 null |
| `@Size` | 字符串或集合长度在指定范围 |
| `@Min` | 数值不能小于指定值 |
| `@Max` | 数值不能大于指定值 |
| `@Email` | 字符串应符合邮箱格式 |

### 10.3 `@Valid` 和 `@Validated`

```java
@Validated
@RestController
public class UserController {
}
```

```java
public ApiResponse<Boolean> addUser(
        @Valid @RequestBody CreateUserRequest request) {
    // 请求体转换后，先执行 DTO 上的格式校验
    return ApiResponse.ok(true);
}
```

可以这样区分：

- `@Valid`：触发对象校验，常和 `@RequestBody` 一起使用；
- `@Validated`：启用 Spring 的校验能力，常放在 Controller 类上，也支持校验分组。

### 10.4 三类校验必须分开

```text
格式校验：邮箱格式是否正确？年龄是否在范围内？
    ↓
Service 业务校验：邮箱是否已被其他用户使用？
    ↓
数据库约束：并发情况下也不能出现重复邮箱
```

`@Email` 不能判断邮箱是否已经存在；Service 查重也不能替代数据库唯一约束。

### 10.5 统一响应

当前项目使用泛型响应：

```java
ApiResponse<UserVo>
ApiResponse<Boolean>
ApiResponse<Void>
```

泛型让不同接口拥有统一外壳，同时保留具体数据类型。

分页响应：

```java
PageResult<UserVo>
```

通常包含：

- 当前页列表；
- 总记录数；
- 当前页码；
- 每页数量；
- 总页数。

### 10.6 全局异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException exception) {
        // 集中把业务异常转换成统一 HTTP 响应，避免每个 Controller 重复 try/catch
        return ResponseEntity
                .status(exception.getCode())
                .body(ApiResponse.error(
                        exception.getCode(), exception.getMessage()));
    }
}
```

执行流程：

```text
Service 抛出 BusinessException
    ↓
异常离开 Controller 方法
    ↓
@RestControllerAdvice 捕获
    ↓
@ExceptionHandler 选择处理方法
    ↓
返回状态码和 ApiResponse
```

---

# 第三部分：数据库与 MyBatis

---

上一章：[`第 9 章`](chapter-09-spring-mvc-http.md)

下一章：[`第 11 章`](chapter-11-sql-basics.md)
返回目录：[`README.md`](README.md)
