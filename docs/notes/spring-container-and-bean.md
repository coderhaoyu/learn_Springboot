# Spring 容器与 Bean：注解背后发生了什么

> 起因：CRUD 和登录都写出来了，但不清楚 `@Component`、`@Service`、`@Bean` 这些注解到底做了什么
> 相关周计划：[第 3 周：认证与安全](../weekly/week-03-auth-security.md)
> 所有示例取自本项目真实代码

## 怎么读这份笔记

分三层，**按顺序读，随时可以停**：

- **第一层（必读）**：容器是什么、三条铁律。看完就能看懂项目里的注解在干什么。
- **第二层**：对照项目代码，把第一层的说法落到具体的类上。
- **第三层（以后再看）**：代理、启动顺序这些细节。现在跳过完全不影响写代码，等撞上问题再回来查。

---

# 第一层 · 必读

## 一、容器是什么：一个比喻

把 Spring 容器想成公司的**行政部**。

你入职第一天需要一台电脑。你不会自己跑去电脑城买（`new`），而是填一张申请单写上「我需要一台电脑」，行政部就把电脑送到你桌上。

Spring 里一模一样：

```java
// 你不这样做（自己去买）
public class AuthService {
    private UserMapper mapper = new UserMapperImpl();   // 自己 new
}

// 你这样做（填申请单）
public class AuthService {
    private final UserMapper mapper;

    public AuthService(UserMapper mapper) {   // 构造器参数 = 申请单
        this.mapper = mapper;
    }
}
```

构造器的参数列表就是申请单：**「我需要一个 UserMapper」**。谁去创建、创建几个、什么时候创建，全都不是你的事。

容器做两件事：

1. **启动时**：把项目里需要的对象全部创建好，放进一个仓库
2. **运行时**：谁的申请单上写了什么类型，就从仓库里拿一个给他

仓库里的这些对象有个名字，叫 **Bean**。

> 前端对照：Angular 的 `@Injectable` + 构造器注入几乎一模一样。React 里最接近的是 Context + Provider —— 顶层提供，深层组件直接取，中间层不用逐层传。

## 二、没有容器会怎样

假设 Spring 不存在，要让 `POST /auth/login` 跑起来，得自己手写装配：

```java
UserMapper mapper = ???                          // 接口，没有实现类，根本没法 new
PasswordEncoder encoder = new BCryptPasswordEncoder();
JwtService jwtService = new JwtService("密钥从哪读", 120);
AuthService authService = new AuthService(mapper, encoder, jwtService);
AuthController controller = new AuthController(authService);
```

三个绕不过去的问题：

1. **`UserMapper` 是接口，没法 new。** 项目里根本没有 `UserMapperImpl` 这个类。
2. **`AuthController` 被迫认识整条链。** 它只想要 `AuthService`，却得先知道 `AuthService` 需要哪三样东西，那三样又各自需要什么。
3. **这段代码写在哪、谁来执行、创建出来的对象存在哪个变量里？**

类少的时候能手工扛住，类一多就没法维护了。容器解决的就是这件事，它有个正式名字叫**依赖装配**。

## 三、两个术语（就这两句）

- **IoC（控制反转）**：原来是「我要什么我自己 new」，反转成「我声明我要什么，别人给我」。反转的是**创建对象的控制权**。
- **DI（依赖注入）**：实现 IoC 的具体手段 —— 通过构造器参数把依赖递进来。

名词记不住无所谓，行为记住就行：**不要自己 new，在构造器参数里声明。**

## 四、三条铁律

新手 90% 的「为什么不生效」都是撞了这三条。

**铁律一：自己 `new` 出来的对象拿不到任何注入。**

```java
AuthService s = new AuthService(...);   // 这个对象和容器毫无关系
```

它身上的 `@Value`、`@Transactional` 全部等同于注释。注解不是魔法，是**容器在创建对象时**才会去处理的标记。

**铁律二：类必须放在启动类所在的包或它的子包下，才会被扫描到。**

启动类在 `com.example.ouradventure`，所以 `com.example.ouradventure.service.AuthService` 能被扫到；放到 `com.example.other` 就扫不到，启动时报错说找不到 Bean。

**铁律三：Bean 默认是单例，所有请求共用同一个实例 —— 所以不要放可变的实例字段。**

```java
@Service
public class UserService {
    private User currentUser;   // 错误
}
```

几百个请求并发时，是同一个对象被几百个线程同时调用。线程 A 刚把它设成张三，线程 B 改成李四，A 再读出来就变成李四了。这种 bug 本地单人测试永远测不出来，上线才炸。

（那「当前登录用户」这种每个请求都不同的数据放哪？见第八节。）

---

# 第二层 · 对照项目代码

## 五、怎么把对象放进容器：三种情况

### 5.1 自己写的类 → 加 `@Component` 家族的注解

| 注解 | 本项目中的类 | 除「注册成 Bean」外的附加行为 |
|---|---|---|
| `@Component` | `JwtService` | 无，最朴素 |
| `@Service` | `AuthService`、`UserService` | 无，纯语义标记 |
| `@RestController` | `AuthController`、`UserController` | 额外扫 `@RequestMapping` 建路由；返回值自动转 JSON |
| `@Configuration` | `SecurityConfig` | 内部的 `@Bean` 方法被特殊处理 |
| `@Repository` | 未使用 | 触发数据库异常转换 |

关键结论：**在「注册成 Bean」这件事上，前四个没有任何区别。** 选哪个只是为了让人一眼看出这个类属于哪一层。

`JwtService` 用 `@Component` 而不是 `@Service`，因为它不是业务逻辑，是通用技术组件。

### 5.2 第三方的类 → 写一个 `@Bean` 方法

`BCryptPasswordEncoder` 是 Spring 自己的类，你没法在它的源码上加注解。于是换个办法：**写一个方法把它 new 出来返回，容器收下这个返回值。**

```java
// SecurityConfig 里
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

注意返回类型写的是**接口** `PasswordEncoder`，不是实现类 `BCryptPasswordEncoder`。容器按 `PasswordEncoder` 这个类型登记它，所以 `AuthService` 的构造器声明 `PasswordEncoder` 就能拿到。

好处很实在：将来想换加密算法，只改这一个方法，`AuthService` 一个字都不用动。这就是「面向接口编程」在这里的实际价值。

一句话规律：**自己写的类加注解，第三方的类用 `@Bean` 方法。**

（`Jwts.builder()...` 那种一路点下去的写法叫 builder 链，见 [Builder 链](./builder-pattern.md)。）

### 5.3 `UserMapper` 是接口，为什么能注入

项目里没有 `UserMapperImpl`，但 `AuthService` 确实拿到了一个能用的实例。

原因是启动类上的 `@MapperScan("com.example.ouradventure.mapper")`。MyBatis 启动时扫这个包，为每个接口在内存里**动态生成**一个实现类，再注册进容器。你注入到的就是这个生成出来的对象。

它收到 `findUserByEmail("a@b.com")` 调用后做四件事：

```text
拿到「接口全限定名 + 方法名」
    ↓
在 XML 里找 namespace 和 id 都匹配的那条 SQL
    ↓
把参数绑到 #{email}，执行
    ↓
按 resultType 把结果集映射成 User 对象
```

这解释了 MyBatis 那两条硬性要求：**XML 的 `namespace` 必须是接口全限定名，`<select id>` 必须等于方法名**。名字对不上就报「找不到语句」。

## 六、怎么从容器取对象

三种写法，项目里用第一种：

```java
// 1. 构造器注入 —— 推荐
public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
}

// 2. 字段注入 —— 教程里常见，别学
@Autowired
private UserMapper userMapper;

// 3. setter 注入 —— 几乎不用
```

构造器注入更好的三个理由：

- **依赖全写在方法签名上**，一眼看出这个类依赖什么；字段注入把依赖藏在类内部
- **字段能声明 `final`**，赋值一次就不可改，天然线程安全
- **脱离容器也能 new**，所以可以直接写单元测试；字段注入的类 new 出来字段全是 `null`

本项目一个 `@Autowired` 都没写也能跑：Spring 4.3 之后，类只有一个构造器时容器默认就用它注入，注解可以省略。

## 七、看懂这条报错

```text
Parameter 2 of constructor in ...AuthService required a bean of type
'...JwtService' that could not be found.
```

翻译过来就一句：**「容器里没有 `JwtService` 这个 Bean。」**

两个检查方向：注解漏写了（类上没有 `@Component`），或者类不在扫描范围内（铁律二）。

好消息是这类错误在**启动时**就报出来，而不是等某个请求执行到那一行才 NPE。这也是构造器注入的一个附带好处。

## 八、请求级数据放哪里

铁律三说了不能放实例字段。那「当前登录用户」怎么办？两种做法：

- 作为方法参数一层层往下传
- 放进**跟线程绑定**的存储：Spring Security 的 `SecurityContextHolder`，底层是 `ThreadLocal`，每个线程各有一份，互不干扰

JWT 过滤器走的是后者：过滤器解析 token 拿到用户身份，塞进去，Controller 再从那里取。详见 [JWT 与 Spring Security](./jwt-and-spring-security.md)。

---

# 第三层 · 以后再看

跳过这一层完全不影响现在写代码。等你开始学事务、或者遇到「注解怎么不生效」的时候再回来。

## 九、代理：容器给你的可能不是你的类

容器交出来的有时不是你写的那个对象，而是一个**包着它的代理对象**。代理实现同样的接口（或继承你的类），但每次方法调用都先经过它一道。

前端里的等价物就是高阶包装函数：

```js
function withTransaction(fn) {
  return (...args) => {
    begin();
    try { const r = fn(...args); commit(); return r }
    catch (e) { rollback(); throw e }
  }
}
```

`@Transactional` 就是这个机制 —— 不是你的方法内部多了几行代码，而是外面套了一层。`@Async`、`@Cacheable`，以及 5.3 里 MyBatis 生成的 `UserMapper` 实现，原理都一样。

由此有两个推论：

**推论一：自己 `new` 的对象注解全部失效**（就是铁律一）。没经过容器，就没人给它套代理。

**推论二：同一个类内部直接调用，不走代理。**

```java
public void a() {
    this.b();      // b 上的 @Transactional 不生效
}

@Transactional
public void b() { ... }
```

因为 `this` 是原始对象，不是代理。代理套在外面，只有从外面进来的调用才会经过它。这是 Spring 最经典的坑之一，学事务时一定会撞上。

（`JwtService` 可以在测试里直接 `new`，是因为它身上没有任何依赖代理的注解。）

## 十、启动时到底发生了什么

`SpringApplication.run()` 之后按这个顺序：

1. **`@SpringBootApplication`** 拆开是三个注解，其中 `@ComponentScan` 从它所在的包 `com.example.ouradventure` 开始，递归扫描所有子包 —— 这就是铁律二的来源
2. 扫到并注册：`SecurityConfig`、`AuthController`、`UserController`、`AuthService`、`UserService`、`JwtService`
3. **`@MapperScan`** 让 MyBatis 为 `UserMapper` 生成代理并注册
4. **`@EnableAutoConfiguration`**（也在 `@SpringBootApplication` 里）根据 pom 里的依赖自动建 Bean：有 mysql 驱动和 `spring.datasource.*` 就建 `DataSource`；有 mybatis-starter 就建 `SqlSessionFactory`；有 webmvc 就启 Tomcat；有 Jackson 就配 JSON 转换器。**你没写一行配置，这些 Bean 就已经在了** —— 这就是「约定优于配置」
5. **按依赖顺序实例化**：先建好 `JwtService`、`PasswordEncoder`、`UserMapper` 代理 → 再建 `AuthService` → 最后 `AuthController`。顺序由容器自己算，和你写代码的顺序无关
6. MVC 扫所有 `@RequestMapping`，建出 `POST /auth/login → AuthController.login()` 这张路由表
7. Tomcat 开始监听 8080

`@Value` 在第 5 步生效：容器创建 `JwtService` 时，把 `app.jwt.secret` 的值解析出来当构造参数传进去。它**不是运行时去读文件**，而是装配时被塞进来的 —— 所以手动 `new` 的时候 `@Value` 无效（又是铁律一）。

顺带一点：`AuthService` 里 catch 的 `DuplicateKeyException` 是 **Spring 的**异常，不是 MySQL 驱动的 `SQLIntegrityConstraintViolationException`。MyBatis-Spring 做了异常转换，把各家数据库的错误码统一成 Spring 的异常体系，好处是换数据库时 catch 不用改。

## 十一、前端概念对照

| Spring | 前端近似物 |
|---|---|
| 容器（ApplicationContext） | Angular Injector / React Context Provider |
| Bean | 注册进容器的单例实例 |
| `@Component` | Angular `@Injectable()` |
| 构造器注入 | Angular 构造器注入（几乎相同） |
| `@ComponentScan` | 自动 import 全部模块并注册 |
| 代理（`@Transactional` 等） | 高阶包装函数 / `Proxy` |
| 自动配置 | 脚手架预设，装了依赖就有默认配置 |

类比只用于入门，别画等号。

---

## 十二、最后记住五条

1. `@Component`（及 `@Service`、`@RestController`、`@Configuration`）= 启动时把这个类 new 一个放进容器
2. 想用别的 Bean，在**构造器参数**里声明它，不要自己 `new`
3. Bean 默认单例，所有请求共用，**因此不要有可变实例字段**
4. 自己 `new` 的对象拿不到注入，注解也不生效
5. 类必须在启动类所在包或其子包下才会被扫描到

## 十三、完全不用看的

`@Scope` 的其他取值、Bean 生命周期回调、`BeanPostProcessor`、循环依赖的解决办法、AOP 切点表达式。

这些等实际撞上再查，提前看只是背名词。

最后一条经验：**API 和方法签名可以边用边查，机制和概念值得提前理解。** 区别在于 API 记错了编译器立刻报错，机制没理解会写出「能跑但是错的」代码 —— 比如在单例 Bean 里放可变字段，本地测不出来，并发才暴露。







