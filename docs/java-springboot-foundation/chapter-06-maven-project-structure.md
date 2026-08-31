# 第 6 章 Maven 与项目结构

## 本章目标

- 读懂 pom.xml 中的依赖、插件和版本。
- 理解 Maven 项目目录和 Spring Boot 资源目录。
- 知道如何用定向方式运行相关操作。

> 本章是独立学习章节。建议先阅读本章，再完成文末练习，并回到项目中寻找对应代码。

### 6.1 `pom.xml`

`pom.xml` 可以理解为 Maven 项目的依赖和构建说明书。

| 配置 | 作用 |
|---|---|
| `parent` | 继承 Spring Boot 的依赖管理和默认配置 |
| `dependencies` | 声明代码运行或测试需要的库 |
| `properties` | 集中定义版本和构建属性 |
| `plugins` | 参与编译、运行、测试和打包 |
| `scope` | 说明依赖在哪个阶段生效 |

依赖和插件的区别：

```text
dependency：给 Java 代码提供类和功能
plugin：参与项目的编译、运行、测试或打包
```

### 6.2 项目目录

```text
src/main/java/com/example/userdemo
├── common
│   ├── exception
│   └── response
├── controller
├── dto
├── entity
├── mapper
├── service
├── vo
└── UserDemoApplication.java

src/main/resources
├── application.properties
└── mapper/UserMapper.xml
```

### 6.3 常用 Maven 操作

```text
./mvnw spring-boot:run       启动应用
./mvnw test                  运行测试
./mvnw package               打包应用
```

学习和排错时，优先使用与当前修改相关的定向测试或编译操作，不把无关的全项目检查混在一起。

---

# 第二部分：Spring Boot 与 Spring MVC

---

上一章：[`第 5 章`](chapter-05-java-exceptions-annotations.md)

下一章：[`第 7 章`](chapter-07-spring-ioc-di.md)
返回目录：[`README.md`](README.md)
