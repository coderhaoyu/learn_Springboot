# 第 7 章 Spring IoC、Bean 和依赖注入

## 本章目标

- 理解 IoC、Bean、容器和依赖注入。
- 掌握构造方法注入以及 Spring 如何组装当前项目。
- 明确组件身份和分层职责不是同一件事。

> 本章是独立学习章节。建议先阅读本章，再完成文末练习，并回到项目中寻找对应代码。

### 7.1 IoC 是什么

IoC 是“Inversion of Control”，即控制反转。

不用 Spring 时，业务代码可能自己创建依赖：

```text
new UserMapperImpl()
    ↓
new UserService(userMapper)
    ↓
new UserController(userService)
```

使用 Spring 后：

```text
Spring 启动
    ↓
扫描组件
    ↓
创建 Bean
    ↓
寻找依赖
    ↓
调用构造方法完成注入
```

对象创建、依赖组装和生命周期管理从业务代码转移给 Spring 容器。

### 7.2 Bean 是什么

Bean 就是由 Spring 容器创建和管理的对象。

当前项目中的典型 Bean：

- `UserController`；
- `UserService`；
- MyBatis 创建的 `UserMapper` 代理；
- `GlobalExceptionHandler`；
- Spring Boot 自动配置的 Web 和数据库基础设施。

### 7.3 构造方法注入

```java
@Service
public class UserService {
    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
}
```

执行过程：

```text
Spring 发现 UserService 是 Bean
    ↓
发现构造方法需要 UserMapper
    ↓
从容器中找到 UserMapper
    ↓
调用构造方法
    ↓
UserService 可以使用 userMapper
```

优点：

- 依赖明确；
- 字段可以是 `final`；
- 对象创建完成后状态完整；
- 测试时容易传入替代依赖。

如果类只有一个构造方法，Spring 通常可以直接使用它完成注入，所以当前项目不需要额外写 `@Autowired`。

### 7.4 `@Autowired`

`@Autowired` 表示让 Spring 自动注入依赖，常见位置有：

- 构造方法；
- 字段；
- Setter 方法。

当前项目选择构造方法注入，不是没有依赖注入，而是依赖注入通过唯一构造方法完成。

### 7.5 分层和 Bean 身份不是一回事

`@Service` 让类成为 Bean，同时表达它是业务层组件；但它不会自动让类拥有所有业务能力。

```text
注解解决“由谁管理、属于什么身份”
分层规则解决“应该写什么逻辑”
```

---

上一章：[`第 6 章`](chapter-06-maven-project-structure.md)

下一章：[`第 8 章`](chapter-08-spring-boot-annotations-config.md)
返回目录：[`README.md`](README.md)
