# 当前项目代码对照表

| 文件 | 建议重点 |
|---|---|
| `src/main/java/com/example/userdemo/UserDemoApplication.java` | 启动、组件扫描、`@MapperScan` |
| `src/main/java/com/example/userdemo/controller/UserController.java` | 路由、参数绑定、调用 Service |
| `src/main/java/com/example/userdemo/service/UserService.java` | 方法、业务规则、分页、Entity 转 VO |
| `src/main/java/com/example/userdemo/mapper/UserMapper.java` | Mapper 方法、`@Param`、返回类型 |
| `src/main/resources/mapper/UserMapper.xml` | SQL、namespace、id、参数和结果映射 |
| `src/main/java/com/example/userdemo/entity/User.java` | 字段、构造方法、Getter / Setter |
| `src/main/java/com/example/userdemo/dto/` | 请求对象和校验注解 |
| `src/main/java/com/example/userdemo/vo/UserVo.java` | 对外响应字段 |
| `src/main/java/com/example/userdemo/common/response/` | 泛型响应和分页结构 |
| `src/main/java/com/example/userdemo/common/exception/` | 自定义异常和全局处理 |
| `src/main/resources/application.properties` | 数据源和 MyBatis 配置 |
| `sql/schema.sql` | 表结构、约束和初始化数据 |

### 推荐的代码阅读顺序

```text
UserDemoApplication
    ↓
UserController
    ↓
CreateUserRequest / UserPageQueryRequest
    ↓
UserService
    ↓
UserMapper
    ↓
UserMapper.xml
    ↓
User
    ↓
UserVo / ApiResponse / PageResult
    ↓
GlobalExceptionHandler
```

---
