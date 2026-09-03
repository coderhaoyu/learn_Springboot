# 从空文件写出 Filter 和 EntryPoint

> 起因：[JWT 与 Spring Security](./jwt-and-spring-security.md) 读完了，每一行在做什么都清楚，但合上笔记打开空文件还是敲不出来
> 分工：那份笔记是**读的视角**（这一行是什么意思），这份是**写的视角**（面对空文件，第一个字打什么）
> 涉及文件：`JwtAuthenticationFilter`、`RestAuthenticationEntryPoint`、`SecurityConfig`
> 概念部分不重复：JWT 结构、过滤器链、401/403 的判定，需要时回上一份笔记查

## 怎么读这份笔记

- **第一层**：搞清「读得懂写不出」到底缺什么，记两个口诀
- **第二层（核心）**：跟着敲一遍，两个类各一次完整的键盘过程
- **第三层**：三档默写、写完自查表、三个练手任务

---

# 第一层 · 先搞清楚缺的是什么

## 一、读得懂写不出，缺的不是知识

读代码和写代码是两种不同的能力：

| | 读 | 写 |
|---|---|---|
| 结构 | 现成的，摆在眼前 | 得自己造出来 |
| 你的任务 | 给每一行配一个解释 | 决定先写哪一行 |
| 卡住时的感觉 | 「这个类是干嘛的」 | 「我该从哪开始」 |

你现在的状态是：每一行的解释都有了，但**没有生成顺序**。

打个比方，让你写 `for (int i = 0; i < n; i++)` 你不用想，因为这个形状在你脑子里是一个整体，不是四个零件。Filter 你还没有这个形状 —— 不是不懂，是不熟。

所以这份笔记不再解释「是什么」，只做一件事：**把两个类的生成顺序讲清楚，让你能照着顺序自己长出来。**

## 二、最有用的一个习惯：先读方法签名

写框架代码时，大部分「我该怎么做」的答案都藏在方法签名里。

```java
protected void doFilterInternal(HttpServletRequest request,      // 能读请求
                                HttpServletResponse response,    // 能写响应
                                FilterChain filterChain)         // 能放行
```

两个方向各读一遍：

- **参数 = 框架发给你的工具**，你能做的事就这么多。想读 header？只能从 `request` 来。想放行？只能靠 `filterChain`
- **返回 `void` = 你的成果不靠 return 交付**，只能靠副作用：往别处塞东西（`SecurityContextHolder`），或者调别人（`filterChain.doFilter`）

`void` 这一点尤其关键。它直接告诉你：这个方法**不是**「算出一个身份然后交出去」，而是「把身份放到一个约定好的地方，然后让流程继续」。想通这句，Filter 的形状就定了。

（`commence` 也是 `void`，同理 —— 它的成果是「写进 response」，不是 return 一个响应对象。）

## 三、两个口诀

记住这两行就够，剩下的能推出来。

**Filter：取 → 验 → 存 → 传**

| 步 | 做什么 | 用什么 |
|---|---|---|
| 取 | 从 `Authorization` 头拿到 token | `request` |
| 验 | 验签 + 查过期，换出 userId | `jwtService` |
| 存 | 把身份放进 `SecurityContextHolder` | 静态方法 |
| 传 | 交给下一关 | `filterChain` |

**唯一的铁律：前三步都可以失败，第四步永远执行。**

失败的表现只有一个 —— 盒子里没身份。不是返回 401（理由见上一份笔记「没带 token 为什么是放行，而不是直接 401」）。

**EntryPoint：码 → 型 → 体**

| 步 | 代码 |
|---|---|
| 码 | `response.setStatus(401)` |
| 型 | `response.setContentType("application/json;charset=UTF-8")` |
| 体 | `response.getWriter().write(BODY)` |

顺序不能换：响应头一旦跟着响应体发出去，就改不了了。

前端对照 —— 这三步在 express 里是一行的三段：

```js
res.status(401).type('application/json').send(body);
//  ↑码          ↑型                      ↑体
```

Servlet API 只是把这一行拆成了三句，语义完全一样。

第一层结束。**两个口诀 + 「参数就是工具、void 就靠副作用」这个习惯，已经够写出七成了。**

---

# 第二层 · 一行行敲出来

下面是两次真实的键盘过程，每一步都是一次自问自答。**建议边读边在编辑器里跟着敲。**

## 四、JwtAuthenticationFilter

### 4.1 第 0 步：这段代码什么时候跑？

先问自己：验 token 这件事，要在哪个时刻做？

答：在 Controller 之前 —— 因为 Spring Security 检查「有没有身份」就发生在 Controller 之前，写在 Controller 里已经太晚了。

「Controller 之前」这个位置的扩展点叫 Filter。**这一步定下来的只是「我要写一个 Filter」，还没有任何具体代码。**

### 4.2 第 1 步：类声明

```java
public class JwtAuthenticationFilter extends OncePerRequestFilter {
}
```

这里唯一背不出来的是 `OncePerRequestFilter` 这个名字。**别试着背它。** 记住需求「我要一个能保证每个请求只跑一次的过滤器基类」，然后用三个办法之一找到名字：

1. 项目里已有同类代码 → 直接看（最快，也是实际工作中最常用的）
2. IDEA 里敲 `extends OncePer` 让补全提示（知道开头几个字母就够）
3. 搜「spring boot filter 每个请求只执行一次」

**记不住类名不算问题，记不住「我需要什么」才是问题。**

### 4.3 第 2 步：让 IDE 生成方法签名

在类体里按 `Ctrl+O`（Mac 上也是 Ctrl，不是 Cmd）→ 选 `doFilterInternal`，签名和 `throws` 全都是生成的：

```java
@Override
protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain) throws ServletException, IOException {
}
```

这一步是**「写不出来」的一个常见误解**：以为得背下 `protected void doFilterInternal(...) throws ServletException, IOException` 这一长串。不用。你只需要知道要覆写哪个方法名，剩下的是工具活。

（为什么是 `doFilterInternal` 而不是 `doFilter`：`doFilter` 被基类占去做去重判断了 —— 上一份笔记「Filter 是什么」。IDE 的列表里两个都在，选错了去重就失效。）

### 4.4 第 3 步：盘点工具

写第一行逻辑之前，先看一眼手里有什么：`request`（读）、`response`（写）、`filterChain`（放行），加上构造器注入进来的 `jwtService`（验 token）。

四步分别用哪个，「两个口诀」那节的表格已经列好。注意 `response` 我们其实用不上 —— 这个过滤器从不自己写响应。

### 4.5 第 4 步：取

```java
String header = request.getHeader("Authorization");
```

写完立刻发现有两个字符串会被反复用到（头名字、`Bearer ` 前缀），提成常量：

```java
private static final String HEADER_NAME = "Authorization";
private static final String TOKEN_PREFIX = "Bearer ";
```

`"Bearer "` 末尾那个空格是个坑，写的时候就要留意，别等出 bug 再查。

### 4.6 第 5 步：早退

问：header 是 null，或者不以 `Bearer ` 开头，怎么办？

答：那这个请求就是匿名的，没什么可验的 —— **直接跳到第四步「传」，然后 return。**

```java
if (header == null || !header.startsWith(TOKEN_PREFIX)) {
    filterChain.doFilter(request, response);
    return;
}

String token = header.substring(TOKEN_PREFIX.length());
```

这里必须先 `filterChain.doFilter(...)` 再 `return`。只写 `return` 请求就断在这儿了（症状见「写完自查表」，比你想的更难查）。

### 4.7 第 6 步：验

`jwtService.parseUserId(token)` 的失败是**抛异常**表达的 —— 不返回 null，也不返回 false。所以调用它的地方**必须**有 try/catch。这不是防御性习惯，是被它的签名逼出来的。

```java
try {
    long userId = jwtService.parseUserId(token);
    // 第 7 步填这里
} catch (JwtException | IllegalArgumentException e) {
    SecurityContextHolder.clearContext();
}
```

catch 里做什么？回到铁律：失败 = 盒子里没身份。所以是清空，不是返回 401。

### 4.8 第 7 步：存

```java
UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(userId, null, List.of());

SecurityContext context = SecurityContextHolder.createEmptyContext();
context.setAuthentication(authentication);
SecurityContextHolder.setContext(context);
```

写的时候只需要记住两件事，其余都是套路：

- **传三个参数**（第三个是权限列表）才算「已认证」。两参版本是「待验证」，token 再正确也照样 401
- 新建 context 再整体 `setContext`，别去改 `getContext()` 返回的那个

（为什么，见上一份笔记「宣布身份」和「为什么新建 context 而不是改现成的」。这里只强调「写的时候要记得」。）

### 4.9 第 8 步：传

```java
filterChain.doFilter(request, response);
```

位置很关键：**在 try/catch 外面。** 这样四种情况（没 token、验签成功、验签失败、`sub` 不是数字）最后都会往下走，区别只在盒子里有没有身份。

写完自查一句话：**每一条可能的执行路径上，是不是都恰好经过一次 `doFilter`？** 是 → 对了。

### 4.10 拼起来

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER_NAME = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HEADER_NAME);                        // 取

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(TOKEN_PREFIX.length());

        try {
            long userId = jwtService.parseUserId(token);                       // 验

            UsernamePasswordAuthenticationToken authentication =               // 存
                    new UsernamePasswordAuthenticationToken(userId, null, List.of());
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        } catch (JwtException | IllegalArgumentException e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);                               // 传
    }
}
```

`@Component` 是为了让容器管它，`SecurityConfig` 才能把它作为参数注入进来（见「写完必须接线」）。

## 五、RestAuthenticationEntryPoint

比 Filter 简单得多，真正的逻辑只有三行。难的是「为什么需要它」，那部分上一份笔记「RestAuthenticationEntryPoint：写出 401」讲过，这里只讲怎么写。

### 5.1 第 0 步：什么时候跑？

**「已经决定要拒绝了，需要有人把这句拒绝写给前端」** 的时候。

注意跟 Filter 的区别：Filter 是「试着认人」，EntryPoint 是「认不出来之后的善后」。它**不做任何判断** —— 被调用到的时候，结论早就定了。想清这一点，你就不会在里面写任何 `if`。

### 5.2 第 1 步：这次是 implements，不是 extends

```java
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
}
```

**怎么判断该用 extends 还是 implements？** 一个够用的规则：

| 情况 | 用 | 本项目的例子 |
|---|---|---|
| 有现成行为要复用或微调 | `extends` 基类 | Filter：去重逻辑是基类给的 |
| 只是要满足一个约定，没有行为可继承 | `implements` 接口 | EntryPoint：三行全是自己写的 |

Filter 也可以写成 `implements Filter`，但那样去重就得自己做，所以选了基类。**这个判断是能推出来的，不用记。**

### 5.3 第 2 步：生成方法

按 `Ctrl+I`（实现接口方法）→ 只有一个 `commence`：

```java
@Override
public void commence(HttpServletRequest request,
                     HttpServletResponse response,
                     AuthenticationException authException) throws IOException, ServletException {
}
```

`commence` 这个词根本背不出来 —— 不用背。接口只有一个方法，IDE 生成的时候没有选择余地。

### 5.4 第 3 步：盘点工具

- `request`：用不到（想让 401 里带上请求路径时才会用）
- `response`：全部工作都在它身上
- `authException`：失败的原因。我们不区分原因、统一一句话，所以也用不到

**参数用不上是正常的** —— 接口是给所有场景设计的，你的场景只用其中一部分。不用为了「参数没用上」心虚。

### 5.5 第 4 步：三行

就是「两个口诀」里的「码 → 型 → 体」：

```java
response.setStatus(HttpStatus.UNAUTHORIZED.value());
response.setContentType("application/json;charset=UTF-8");
response.getWriter().write(BODY);
```

三个易错点：

- `setStatus` 必须在 `write` 之前
- `charset=UTF-8` 别省，中文会乱码
- body 是手写的 JSON 字符串，字段名要跟 `ApiResponse` 对得上（这里写死了，改进方式见上一份笔记「五个可以改进的地方」第 3 条）

### 5.6 拼起来

```java
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String BODY = "{\"code\":401,\"message\":\"未登录或登录已过期\",\"data\":null}";

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(BODY);
    }
}
```

顺带一条 Java 规则：接口声明的是 `throws IOException, ServletException`，我们的实现只写了 `IOException`。**实现方法可以少声明异常，不能多声明** —— 调用方是按接口的声明来 catch 的，少抛不会破坏它，多抛会。

## 六、两个类之间没有调用关系（一个隐藏的卡点）

如果你以为「Filter 发现 token 不对，就调用 EntryPoint 返回 401」，那么写 Filter 的时候你会想往里注入一个 EntryPoint，然后卡住 —— 因为代码里根本没有这条线。

实际的传递路径是这样的：

```text
JwtAuthenticationFilter       把身份放进盒子（或者不放）
        ↓ 一句话不说，只是放行
AuthorizationFilter           按规则检查：这条路径要身份，盒子里有吗？没有 → 抛异常
        ↓ 异常往外冒
ExceptionTranslationFilter    接住 → 发现当前是匿名 → 调 EntryPoint
        ↓
RestAuthenticationEntryPoint  写出 401 JSON
```

**两个类通过「盒子」间接通信，中间隔着两个 Spring Security 自带的过滤器。** 这就是它们能各写各的、互不引用的原因 —— 也是为什么写 Filter 时你完全不用考虑 401 长什么样。

（完整的链和顺序见上一份笔记「Spring Security 的过滤器链完整长什么样」「401 和 403 到底由谁决定」。）

## 七、写完必须接线

两个类写完不会自动生效，尤其 EntryPoint —— 没人知道它存在。`SecurityConfig` 里各一行：

```java
.exceptionHandling(ex -> ex.authenticationEntryPoint(restAuthenticationEntryPoint))     // 声明 EntryPoint
.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);  // 插入 Filter
```

漏掉的症状不一样，值得分清：

- 漏 `exceptionHandling` → 功能正常，但返回 **403** 而不是 401，前端不会自动跳登录页
- 漏 `addFilterBefore` → token 完全不起作用，所有受保护接口一律 401

第二层结束。**到这儿，两个类你应该能在「只查类名」的条件下写出来了。**

---

# 第三层 · 怎么练到真的能写

## 八、三档默写

别一上来就空白默写，会卡在类名上然后放弃。分三档，每档之间隔一天：

| 档 | 允许看 | 目标 |
|---|---|---|
| 1 | 对着源码抄一遍，边抄边说出每行属于「取验存传」哪一步 | 手熟 + 建立结构感 |
| 2 | 只看「两个口诀」那两张表 | 能自己长出结构 |
| 3 | 空白文件，只允许查类名（`OncePerRequestFilter`、`commence` 这种） | 达标 |

第 3 档卡住时，问自己「先读方法签名」那句：**「这个方法的参数给了我什么？返回 void 意味着结果怎么交付？」** 通常就能接上。

**判断标准不是「一字不差」**，是这几条：四步都在、每条路径都恰好一次 `doFilter`、构造器传三个参数、`setStatus` 在 `write` 前面。变量名不一样完全没关系。

## 九、写完自查表

按「你可能漏掉什么」组织。症状记住了，以后调试能省大量时间：

| 漏掉 / 写错 | 症状 |
|---|---|
| 最后那句 `filterChain.doFilter` | **返回 200 + 空响应体**，前端拿到 `data` 是空的却不报错 |
| 早退分支里只 `return` 没 `doFilter` | 同上，且所有不带 token 的请求（包括登录）都这样 |
| `UsernamePasswordAuthenticationToken` 用了两参构造器 | token 正确却一直 401 |
| `"Bearer"` 少了末尾空格 | 一直 401（token 多一个前导空格，验签失败） |
| 覆写成 `doFilter` 而不是 `doFilterInternal` | 去重失效，转发到 `/error` 的请求会解析两次 token |
| `addFilterBefore` 排到了权限检查之后 | token 正确却一直 401（检查时盒子还是空的） |
| `SecurityConfig` 漏 `exceptionHandling` | 返回 403 而不是 401 |
| `setStatus` 写在 `write` 之后 | 状态码还是 200，前端拦截器不认 |
| `contentType` 漏 `charset=UTF-8` | 响应里的中文乱码 |
| 想用 `@RestControllerAdvice` 处理 401 | 完全不触发（Filter 在 DispatcherServlet 之前） |

两点值得单独记：

- **漏 `doFilter` 不是「请求挂住」，是立刻返回一个空的 200。** 用一个最小 Tomcat 实测过：过滤器方法返回后，容器直接把响应交出去，`Content-Length: 0`，耗时几毫秒。这比超时难查得多 —— 前端不报错，只是数据莫名其妙没了
- **「token 正确却一直 401」有三个不同原因**，光看症状分不出来。所以调试第一步是在 catch 里加一行 `log.debug`，先确认到底是验签失败还是身份没生效（上一份笔记「验签失败也不 401」）

## 十、三个练手任务

**1. 三行 Filter，把 Filter 从抽象变具体（10 分钟）**

新建一个类，只做一件事：打印每个请求的路径。

```java
@Component
public class RequestLogFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLogFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("→ {} {}", request.getMethod(), request.getRequestURI());
        filterChain.doFilter(request, response);
    }
}
```

启动，随便点几下前端，看控制台。然后**故意把 `doFilter` 那行注释掉**再试一次 —— 亲眼看见「200 + 空响应体」，这个坑以后就不会踩了。看完把这个类删掉。

**2. 三种 token 各跑一遍，确认 401 从哪来（15 分钟）**

不带 token：

```bash
curl -i http://localhost:8080/users/1
```

带一个假 token：

```bash
curl -i -H "Authorization: Bearer hello.world.abc" http://localhost:8080/users/1
```

带登录接口返回的真 token：

```bash
curl -i -H "Authorization: Bearer 把真token贴这里" http://localhost:8080/users/1
```

前两个应该是 401 加上你在 EntryPoint 里写的那段 JSON，第三个是 200。**看到自己手写的 JSON 原样出现在响应体里**，EntryPoint 的作用就有实感了。

**3. 写 `GET /users/me`（30 分钟，项目现在真缺这个接口）**

这是**唯一能证明「身份真的进了盒子」的接口** —— 前两个练习只能证明「有身份 / 没身份」，这个能证明「身份是几号」。

要做的：`UserController` 里加一个方法，从 `SecurityContextHolder` 或者 `@AuthenticationPrincipal` 拿到 userId（两种写法见上一份笔记「把身份放进『当前请求的身份盒子』」），调 `userService.findById`，返回 `ApiResponse<UserVo>`。

自查：用两个不同账号的 token 各请求一次，返回的必须是各自的信息。如果撞上 `ClassCastException`，就是取出来的类型和 Filter 里第一个参数塞进去的类型不一致 —— 这两处必须对应。

## 十一、一句话总结

**读不懂是知识问题，写不出是结构问题。**

结构就三件事：

1. 方法签名告诉你能用什么、成果怎么交付（`void` = 靠副作用）
2. Filter 是「取验存传」，前三步可以失败，第四步必须执行
3. EntryPoint 是「码型体」，它不做判断，只负责把已经定了的结论写出去

类名忘了随时能查，这三件事忘了就还是写不出来。








