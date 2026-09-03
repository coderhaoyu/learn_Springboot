# JWT 与 Spring Security：一个请求怎么被认出身份

> 起因：登录接口能返回 token 了，但 `JwtAuthenticationFilter`、`SecurityConfig`、`RestAuthenticationEntryPoint` 这几个文件是代读代写的，不清楚每一行在做什么
> 相关周计划：[第 3 周：认证与安全](../weekly/week-03-auth-security.md)
> 前置笔记：[Spring 容器与 Bean](./spring-container-and-bean.md)（容器、Bean、单例、代理不再重复）
> 后续笔记：[从空文件写出 Filter 和 EntryPoint](./write-filter-and-entrypoint.md)（这份读完还是写不出来的话，看那份）
> 涉及文件：`JwtService`、`JwtAuthenticationFilter`、`RestAuthenticationEntryPoint`、`SecurityConfig`、`application.properties`
> 版本：Spring Boot 4.1.0 / Spring Security 7.1.0 / jjwt 0.12.6

## 怎么读这份笔记

分三层，**按顺序读，读累了就停**：

- **第一层（必读）**：用大白话讲清 token 是什么、我们那四个文件各干什么。不涉及任何类名细节。
- **第二层**：逐行读我们自己写的那四个文件，搞清每一行在做什么。
- **第三层（以后再看）**：JWT 内部结构、完整过滤器链、401/403 的判定规则、可以改进的地方。**现在整层跳过完全没问题**，等好奇或者出 bug 了再回来翻。

第一层看完，你应该能跟人讲清「登录之后为什么后面的请求就知道我是谁」。这是最重要的一步，剩下的都是细节。

---

# 第一层 · 先建立直觉

## 一、一个比喻：演唱会手环

这套东西的全部逻辑，就是演唱会入场那一套：

```text
进场时  ：出示身份证 → 换一个纸手环      ← 这是「登录」
手环上写：7 号观众，今晚 24:00 前有效     ← 这是 token 的内容
手环边上：主办方专用的荧光防伪章          ← 这是「签名」
之后每次：进内场、进休息区都看一眼手环     ← 这是每个请求带 token
```

关键在检票员的行为：**他不查名单。** 他只看两件事 —— 防伪章是不是真的、时间过没过。章是真的、没过期，手环上写 7 号他就当你是 7 号。

我们的服务器就是这个检票员。它**不记**谁登录过，全靠 token 自己证明自己。

一句话总结：**登录时发一张自带签名的凭证，之后每个请求带上它，服务器验一下签名就知道你是谁。**

## 二、为什么要这么麻烦

因为 **HTTP 没有记忆**。

每个 HTTP 请求都是一个陌生人来敲门。服务器刚处理完 `POST /auth/login`，下一秒 `GET /users/1` 到了，从协议层面它完全看不出这两个请求是同一个人发的。

所以「登录状态」不是 HTTP 自带的，必须自己造。造法只有两种：

| | 服务器记小本本（Session） | 客户端拿凭证（Token） |
|---|---|---|
| 怎么做 | 服务器存一条「`abc123` → 用户 7」，只给客户端一个编号 | 服务器发一张写着「用户 7」的带签名凭证，自己什么都不存 |
| 身份数据在哪 | 服务器内存里 | token 里，客户端保管 |
| 本项目 | 没用 | **用这个** |

选 token 的三个理由：

1. 服务器不用为每个在线用户占内存
2. 部署两台机器不用做任何额外处理（Session 方案下，用户在 A 机登录、请求打到 B 机就找不到了）
3. App、小程序、跨域前端都好用（Session 依赖 Cookie，这些场景里 Cookie 很麻烦）

代价也有，而且不小 —— 但那个放到第三层讲（第十一节），现在先不管。

## 三、我们写了四个文件，各干一件事

| 文件 | 一句话职责 | 对应比喻 |
|---|---|---|
| `JwtService` | 造手环、验手环 | 手环机器 |
| `JwtAuthenticationFilter` | 看一眼手环，知道「这是 7 号」 | 检票员认人 |
| `SecurityConfig` | 规定哪些门要手环、哪些门不要 | 场馆的门禁规则 |
| `RestAuthenticationEntryPoint` | 没手环被拦下时，怎么把「401」这句话写给前端 | 门口的提示牌 |

**这份笔记最重要的一句话：「认人」和「判权限」是两件完全不同的事，由两个不同的文件负责。**

- `JwtAuthenticationFilter` 只回答「**你是谁**」。答不出来就当你是匿名的，然后**放你过去**。
- `SecurityConfig` 只回答「**这扇门允不允许匿名**」。不允许才拦。

很多人读这段代码卡住，就是因为把这两件事当成了一件。第七节会具体解释为什么「没带 token 也放过去」不是漏洞。

## 四、两条路

两种请求走的路完全不同。

**A. 登录（`POST /auth/login`）—— 领手环**

```text
前端提交 email + password
    ↓
/auth/** 是公开路径，不需要身份，直接放过
    ↓
AuthController.login → AuthService.login
    ↓
按 email 查库 → BCrypt 比对密码
    ↓
JwtService.generateToken(userId) 签发 token
    ↓
返回 { token, expiresIn, user }
    ↓
前端存进 localStorage
```

**B. 访问受保护接口（`GET /users/1`）—— 验手环**

```text
前端 axios 请求拦截器自动加上 Authorization: Bearer <token>
    ↓
JwtAuthenticationFilter：取 header → 验签 → 得到 userId → 存进「当前请求的身份盒子」
    ↓
Spring Security 检查：这条路径要求登录，盒子里有身份吗？
    ├── 有 → 放进去 → Controller → Service → Mapper → MySQL
    └── 没有 → RestAuthenticationEntryPoint 写出 401 JSON
```

第一层结束。**如果只看到这儿就合上笔记，你已经能读懂项目里的登录流程了。**

---

# 第二层 · 逐行读我们的代码

## 五、JwtService：造手环 / 验手环

### 5.1 构造器：密钥从哪来

```java
public JwtService(@Value("${app.jwt.secret}") String secret,
                  @Value("${app.jwt.expire-minutes}") long expireMinutes) {
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expireMinutes = expireMinutes;
}
```

- `@Value("${...}")`：容器创建这个 Bean 时，把 `application.properties` 里的值当构造参数传进来（见前置笔记「启动时到底发生了什么」）
- `secret.getBytes(UTF_8)`：把字符串变成字节数组。签名算法的输入是字节，不是字符
- `Keys.hmacShaKeyFor(bytes)`：把字节数组包装成一把「钢印」（`SecretKey`）

**这把密钥就是整套机制唯一的信任根。** 谁有它谁就能给自己签发任意身份的 token，所以绝对不能泄露。

两个字段都是 `final`：`JwtService` 是单例，所有请求线程共用一个实例，`final` 字段只在构造时赋值、之后不可变，天然线程安全（推理见前置笔记「三条铁律」第三条）。

### 5.2 generateToken：造手环

```java
Instant now = Instant.now();
Instant expiry = now.plusSeconds(getExpireSeconds());

return Jwts.builder()
        .subject(String.valueOf(userId))
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiry))
        .signWith(secretKey)
        .compact();
```

一路点下去的这个写法叫 builder 链（详见 [Builder 链](./builder-pattern.md)），读法是「一路攒参数 → 最后一个方法产出成品」：

| 这一行 | 在手环上写什么 |
|---|---|
| `.subject(...)` | 「7 号观众」 |
| `.issuedAt(...)` | 发放时间 |
| `.expiration(...)` | 「24:00 前有效」 |
| `.signWith(secretKey)` | 盖上防伪章 |
| `.compact()` | 拼成手环成品（一个字符串） |

几个细节：

- `.subject(String.valueOf(userId))`：这个位置规范要求必须是字符串，所以 `Long` 要转成 `String`。这也是解析时要 `Long.parseLong` 转回来的原因
- **为什么放 userId 而不是 email**：主键不会变，email 用户可能改；主键也是后面所有查询的入口
- **为什么不把昵称也塞进去**：token 一签发内容就冻结了。用户改了昵称，token 里的旧昵称就是错的。只放 ID，昵称每次由数据库提供
- `Instant` 是 Java 8 的新时间类型，`Date` 是旧 API。JJWT 的接口只收 `Date`，所以用 `Date.from(instant)` 转一下。**新代码优先用 `Instant`，只在库要求时才转 `Date`**

### 5.3 parseUserId：验手环

```java
public long parseUserId(String token) {
    Jws<Claims> jws = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token);

    String subject = jws.getPayload().getSubject();

    return Long.parseLong(subject);
}
```

前四步还是 builder 链：`Jwts.parser()` 拿到构建器 → `.verifyWith(secretKey)` 说明用哪把密钥验 → `.build()` 产出真正的解析器 → `.parseSignedClaims(token)` 真正执行。

`parseSignedClaims` 一个方法里做完了所有校验：

```text
1. 把 token 拆开，读出里面写的内容
2. 用密钥重算一遍签名，和 token 自带的签名比对   ← 不一致就抛异常
3. 检查有没有过期                              ← 过期了就抛异常
```

**这里有一个必须理解的点：校验失败是靠「抛异常」表达的，不是返回 `false`。**

这个方法要么正常返回，要么抛异常。所以你在代码里找不到任何 `if (验证通过)` 这样的判断 —— 判断藏在异常里。这也就是调用方必须 `try/catch` 的原因。

（哪几种坏 token 抛哪个异常，见第十二节。现在只要知道「都是 `JwtException` 的子类」就够了。）

## 六、Filter 是什么

一句话：**请求到你的 Controller 之前必须经过的关卡。**

```text
浏览器 → Tomcat → [Filter 链] → DispatcherServlet → Controller → Service → Mapper
                      ↑
              我们的 JWT 过滤器在这里
```

前端里的等价物就是 express 中间件，模型几乎一模一样：

```js
app.use((req, res, next) => {
  const token = req.headers.authorization;
  if (token) req.user = verify(token);
  next();                                  // ← 相当于 chain.doFilter
});
```

写 Filter 只要记住三件事：

1. **`chain.doFilter(request, response)` 表示「交给下一关」**，不调用请求就到不了 Controller（后果见 7.6）
2. 我们的类 `extends OncePerRequestFilter`，作用是**保证一次请求只执行一次**（Servlet 有内部转发机制，不加这层保护，一个转发到 `/error` 的请求会把 token 解析两遍）
3. 因为继承了它，要覆写的方法叫 `doFilterInternal` 而不是 `doFilter` —— `doFilter` 已经被基类用来做去重判断了

**为什么认证代码非要写在 Filter 里，不能写在 Controller？** 因为 Spring Security 检查「这个请求有没有身份」这件事，发生在到达 Controller **之前**。写到 Controller 里就太晚了，请求早就被拦下来返回 401 了。

## 七、逐行读 JwtAuthenticationFilter

### 7.1 两个常量

```java
private static final String HEADER_NAME = "Authorization";
private static final String TOKEN_PREFIX = "Bearer ";
```

`Bearer` 的字面意思是「持票人」—— 谁拿着这张票就认谁，不再追问是不是本人。正好是 token 的语义。

**注意 `"Bearer "` 末尾有一个空格。** 后面 `substring(TOKEN_PREFIX.length())` 靠这个长度正好跳过前缀和空格。写成 `"Bearer"` 会让 token 多出一个前导空格，验签直接失败。

### 7.2 没带 token 为什么是放行，而不是直接 401

```java
if (header == null || !header.startsWith(TOKEN_PREFIX)) {
    filterChain.doFilter(request, response);
    return;
}
```

第一次看会觉得这是漏洞。回到第三节那句话：这个过滤器只回答「你是谁」，答不出来就当匿名，**允不允许匿名是 `SecurityConfig` 的事**。

而且如果在这里直接返回 401，`/auth/login` 和 `/auth/register` 就永远进不来了 —— 登录请求本来就不可能带 token。

所以：没 token 的请求继续往下走，公开路径正常通过、受保护路径被拒。**判断集中在一个地方，两处规则才不会打架。**

### 7.3 宣布身份（最容易踩的一行）

```java
UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(userId, null, List.of());
```

这个类有两个构造器，**含义完全不同**：

| 写法 | 效果 | 用途 |
|---|---|---|
| `new ...(principal, credentials)` | 「待验证」 | 表单登录时递交凭据 |
| `new ...(principal, credentials, authorities)` | **「已通过验证」** | 已经验过了，直接宣布身份 |

**必须用三个参数的那个。** 用错成两参版本，症状非常迷惑人：token 明明是对的，却一直 401。

三个参数分别是：

- **principal（当事人）**：传 `userId`（`long` 自动装箱成 `Long`）。Controller 后面取出来的就是这个
- **credentials（凭据）**：传 `null`。密码在登录时已经验过了，此刻没有也不该保留
- **authorities（权限）**：传 `List.of()`（空列表）。项目还没有角色概念，所有登录用户权限相同

`List.of()` 是 Java 9 的不可变列表工厂方法，比 `new ArrayList<>()` 更能表达「就是空的，不会再改」。

### 7.4 把身份放进「当前请求的身份盒子」

```java
SecurityContext context = SecurityContextHolder.createEmptyContext();
context.setAuthentication(authentication);
SecurityContextHolder.setContext(context);
```

`SecurityContextHolder` 就是第一层里说的那个「盒子」。它的关键性质是**跟当前请求的线程绑定**：Tomcat 给每个请求分配一个线程，请求全程（Filter → Controller → Service）都在同一个线程上跑，所以过滤器塞进去的东西，Service 里能直接读到，不用一路当参数传。

将来写 `/users/me` 时这样取（推荐第二种，Controller 里不出现 Security 的静态调用，也更好测）：

```java
// 方式一：直接取
Long userId = (Long) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();

// 方式二：让 Spring 注入
@GetMapping("/me")
public ApiResponse<UserVo> me(@AuthenticationPrincipal Long userId) { ... }
```

`getPrincipal()` 取出来的类型，就是 7.3 里第一个参数传进去的类型。**这两处必须对应**，否则运行时 `ClassCastException`。

（为什么是 `createEmptyContext()` 新建一个而不是直接改现成的、谁负责在请求结束时清空这个盒子 —— 见第十五节。）

### 7.5 验签失败也不 401

```java
} catch (JwtException | IllegalArgumentException e) {
    SecurityContextHolder.clearContext();
}
```

- `JwtException`：签名不对、格式错误、已过期的共同父类
- `IllegalArgumentException`：`Long.parseLong` 抛的 `NumberFormatException` 的父类
- `catch (A | B e)` 是 Java 7 的多重捕获语法，等价于两个 catch 块做同一件事

处理方式还是「降级为匿名」，理由和 7.2 一样：**401 只由一个地方发出。**

注意这里**把异常吞掉了，一行日志都没有**。将来调试「为什么一直 401」会很难查，加一行会省事很多：

```java
// 类顶部（SLF4J，Spring Boot 默认带）
private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

// catch 里
log.debug("token 校验失败: {}", e.getMessage());
```

用 `debug` 级别：token 过期是正常现象，用 `warn`/`error` 会把日志刷满。另外不要打印 token 本身。

（一个坑：`OncePerRequestFilter` 从父类继承了一个 `logger` 字段，看起来能直接用，但它**不支持 `{}` 占位符**，会原样打印出 `{}`。要用占位符就自己声明上面那个 SLF4J 的 `Logger`。）

### 7.6 最后一行必须调用

```java
filterChain.doFilter(request, response);
```

漏掉这行，请求到不了 Controller。注意**症状不是「请求挂住」** —— 过滤器方法一返回，容器就把响应直接交出去了，前端拿到的是一个 `200` 加空响应体（`Content-Length: 0`），几毫秒就回来。不报错、不超时、只是数据没了，比挂住难查得多。这是写 Filter 最典型的错误。

注意它在 try/catch **外面**，所以四种情况（没 token、验签成功、验签失败、`sub` 格式错误）最后都会往下走，区别只在盒子里有没有身份。

## 八、逐行读 SecurityConfig

五段配置，一段一句话。

### 8.1 关掉 CSRF

```java
.csrf(AbstractHttpConfigurer::disable)
```

CSRF（跨站请求伪造）成立的前提是「**浏览器会自动带上 Cookie**」：攻击者在自己的站点放一个指向你后端的表单，用户点了，浏览器就自动附上 Cookie，后端以为是本人操作。

我们的身份在 `Authorization` 头里、由前端 JS 手动加，第三方站点没法让浏览器自动附上这个头，攻击链断了，所以这个防护没有意义（留着反而会让所有 POST 都要求额外带 CSRF token）。

**但要记住这是配套结论，不是通用做法**：哪天把 token 改成存 Cookie（比如为了防 XSS 用 HttpOnly Cookie），CSRF 防护就必须重新打开。

（`AbstractHttpConfigurer::disable` 是方法引用，等价于 `csrf -> csrf.disable()`。）

### 8.2 不要 Session

```java
.sessionManagement(session ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
```

明确告诉 Spring Security：不创建 session，也不从 session 里恢复身份，每个请求都必须自带 token。

不写这行也能跑（因为没人往 session 里写东西），但显式声明有两个好处：意图清楚；避免某个组件顺手创建了 session，造成「看起来无状态其实有状态」。

### 8.3 门禁规则

```java
.authorizeHttpRequests(auth -> auth
        .requestMatchers("/auth/**").permitAll()
        .anyRequest().authenticated())
```

`/auth/**` 下的路径不需要身份，其他所有路径必须登录。三个要点：

- **顺序敏感**：从上往下匹配，命中第一条就生效。所以 `anyRequest()` 必须放最后，放前面会把后面的规则全盖住
- `**` 匹配任意多层（`/auth/login`、`/auth/a/b` 都算），`*` 只匹配一层
- **这里写的是后端真实路径。** 前端请求的是 `/api/auth/login`，但 `frontend/vite.config.ts` 的代理把 `/api` 前缀去掉了，到后端时是 `/auth/login`。所以规则里不能写 `/api/auth/**`

改成 `authenticated()` 的直接后果：`UserController` 下所有接口现在都需要登录了。这是有意的收紧，对应第 3 周「业务接口默认要求登录」。

### 8.4 指定「没身份时谁来写响应」

```java
.exceptionHandling(ex ->
        ex.authenticationEntryPoint(restAuthenticationEntryPoint))
```

不配这行的话，默认行为是返回 **403**。两个问题：语义不对（401 才表示「请去登录」）；前端 `client.ts` 的响应拦截器是按 401 判断「会话过期并跳登录页」的，403 会让自动跳转失效。

（401 和 403 更准确的分界见第十四节。）

### 8.5 把我们的过滤器插进链里

```java
.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
```

含义：把我们的过滤器放在 `UsernamePasswordAuthenticationFilter` **之前**。

容易误解的是这跟表单登录没关系 —— 项目根本没启用表单登录，那个类只是被当作**位置坐标**。选它是社区惯例，因为这个位置正好满足两个条件：**晚于身份盒子的初始化、早于权限检查**。

顺序错了的症状很典型：如果排到权限检查之后，无论 token 多正确都是 401，因为检查时盒子还是空的。

（这条链完整长什么样，见第十三节。）

另外顺带一点：这个 `@Bean` 方法的参数（`HttpSecurity`、两个我们自己的类）也是由容器注入的，规则和构造器注入完全一样 —— 按类型去容器里找。`HttpSecurity` 本身也是个 builder，每个 `.xxx()` 都在往里攒配置，最后 `http.build()` 产出成品。

## 九、RestAuthenticationEntryPoint：写出 401

```java
private static final String BODY = "{\"code\":401,\"message\":\"未登录或登录已过期\",\"data\":null}";

@Override
public void commence(HttpServletRequest request, HttpServletResponse response,
                     AuthenticationException authException) throws IOException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().write(BODY);
}
```

**为什么不能用 `GlobalExceptionHandler`（`@RestControllerAdvice`）？**

因为 `@RestControllerAdvice` 是 Spring MVC 的机制，只管 DispatcherServlet 内部发生的异常。Filter 跑在 DispatcherServlet **之前**（看第六节那张图），异常抛出时 MVC 还没接手，全局异常处理器根本没机会介入。

所以一个项目里出现两套「错误响应生成」逻辑不是设计冗余，是两个层次各自的必需品。

三个细节：

- 方法名叫 `commence`（开始），因为在传统 Web 里它的职责是「启动认证流程」= 跳转到登录页。纯接口项目没页面可跳，改成直接写 401
- `setStatus` 必须在 `getWriter().write()` 之前 —— 响应头一旦跟着响应体发出去就改不了了
- `charset=UTF-8` 不能省，否则中文消息可能乱码

## 十、对照第 3 周验收标准

| 验收项 | 状态 |
|---|---|
| 注册只保存 BCrypt 摘要 | 已完成（`AuthService.registerUser`） |
| 登录签发带过期时间的 JWT | 已完成 |
| 业务接口默认要求登录 | 已完成（`anyRequest().authenticated()`） |
| 无 token / 错误 token / 过期 token 返回 401 | 已完成 |
| 密码、摘要、token 不出现在响应中 | 已完成（`UserVo` 里没有 password 字段） |
| `GET /users/me` 返回当前用户 | **未做**，用 7.4 的方式取 userId |
| 已认证但无权限返回 403 | **未做**，需要先有角色（见 7.3 的 authorities） |

第二层结束。**到这里为止，四个文件的每一行你都知道在做什么了。**

---

# 第三层 · 以后再看

下面全是「知道更好，但不知道也能正常写代码」的内容。建议的读法：好奇某一节就翻那一节，不用通读。

## 十一、无状态的代价

第二节说选 token 是因为服务器什么都不用存。反面就是：**服务器什么都不知道。**

1. **发出去的 token 收不回来。** 用户点「退出登录」，前端只是把 localStorage 删掉，那张 token 在过期前仍然有效 —— 谁抄走了就能继续用
2. **禁用或删除用户不会立即生效。** `parseUserId` 不查库，签名和过期时间没问题就认
3. **token 里的信息会变旧**（这就是 5.2 里「只放 ID 不放昵称」的原因）

工业上的缓解手段：有效期设得很短（15 分钟）+ 用 refresh token 换新的；或者维护一个「已吊销 token」黑名单（但那又把状态加回来了）。

本项目 `app.jwt.expire-minutes=2880`，也就是 **48 小时**。学习项目可以，真实项目偏长 —— 结合第 1 点，泄露一张 token 等于两天的访问权。

## 十二、JWT 内部长什么样

### 12.1 三段结构

`xxxxx.yyyyy.zzzzz`，用 `.` 分成三段，每段都是 Base64URL 编码：

```text
header    ： 用什么算法签的
payload   ： 内容（正式叫 claims，「声明」）
signature ： 防伪章
```

拿本项目的密钥真签一个（`sub=7` 表示 7 号用户）：

```text
eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI3IiwiaWF0IjoxNzg4MDAwMDAwLCJleHAiOjE3ODgxNzI4MDB9.ZMR8IxzMajIbdQKUpRSL8nV73C4PAa68je-q2OwVnipPHWiDyX5WItqmGSTBZcNrPNzYCPbtTUb5yst71N9NSg
```

前两段任何人都能解开：

```bash
python3 -c "import base64,sys;[print(base64.urlsafe_b64decode(p+'='*(-len(p)%4)).decode()) for p in sys.argv[1].split('.')[:2]]" 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI3IiwiaWF0IjoxNzg4MDAwMDAwLCJleHAiOjE3ODgxNzI4MDB9.ZMR8IxzMajIbdQKUpRSL8nV73C4PAa68je-q2OwVnipPHWiDyX5WItqmGSTBZcNrPNzYCPbtTUb5yst71N9NSg'
```

输出：

```json
{"alg":"HS512"}
{"sub":"7","iat":1788000000,"exp":1788172800}
```

### 12.2 最重要的一条：签名不是加密

前两段是 **Base64 编码，不是加密** —— 没有密钥参与，**任何拿到 token 的人都能读出内容**。

推论：

- **不能**往里面放密码、密码摘要、手机号这些敏感信息
- 放 `userId` 是安全的 —— 它本来就会出现在 URL 和响应里
- 签名保证的是「**没被改过**」（完整性），不是「**看不见**」（保密性）

### 12.3 篡改为什么一定会被发现

签名的算法是：

```text
signature = HMAC-SHA512(base64url(header) + "." + base64url(payload), 密钥)
```

服务端收到 token 后**用同样的方式重算一遍**，再和第三段比对：

- 攻击者把 `"sub":"7"` 改成 `"sub":"1"`（想冒充 1 号）→ 前两段变了 → 重算出的签名和第三段对不上 → 拒绝
- 想连签名一起改 → 需要密钥，而密钥只在服务端的 `application.properties` 里

所以**密钥泄露 = 任何人都能给自己签发任意身份的 token**。这就是 5.1 里说的「唯一的信任根」。

### 12.4 四种坏 token 分别抛什么异常

实际跑一遍（jjwt 0.12.6）：

| 情况 | 抛出的异常 |
|---|---|
| 改了内容 | `io.jsonwebtoken.security.SignatureException` |
| 用别的密钥签的 | `io.jsonwebtoken.security.SignatureException` |
| 已过期 | `io.jsonwebtoken.ExpiredJwtException` |
| 根本不是 JWT（如 `hello.world.abc`） | `io.jsonwebtoken.MalformedJwtException` |

四个都继承 `JwtException` —— 这就是 7.5 里一句 `catch (JwtException e)` 就够的原因。

### 12.5 payload 里的字段名不是随便起的

`sub`、`iat`、`exp` 是 RFC 7519 定义的标准字段：

| 字段 | 全称 | 含义 | 本项目 |
|---|---|---|---|
| `sub` | subject | 这张证是关于谁的 | 用户主键 |
| `iat` | issued at | 签发时间 | 有 |
| `exp` | expiration | 过期时间 | 有，48h |
| `iss` | issuer | 签发方 | 未用 |
| `aud` | audience | 接收方 | 未用 |
| `nbf` | not before | 在此之前无效 | 未用 |
| `jti` | JWT ID | token 唯一编号，做黑名单用 | 未用 |

注意时间单位是**秒**不是毫秒（上面解出来的 `1788172800` 只有 10 位）。JJWT 会自动换算，自己手写时容易错。

顺带一提 `Jws<Claims>` 这个类型：`Jws` 里的 S 是 **S**ignature，意思是「已验过签的 JWT」—— 能拿到 `Jws` 对象本身就说明签名通过了。泛型 `<Claims>` 说明内容被解析成了 `Claims`（本质是 `Map<String, Object>` 的封装，额外提供 `getSubject()`、`getExpiration()` 这些便捷方法）。

### 12.6 为什么是 HS512 而不是教程里常见的 HS256

`Keys.hmacShaKeyFor(bytes)` 会**按密钥长度自动挑算法**：

| 密钥长度 | 选中的算法 |
|---|---|
| ≥ 512 bit（64 字节） | HmacSHA512 |
| ≥ 384 bit（48 字节） | HmacSHA384 |
| ≥ 256 bit（32 字节） | HmacSHA256 |
| < 256 bit | 直接抛 `WeakKeyException` |

本项目的 secret 是 64 个字符 = 512 bit，所以走 HS512。

看到 `WeakKeyException` 是密钥填短了，不是代码写错 —— RFC 7518 要求 HMAC 密钥不短于摘要长度，jjwt 干脆在太短时直接启动失败。

（另外，`signWith(secretKey)` 只传一个参数就够了，因为 0.12.x 从密钥长度推断算法。旧教程里的 `signWith(key, SignatureAlgorithm.HS256)` 是 0.11 及更早的写法，看到别照抄。）

## 十三、Spring Security 的过滤器链完整长什么样

Spring Security 往 Tomcat 的过滤器链里只插了**一个** Filter（`FilterChainProxy`），但这个 Filter 内部又维护了一条自己的链。本项目当前配置下按顺序大致是：

| 顺序 | 过滤器 | 作用 |
|---|---|---|
| 1 | `DisableEncodeUrlFilter` | 禁止把 sessionId 拼进 URL |
| 2 | `WebAsyncManagerIntegrationFilter` | 异步请求的上下文传递 |
| 3 | **`SecurityContextHolderFilter`** | 请求开始时准备身份盒子，**结束时清空** |
| 4 | `HeaderWriterFilter` | 写安全响应头 |
| 5 | ~~`CsrfFilter`~~ | 已关闭 |
| 6 | `LogoutFilter` | 处理 `/logout` |
| 7 | **`JwtAuthenticationFilter`** | ← 我们插进来的位置 |
| 8 | ~~`UsernamePasswordAuthenticationFilter`~~ | 表单登录，未启用，只作为定位锚点 |
| 9 | `AnonymousAuthenticationFilter` | 到这儿还没身份的，标记成「匿名用户」 |
| 10 | **`ExceptionTranslationFilter`** | 捕获后面抛的认证/授权异常，调用 EntryPoint |
| 11 | **`AuthorizationFilter`** | 执行 `authorizeHttpRequests` 里的规则 |

这张表能直接回答三个问题：

- **为什么身份必须在第 7 步建立？** 因为第 11 步才检查。早于第 3 步也不行 —— `SecurityContextHolderFilter` 会把盒子整个重置掉
- **401 到底是谁发出的？** 第 11 步发现没身份 → 抛异常 → 第 10 步接住 → 调 EntryPoint 写响应
- **为什么 `addFilterBefore` 选那个位置？** 第 7 位正好夹在第 3 位和第 11 位之间

## 十四、401 和 403 到底由谁决定

Spring Security 把失败分成两类，对应两个处理器：

| 情况 | 语义 | 处理器 | 状态码 |
|---|---|---|---|
| 还不知道你是谁 | 未认证 | `AuthenticationEntryPoint` | 401 Unauthorized |
| 知道你是谁，但你没权限 | 已认证、授权失败 | `AccessDeniedHandler` | 403 Forbidden |

比「401 是没登录、403 是没权限」更精确的说法是 —— 判断依据是**当前身份是不是匿名**。`ExceptionTranslationFilter` 的实际逻辑：

```text
捕获到 AccessDeniedException
    ↓
当前身份是匿名（或 remember-me）吗？
    ├── 是 → 调 AuthenticationEntryPoint  → 401
    └── 否 → 调 AccessDeniedHandler        → 403
```

**项目现在构造不出 403 场景**，因为 7.3 里 authorities 传的是空列表，所有登录用户权限相同。等有了角色，把 `List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))` 放进去、规则里配上 `.hasRole("ADMIN")`，权限不足才会走 403。

## 十五、身份盒子的两个细节

三层结构长这样：

```text
SecurityContextHolder   ← 静态工具类，内部是 ThreadLocal
    └── SecurityContext ← 一个容器，只装一个 Authentication
            └── Authentication  ← 身份本体：principal / credentials / authorities
```

`ThreadLocal` 就是「每个线程各有一份」的变量，这是 7.4 里「跟请求线程绑定」的实现方式。

### 15.1 为什么新建 context 而不是改现成的

看起来更短的写法是：

```java
SecurityContextHolder.getContext().setAuthentication(authentication);   // 不推荐
```

Spring Security 6 起官方明确建议用 `createEmptyContext()` + `setContext()`。原因是 `getContext()` 在某些策略下（比如子线程继承父线程上下文的 `MODE_INHERITABLETHREADLOCAL`）可能返回一个**被多个线程共享**的实例，直接改它会影响别的线程。新建一个再整体替换就没这个问题。

这类「看起来啰嗦，但避免共享可变状态」的写法，在并发代码里很常见。

### 15.2 谁负责清空它

这件事很关键：Tomcat 的线程是**复用**的。如果请求结束不清理，下一个请求复用到这个线程时会读到上一个用户的身份 —— 这是很严重的越权 bug。

好消息是不用自己清。第十三节表格里第 3 位的 `SecurityContextHolderFilter` 在 `finally` 块里调了 `clearContext()`，请求无论正常结束还是抛异常都会清空。

所以 7.5 里 catch 分支的 `clearContext()` 其实不是必须的，但写着无害，属于防御性写法。

## 十六、五个可以改进的地方

都不影响功能，属于「知道了以后可以顺手改」。

**1. 密钥进了 git。** `application.properties` 里的 `app.jwt.secret` 一旦提交就等于公开（历史记录删不掉）。生产做法是从环境变量读 `app.jwt.secret=${JWT_SECRET}`，本地放 `application-local.properties` 并加进 `.gitignore`。这个仓库是学习用的，风险可控，但习惯值得早点建立。

**2. `parseUserId` 的注释没写抛什么异常。** 在 Java 里，**方法抛什么异常属于方法的对外契约**，写进 Javadoc 的价值和写返回值一样大 —— 调用方靠它决定要 catch 什么。另外方法注释写的是「解析 token」，但它实际做的是「验签 + 查过期 + 取出 userId」，解析只是副产品：

```java
/**
 * 校验 token 并取出用户 ID。
 *
 * @throws io.jsonwebtoken.JwtException 签名不符、格式错误或已过期
 * @throws NumberFormatException        sub 不是合法的数字
 */
```

**3. 401 的 JSON 是手写死的。** 如果哪天 `ApiResponse` 的字段名改了（比如 `message` 改成 `msg`），这个字符串不会跟着变，编译器也不报错，前端会拿到字段名不一致的响应。更稳妥的写法是注入 `ObjectMapper`（容器里已有，Spring Boot 自动配好的）去序列化：

```java
objectMapper.writeValue(response.getWriter(), ApiResponse.error(401, "未登录或登录已过期"));
```

这样响应结构由 `ApiResponse` 单点定义，不会漂移。

**4. 过滤器其实被注册了两次。** `JwtAuthenticationFilter` 上有 `@Component`，所以它是一个 Bean；而 Spring Boot 有个约定 —— **容器里任何 `Filter` 类型的 Bean 都会被自动注册到 Servlet 容器**。于是它同时出现在两个地方：

```text
Tomcat 过滤器链：FilterChainProxy → … → JwtAuthenticationFilter（自动注册，排最后）
                     └── 内部安全链：… → JwtAuthenticationFilter（手动 addFilterBefore） → …
```

实际不会解析两次 token，因为第六节说的 `OncePerRequestFilter` 会去重：安全链里先跑过并打了标记，外层那个进来时直接跳过。想彻底避免：去掉 `@Component`、在 `SecurityConfig` 里直接 `new`。知道有这回事就行。

**5. 认证不查库。** 用户被删除或禁用后，旧 token 在过期前依然能通过认证，要等后面的 Service 拿着 userId 查库时才发现用户不存在。当前阶段没问题。如果以后要「封号立即生效」，得在过滤器里加一次用户状态查询（每个请求多一次查询，或者加缓存）—— 这是实时性和性能的取舍。

---

## 十七、最后记住这几条

1. token 的内容是**编码不是加密**，人人可读 —— 只放 ID 这类非敏感、不会变旧的数据
2. 签名保证「没被改过」，**密钥是唯一的信任根**，泄露等于整个身份系统失效
3. 验签失败是靠**抛异常**表达的，所以调用处必须 try/catch
4. 认证代码写在 Filter 里，因为它必须在权限检查之前完成，写到 Controller 就太晚了
5. 过滤器只「**认人**」，`SecurityConfig` 只「**判权限**」，401 只从一个地方发出
6. `UsernamePasswordAuthenticationToken` 必须用**三参**构造器才算已认证
7. 当前用户放在 `SecurityContextHolder`（跟请求线程绑定），Controller 从那里取
8. Filter 抛的异常 `@RestControllerAdvice` 接不到，所以 401 的响应体要自己写
9. `authorizeHttpRequests` 的规则**从上到下**匹配，`anyRequest()` 放最后
10. 规则里写的是**后端路径**（`/auth/**`），前端的 `/api` 前缀已被 vite 代理去掉

## 十八、完全不用看的

`UserDetailsService` / `AuthenticationManager` / `AuthenticationProvider`（表单登录体系，纯 token 方案用不到）、refresh token、OAuth2 与 `spring-security-oauth2-resource-server`（生产里常用它替代手写过滤器）、RSA 非对称签名 `RS256`（多服务共享公钥时才需要）、方法级注解 `@PreAuthorize`、CORS 细节配置。

这些等实际需要时再查。现在把「一个请求如何被认出身份、谁在什么时候拒绝它」这条链路想清楚，比多认识几个类名有用。

















