# cola-demo

基于 [COLA 4.x](https://github.com/alibaba/COLA)（Clean Object-oriented & Layered Architecture）架构的示例项目。

## 项目结构

```
cola-demo/
├── demo-client/              客户端API层 - 对外暴露接口和DTO
├── demo-adapter/             适配器层 - REST Controller
├── demo-app/                 应用层 - Service实现
├── demo-domain/              领域层 - 实体 + Gateway接口
├── demo-infrastructure/      基础设施层 - Gateway实现 + 持久化
└── demo-start/               启动模块 - SpringBoot入口
```

## 模块依赖

```
adapter → app → client
              → infrastructure → domain
start → adapter, infrastructure
```

## 技术栈

- Java 1.8
- Spring Boot 2.7.2
- COLA Components 4.3.2

## 快速开始

```bash
# 编译
mvn clean install -DskipTests

# 启动
cd demo-start && mvn spring-boot:run
```

## API示例

```bash
# 新增客户
curl -X POST http://localhost:8080/customer \
  -H "Content-Type: application/json" \
  -d '{"customerName":"TestUser","companyType":"IT"}'

# 查询客户
curl http://localhost:8080/customer?customerName=Test
```
