# cola-demo

基于 [COLA 5.x](https://github.com/alibaba/COLA)（Clean Object-oriented & Layered Architecture）架构的示例项目。

## 项目简介

cola-demo 是一个完整的 COLA 架构示例，展示了如何基于 DDD + CQRS 构建分层架构的微服务应用。项目采用六层架构设计，严格遵循依赖规则，将业务逻辑清晰分离为客户端层、适配器层、应用层、领域层和基础设施层。

## 项目结构

```
cola-demo/
├── cola-demo-client/              # 客户端层 - 对外暴露接口和DTO定义
│   ├── api/                       # 服务接口定义 (ICustomerService, IRoleService, IPermissionService)
│   ├── dto/                       # Command、Query 对象
│   │   └── data/                  # DTO 数据传输对象 (CustomerDTO, UserDTO, UserAuthInfo)
│   └── common/                    # 通用类型 (BizErrorCode, DomainException, PageResult分页工具)
├── cola-demo-adapter/             # 适配器层 - REST Controller入口
│   ├── web/                       # REST API 控制器
│   │   └── handler/               # 全局异常处理 (GlobalExceptionHandler)
│   ├── security/                  # JWT 认证 + Spring Security
│   └── filter/                    # 过滤器 (TraceIdFilter)
├── cola-demo-app/                 # 应用层 - Service业务逻辑 + Handler
│   ├── service/                   # 应用服务 (AuthService)
│   ├── executor/                  # CQRS Handler（按实体聚合）
│   ├── eventhandler/              # 领域事件处理器 (CustomerEventHandler, RoleEventHandler)
│   └── convertor/                 # MapStruct DTO 转换器
├── cola-demo-domain/              # 领域层 - 实体Entity + Gateway接口
│   ├── customer/                  # 客户领域 (Customer, CustomerCreatedEvent, CompanyType)
│   │   └── gateway/               # 领域网关接口 (CustomerGateway)
│   ├── user/                      # 用户领域 (User, Role, Permission, Password, ResourceType)
│   │   └── gateway/               # 领域网关接口 (UserGateway, RoleGateway, PermissionGateway)
│   └── common/                    # 领域通用类 (AggregateRoot, DomainEvent, DomainEventPublisher)
├── cola-demo-infrastructure/      # 基础设施层 - Gateway实现 + MyBatis持久化
│   ├── gatewayimpl/               # 领域网关实现
│   ├── event/                     # 领域事件发布实现 (SpringDomainEventPublisher)
│   ├── mapper/                    # MyBatis Mapper
│   ├── convertor/                 # MapStruct Entity 转换器
│   ├── dataobject/                # MyBatis Entity
│   └── config/                    # 配置类 (MyBatisPlusConfig)
└── cola-demo-start/               # 启动模块 - SpringBoot入口配置
    └── config/                    # SecurityConfig, OpenApiConfig
```

### 各层职责

| 层级 | 目录 | 职责 | 可依赖 |
|------|------|------|--------|
| **Client** | `client/api`, `client/dto`, `client/common` | 定义对外接口、DTO、Command、Query、异常契约 | 无 |
| **Adapter** | `adapter/web`, `adapter/security` | 接收HTTP请求，调用应用层服务 | Client, App |
| **App** | `app/service`, `app/executor`, `app/eventhandler` | 编排业务流程，执行CQRS命令/查询，处理领域事件 | Client, Domain |
| **Domain** | `domain/customer`, `domain/user`, `domain/common` | 核心业务实体、领域规则、网关接口、领域事件 | Client |
| **Infrastructure** | `infrastructure/gatewayimpl`, `infrastructure/mapper` | 数据库持久化、领域事件发布、外部服务调用 | Domain, Client |

### 分层规则

```
client (无依赖)
  ↑
domain → client
  ↑
app → client, domain
  ↑
adapter → app (仅通过app间接访问domain和client)
  ↑
infrastructure → domain, client
  ↑
start → adapter, infrastructure
```

**关键约束**：
- Adapter 层不直接依赖 Domain 层，必须通过 App 层间接访问
- Domain Gateway 返回 `PageResponse<Domain>`，通过 `PageResult` 工具类自动转换分页参数和结果
- 跨层异常契约（如 `DomainException`）放在 Client 层的 `common` 包

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| **Java** | 17 | 支持 Virtual Threads |
| **Spring Boot** | 3.5.12 | 基于 Spring Framework 6.2 |
| **COLA Components** | 5.0.0 | 支持 Spring Boot 3.x |
| **Spring Security** | 6.x | Lambda DSL + JWT 无状态认证 |
| **MyBatis-Plus** | 3.5.10.1 | Spring Boot 3 starter |
| **MapStruct** | 1.6.3 | 编译期对象映射，配合 lombok-mapstruct-binding |
| **MySQL** | 8.x | HikariCP 连接池 |
| **Flyway** | (Spring Boot 管理) | 数据库版本管理 |
| **springdoc** | 2.8.16 | OpenAPI 3 + Swagger UI |
| **JJWT** | 0.12.7 | JWT Token 生成与验证 |

## 快速开始

### 1. 环境要求

- JDK 17+
- MySQL 8.0+
- Maven 3.5+

### 2. 准备数据库

```sql
CREATE DATABASE IF NOT EXISTS `cola-demo` DEFAULT CHARSET utf8mb4;
```

或使用 Docker Compose 启动 MySQL：

```bash
docker-compose up -d mysql
```

### 3. 配置环境变量

复制 `.env.example` 为 `.env` 并填写实际值：

```bash
cp .env.example .env
```

### 4. 编译并启动

```bash
# 编译项目
mvn clean install -DskipTests

# 启动应用
cd cola-demo-start && mvn spring-boot:run
```

启动成功后访问：
- API 文档：`http://localhost:8080/swagger-ui.html`
- 健康检查：`http://localhost:8080/actuator/health`

## 核心设计模式

### DDD 战术模式

| 模式 | 实现 | 说明 |
|------|------|------|
| **聚合根** | `AggregateRoot` | Customer, User, Role, Permission 均实现此接口 |
| **值对象** | `CompanyType`, `ResourceType`, `Password` | 不可变，封装领域概念 |
| **领域事件** | `DomainEvent`, `CustomerCreatedEvent`, `RoleCreatedEvent` | 异步解耦，Spring ApplicationEvent 传递 |
| **领域异常** | `DomainException` + `BizErrorCode` | 错误码枚举保证一致性 |
| **网关接口** | `CustomerGateway`, `UserGateway`, `RoleGateway`, `PermissionGateway` | Domain 定义契约，Infrastructure 实现 |

### CQRS 模式

按实体聚合 Handler，每个实体一个 Handler 类，包含该实体的所有命令和查询方法：

| Handler | 方法 | 事务 |
|---------|------|------|
| `CustomerHandler` | add, listByName, page | 写: `@Transactional(rollbackFor)`, 读: `@Transactional(readOnly=true)` |
| `RoleHandler` | add, list, assignToUser, removeFromUser | 同上 |
| `PermissionHandler` | add, list, assignToRole, removeFromRole | 同上 |

事务统一在 App 层 Handler 中管理，Domain 层和 Infrastructure 层不使用事务注解。

### 分页查询

`PageResult`（client/common）是纯工具类，提供三个静态方法覆盖分页全链路：

| 方法 | 作用 | 使用层 |
|------|------|--------|
| `toPage(PageQuery)` | COLA PageQuery → MyBatis-Plus Page | Infrastructure |
| `toPageResponse(IPage, Function)` | IPage → PageResponse（同时转换记录类型） | Infrastructure |
| `map(PageResponse, Function)` | PageResponse\<T\> → PageResponse\<R\> | App |

Gateway 直接返回 `PageResponse<Domain>`，无需中间数据对象。Infrastructure 层一步完成 IPage → PageResponse，App 层一行完成 Domain → DTO 映射。

### MapStruct 对象映射

Convertor 由 MapStruct 自动生成，`componentModel = "spring"` 支持构造器注入：

- **App 层 Convertor**：Domain → DTO，如 `CustomerConvertor.toDTO(Customer)`
- **Infrastructure 层 Convertor**：Domain ↔ Entity，设置 `unmappedTargetPolicy = IGNORE` 避免样板代码
- **值对象转换**：通过 MapStruct `default` 方法处理，如 `Password → String`

### 领域事件

- **定义**：Domain 层继承 `DomainEvent`
- **发布**：App 层 Handler 中通过 `DomainEventPublisher.publish()` 发布
- **消费**：App 层 `@EventListener` 处理（如 `CustomerEventHandler`）
- **实现**：Infrastructure 层 `SpringDomainEventPublisher` 基于 Spring ApplicationEvent

### 异常处理

项目通过两层机制统一处理异常：

- **Security 过滤器链**：`AuthenticationEntryPoint` / `AccessDeniedHandler` 处理认证授权异常（401/403）
- **Controller 层**：`GlobalExceptionHandler` 处理 `DomainException`（400）、参数校验异常（400）、权限不足（403）及兜底异常（500）

领域异常统一使用 `BizErrorCode` 枚举 + `DomainException`，杜绝硬编码错误码：

```json
{
  "success": false,
  "errCode": "B_CUSTOMER_NAME_NOT_BLANK",
  "errMessage": "客户名称不能为空"
}
```

### 日志追踪

`TraceIdFilter` 自动为每个请求生成唯一 traceId 并写入 MDC，日志自动包含追踪信息，响应头返回 `X-Trace-Id`。

## API 接口

### 认证接口

```bash
# 登录
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 获取当前用户信息
curl http://localhost:8080/auth/me -H "Authorization: Bearer {token}"

# 登出
curl -X POST http://localhost:8080/auth/logout -H "Authorization: Bearer {token}"
```

### 客户管理接口

```bash
# 添加客户
curl -X POST http://localhost:8080/customer/add \
  -H "Content-Type: application/json" -H "Authorization: Bearer {token}" \
  -d '{"customerName":"阿里巴巴","companyType":"STATE_OWNED"}'

# 查询客户列表
curl "http://localhost:8080/customer/list?customerName=阿里" -H "Authorization: Bearer {token}"

# 分页查询客户
curl "http://localhost:8080/customer/page?pageIndex=1&pageSize=10&customerName=阿里" -H "Authorization: Bearer {token}"
```

### 角色管理接口

```bash
# 添加角色
curl -X POST http://localhost:8080/role/add \
  -H "Content-Type: application/json" -H "Authorization: Bearer {token}" \
  -d '{"roleCode":"ADMIN","roleName":"管理员"}'

# 查询角色列表
curl "http://localhost:8080/role/list" -H "Authorization: Bearer {token}"
```

### 权限管理接口

```bash
# 添加权限
curl -X POST http://localhost:8080/permission/add \
  -H "Content-Type: application/json" -H "Authorization: Bearer {token}" \
  -d '{"permissionCode":"CUSTOMER_VIEW","permissionName":"查看客户","resourceType":"MENU","resourcePath":"/customer"}'

# 查询权限列表
curl "http://localhost:8080/permission/list" -H "Authorization: Bearer {token}"
```

## 项目亮点

- **严格分层**: Adapter 不直接依赖 Domain，通过 App 层间接访问
- **CQRS 模式**: 按实体聚合 Handler，Command 和 Query 分离，写操作发布领域事件
- **DDD 战术模式**: 聚合根、值对象、领域事件、领域异常、网关接口
- **MapStruct 自动映射**: 编译期生成转换代码，Spring 注入，零样板代码
- **分页全链路转换**: PageResult 工具类三步覆盖，Gateway 直接返回 PageResponse
- **错误码一致性**: BizErrorCode 枚举 + DomainException + 全局异常处理
- **JWT 认证**: Spring Security 6 Lambda DSL + 无状态认证
- **全链路追踪**: MDC TraceId 自动注入
- **API 文档**: springdoc OpenAPI 3 + Swagger UI
- **数据库版本管理**: Flyway 自动迁移
- **Docker 支持**: Dockerfile + docker-compose

## 生产环境检查清单

- [ ] 使用 `prod` 配置文件启动
- [ ] 配置正确的数据库连接
- [ ] 设置 JWT Secret 环境变量（至少 64 字符）
- [ ] 限制 Actuator 端点访问
- [ ] 配置日志收集（ELK）
- [ ] 配置监控告警（Prometheus + Grafana）
- [ ] TokenBlacklist 替换为 Redis 实现（多实例部署）
