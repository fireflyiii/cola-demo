# cola-demo

基于 [COLA 4.x](https://github.com/alibaba/COLA)（Clean Object-oriented & Layered Architecture）架构的示例项目。

## 项目简介

cola-demo 是一个完整的 COLA 架构示例，展示了如何构建分层架构的微服务应用。项目采用四层架构设计，将业务逻辑清晰分离为适配器层、应用层、领域层和基础设施层。

## 项目结构

```
cola-demo/
├── cola-demo-client/              # 客户端层 - 对外暴露接口和DTO定义
├── cola-demo-adapter/             # 适配器层 - REST Controller入口
├── cola-demo-app/                 # 应用层 - Service业务逻辑 + CQRS Executor
├── cola-demo-domain/              # 领域层 - 实体Entity + Gateway接口
├── cola-demo-infrastructure/      # 基础设施层 - Gateway实现 + MyBatis持久化
└── cola-demo-start/               # 启动模块 - SpringBoot入口配置
```

### 各层职责

| 层级 | 目录 | 职责 |
|------|------|------|
| **Adapter** | `adapter/web` | 接收HTTP请求，调用应用层服务 |
| **App** | `app/service`, `app/executor` | 编排业务流程，执行CQRS命令/查询 |
| **Client** | `client/dto`, `client/api` | 定义接口、数据传输对象 |
| **Domain** | `domain/customer` | 核心业务实体、领域规则、网关接口 |
| **Infrastructure** | `infrastructure/gatewayimpl`, `infrastructure/mapper` | 数据库持久化、外部服务调用 |

## 模块依赖关系

```
adapter → app → client, domain
infrastructure → domain, client
start → adapter, app, client, domain, infrastructure
```

## 技术栈

- **Java**: 1.8
- **Spring Boot**: 2.7.2
- **COLA Components**: 4.3.2
- **MyBatis-Plus**: 3.5.3
- **MySQL**: 8.x + HikariCP 连接池
- **Flyway**: 数据库版本管理

## 快速开始

### 1. 环境要求

- JDK 1.8+
- MySQL 8.0+
- Maven 3.5+

### 2. 准备数据库

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS cola-demo DEFAULT CHARSET utf8mb4;

-- 配置连接信息 (application.yml中已配置)
-- 数据库: cola-demo
-- 用户名: cola
-- 密码: 1qaz2wsx3edc
```

Flyway 会在应用启动时自动执行 `src/main/resources/db/migration/V1__init_schema.sql` 建表脚本。

### 3. 编译并启动

```bash
# 编译项目
mvn clean install -DskipTests

# 启动应用
cd cola-demo-start && mvn spring-boot:run
```

启动成功后访问 `http://localhost:8080`

## API 接口

### 1. 添加客户

**端点**: `POST /customer`

**请求参数**:
```json
{
  "customerName": "客户名称",
  "companyType": "公司类型(，如PERSON/COMPANY)"
}
```

**示例**:
```bash
curl -X POST http://localhost:8080/customer \
  -H "Content-Type: application/json" \
  -d '{"customerName":"张三","companyType":"PERSON"}'
```

**响应**:
```json
{"success":true,"errCode":null,"errMessage":null}
```

### 2. 根据名称查询客户

**端点**: `GET /customer?customerName=xxx`

**查询参数**: `customerName` - 客户名称（支持模糊查询）

**示例**:
```bash
curl "http://localhost:8080/customer?customerName=张三"
```

**响应**:
```json
{
  "success": true,
  "data": [
    {
      "customerId": 1,
      "customerName": "张三",
      "companyType": "PERSON"
    }
  ],
  "empty": false
}
```

## 核心领域模型

### Customer 实体 (Domain)

```java
// 位置: cola-demo-domain/src/main/java/com/alibaba/cola/demo/domain/customer/Customer.java
public class Customer {
    private Long customerId;
    private String customerName;
    private String companyType;

    // 领域行为：校验客户信息
    public void validate() { ... }

    // 领域行为：判断是否为同一客户
    public boolean isSameCustomer(Customer other) { ... }
}
```

### CustomerGateway 接口 (Domain)

```java
// 位置: cola-demo-domain/src/main/java/com/alibaba/cola/demo/domain/customer/gateway/CustomerGateway.java
public interface CustomerGateway {
    void create(Customer customer);
    List<Customer> findByName(String customerName);
}
```

## 项目亮点

- **严格分层**: 清晰分离各层职责，便于维护和测试
- **CQRS模式**: Command和Query分离，提升系统性能
- **领域驱动设计**: 实体封装业务逻辑，领域网关解耦依赖
- **数据库版本管理**: 使用Flyway进行自动数据库迁移

## 扩展阅读

- [COLA 架构详解](https://github.com/alibaba/COLA)
- [COLA 4.x 分层架构](https://start.aliyun.com/)