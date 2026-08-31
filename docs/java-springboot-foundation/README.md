# Java 与 Spring Boot 后端学习手册

> 本目录把原来的长文档拆成“每章一个 Markdown 文件”，适合按章节逐个学习。
>
> 项目路径：`/Users/wuhaoyu/IdeaProjects/user-demo`

## 学习方式

每一章建议按下面的节奏完成：

```text
阅读本章
    ↓
脱离 Spring 写一个最小例子
    ↓
在当前项目中找到对应文件
    ↓
回答本章自测问题
    ↓
进入对应周计划完成业务任务
```

这套资料专门为当前项目的学习阶段整理：

- Java 21
- Spring Boot 4.1.0
- Spring MVC
- MyBatis Spring Boot Starter 4.1.0
- MySQL
- Maven
- JUnit 5 / Spring Boot Test

## 章节目录

### 第一部分：Java 语法与方法

| 章节 | 内容 | 学习重点 |
|---|---|---|
| [第 1 章：Java 文件、变量和类型](chapter-01-java-types.md) | Java 基本语法 | 类型、对象、控制流、`null` |
| [第 2 章：Java 方法（重点）](chapter-02-java-methods.md) | 方法专项学习 | 方法签名、参数、返回值、重载、泛型方法 |
| [第 3 章：类、对象、接口与泛型](chapter-03-java-oop-generics.md) | 面向对象 | 封装、接口、实现和抽象边界 |
| [第 4 章：集合、Stream 和常用 Java API](chapter-04-java-collections-api.md) | 数据处理 | `List`、`Set`、`Map`、Stream |
| [第 5 章：异常、注解和代码阅读](chapter-05-java-exceptions-annotations.md) | 错误和元数据 | `throw`、业务异常、注解、阅读方法 |
| [第 6 章：Maven 与项目结构](chapter-06-maven-project-structure.md) | 工程基础 | `pom.xml`、依赖、目录和运行方式 |

### 第二部分：Spring Boot 与 Spring MVC

| 章节 | 内容 | 学习重点 |
|---|---|---|
| [第 7 章：Spring IoC、Bean 和依赖注入](chapter-07-spring-ioc-di.md) | Spring 核心 | 容器、Bean、构造方法注入 |
| [第 8 章：Spring Boot 启动、配置和常用注解](chapter-08-spring-boot-annotations-config.md) | Boot 基础 | 启动类、组件、配置、事务 |
| [第 9 章：Spring MVC 与 HTTP 请求](chapter-09-spring-mvc-http.md) | Web 请求 | 路由、参数绑定、状态码 |
| [第 10 章：校验、DTO、VO、异常与响应](chapter-10-validation-dto-vo-exception.md) | 接口规范 | 校验、数据边界、全局异常 |

### 第三部分：数据库与 MyBatis

| 章节 | 内容 | 学习重点 |
|---|---|---|
| [第 11 章：数据库基础和 SQL 语法](chapter-11-sql-basics.md) | 数据库基础 | 建表、查询、修改、关联、索引、事务 |
| [第 12 章：MyBatis 接口、XML 和对象映射](chapter-12-mybatis-mapping.md) | 持久化 | Mapper、XML、参数和结果映射 |
| [第 13 章：CRUD、分页、事务、索引和乐观锁](chapter-13-crud-transaction-lock.md) | 业务数据操作 | 分层、分页、并发和一致性 |

### 第四部分：测试、安全与交付

| 章节 | 内容 | 学习重点 |
|---|---|---|
| [第 14 章：测试、调试与工程质量](chapter-14-testing-debugging.md) | 质量保障 | 测试分层、MockMvc、日志、调用栈 |
| [第 15 章：认证、安全和部署概念](chapter-15-security-deployment.md) | 后续预习 | 认证、授权、JWT、Docker 和配置 |

## 每周计划对应关系

| 阶段 | 先学章节 | 对应详细计划 |
|---|---|---|
| 基础补课 | 第 1–6 章 | [基础补课路线](weekly-roadmap.md) |
| 第 1 周：数据库 CRUD | 第 1、2、6、9、11、12、13 章 | [week-01](../weekly/week-01-database-crud.md) |
| 第 2 周：规范 CRUD | 第 2、4、8、9、10、13、14 章 | [week-02](../weekly/week-02-standard-crud.md) |
| 第 3 周：认证与权限 | 第 5、7、8、9、10、15 章 | [week-03](../weekly/week-03-auth-security.md) |
| 第 4 周：项目与成员 | 第 3、7、8、11、13 章 | [week-04](../weekly/week-04-project-member.md) |
| 第 5 周：任务与复杂查询 | 第 2、3、11、12、13 章 | [week-05](../weekly/week-05-task-query.md) |
| 第 6 周：测试与工程质量 | 第 11、12、13、14 章 | [week-06](../weekly/week-06-test-quality.md) |
| 第 7 周：部署与收尾 | 第 8、14、15 章 | [week-07](../weekly/week-07-deployment.md) |
| 最终验收：任务评论 | 全部章节，重点第 3、9–14 章 | [week-08](../weekly/week-08-final-assessment.md) |

## 辅助资料

- [每周学习路线](weekly-roadmap.md)：把章节和每周任务串起来。
- [当前项目代码对照表](project-code-map.md)：按文件理解现有代码。
- [第一次学习任务](exercises.md)：先做方法和请求链路练习。
- [速查表与官方资料](cheatsheet.md)：复习 Java、Spring Boot 和 SQL 关键字。

## 推荐开始顺序

如果现在最不熟悉的是 Java 方法，建议按下面顺序开始：

1. [第 1 章](chapter-01-java-types.md)：先补齐类型、对象和基本语法；
2. [第 2 章](chapter-02-java-methods.md)：重点学习方法；
3. [第 3 章](chapter-03-java-oop-generics.md)：理解类、接口和泛型；
4. [第 7 章](chapter-07-spring-ioc-di.md)：理解 Spring 如何管理对象；
5. [第 9 章](chapter-09-spring-mvc-http.md)：理解请求如何进入 Java；
6. [第 11 章](chapter-11-sql-basics.md)：学习 SQL；
7. [第 12 章](chapter-12-mybatis-mapping.md)：学习 Java 方法如何连接 XML SQL；
8. 再开始第 1 周数据库 CRUD。

## 当前项目代码入口

- 启动类：`src/main/java/com/example/userdemo/UserDemoApplication.java`
- Controller：`src/main/java/com/example/userdemo/controller/UserController.java`
- Service：`src/main/java/com/example/userdemo/service/UserService.java`
- Mapper：`src/main/java/com/example/userdemo/mapper/UserMapper.java`
- XML：`src/main/resources/mapper/UserMapper.xml`
- 建表脚本：`sql/schema.sql`

每次学习完一章，都回到这些文件中找一个对应例子，不要只停留在文档阅读。
