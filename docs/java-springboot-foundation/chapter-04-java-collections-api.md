# 第 4 章 集合、Stream 和常用 Java API

## 本章目标

- 掌握 List、Set、Map 的区别和常用方法。
- 读懂 Stream、Lambda、filter、map 和方法引用。
- 能够选择易读的集合处理方式。

> 本章是独立学习章节。建议先阅读本章，再完成文末练习，并回到项目中寻找对应代码。

### 4.1 `List`、`Set`、`Map`

| 类型 | 特点 | 典型场景 |
|---|---|---|
| `List` | 有顺序、允许重复 | 分页结果、用户列表 |
| `Set` | 不强调重复元素 | 成员 id 集合 |
| `Map` | 键值对 | 按 id 快速查对象 |

```java
List<User> users = new ArrayList<>();
Set<Long> memberIds = new HashSet<>();
Map<Long, User> userMap = new HashMap<>();
```

### 4.2 常用集合方法

| 方法 | 作用 |
|---|---|
| `add(value)` | 添加元素 |
| `get(index)` | 按下标取元素，`List` 使用 |
| `size()` | 获取元素数量 |
| `isEmpty()` | 判断是否没有元素 |
| `contains(value)` | 判断是否包含元素 |
| `remove(value)` | 删除元素 |
| `put(key, value)` | 写入键值对，`Map` 使用 |
| `get(key)` | 按键取值，`Map` 使用 |
| `containsKey(key)` | 判断键是否存在 |
| `stream()` | 创建流 |

### 4.3 Stream 常用操作

```java
List<UserVo> result = users.stream()
        .filter(user -> user.getAge() >= 18)
        .map(this::convertToVo)
        .toList();
```

执行顺序：

```text
users
    ↓ filter：保留符合条件的 User
    ↓ map：把 User 转成 UserVo
    ↓ toList：收集成列表
```

常见操作：

| 操作 | 作用 |
|---|---|
| `filter` | 过滤元素 |
| `map` | 把一种元素转换成另一种元素 |
| `sorted` | 排序 |
| `distinct` | 去重 |
| `count` | 统计数量 |
| `toList` | 收集成列表 |
| `forEach` | 对每个元素执行动作 |

复杂链式操作要加注释或拆成多个步骤。可读性比减少几行代码更重要。

### 4.4 `Objects.equals`

当两个对象可能存在空值且要比较内容时，可以使用：

```java
Objects.equals(firstId, secondId)
```

它表达的是“比较两个对象的内容是否相等”，而不是比较引用地址。当前 `UserService.updateUser` 的邮箱查重逻辑中就使用了类似思路。

### 4.5 常见日期类型

后续用户、项目和任务表会涉及时间字段，优先认识：

- `LocalDate`：只有日期；
- `LocalDateTime`：日期和时间；
- `Instant`：时间线上的瞬时点。

数据库字段和 Java 时间类型要在设计阶段明确对应关系，不要在 Controller 中随意拼接时间字符串。

---

上一章：[`第 3 章`](chapter-03-java-oop-generics.md)

下一章：[`第 5 章`](chapter-05-java-exceptions-annotations.md)
返回目录：[`README.md`](README.md)
