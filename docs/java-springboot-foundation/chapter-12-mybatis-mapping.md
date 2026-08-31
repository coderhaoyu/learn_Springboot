# 第 12 章 MyBatis 接口、XML 和对象映射

## 本章目标

- 理解 Mapper 接口和 XML 的匹配规则。
- 掌握 namespace、id、@Param、#{ }、resultType 和主键回填。
- 知道动态 SQL 应该如何与 Service 业务规则配合。

> 本章是独立学习章节。建议先阅读本章，再完成文末练习，并回到项目中寻找对应代码。

### 12.1 Mapper 接口

```java
public interface UserMapper {
    User findById(long id);

    List<User> findByPage(
            @Param("offset") int offset,
            @Param("limit") int limit);

    long count();
}
```

Mapper 接口只描述数据访问能力，不负责业务流程。

### 12.2 XML 的匹配规则

```xml
<mapper namespace="com.example.userdemo.mapper.UserMapper">
    <select id="findById" resultType="com.example.userdemo.entity.User">
        SELECT id, name, age, email
        FROM users
        WHERE id = #{id}
    </select>
</mapper>
```

匹配关系：

```text
namespace = Mapper 接口的全限定类名
id        = Mapper 接口的方法名
参数      = Java 方法传入的数据
resultType = 查询结果要转换成的 Java 类型
```

### 12.3 `@MapperScan`

启动类上的：

```java
@MapperScan("com.example.userdemo.mapper")
```

表示扫描指定包中的 Mapper 接口，并让 MyBatis 创建可以注入的代理对象。

```text
UserMapper 没有手写实现类
    ↓
MyBatis 读取接口和 XML
    ↓
创建 Mapper 代理
    ↓
Spring 注册代理 Bean
    ↓
Service 通过构造方法取得代理
```

### 12.4 `@Param`

多个简单参数需要明确命名：

```java
List<User> findByPage(
        @Param("offset") int offset,
        @Param("limit") int limit);
```

XML 才能稳定使用：

```xml
LIMIT #{limit} OFFSET #{offset}
```

只传一个对象时，可以使用对象属性名：

```xml
WHERE email = #{email}
```

### 12.5 `#{}` 和 `${}`

优先使用 `#{}`：

```xml
WHERE email = #{email}
```

它表示参数绑定。

`${}` 是文本拼接：

```xml
ORDER BY ${sortColumn}
```

它只适合经过严格白名单控制的 SQL 结构拼接，不应直接接收客户端原始输入。

记忆：

```text
#{value}：绑定一个值
${text}：拼接 SQL 文本
```

### 12.6 `resultType` 和 `resultMap`

字段简单且命名能够对应时使用 `resultType`：

```xml
<select id="findById" resultType="com.example.userdemo.entity.User">
    SELECT id, name, age, email
    FROM users
    WHERE id = #{id}
</select>
```

字段名称复杂、需要别名或关联对象时使用 `resultMap`：

```text
created_at
    ↓
createdAt
```

当前配置：

```properties
mybatis.configuration.map-underscore-to-camel-case=true
```

### 12.7 主键回填

```xml
<insert id="addUser" useGeneratedKeys="true" keyProperty="id">
    INSERT INTO users(name, age, email)
    VALUES (#{name}, #{age}, #{email})
</insert>
```

- `useGeneratedKeys`：使用数据库生成的主键；
- `keyProperty`：把主键回填到 Java 对象的哪个属性。

### 12.8 Mapper 返回值

| 返回类型 | 适合场景 |
|---|---|
| `User` | 单条查询 |
| `List<User>` | 多条查询 |
| `long` / `int` | 统计或受影响行数 |
| `void` | 调用者不关心返回值 |

如果业务需要判断修改或删除是否真的影响了记录，返回受影响行数通常比 `void` 提供更多信息。

### 12.9 动态 SQL

任务模块会根据可选筛选条件拼接查询条件。动态 SQL 的思路是：

```text
固定 SELECT、FROM
    ↓
根据实际传入的条件决定是否追加 WHERE 条件
    ↓
所有值仍然使用参数绑定
```

动态 SQL 重点学习 `<if>`、`<where>`、`<trim>`，但不要把 SQL 结构和业务判断全部堆在 XML 中。业务规则仍然属于 Service。

---

上一章：[`第 11 章`](chapter-11-sql-basics.md)

下一章：[`第 13 章`](chapter-13-crud-transaction-lock.md)
返回目录：[`README.md`](README.md)
