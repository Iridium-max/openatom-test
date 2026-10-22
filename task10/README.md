# task10 用户认证

适合招新题的小型 Spring Boot 项目：Java 17、Spring Boot 3.5.7、Maven Wrapper 3.9.14、JPA、MySQL、JJWT 0.12.6、BCrypt。

## 1. 数据库与启动

需要 JDK 17 和 MySQL 8。先在 MySQL 客户端执行 sql/init.sql（不会删除已有数据）。

默认连接 localhost:3306/task10，账号 root，密码沿用本机原有练习配置 123456。可用环境变量覆盖：

~~~powershell
cd C:\Users\app\Desktop\task10
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "你的MySQL密码"
# 可选：DB_URL、SERVER_PORT、JWT_SECRET
.\mvnw.cmd clean verify
.\mvnw.cmd spring-boot:run
~~~

或者构建后运行 java -jar target/task10-0.0.1-SNAPSHOT.jar。接口默认地址 http://localhost:8080。

本机练习密钥已经写入 application.properties；部署时用 JWT_SECRET 替换为至少 32 个随机字节的 Base64。密钥变更后旧 Token 失效。环境变量请在启动应用的同一个终端设置。

## 2. 接口示例（PowerShell）

注册：

~~~powershell
$body = @{ username = "student"; password = "hello123" } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8080/register -ContentType "application/json; charset=utf-8" -Body ([Text.Encoding]::UTF8.GetBytes($body))
~~~

响应：{"message":"注册成功"}。用户名重复返回 HTTP 409、{"message":"用户名已存在"}。

第一次登录：

~~~powershell
$login = Invoke-RestMethod -Method Post -Uri http://localhost:8080/login -ContentType "application/json; charset=utf-8" -Body ([Text.Encoding]::UTF8.GetBytes($body))
$login
$token = $login.token
~~~

响应包含 message=登录成功、token=JWT、expiresIn=604800（秒，即7天）。

第二次免密登录（不传请求体）：

~~~powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/login -Headers @{ Authorization = "Bearer $token" }
~~~

响应：{"message":"登录成功"}。此操作不续期，也不重新签发 Token。

Token 过期、签名错误、格式错误、用户已删除或没有 Token 时，需要用账号密码登录。可在同一次请求中携带账号密码作为回退。账号或密码错误返回 HTTP 401、{"message":"用户名或密码错误"}；缺少凭据返回 HTTP 401、{"message":"请使用用户名和密码登录"}。

请求体使用 JSON。用户名去掉首尾空格后不能为空且最多50字符；密码不能为空且最多72个UTF-8字节（BCrypt限制），密码保留原始空格。有效 Token 优先于请求体中的账号密码。

## 3. 数据库 SQL

~~~sql
CREATE DATABASE IF NOT EXISTS task10
    DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE task10;
CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    register_time DATETIME(6) NOT NULL,
    CONSTRAINT uk_users_username UNIQUE (username)
);
~~~

注册时只保存 BCrypt 哈希，不保存明文密码；register_time 由后端生成。用户名唯一约束也防止并发重复注册。所有查询通过 Repository 的方法参数交给 JPA 绑定，没有拼接 SQL。JWT 的 subject 保存用户名，签名和过期时间在服务器校验。JWT 仅签名，不用于存储密码。

## 4. 文件结构

- pom.xml：依赖及 Java 17 配置
- .mvn/wrapper/maven-wrapper.properties：Maven 版本
- src/main/java/com/example/task10/Task10Application.java：原有启动类
- entity/User.java：users 实体（从原 src/main/java/entity/User.java 移到与包名一致的位置）
- repository/UserRepository.java：参数化查询
- service/UserService.java：注册、密码验证、Token 登录
- controller/UserController.java：POST /register、POST /login
- controller/ApiExceptionHandler.java：统一错误 JSON
- dto/AuthRequest.java、dto/AuthResponse.java：请求和响应
- util/JwtUtil.java：JWT 签发、验签、7天过期
- config/PasswordConfig.java：BCrypt
- src/main/resources/application.properties：MySQL 和 JWT 配置
- sql/init.sql：建库建表
- src/test/java/com/example/task10/Task10ApplicationTests.java：集成测试
- src/test/resources/application-test.properties：测试用内存数据库

以上 Java 子目录均位于 com/example/task10 下。没有引入完整 Spring Security Web 过滤器，便于初学者理解。

## 5. 编译、测试和 IDEA

运行 .\mvnw.cmd clean verify。测试 profile 使用 H2（仅测试依赖），不修改本机 MySQL。覆盖密码哈希、重复注册、错误密码、SQL注入尝试、JWT七天有效期、免密登录、过期及伪造Token、账号密码回退、删除用户、输入校验。

正常启动使用 MySQL，没有 H2 运行模式。开发环境 ddl-auto=update 可自动建表，但不能替代既有表的数据迁移。

IDEA 中 Project SDK、Maven importer JDK、Maven runner JRE 选 JDK 17，Maven home 选择 Wrapper，然后 Reload All Maven Projects。无需手动添加 jakarta.persistence JAR，JPA starter 已引入。不要额外添加 Maven 内部 ArtifactHandlerManager 依赖。

原项目 Java 25 与本机 JDK 17 不一致，现已统一为17；将原 Boot 4.1.1 配置换为 Boot 3.5.7，并使用对应的 web/test starter。历史 IDE 的 ArtifactHandlerManager 报错不能只凭日志确定根因，本项目以 Wrapper 实际构建结果为准。

参考：[Spring Boot 3.5 系统要求](https://docs.spring.io/spring-boot/3.5/system-requirements.html)、[JJWT官方文档](https://github.com/jwtk/jjwt/tree/0.12.6)。
## 6. 本次验证结果

2026-10-22：Maven Wrapper 3.9.14 + JDK 17.0.18 执行 clean verify，BUILD SUCCESS；6项测试，0失败、0错误。测试仅启用依赖注入监听器，无需 Mockito 动态附加代理。

已使用本机 MySQL 8.0.45 启动最终 JAR，监听8080；实测注册、账号密码登录、Authorization Bearer免密登录均成功。数据库存储60字符BCrypt哈希和注册时间。测试账号已清理。数据库建表SQL已在本机执行。
