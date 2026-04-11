# COLA Demo 生产环境特性使用示例

本文档展示项目中集成的生产环境高优先级特性的使用方式。

---

## 1. 事务管理 (Transaction Management)

事务统一在 App 层的 Executor/Handler 中管理，符合 COLA 架构规范。

### 1.1 写操作事务

```java
@Component
public class CustomerAddHandler {

    @Autowired
    private CustomerGateway customerGateway;

    @Transactional(rollbackFor = Exception.class)
    public Response execute(CustomerAddCmd cmd) {
        Customer customer = new Customer();
        customer.setCustomerName(cmd.getCustomerName());
        customer.setCompanyType(cmd.getCompanyType());
        customerGateway.create(customer);
        return Response.buildSuccess();
    }
}
```

### 1.2 只读事务（查询优化）

```java
@Component
public class CustomerListByNameHandler {

    @Autowired
    private CustomerGateway customerGateway;

    @Transactional(readOnly = true)
    public MultiResponseResponse<CustomerDTO> execute(CustomerListByNameQry qry) {
        List<Customer> customers = customerGateway.listByName(qry.getCustomerName());
        List<CustomerDTO> dtos = customers.stream()
                .map(CustomerConvertor::toDTO)
                .collect(Collectors.toList());
        return MultiResponse.of(dtos);
    }
}
```

### 1.3 事务使用原则

| 场景 | 注解 | 说明 |
|------|------|------|
| 写操作 | `@Transactional(rollbackFor = Exception.class)` | 默认传播行为 REQUIRED，保证原子性 |
| 读操作 | `@Transactional(readOnly = true)` | 只读优化，Spring 和数据库层面都会优化 |
| Domain 层 | 不使用事务注解 | 保持领域纯净，事务由 App 层控制 |
| Infrastructure 层 | 不使用事务注解 | 数据访问层不参与事务控制 |

---

## 2. 日志追踪 (MDC TraceId)

### 2.1 在代码中使用日志

`TraceIdFilter` 自动为每个请求生成唯一的 traceId 并写入 MDC，日志自动包含：

```java
@Slf4j
@Component
public class CustomerAddHandler {

    @Transactional(rollbackFor = Exception.class)
    public Response execute(CustomerAddCmd cmd) {
        log.info("开始添加客户, 客户名称: {}", cmd.getCustomerName());

        try {
            customerGateway.create(customer);
            log.info("客户添加成功, 客户ID: {}", customer.getCustomerId());
        } catch (Exception e) {
            log.error("客户添加失败, 客户名称: {}", cmd.getCustomerName(), e);
            throw e;
        }

        return Response.buildSuccess();
    }
}
```

### 2.2 日志输出示例

```
2024-01-15 10:30:45.123 [traceId=abc123def456] INFO  c.a.c.d.a.e.CustomerAddHandler - 开始添加客户, 客户名称: 阿里巴巴
2024-01-15 10:30:45.234 [traceId=abc123def456] INFO  c.a.c.d.i.g.CustomerGatewayImpl - 插入客户数据
2024-01-15 10:30:45.345 [traceId=abc123def456] INFO  c.a.c.d.a.e.CustomerAddHandler - 客户添加成功, 客户ID: 1001
```

### 2.3 在响应头中查看 TraceId

```bash
curl -i http://localhost:8080/customer/list?customerName=阿里

# 响应头中包含:
# X-Trace-Id: abc123def456
```

---

## 3. JWT 认证

### 3.1 登录获取 Token

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

响应示例：

```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "expiresIn": 86400000,
  "tokenType": "Bearer",
  "user": {
    "username": "admin",
    "roles": ["ADMIN", "USER"]
  }
}
```

### 3.2 使用 Token 访问受保护接口

```bash
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIs..." \
  http://localhost:8080/customer/list?customerName=阿里
```

### 3.3 获取当前用户信息

```bash
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIs..." \
  http://localhost:8080/auth/me
```

### 3.4 登出

```bash
curl -X POST -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIs..." \
  http://localhost:8080/auth/logout
```

---

## 4. 健康检查与监控 (Actuator)

### 4.1 健康检查端点

```bash
# 应用健康状态
curl http://localhost:8080/actuator/health
```

响应示例：

```json
{
  "status": "UP"
}
```

### 4.2 应用信息

```bash
curl http://localhost:8080/actuator/info
```

### 4.3 指标监控

```bash
# 查看所有指标
curl http://localhost:8080/actuator/metrics

# 查看JVM内存使用
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# 查看HTTP请求统计
curl http://localhost:8080/actuator/metrics/http.server.requests

# Prometheus 格式指标
curl http://localhost:8080/actuator/prometheus
```

---

## 5. 多环境配置

### 5.1 开发环境 (dev)

```bash
# 使用开发配置启动
java -jar -Dspring.profiles.active=dev cola-demo-start-1.0.0-SNAPSHOT.jar
```

特点：
- 启用 SQL 日志输出
- 开启详细调试信息
- 禁用严格的安全策略

### 5.2 生产环境 (prod)

```bash
# 使用生产配置启动
java -jar -Dspring.profiles.active=prod cola-demo-start-1.0.0-SNAPSHOT.jar
```

特点：
- 关闭 SQL 日志
- 启用严格的安全策略
- 禁用 Actuator 敏感端点

---

## 6. 异常处理

项目通过 `GlobalExceptionHandler`（Controller 层异常）和 `SecurityConfig` 中的 `AuthenticationEntryPoint`/`AccessDeniedHandler`（Security 过滤器链异常）统一处理异常，返回标准化的错误响应。

### 6.1 异常处理范围

**Security 过滤器链**（由 `AuthenticationEntryPoint` / `AccessDeniedHandler` 处理）：

| 场景 | HTTP 状态码 | 错误信息 |
|------|-------------|----------|
| Token 已过期 | 401 | Token已过期，请重新登录 |
| Token 无效/签名错误 | 401 | Token无效或已过期 |
| Token 已被注销 | 401 | Token已被注销 |
| 未携带 Token 访问受保护资源 | 401 | 未认证，请先登录 |
| 已认证但权限不足 | 403 | 权限不足 |

**Controller 层**（由 `GlobalExceptionHandler` 处理）：

| 异常类型 | HTTP 状态码 | 错误信息 |
|----------|-------------|----------|
| `BadCredentialsException` | 401 | 用户名或密码错误 |
| `AuthenticationException` | 401 | 认证失败 |
| `MethodArgumentNotValidException` | 400 | 字段校验错误详情 |
| `ConstraintViolationException` | 400 | 约束违反详情 |
| `HttpMessageNotReadableException` | 400 | 请求体格式错误 |
| `AccessDeniedException` | 403 | 权限不足 |
| `Exception` (兜底) | 500 | 服务器内部错误 |

### 6.2 认证异常示例

用户名或密码错误时：

```json
{
  "success": false,
  "errCode": "401",
  "errMessage": "用户名或密码错误"
}
```

Token 过期时：

```json
{
  "success": false,
  "errCode": "401",
  "errMessage": "Token已过期，请重新登录"
}
```

### 6.3 参数校验异常

使用 `@Valid` 注解校验参数：

```java
public class CustomerController {
    @PostMapping("/add")
    public Response addCustomer(@RequestBody @Valid CustomerAddCmd cmd) {
        return customerService.addCustomer(cmd);
    }
}
```

校验失败时响应：

```json
{
  "success": false,
  "errCode": "400",
  "errMessage": "客户名称不能为空"
}
```

---

## 7. 分页查询

### 7.1 分页请求

```bash
curl "http://localhost:8080/customer/page?pageIndex=1&pageSize=10&customerName=阿里"
```

### 7.2 分页响应

```json
{
  "success": true,
  "data": [
    {
      "customerId": 1,
      "customerName": "阿里巴巴",
      "companyType": "COMPANY"
    }
  ],
  "totalCount": 1,
  "pageSize": 10,
  "pageIndex": 1
}
```

### 7.3 分页实现代码

```java
@Component
public class CustomerPageHandler {

    @Autowired
    private CustomerGateway customerGateway;

    @Transactional(readOnly = true)
    public PageResponseResponse<CustomerDTO> execute(CustomerPageQry qry) {
        int page = qry.getPageIndex();
        int pageSize = qry.getPageSize();

        PageResultResult<Customer> pageResult = customerGateway.pageByName(
            qry.getCustomerName(), page, pageSize);

        List<CustomerDTO> dtos = pageResult.getRecords().stream()
                .map(CustomerConvertor::toDTO)
                .collect(Collectors.toList());

        return PageResponse.of(dtos, pageResult.getTotal().intValue(), pageSize, page);
    }
}
```

---

## 8. 完整示例代码参考

| 特性 | 示例文件 | 路径 |
|------|----------|------|
| 写事务 | `CustomerAddHandler.java` | `cola-demo-app/.../app/executor/` |
| 只读事务 | `CustomerListByNameHandler.java` | `cola-demo-app/.../app/executor/query/` |
| 分页查询 | `CustomerPageHandler.java` | `cola-demo-app/.../app/executor/query/` |
| 日志追踪 | `TraceIdFilter.java` | `cola-demo-adapter/.../adapter/filter/` |
| JWT 认证 | `AuthController.java` | `cola-demo-adapter/.../adapter/web/` |
| 全局异常处理 | `GlobalExceptionHandler.java` | `cola-demo-adapter/.../adapter/web/handler/` |
| 健康检查 | Actuator 端点 | `/actuator/health` |

---

## 9. 生产环境检查清单

- [ ] 使用 `prod` 配置文件启动
- [ ] 配置正确的数据库连接
- [ ] 设置 JWT Secret 环境变量
- [ ] 限制 Actuator 端点访问
- [ ] 配置日志收集（ELK）
- [ ] 配置监控告警（Prometheus + Grafana）
