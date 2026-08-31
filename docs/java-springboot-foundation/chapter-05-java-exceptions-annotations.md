# 第 5 章 异常、注解和代码阅读

## 本章目标

- 理解异常如何中断方法流程并向上层传递。
- 理解 throw、throws、自定义业务异常和注解。
- 掌握阅读 Java 类和方法的固定顺序。

> 本章是独立学习章节。建议先阅读本章，再完成文末练习，并回到项目中寻找对应代码。

### 5.1 异常的作用

异常表示当前方法无法完成约定动作：

```java
public UserVo findById(long id) {
    User user = userMapper.findById(id);
    if (user == null) {
        throw new BusinessException(404, "用户不存在");
    }
    return convertToVo(user);
}
```

执行流程：

```text
查询数据
    ↓
发现业务条件不成立
    ↓
throw
    ↓
当前方法结束
    ↓
上层或全局处理器接收异常
```

### 5.2 `throw` 和 `throws`

- `throw`：实际抛出异常对象；
- `throws`：在方法签名中声明可能抛出的异常。

当前阶段重点掌握运行时异常和自定义 `BusinessException`。

### 5.3 自定义业务异常

当前项目的 `BusinessException` 包含：

- 业务失败信息；
- 一个用于当前阶段响应的状态码。

后续项目变复杂后，可以再区分“业务错误码”和“HTTP 状态码”。当前先保持简单，重点理解异常如何从 Service 传到全局处理器。

### 5.4 注解是什么

注解是附着在类、方法、字段或参数上的元数据：

```java
@Service
public class UserService {
}
```

它本身不是完整业务逻辑。框架启动或运行时会读取它：

```text
开发者写注解
    ↓
Spring / MyBatis 扫描并读取注解
    ↓
注册 Bean、路由、校验器或数据库代理
```

看到注解时固定问四个问题：

1. 它标记在哪里？
2. 谁会读取它？
3. 它改变了什么行为？
4. 它应该属于哪一层？

### 5.5 Java 代码阅读方法

阅读一个类：

1. 看它属于哪个包；
2. 看它实现或依赖哪些类型；
3. 看字段代表什么状态；
4. 看构造方法如何初始化；
5. 看公开方法提供什么能力；
6. 看异常由谁处理。

阅读一个方法：

1. 先看返回类型；
2. 再看参数；
3. 再看第一行调用了什么；
4. 追踪返回值如何产生；
5. 找到失败分支和异常出口。

---

上一章：[`第 4 章`](chapter-04-java-collections-api.md)

下一章：[`第 6 章`](chapter-06-maven-project-structure.md)
返回目录：[`README.md`](README.md)
