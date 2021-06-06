## 基于 Spring Boot 开发的牛课网社区首页
使用技术栈
- spring boot 
- spring mvc
- thymeleaf
- mybatis
- logback 记录日志
- spring mail
- kaptcha 验证码
- ...

## 模块

- 首页（分页显示功能，十条）
- 注册登陆模块
   

## 使用

- Use docker to start mysql
```
docker run --name my-mysql -e MYSQL_ROOT_PASSWORD=123 -e MYSQL_DATABASE=community -p 3306:3306 -d mysql
```

- Use flyway init table
```
mvn flyway:clean flyway:migrate
```
