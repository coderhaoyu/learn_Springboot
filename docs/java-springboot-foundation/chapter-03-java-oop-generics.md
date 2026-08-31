# 第 3 章 类、对象、接口与泛型

## 本章目标

- 理解类、对象、封装、接口、实现类和泛型。
- 知道 MyBatis Mapper 接口为什么可以没有手写实现类。
- 避免在 CRUD 初期为了抽象而抽象。

> 本章是独立学习章节。建议先阅读本章，再完成文末练习，并回到项目中寻找对应代码。

### 3.1 类和对象

类是对象的设计；对象是类创建出来的实例。

```java
User user = new User();
```

- `User`：类型；
- `user`：变量；
- `new User()`：创建对象。

### 3.2 封装

字段通常使用 `private`，通过方法访问：

```java
private String name;

public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}
```

封装的价值是控制对象内部数据的访问方式，而不是为了机械生成 Getter / Setter。

### 3.3 接口和实现

接口描述能力：

```java
public interface UserRepository {
    User findById(long id);
}
```

实现类提供具体做法：

```java
public class MemoryUserRepository implements UserRepository {
    @Override
    public User findById(long id) {
        return null;
    }
}
```

MyBatis Mapper 接口可以先理解为：

```text
接口声明数据库能力
    ↓
MyBatis 根据接口和 XML 创建代理实现
    ↓
Service 通过接口调用数据库
```

### 3.4 `@Override`

```java
@Override
public User findById(long id) {
    // 编译器会检查这个方法是否真的覆盖接口方法
    return null;
}
```

它能避免方法名或参数写错后，误以为自己完成了覆盖。

### 3.5 泛型基础

```java
List<User> users;
List<UserVo> userVos;
ApiResponse<UserVo> response;
PageResult<UserVo> pageResult;
```

泛型让类型信息在集合和响应对象中保留下来：

```text
List<User> 只能放 User
ApiResponse<UserVo> 的 data 类型是 UserVo
```

### 3.6 什么时候先不用复杂抽象

当前项目处于 CRUD 学习阶段，不需要为了“看起来专业”提前创建：

- 多层 Repository 抽象；
- 通用 BaseService；
- 自动代码生成器；
- 大量接口和实现类；
- 与业务无关的设计模式。

先让每个类的职责清晰，再根据重复和变化决定是否抽象。

---

上一章：[`第 2 章`](chapter-02-java-methods.md)

下一章：[`第 4 章`](chapter-04-java-collections-api.md)
返回目录：[`README.md`](README.md)
