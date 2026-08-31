# 当前项目代码对照表

## 当前已有代码

| 文件 | 建议重点 |
|---|---|
| `/Users/wuhaoyu/IdeaProjects/user-demo/src/main/java/com/example/userdemo/UserDemoApplication.java` | 启动、组件扫描、`@MapperScan` |
| `/Users/wuhaoyu/IdeaProjects/user-demo/src/main/java/com/example/userdemo/controller/UserController.java` | 路由、参数绑定、调用 Service |
| `/Users/wuhaoyu/IdeaProjects/user-demo/src/main/java/com/example/userdemo/service/UserService.java` | 业务规则、分页、Entity 转 VO |
| `/Users/wuhaoyu/IdeaProjects/user-demo/src/main/java/com/example/userdemo/mapper/UserMapper.java` | Mapper 方法、参数和返回类型 |
| `/Users/wuhaoyu/IdeaProjects/user-demo/src/main/resources/mapper/UserMapper.xml` | SQL、namespace、参数和结果映射 |
| `/Users/wuhaoyu/IdeaProjects/user-demo/src/main/java/com/example/userdemo/entity/User.java` | 字段、构造方法和对象映射 |
| `/Users/wuhaoyu/IdeaProjects/user-demo/src/main/java/com/example/userdemo/dto/` | 请求对象和校验注解 |
| `/Users/wuhaoyu/IdeaProjects/user-demo/src/main/java/com/example/userdemo/vo/UserVo.java` | 对外响应字段 |
| `/Users/wuhaoyu/IdeaProjects/user-demo/src/main/java/com/example/userdemo/common/response/` | 泛型响应和分页结构 |
| `/Users/wuhaoyu/IdeaProjects/user-demo/src/main/java/com/example/userdemo/common/exception/` | 自定义异常和全局处理 |
| `/Users/wuhaoyu/IdeaProjects/user-demo/src/main/resources/application.properties` | 数据源和 MyBatis 配置 |
| `/Users/wuhaoyu/IdeaProjects/user-demo/sql/schema.sql` | 当前用户表、约束和初始化数据 |

## 目标后端结构

新增模块按业务组织。现有用户文件先不为了目录形式强行搬迁，等用户模块稳定后再统一整理。

```text
src/main/java/com/example/userdemo/
├── common/
│   ├── exception/
│   ├── response/
│   ├── security/
│   └── config/
├── auth/
│   ├── controller/
│   ├── service/
│   ├── dto/
│   └── vo/
├── user/
├── couple/
├── challenge/
├── checkin/
├── moment/
├── anniversary/
└── achievement/
```

每个业务模块只在确实需要时创建 Entity、DTO、VO、Mapper、Service 和 Controller，不提前生成空目录或无用途的抽象。

## 后续前端结构

后端核心验收通过后，前端再放在：

```text
frontend/
├── src/
│   ├── api/
│   ├── components/
│   ├── router/
│   ├── stores/
│   ├── types/
│   ├── views/
│   └── App.vue
└── package.json
```

## 推荐的后端代码阅读顺序

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

进入新模块后，继续使用同样的阅读顺序：

```text
需求和 API
    ↓
DTO
    ↓
Controller
    ↓
Service
    ↓
Mapper
    ↓
XML SQL
    ↓
Entity / VO
    ↓
异常和测试
```

完整模块职责、数据关系和 API 目录见：[产品设计：我们的冒险](../couple-challenge-design.md)。
