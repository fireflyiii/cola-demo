# cola-demo

基于 [COLA 4.x](https://github.com/alibaba/COLA)（Clean Object-oriented & Layered Architecture）架构的示例项目。

## 项目结构

```
cola-demo/
├── cola-demo-client/              客户端API层 - 对外暴露接口和DTO
├── cola-demo-adapter/             适配器层 - REST Controller
├── cola-demo-app/                 应用层 - Service + CQRS Executor
├── cola-demo-domain/              领域层 - 实体 + Gateway接口
├── cola-demo-infrastructure/      基础设施层 - Gateway实现 + 持久化
└── cola-demo-start/               启动模块 - SpringBoot入口
```

## 模块依赖

```
adapter → app → client, domain
infrastructure → domain, client
start → adapter, infrastructure
```

## 技术栈

- Java 1.8
- Spring Boot 2.7.2
- COLA Components 4.3.2
- MyBatis-Plus 3.5.3
- MySQL + HikariCP
- Flyway（数据库版本管理）

## 快速开始

### 1. 准备数据库

```sql
CREATE DATABASE IF NOT EXISTS cola_demo DEFAULT CHARSET utf8mb4;
```

### 2. 编译并启动

```bash
# 编译
mvn clean install -DskipTests

# 启动
cd cola-demo-start && mvn spring-boot:run
```

Flyway会在启动时自动执行建表脚本。

## API示例

```bash
# 新增客户
curl -X POST http://localhost:8080/customer \
  -H "Content-Type: application/json" \
  -d '{"customerName":"TestUser","companyType":"IT"}'

# 查询客户
curl http://localhost:8080/customer?customerName=Test
```