# 第 1 章 Java 文件、变量和类型

## 本章目标

- 读懂 Java 文件、包、变量、类型和基本控制流。
- 理解基本类型、包装类型、String、final 和 null。
- 能够阅读当前项目中的 Entity 和 DTO 字段。

> 本章是独立学习章节。建议先阅读本章，再完成文末练习，并回到项目中寻找对应代码。

### 1.1 Java 文件的基本结构

```java
package com.example.userdemo.service;

import com.example.userdemo.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    // 字段、构造方法和实例方法都属于这个类
}
```

### 1.2 代码结构怎么读

- `package`：声明当前类所属的包；
- `import`：引入其他包中的类型；
- `@Service`：告诉 Spring 这是一个业务组件；
- `public class UserService`：定义一个公开类；
- `{}`：表示代码块；
- `;`：通常表示一条语句结束。

Java 文件名通常和 `public` 类名一致。例如：

```text
UserService.java
    ↔
public class UserService
```

### 1.3 变量声明

Java 变量必须写明类型：

```java
int page = 1;
long id = 10L;
Integer age = 28;
Long userId = 10L;
String email = "user@example.com";
boolean enabled = true;
```

基本结构是：

```text
类型 变量名 = 初始值;
```

Java 编译器会根据类型检查赋值和方法调用是否合法。

### 1.4 基本类型和包装类型

| 基本类型 | 包装类型 | 项目中的理解 |
|---|---|---|
| `int` | `Integer` | 普通整数和可表达 `null` 的整数 |
| `long` | `Long` | 数据库主键和较大整数 |
| `boolean` | `Boolean` | 布尔状态 |
| `double` | `Double` | 小数，金额场景通常另选类型 |
| `char` | `Character` | 单个字符 |

当前项目使用 `Integer age` 和 `Long id`，因为包装类型可以表达“当前没有值”：

- 新增对象刚创建时，数据库生成的 `id` 还没有回填；
- 请求 DTO 中的年龄没有提交时，可以由 `@NotNull` 识别；
- 基本类型不能表示 `null`。

### 1.5 `String`

```java
String firstName = "阿西";
String lastName = "同学";
String fullName = firstName + lastName;
```

先掌握三个常用规则：

- 字符串使用双引号；
- 比较内容使用 `.equals()`；
- `==` 比较的是引用关系，不适合用来判断两个字符串内容是否相同。

常见字符串方法：

| 方法 | 作用 |
|---|---|
| `length()` | 获取长度 |
| `isEmpty()` | 判断是否为空字符串 |
| `isBlank()` | 判断是否为空或全是空白 |
| `equals()` | 比较内容 |
| `startsWith()` | 判断前缀 |
| `contains()` | 判断是否包含片段 |
| `substring()` | 截取字符串 |
| `trim()` | 去除首尾空白，具体场景按需求使用 |

### 1.6 `final`

```java
final int pageSize = 20;
```

`final` 表示变量完成赋值后不能再次改变。Service 中的依赖经常声明为 `final`：

```java
private final UserMapper userMapper;
```

这表达了一个设计意图：

```text
UserService 创建完成后，使用哪个 UserMapper 就确定了
```

### 1.7 条件判断

```java
if (age >= 18) {
    System.out.println("成年人");
} else {
    System.out.println("未成年人");
}
```

后端业务中的 `if` 应该表达业务规则，例如：

```text
如果用户不存在
    抛出用户不存在异常
否则
    继续修改用户
```

### 1.8 循环

普通 `for`：

```java
for (int index = 0; index < 3; index++) {
    System.out.println(index);
}
```

增强 `for`：

```java
for (User user : userList) {
    System.out.println(user.getName());
}
```

增强 `for` 的结构是：

```text
for (元素类型 元素变量 : 集合) {
    // 处理当前元素
}
```

### 1.9 `null` 的理解

`null` 表示一个对象引用当前没有指向对象：

```java
User user = null;
```

在本项目中，数据库查询“没有找到记录”时，Mapper 可能返回 `null`，Service 再根据业务规则决定是否抛出异常。

学习时要区分：

- **数据契约已明确的值**：直接按类型使用；
- **业务上允许不存在的查询结果**：由 Service 明确处理；
- 不要在每个方法里无目的地添加重复检查，让真正的业务规则被淹没。

---

下一章：[`第 2 章`](chapter-02-java-methods.md)
返回目录：[`README.md`](README.md)
