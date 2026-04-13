# cola-demo

基于 [COLA 5.x](https://github.com/alibaba/COLA)（Clean Object-oriented & Layered Architecture）架构的示例项目。

## 项目简介

cola-demo 是一个完整的 COLA 架构示例，展示了如何基于 DDD + CQRS 构建分层架构的微服务应用。项目采用六层架构设计，严格遵循依赖规则，将业务逻辑清晰分离为客户端层、适配器层、应用层、领域层和基础设施层。

## 项目结构

```
cola-demo/
├── cola-demo-client/              # 客户端层 - 对外暴露接口和DTO定义
│   ├── api/                       # 服务接口定义 (ICustomerService, IRoleService, IPermissionService, IOrderService, IApiAppService, IAuthService)
│   ├── dto/                       # Command、Query 对象 (CustomerPageQry, RolePageQry, PermissionPageQry, ApiAppPageQry 等)
│   │   └── data/                  # DTO 数据传输对象 (均继承 COLA DTO: CustomerDTO, UserDTO, RoleDTO 等)
│   └── common/                    # 通用类型 (BizErrorCode, DomainException, PageResult分页映射)
├── cola-demo-adapter/             # 适配器层 - REST Controller入口
│   ├── web/                       # REST API 控制器 + 全局异常处理 (GlobalExceptionHandler)
│   ├── security/                  # JWT 认证 + API Key 认证 + Spring Security + SecurityCurrentUserProvider
│   └── filter/                    # 过滤器 (TraceIdFilter)
├── cola-demo-app/                 # 应用层 - Service业务逻辑 + Handler
│   ├── service/                   # 应用服务 (AuthService)
│   │   └── impl/                  # 服务实现 (CustomerServiceImpl, AuthServiceImpl, OrderServiceImpl, ApiAppServiceImpl, RoleServiceImpl, PermissionServiceImpl)
│   ├── executor/                  # CQRS Handler（按实体聚合）
│   ├── eventhandler/              # 领域事件处理器 (CustomerEventHandler, RoleEventHandler)
│   └── convertor/                 # MapStruct DTO 转换器
├── cola-demo-domain/              # 领域层 - 实体Entity + Gateway接口
│   ├── customer/                  # 客户领域 (Customer, CustomerCreatedEvent)
│   │   └── gateway/               # 领域网关接口 (CustomerGateway)
│   ├── order/                     # 订单领域 (Order)
│   │   └── gateway/               # 领域网关接口 (OrderGateway)
│   ├── apiapp/                    # API应用领域 (ApiApp)
│   │   └── gateway/               # 领域网关接口 (ApiAppGateway)
│   ├── user/                      # 用户领域 (User, Role, Permission, Password, RoleCreatedEvent)
│   │   └── gateway/               # 领域网关接口 (UserGateway, RoleGateway, PermissionGateway)
│   ├── common/                    # 领域通用类 (AggregateRoot, DomainEvent, DomainEventPublisher) + 领域SPI (TokenBlacklist, LoginRateLimiter, CurrentUserProvider, PathMatcher)
│   └── enums/                     # 领域枚举 (CompanyType, OrderStatus, ResourceType)
├── cola-demo-infrastructure/      # 基础设施层 - Gateway实现 + MyBatis持久化
│   ├── gatewayimpl/               # 领域网关实现
│   ├── event/                     # 领域事件发布实现 (SpringDomainEventPublisher)
│   ├── mapper/                    # MyBatis Mapper
│   ├── convertor/                 # MapStruct Entity 转换器
│   ├── dataobject/                # MyBatis Entity
│   ├── config/                    # 配置类 (MyBatisPlusConfig, MyBatisPlusMetaObjectHandler)
│   ├── security/                  # Redis 安全实现 (RedisTokenBlacklist, RedisLoginRateLimiter)
│   ├── cache/                     # Redis 缓存服务 (RedisCacheService)
│   └── util/                      # 工具类 (PageHelper分页桥接)
└── cola-demo-start/               # 启动模块 - SpringBoot入口配置
    └── config/                    # SecurityConfig, OpenApiConfig
```

### 各层职责

| 层级 | 目录 | 职责 | 可依赖 |
|------|------|------|--------|
| **Client** | `client/api`, `client/dto`, `client/common` | 定义对外接口、DTO、Command、Query、异常契约 | 无 |
| **Adapter** | `adapter/web`, `adapter/security` | 接收HTTP请求，调用应用层服务 | Client, App, Domain(通用接口) |
| **App** | `app/service`, `app/executor`, `app/eventhandler` | 编排业务流程，执行CQRS命令/查询，处理领域事件 | Client, Domain |
| **Domain** | `domain/customer`, `domain/user`, `domain/common` | 核心业务实体、领域规则、网关接口、领域事件、领域SPI | Client |
| **Infrastructure** | `infrastructure/gatewayimpl`, `infrastructure/mapper` | 数据库持久化、领域事件发布、外部服务调用、领域SPI实现 | Domain, Client |

### 分层规则

```
client (无依赖)
  ↑
domain → client
  ↑
app → client, domain
  ↑
adapter → app (通过app间接访问domain和client，同时直接使用domain.common中的领域SPI接口)
  ↑
infrastructure → domain, client
  ↑
start → adapter, infrastructure
```

**关键约束**：
- Adapter 层不直接依赖 Domain 层业务逻辑，仅使用 `domain.common` 中的领域 SPI 接口（TokenBlacklist、LoginRateLimiter、CurrentUserProvider、PathMatcher）
- Infrastructure 层不依赖 Adapter 层，不依赖 Spring Security；通过 `domain.common` 中的 SPI 接口解耦
- 所有 DTO 继承 `com.alibaba.cola.dto.DTO`，所有 Cmd 继承 `Command`，查询对象继承 `PageQuery`
- Infrastructure 层 Gateway 返回 `PageResponse<Domain>`，通过 `PageHelper` 桥接 MyBatis-Plus 分页；App 层通过 `PageResult.map` 转换 Domain → DTO
- Controller 分页接口使用 `@PostMapping` + `@RequestBody` 绑定 Qry 对象，JSON Body 传递分页和查询参数
- 跨层异常契约（如 `DomainException`）放在 Client 层的 `common` 包

### 领域 SPI 模式

领域层定义接口（SPI），基础设施层或适配器层提供实现，通过 Spring IoC 自动注入，实现层间解耦：

| SPI 接口 | 定义位置 | 实现位置 | 说明 |
|----------|----------|----------|------|
| `TokenBlacklist` | `domain.common` | `infrastructure.security.RedisTokenBlacklist` | 基于 Redis 的 Token 黑名单 |
| `LoginRateLimiter` | `domain.common` | `infrastructure.security.RedisLoginRateLimiter` | 基于 Redis 的登录限流 |
| `CurrentUserProvider` | `domain.common` | `adapter.security.SecurityCurrentUserProvider` | 当前用户获取（解耦 SecurityContextHolder） |
| `DomainEventPublisher` | `domain.common` | `infrastructure.event.SpringDomainEventPublisher` | 基于 Spring ApplicationEvent 的领域事件发布 |

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
| **Redis** | 7.x | Redisson 客户端 |
| **Redisson** | 3.46.0 | 分布式缓存、限流、Token 黑名单 |
| **Flyway** | (Spring Boot 管理) | 数据库版本管理 |
| **springdoc** | 2.8.16 | OpenAPI 3 + Swagger UI |
| **JJWT** | 0.12.7 | JWT Token 生成与验证 |

## 快速开始

### 1. 环境要求

- JDK 17+
- MySQL 8.0+
- Redis 7.0+
- Maven 3.5+

### 2. 准备基础设施

```sql
CREATE DATABASE IF NOT EXISTS `cola-demo` DEFAULT CHARSET utf8mb4;
```

或使用 Docker Compose 启动 MySQL 和 Redis：

```bash
docker-compose up -d mysql redis
```

### 3. 配置环境变量

复制 `.env.example` 为 `.env` 并填写实际值：

```bash
cp .env.example .env
```

环境变量说明：

| 变量 | 说明 | 默认值 |
|------|------|--------|
| `DB_USERNAME` | 数据库用户名 | - |
| `DB_PASSWORD` | 数据库密码 | - |
| `JWT_SECRET` | JWT 签名密钥（至少64字符） | - |
| `REDIS_HOST` | Redis 主机地址 | 127.0.0.1 |
| `REDIS_PORT` | Redis 端口 | 6379 |
| `REDIS_DATABASE` | Redis 数据库编号 | 0 |
| `REDIS_SSL_ENABLED` | Redis SSL 开关 | false |

### 4. 编译并启动

```bash
# 编译项目
mvn clean install -DskipTests

# 启动应用
cd cola-demo-start && mvn spring-boot:run
```

启动成功后访问：
- API 文档：`http://localhost:8080/swagger-ui/index.html`
- 健康检查：`http://localhost:8080/actuator/health`

## 核心设计模式

### DDD 战术模式

| 模式 | 实现 | 说明 |
|------|------|------|
| **聚合根** | `AggregateRoot` | Customer, User, Role, Permission 均实现此接口 |
| **值对象** | `CompanyType`, `ResourceType`, `Password` | 不可变，封装领域概念 |
| **领域事件** | `DomainEvent`, `CustomerCreatedEvent`, `RoleCreatedEvent` | 异步解耦，Spring ApplicationEvent 传递 |
| **领域异常** | `DomainException` + `BizErrorCode` | 错误码枚举保证一致性 |
| **网关接口** | `CustomerGateway`, `UserGateway`, `RoleGateway`, `PermissionGateway`, `OrderGateway`, `ApiAppGateway` | Domain 定义契约，Infrastructure 实现 |
| **领域 SPI** | `TokenBlacklist`, `LoginRateLimiter`, `CurrentUserProvider`, `DomainEventPublisher` | Domain 定义接口，Infrastructure/Adapter 实现，解耦框架依赖 |

### CQRS 模式

按实体聚合 Handler，每个实体一个 Handler 类，包含该实体的所有命令和查询方法：

| Handler | 方法 | 事务 |
|---------|------|------|
| `CustomerHandler` | add, listByName, page | 写: `@Transactional(rollbackFor)`, 读: `@Transactional(readOnly=true)` |
| `RoleHandler` | add, list, page, assignToUser, removeFromUser | 同上 |
| `PermissionHandler` | add, list, page, assignToRole, removeFromRole | 同上 |
| `OrderHandler` | add, listByName | 同上 |
| `ApiAppHandler` | add, list, page, getByApiKey | 同上 |

事务统一在 App 层 Handler 中管理，Domain 层和 Infrastructure 层不使用事务注解。

### 分页查询

基于 COLA 原生 `PageQuery` / `PageResponse` 体系，通过 `PageHelper`（Infrastructure 层）和 `PageResult`（Client 层）实现分页全链路统一，且 Client 层不依赖任何持久化框架。

**设计原则**：
- 查询对象直接继承 COLA `PageQuery`（含 pageIndex、pageSize、orderBy 等分页参数），只添加业务查询字段
- Controller 使用 `@PostMapping` + `@RequestBody` 接收 Qry 对象，JSON Body 传递分页和查询参数
- Infrastructure 层使用 `PageHelper` 一步完成 IPage → PageResponse 转换
- App 层使用 `PageResult.map` 一行完成 Domain → DTO 映射

**工具类职责划分**：

| 工具类 | 位置 | 方法 | 作用 | 使用层 |
|--------|------|------|------|--------|
| `PageHelper` | `infrastructure/util` | `toPage(PageQuery)` | COLA PageQuery → MyBatis-Plus Page | Infrastructure |
| `PageHelper` | `infrastructure/util` | `toPageResponse(IPage, Function)` | IPage → PageResponse（同时转换记录类型） | Infrastructure |
| `PageResult` | `client/common` | `map(PageResponse, Function)` | PageResponse\<T\> → PageResponse\<R\> | App |

**数据流**：

```
Controller (@PostMapping + @RequestBody Qry) → App (PageResult.map) → Gateway → Infrastructure (PageHelper.toPageResponse + PageHelper.toPage)
```

**示例代码**：

```java
// 查询对象 — 继承 PageQuery，只定义业务字段
@Data
@EqualsAndHashCode(callSuper = true)
public class RolePageQry extends PageQuery {
    private String roleName;
    private Integer status;
}
```

### DTO/Cmd 规范

所有客户端传输对象统一继承 COLA 基类，保持架构一致性：

| 类型 | 基类 | 示例 |
|------|------|------|
| 写操作命令 | `Command extends DTO` | `LoginCmd`, `RoleAddCmd`, `CustomerAddCmd` |
| 分页查询 | `PageQuery extends Query extends Command` | `CustomerPageQry`, `RolePageQry` |
| 数据传输 | `DTO` | `CustomerDTO`, `RoleDTO`, `UserDTO` |

COLA 的 `DTO` 抽象类实现 `Serializable`，继承它可自动获得序列化支持并语义明确地标识传输对象类型。

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

## 安全机制

### JWT Token 认证

适用于内部用户通过用户名密码登录后访问 API。

- **AccessToken**：有效期 24 小时，用于接口请求认证
- **RefreshToken**：有效期 7 天，用于刷新 AccessToken
- **Refresh Token Rotation**：每次刷新后旧 Refresh Token 立即失效（加入 Redis 黑名单），防止重放攻击
- **Token 黑名单**：基于 Redis 实现，登出时将 Token 加入黑名单，支持多实例部署

```bash
# 登录
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 刷新Token（旧Refresh Token自动失效）
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"{refreshToken}"}'

# 获取当前用户信息
curl http://localhost:8080/api/v1/auth/me -H "Authorization: Bearer {token}"

# 登出
curl -X POST http://localhost:8080/api/v1/auth/logout -H "Authorization: Bearer {token}"
```

### 登录限流

基于 Redis 实现登录失败限流，防止暴力破解：
- 连续登录失败达到阈值后锁定账号
- 锁定期间返回 `B_AUTH_LOGIN_RATE_LIMITED` 错误码
- 登录成功后自动清除失败计数

### API Key 认证

适用于第三方系统调用 API，通过 `X-API-Key` 请求头进行身份验证。

**认证流程**：

1. 管理员通过 JWT Token 登录后，创建 API 应用并获取 API Key
2. 第三方系统在请求头中携带 `X-API-Key` 访问授权路径的 API

**认证校验规则**：

- API Key 必须有效（存在于系统中）
- API 应用状态必须为启用（`status=1`）
- API 应用未过期（`expiresAt` 为空表示永不过期）
- 请求路径必须在 API 应用授权的路径范围内

**路径匹配规则**：

| 模式 | 说明 | 示例 |
|------|------|------|
| 精确匹配 | 无通配符，路径完全一致 | `/open/order/add` |
| `/*` | 匹配单级子路径 | `/open/*` 匹配 `/open/order` 但不匹配 `/open/order/add` |
| `/**` | 匹配多级子路径 | `/open/**` 匹配 `/open/order`、`/open/order/add` 等 |

多个路径用逗号分隔，如 `/open/**,/customer/list`。

**认证优先级**：`AuthenticationFilter` 按优先级依次尝试 API Key → JWT Token，任一成功即通过认证。

### 缓存安全

`RedisCacheService` 提供防缓存穿透保护：
- 查询结果为空时缓存空值标记（60 秒短 TTL），避免反复穿透到数据库
- 支持单对象和列表的缓存/淘汰

## API 接口

### 认证接口

| 方法 | 路径 | 认证 | 说明 |
|------|------|------|------|
| POST | `/api/v1/auth/login` | 公开 | 用户登录 |
| POST | `/api/v1/auth/refresh` | 公开 | 刷新Token（Rotation） |
| POST | `/api/v1/auth/logout` | JWT | 用户登出 |
| GET | `/api/v1/auth/me` | JWT | 获取当前用户信息 |

### 客户管理接口

```bash
# 添加客户
curl -X POST http://localhost:8080/api/v1/customer/add \
  -H "Content-Type: application/json" -H "Authorization: Bearer {token}" \
  -d '{"customerName":"阿里巴巴","companyType":"STATE_OWNED"}'

# 查询客户列表
curl "http://localhost:8080/api/v1/customer/list?customerName=阿里" -H "Authorization: Bearer {token}"

# 分页查询客户（POST + JSON Body，框架自动处理分页参数）
curl -X POST http://localhost:8080/api/v1/customer/page \
  -H "Content-Type: application/json" -H "Authorization: Bearer {token}" \
  -d '{"pageIndex":1, "pageSize":10, "customerName":"阿里"}'
```

### 角色管理接口

```bash
# 添加角色
curl -X POST http://localhost:8080/api/v1/role/add \
  -H "Content-Type: application/json" -H "Authorization: Bearer {token}" \
  -d '{"roleCode":"ADMIN","roleName":"管理员"}'

# 查询角色列表
curl "http://localhost:8080/api/v1/role/list" -H "Authorization: Bearer {token}"

# 分页查询角色（POST + JSON Body，支持业务条件筛选）
curl -X POST http://localhost:8080/api/v1/role/page \
  -H "Content-Type: application/json" -H "Authorization: Bearer {token}" \
  -d '{"pageIndex":1, "pageSize":10, "roleName":"管理", "status":1}'

# 分配角色给用户
curl -X POST http://localhost:8080/api/v1/role/assignToUser \
  -H "Content-Type: application/json" -H "Authorization: Bearer {token}" \
  -d '{"userId":1,"roleCode":"ADMIN"}'

# 移除用户角色
curl -X DELETE http://localhost:8080/api/v1/role/removeFromUser \
  -H "Content-Type: application/json" -H "Authorization: Bearer {token}" \
  -d '{"userId":1,"roleCode":"ADMIN"}'
```

### 权限管理接口

```bash
# 添加权限
curl -X POST http://localhost:8080/api/v1/permission/add \
  -H "Content-Type: application/json" -H "Authorization: Bearer {token}" \
  -d '{"permissionCode":"CUSTOMER_VIEW","permissionName":"查看客户","resourceType":"MENU","resourcePath":"/customer"}'

# 查询权限列表
curl "http://localhost:8080/api/v1/permission/list" -H "Authorization: Bearer {token}"

# 分页查询权限
curl -X POST http://localhost:8080/api/v1/permission/page \
  -H "Content-Type: application/json" -H "Authorization: Bearer {token}" \
  -d '{"pageIndex":1, "pageSize":10, "permissionName":"查看", "resourceType":"MENU"}'

# 分配权限给角色
curl -X POST http://localhost:8080/api/v1/permission/assignToRole \
  -H "Content-Type: application/json" -H "Authorization: Bearer {token}" \
  -d '{"roleCode":"ADMIN","permissionCode":"CUSTOMER_VIEW"}'

# 移除角色权限
curl -X DELETE http://localhost:8080/api/v1/permission/removeFromRole \
  -H "Content-Type: application/json" -H "Authorization: Bearer {token}" \
  -d '{"roleCode":"ADMIN","permissionCode":"CUSTOMER_VIEW"}'
```

### 订单管理接口

订单接口支持 JWT Token 和 API Key 两种认证方式。

```bash
# 添加订单
curl -X POST http://localhost:8080/api/v1/order/add \
  -H "Content-Type: application/json" -H "X-API-Key: {apiKey}" \
  -d '{"orderName":"订单001","amount":100.00,"customerName":"张三"}'

# 查询订单列表（按名称模糊查询）
curl "http://localhost:8080/api/v1/order/list?orderName=订单" -H "X-API-Key: {apiKey}"
```

### API 应用管理接口

API 应用管理需要 ADMIN 角色，用于管理第三方系统访问的 API Key。

```bash
# 创建API应用
curl -X POST http://localhost:8080/api/v1/api-app/add \
  -H "Content-Type: application/json" -H "Authorization: Bearer {token}" \
  -d '{"appName":"第三方订单系统","allowedPaths":"/open/**"}'

# 查询API应用列表
curl "http://localhost:8080/api/v1/api-app/list" -H "Authorization: Bearer {token}"

# 分页查询API应用
curl -X POST http://localhost:8080/api/v1/api-app/page \
  -H "Content-Type: application/json" -H "Authorization: Bearer {token}" \
  -d '{"pageIndex":1, "pageSize":10, "appName":"订单"}'
```

## 数据库设计

Flyway 自动管理数据库版本迁移，表结构如下：

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `customer` | 客户 | customer_name, company_type |
| `sys_user` | 用户 | username(唯一), password(BCrypt), status |
| `sys_role` | 角色 | role_code(唯一), role_name, status |
| `sys_user_role` | 用户角色关联 | user_id, role_id(联合唯一) |
| `sys_permission` | 权限 | permission_code(唯一), resource_type(MENU/BUTTON/API), resource_path |
| `sys_role_permission` | 角色权限关联 | role_id, permission_id(联合唯一) |
| `sys_api_app` | API应用 | app_name, api_key(唯一), allowed_paths, expires_at, status |
| `t_order` | 订单 | order_name, amount(DECIMAL), customer_name, status |

所有表统一包含：审计字段（created_by, created_time, updated_by, updated_time）、软删除标记（deleted）。

初始化种子数据：ADMIN 角色、4 个客户管理权限、admin 用户（密码 admin123）。

## Docker 部署

### 构建镜像

```bash
mvn clean package -DskipTests
docker build -t cola-demo:latest .
```

### Docker Compose

```bash
# 启动 MySQL + Redis
docker-compose up -d mysql redis

# 完整部署（含应用）
# 需先构建镜像，然后添加 app 服务到 docker-compose.yml
```

### 安全特性

Dockerfile 安全加固：
- 非 root 用户运行（app:app）
- G1GC + MaxRAMPercentage=75.0 限制内存
- 内置 HEALTHCHECK 健康检查
- `/dev/./urandom` 加速安全熵源

## 项目亮点

- **严格分层**: Adapter 不直接依赖 Domain 业务逻辑；Infrastructure 不依赖 Adapter 和 Spring Security
- **领域 SPI 模式**: Domain 层定义接口契约，Infrastructure/Adapter 层提供实现，解耦框架依赖
- **COLA 规范**: DTO/Cmd/PageQuery 统一继承 COLA 基类，架构一致性强
- **CQRS 模式**: 按实体聚合 Handler，Command 和 Query 分离，写操作发布领域事件
- **DDD 战术模式**: 聚合根、值对象、领域事件、领域异常、网关接口
- **MapStruct 自动映射**: 编译期生成转换代码，Spring 注入，零样板代码
- **分页全链路统一**: PageHelper(Infrastructure) + PageResult(Client) 职责分离，Client 层不依赖持久化框架
- **错误码一致性**: BizErrorCode 枚举 + DomainException + 全局异常处理
- **JWT + Refresh Token Rotation**: Spring Security 6 Lambda DSL + 无状态认证 + Token 轮换防重放
- **API Key 认证**: 支持第三方系统通过 API Key 访问，路径权限控制，统一认证过滤器
- **登录限流**: 基于 Redis 的登录失败计数，防止暴力破解
- **缓存穿透防护**: 空值标记缓存，保护数据库
- **全链路追踪**: MDC TraceId 自动注入
- **API 文档**: springdoc OpenAPI 3 + Swagger UI
- **数据库版本管理**: Flyway 自动迁移
- **Docker 支持**: 安全加固 Dockerfile + docker-compose
- **生产就绪**: Prometheus 监控、日志滚动、多环境配置

## 生产环境检查清单

- [ ] 使用 `prod` 配置文件启动
- [ ] 配置正确的数据库连接和连接池大小
- [ ] 设置 JWT Secret 环境变量（至少 64 字符）
- [ ] 配置 Redis 连接（Token 黑名单、登录限流、缓存均依赖 Redis）
- [ ] 限制 Actuator 端点访问
- [ ] 配置日志收集（ELK）
- [ ] 配置监控告警（Prometheus + Grafana）
- [ ] 配置 CORS 允许域名
- [ ] 启用 Redis SSL（云服务）
