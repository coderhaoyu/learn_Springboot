# 第 8 章 Spring Boot 启动、配置和常用注解

## 本章目标

- 理解 Spring Boot 启动类、自动配置和组件扫描。
- 掌握常用组件、配置和事务注解。
- 知道配置如何从 Java 代码中分离出来。

> 本章是独立学习章节。建议先阅读本章，再完成文末练习，并回到项目中寻找对应代码。

### 8.1 `@SpringBootApplication`

当前启动类：

```java
@SpringBootApplication
@MapperScan("com.example.userdemo.mapper")
public class UserDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserDemoApplication.class, args);
    }
}
```

`@SpringBootApplication` 可以先理解为三个能力的组合：

```text
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
```

分别表示：

- 这是 Spring Boot 配置入口；
- 根据项目依赖自动准备基础设施；
- 扫描启动类所在包及其子包中的组件。

因此启动类通常放在根包 `com.example.userdemo` 下。

### 8.2 组件注解

| 注解 | 作用 | 当前项目中的位置 |
|---|---|---|
| `@Component` | 注册通用组件 Bean | 通用组件 |
| `@Service` | 标记业务组件 | `UserService` |
| `@Repository` | 标记持久化组件 | 手写 DAO 时常见 |
| `@Controller` | 标记 MVC 控制器 | 返回视图时常见 |
| `@RestController` | Controller + 直接返回响应体 | `UserController` |

`@Service`、`@Repository` 本质上属于更有语义的组件标记。它们不能改变分层职责：

- Controller 仍然不应该直接写 SQL；
- Service 仍然不应该拼装 HTTP 专用细节；
- Mapper 仍然只负责数据访问。

### 8.3 配置注解

| 注解 | 作用 |
|---|---|
| `@Configuration` | 标记 Java 配置类 |
| `@Bean` | 把方法返回的对象注册为 Bean |
| `@Value` | 注入单个配置值 |
| `@ConfigurationProperties` | 把一组配置绑定到类型明确的对象 |
| `@Profile` | 按环境启用配置或 Bean，后续学习 |

示例：

```java
@Configuration
public class AppConfig {

    @Bean
    public SomeClient someClient() {
        // 第三方对象无法直接修改源码时，在配置类集中描述创建过程
        return new SomeClient();
    }
}
```

### 8.4 `application.properties`

当前配置文件中：

```properties
spring.application.name=user-demo
spring.datasource.url=jdbc:mysql://localhost:3306/user_demo
mybatis.mapper-locations=classpath:mapper/*.xml
```

配置的作用是把环境差异从 Java 代码中拿出来：

```text
Java 代码保持稳定
    ↓
开发、测试、生产通过不同配置连接不同资源
```

密码和密钥后续不要直接写入仓库，应使用环境变量或安全配置注入。

### 8.5 `@Transactional`

事务表示一组数据库操作要么全部成功，要么全部回滚。

例如创建项目需要：

```text
插入 projects
    ↓
插入 project_members（负责人）
```

如果第二步失败，第一步也应该回滚。事务边界通常放在 Service 的业务方法上：

```java
@Transactional
public void createProject(ProjectCreateRequest request) {
    // 插入项目
    // 插入负责人成员记录
}
```

记住：

1. 事务边界通常位于 Service；
2. 不是所有方法都需要事务；
3. 事务解决的是一组操作的一致性，不是参数格式校验；
4. 通过 Spring Bean 代理调用时，事务声明才会参与运行。

### 8.6 常见启动问题

| 现象 | 优先检查 |
|---|---|
| 找不到 Bean | 组件注解、包扫描、构造方法依赖 |
| Mapper 无法注入 | `@MapperScan` 包路径、接口和 XML |
| 配置未生效 | 属性名、配置文件位置、环境配置 |
| 数据库连接失败 | URL、端口、用户名、密码、数据库是否启动 |

---

上一章：[`第 7 章`](chapter-07-spring-ioc-di.md)

下一章：[`第 9 章`](chapter-09-spring-mvc-http.md)
返回目录：[`README.md`](README.md)
