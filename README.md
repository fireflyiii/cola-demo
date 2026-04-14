# cola-demo

基于 [COLA 5.x](https://github.com/alibaba/COLA)（Clean Object-oriented & Layered Architecture）架构的示例项目，展示了如何基于 DDD + CQRS 构建分层架构的微服务应用。

## 项目结构

```
cola-demo/
├── cola-demo-client/          # 客户端层 - 对外接口、DTO、Command、Query、异常契约、SPI
├── cola-demo-adapter/         # 适配器层 - REST Controller、安全认证、AOP 审计/日志、i18n
├── cola-demo-app/             # 应用层 - CQRS Handler、Service、领域事件处理、MapStruct Convertor
├── cola-demo-domain/          # 领域层 - 聚合根、值对象、领域事件、网关接口、领域 SPI
├── cola-demo-infrastructure/  # 基础设施层 - 持久化、Redis 安全、消息队列、分布式锁、缓存、定时任务
├── cola-demo-start/           # 启动模块 - Spring Boot 入口、Security 配置、OpenAPI、Flyway
├── cola-demo-gateway/         # API 网关 - Spring Cloud Gateway 路由、限流、追踪
└── cola-demo-eureka-server/   # 注册中心 - Eureka Server
```

### 分层依赖规则

```
client (无依赖)
  ↑
domain → client
  ↑
app → client, domain
  ↑
adapter → app, domain.common (仅 SPI 接口)
  ↑
infrastructure → domain, client
  ↑
start → adapter, infrastructure

gateway → eureka, sentinel (独立网关模块)
```

**关键约束**：
- Adapter 不直接依赖 Domain 业务逻辑，仅使用 `domain.common` 中的 SPI 接口
- Infrastructure 不依赖 Adapter 和 Spring Security，通过 SPI 接口解耦

### 各层职责

| 层级 | 职责 | 可依赖 |
|------|------|--------|
| **Client** | 对外接口、DTO、Command、Query、异常契约、i18n SPI | 无 |
| **Adapter** | HTTP 入口、认证编排、AOP 审计/日志/限流、i18n 实现 | Client, App, Domain(通用接口) |
| **App** | 业务流程编排、CQRS 命令/查询、领域事件处理 | Client, Domain |
| **Domain** | 核心业务实体、领域规则、网关接口、领域 SPI | Client |
| **Infrastructure** | 持久化、消息队列、分布式锁、Redis 安全、缓存、定时任务 | Domain, Client |
| **Gateway** | API 路由转发、限流熔断、追踪注入、CORS | Eureka, Sentinel |

## 领域模型

项目包含 4 个业务领域：

| 领域 | 聚合根 | 说明 |
|------|--------|------|
| **Customer** | Customer | 客户管理，含 CompanyType 值对象 |
| **User/RBAC** | User, Role, Permission | 用户-角色-权限，含 Password 值对象、ResourceType 枚举 |
| **Order** | Order | 订单管理，支持 JWT 和 API Key 双模式认证 |
| **ApiApp** | ApiApp | 第三方 API 应用管理，API Key + 路径授权 |

## 领域 SPI 模式

领域层/客户端层定义接口（SPI），基础设施层/适配器层提供实现，通过 Spring IoC 自动注入实现层间解耦：

| SPI 接口 | 实现位置 | 说明 |
|----------|----------|------|
| `TokenBlacklist` | infrastructure (Redis) | JWT Token 黑名单 |
| `LoginRateLimiter` | infrastructure (Redis) | 登录失败限流 |
| `CurrentUserProvider` | adapter (SecurityContext) | 当前用户获取 |
| `DomainEventPublisher` | infrastructure (Spring + RocketMQ) | 领域事件双发（本地+远程） |
| `PathMatcher` | adapter (AntPathMatcher) | 路径匹配，支持 URL 解码防绕过 |
| `MessagePublisher` | infrastructure (RocketMQ) | 跨服务消息发布 |
| `DistributedLock` | infrastructure (Redisson) | 分布式锁 |
| `ErrorCodeResolver` | adapter (MessageSource) | 错误码 i18n 消息解析 |

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| **Java** | 17 | 支持 Virtual Threads |
| **Spring Boot** | 3.5.12 | 应用框架 |
| **Spring Cloud** | 2025.0.0 | 服务治理 |
| **Spring Cloud Alibaba** | 2025.0.0.0 | Sentinel 限流熔断 |
| **COLA Components** | 5.0.0 | DTO 基类、Response、PageQuery |
| **Spring Security** | 6.x | Lambda DSL + JWT 无状态认证 |
| **MyBatis-Plus** | 3.5.10.1 | ORM |
| **MapStruct** | 1.6.3 | 编译期对象映射 |
| **MySQL** | 8.x | 数据库 (HikariCP) |
| **Redis / Redisson** | 7.x / 3.46.0 | 缓存、Token 黑名单、限流、分布式锁 |
| **Eureka** | (Spring Cloud) | 服务注册与发现 |
| **Spring Cloud Gateway** | (Spring Cloud) | API 网关 |
| **RocketMQ** | 5.3.1 | 消息队列 |
| **Sentinel** | 1.8.9 | 限流、熔断、系统保护 |
| **Zipkin** | (Docker) | 分布式追踪 |
| **Apollo** | 2.3.0 | 配置中心 + Sentinel 规则持久化 |
| **Flyway** | (Spring Boot) | 数据库版本管理 |
| **springdoc** | 2.8.16 | OpenAPI 3 + Swagger UI |

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

或使用 Docker Compose 一键启动：

```bash
docker-compose up -d
```

也可按需启动单个服务：

```bash
docker-compose up -d mysql redis            # 数据库和缓存
docker-compose up -d mysql redis eureka      # 含注册中心
docker-compose up -d                          # 全部基础设施
```

Docker Compose 包含的服务：MySQL (3306)、Redis (6379)、Eureka (8761)、RocketMQ (9876/10911)、Zipkin (9411)、Gateway (9090)。

### 3. 配置环境变量

复制 `.env.example` 为 `.env` 并填写实际值。主要变量：

| 变量 | 说明 | 默认值 |
|------|------|--------|
| `DB_USERNAME` / `DB_PASSWORD` | 数据库凭据 | - |
| `JWT_SECRET` | JWT 签名密钥（至少64字符） | - |
| `REDIS_HOST` | Redis 主机地址 | 127.0.0.1 |
| `APOLLO_META` | Apollo Meta Server 地址 | - |
| `EUREKA_SERVER_URL` | Eureka Server 地址 | http://localhost:8761/eureka/ |
| `ROCKETMQ_NAME_SERVER` | RocketMQ NameServer 地址 | localhost:9876 |
| `ZIPKIN_ENDPOINT` | Zipkin Server 地址 | http://localhost:9411/api/v2/spans |
| `CORS_ALLOWED_ORIGINS` | CORS 允许的来源域名 | http://localhost:3000 |
| `EUREKA_ENABLED` | Eureka 开关 | true |

### 4. 配置中心（Apollo）

项目集成 Apollo 配置中心，使用 `apollo-client-config-data` + `spring.config.import` 方式，无需 bootstrap 文件。

- 通过 JVM 参数 `-Denv=DEV/SIT/UAT/PROD` 切换环境
- `optional:` 前缀确保 Apollo 不可用时使用本地 YAML 正常启动
- 配置优先级：Apollo > 本地 application.yml > 默认值
- 参考配置：`cola-demo-start/src/main/resources/apollo-config-reference.properties`

### 5. 编译并启动

```bash
mvn clean install -DskipTests

# 启动应用
cd cola-demo-start && mvn spring-boot:run

# 启动网关（需先启动 Eureka，或关闭 Eureka 走直连）
cd cola-demo-gateway && mvn spring-boot:run

# 无 Eureka 本地开发模式
EUREKA_ENABLED=false ROUTE_URI=http://localhost:8080 mvn spring-boot:run -pl cola-demo-gateway
```

启动成功后：
- API 文档：`http://localhost:8080/swagger-ui/index.html`
- 健康检查：`http://localhost:8080/actuator/health`
- Eureka 控制台：`http://localhost:8761`
- API 网关：`http://localhost:9090/api/...`

## 核心设计

### DDD 战术模式

- **聚合根**：Customer、User、Role、Permission 均实现 `AggregateRoot` 接口
- **值对象**：CompanyType、ResourceType、Password — 不可变，封装领域概念
- **领域事件**：CustomerCreatedEvent、RoleCreatedEvent — 双发模式（本地 Spring Event + 远程 RocketMQ）
- **领域异常**：`BizErrorCode` 枚举 + `DomainException`，错误码一致性 + i18n 消息解析
- **网关接口**：Domain 定义契约，Infrastructure 实现

### CQRS 模式

按实体聚合 Handler，每个实体一个 Handler 类，包含该实体的所有命令和查询方法。事务统一在 App 层 Handler 中管理。

### 分页查询

基于 COLA `PageQuery` / `PageResponse` 体系，通过 `PageHelper`（Infrastructure 层）和 `PageResult`（Client 层）实现分页全链路统一，Client 层不依赖持久化框架。`PageResult.validatePageSize()` 校验 pageSize 上限（100），防止恶意大分页。

### 领域事件双发

`SpringDomainEventPublisher` 同时执行本地发布（Spring ApplicationEvent）和远程发布（RocketMQ），远程发布失败不影响本地事务。Topic 格式：`domain-event:{EventClassName}`。

### 异常处理

两层机制：Security 过滤器链处理认证授权异常（401/403），`GlobalExceptionHandler` 处理 `DomainException`（400）、参数校验异常、权限不足及兜底异常（500）。错误消息通过 `ErrorCodeResolver` 支持国际化。

### 对象映射

MapStruct 自动生成 Convertor，App 层负责 Domain → DTO，Infrastructure 层负责 Domain ↔ Entity。统一使用 `Convertor` 后缀。

## 安全机制

### JWT Token 认证

适用于内部用户登录。AccessToken 有效期 24h，RefreshToken 有效期 7d，支持 Refresh Token Rotation（旧 Token 自动加入黑名单）。Token 黑名单基于 Redis 实现，支持多实例部署。

### API Key 认证

适用于第三方系统调用，通过 `X-API-Key` 请求头认证。API Key 以 SHA-256 哈希存储，支持路径授权（精确匹配 / `/*` / `/**`）和过期时间控制。认证优先级：API Key → JWT Token。

### 登录限流

基于 Redis 实现登录失败计数，连续失败达到阈值后锁定账号，登录成功后自动清除。

### Sentinel 限流熔断

集成 Spring Cloud Alibaba Sentinel，`SentinelConfig` 统一将 BlockException 转换为 COLA Response 格式。支持 Apollo 规则持久化和动态推送。

## 运维特性

- **优雅停机**：Spring Boot graceful shutdown + Eureka 主动注销
- **自定义健康指标**：Redis + RocketMQ HealthIndicator
- **分布式调度**：`@Scheduled` + Redisson 分布式锁，多实例安全执行
- **分布式追踪**：Micrometer Tracing + Brave + Zipkin，全链路追踪
- **日志追踪**：TraceIdFilter 自动注入 MDC，响应头返回 `X-Trace-Id`
- **审计日志**：`@OperationLog` 注解 + AOP 切面，自动记录操作人、类型和结果
- **请求日志**：`RequestLoggingAspect` 自动记录 Controller 请求耗时
- **国际化**：ErrorCodeResolver SPI + MessageSource，默认英文，`Accept-Language: zh` 切换中文
- **缓存穿透防护**：空值标记缓存（短 TTL），保护数据库

## API 接口

| 模块 | 路径前缀 | 认证方式 | 说明 |
|------|----------|----------|------|
| 认证 | `/api/v1/auth/` | 公开/JWT | 登录、登出、刷新 Token、获取当前用户 |
| 客户 | `/api/v1/customer/` | JWT | 客户 CRUD + 分页 |
| 角色 | `/api/v1/role/` | JWT | 角色 CRUD + 分配/移除用户 |
| 权限 | `/api/v1/permission/` | JWT | 权限 CRUD + 分配/移除角色 |
| 订单 | `/api/v1/order/` | JWT / API Key | 订单管理 |
| API 应用 | `/api/v1/api-app/` | JWT (ADMIN) | 第三方 API Key 管理 |

默认管理员账号：`admin` / `admin123`

## 数据库

Flyway 自动管理数据库版本迁移，主要表：

| 表名 | 说明 |
|------|------|
| `customer` | 客户 |
| `sys_user` | 用户 |
| `sys_role` | 角色 |
| `sys_user_role` | 用户角色关联 |
| `sys_permission` | 权限 |
| `sys_role_permission` | 角色权限关联 |
| `sys_api_app` | API 应用（API Key 哈希存储） |
| `t_order` | 订单 |

所有表统一包含审计字段（created_by, created_time, updated_by, updated_time）和软删除标记（deleted）。

## Docker 部署

```bash
mvn clean package -DskipTests

docker build -t cola-demo:latest .
docker build -f Dockerfile.eureka -t cola-demo-eureka:latest .
docker build -f Dockerfile.gateway -t cola-demo-gateway:latest .
```

Dockerfile 安全加固：非 root 用户运行、G1GC + 内存限制、内置 HEALTHCHECK。

## 项目亮点

- **严格分层**：Adapter 不依赖 Domain 业务逻辑，Infrastructure 不依赖 Adapter 和 Spring Security
- **领域 SPI 模式**：8 个 SPI 接口实现层间解耦，Domain/Client 不依赖任何框架
- **COLA 规范**：DTO/Cmd/PageQuery 统一继承 COLA 基类
- **DDD + CQRS**：聚合根、值对象、领域事件、网关接口 + 按实体聚合 Handler
- **双模认证**：JWT (内部用户) + API Key (第三方系统)，Refresh Token Rotation 防重放
- **领域事件双发**：Spring Event (本地) + RocketMQ (远程)，远程失败不影响本地事务
- **分页全链路统一**：PageHelper + PageResult，Client 层零持久化依赖
- **全链路可观测**：TraceId + Micrometer Tracing + Zipkin + 自定义健康指标
- **国际化**：ErrorCodeResolver SPI + MessageSource，Client 层无 Spring 依赖
- **微服务治理**：Eureka 注册发现 + Gateway 路由 + Sentinel 限流 + Apollo 配置中心
