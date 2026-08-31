# 速查表与官方资料

## Java 关键字

| 关键字 | 含义 |
|---|---|
| `class` | 定义类 |
| `interface` | 定义接口 |
| `new` | 创建对象 |
| `this` | 当前对象 |
| `final` | 不再重新赋值 |
| `static` | 属于类本身 |
| `return` | 返回结果并结束方法 |
| `void` | 没有业务返回值 |
| `throw` | 抛出异常 |
| `extends` | 继承类 |
| `implements` | 实现接口 |
| `public` | 对外可访问 |
| `private` | 仅当前类可访问 |

## Spring Boot 常用注解

| 注解 | 记忆方式 |
|---|---|
| `@SpringBootApplication` | 启动、自动配置、扫描组件 |
| `@Component` | 通用 Bean |
| `@Service` | 业务 Bean |
| `@Repository` | 持久化组件 |
| `@RestController` | 返回 JSON 的 Controller |
| `@RequestMapping` | 公共路径或请求匹配规则 |
| `@GetMapping` | GET 路由 |
| `@PostMapping` | POST 路由 |
| `@PutMapping` | PUT 路由 |
| `@DeleteMapping` | DELETE 路由 |
| `@PathVariable` | URL 路径参数 |
| `@RequestParam` | 查询参数 |
| `@ModelAttribute` | 查询参数绑定对象 |
| `@RequestBody` | JSON 请求体 |
| `@Valid` | 校验对象 |
| `@Validated` | 启用 Spring 校验 |
| `@RestControllerAdvice` | 全局 REST 异常处理 |
| `@ExceptionHandler` | 指定异常处理方法 |
| `@Transactional` | 声明事务边界 |
| `@MapperScan` | 扫描 MyBatis Mapper |

## SQL 关键字

| 关键字 | 作用 |
|---|---|
| `CREATE TABLE` | 创建表 |
| `INSERT INTO` | 新增记录 |
| `SELECT` | 查询记录 |
| `UPDATE` | 修改记录 |
| `DELETE` | 删除记录 |
| `WHERE` | 查询或修改条件 |
| `ORDER BY` | 排序 |
| `LIMIT` | 限制返回数量 |
| `OFFSET` | 跳过记录数量 |
| `COUNT` | 统计数量 |
| `JOIN` | 关联多张表 |
| `GROUP BY` | 分组 |
| `CREATE INDEX` | 创建索引 |
| `EXPLAIN` | 查看执行计划 |
| `COMMIT` | 提交事务 |
| `ROLLBACK` | 回滚事务 |

## 官方资料

- [Java SE 21 语言规范](https://docs.oracle.com/javase/specs/jls/se21/html/)
- [Spring Boot：`@SpringBootApplication`](https://docs.spring.io/spring-boot/reference/using/using-the-springbootapplication-annotation.html)
- [Spring Framework：IoC 容器](https://docs.spring.io/spring-framework/reference/core/beans.html)
- [Spring MVC：请求映射](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-requestmapping.html)
- [Spring MVC：注解式 Controller](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller.html)
- [Spring Framework：事务管理](https://docs.spring.io/spring-framework/reference/data-access/transaction.html)
- [Jakarta Bean Validation 规范](https://jakarta.ee/specifications/bean-validation/3.1/jakarta-validation-spec-3.1)
- [MyBatis：Mapper XML](https://mybatis.org/mybatis-3/sqlmap-xml.html)
- [MyBatis-Spring：Mapper 扫描与注入](https://mybatis.org/spring/mappers.html)

查官方资料时，不要只复制代码，至少回答：

```text
这个能力解决什么问题？
它应该放在哪一层？
它改变了请求、对象、事务或 SQL 的哪一部分？
```

---

## 最终掌握标准

完成基础补课和第 1–2 周后，你应该能够：

- 读懂一个 Java 类和方法签名；
- 自己设计简单方法的参数和返回类型；
- 解释构造方法、接口、泛型和集合的作用；
- 解释 Spring 如何创建和组装 Bean；
- 说清楚常用 Spring Boot 注解的职责；
- 说清楚 URL 参数、查询参数和 JSON 请求体的区别；
- 读懂 `CREATE TABLE`、`SELECT`、`INSERT`、`UPDATE`、`DELETE`；
- 解释 Mapper 接口如何找到 XML SQL；
- 解释 DTO、Entity、VO 的数据流向；
- 解释 Service 查重、数据库唯一约束和事务的区别；
- 从 HTTP 请求一路追踪到 MySQL，再解释 JSON 如何返回。

等这些内容能够脱离文档讲清楚，再继续深入认证、安全、复杂查询、测试和部署。
