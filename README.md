## 基于 Spring Boot 开发的牛课网社区首页
使用技术栈
- Spring boot 
- Spring MVC
- MySQL
- Flyway
- Thymeleaf
- Mybatis
- Spring mail
- Kaptcha 验证码
- AJAX
- 拦截器
- 前缀树过滤敏感词
- 事务
- Spring AOP
- Redis
- Kafka
- Elasticsearch
- Spring Security(授权认证)
- ...

## 模块

- 首页
- 注册登陆模块
- 用户个人信息
- 帖子
- 消息模块
- 统一处理异常
   

## 使用

- Use docker to start mysql
```
docker run --name my-mysql -e MYSQL_ROOT_PASSWORD=123 -e MYSQL_DATABASE=community -p 3306:3306 -d mysql
```

- Use flyway init table
```
mvn flyway:clean flyway:migrate
```

- Use docker to start redis
```
docker run --name community-redis -p 6379:6379 -d redis
```
```
docker exec -it 69719 redis-cli
```
- start Zookeeper & kafka service

```
cd ~/Downloads/kafka_2.13-2.6.0/bin
sh zookeeper-server-start.sh ../config/zookeeper.properties
```
```
cd ~/Downloads/kafka_2.13-2.6.0/bin
sh kafka-server-start.sh ../config/server.properties
```
You should make sure to start zookeeper service first and then Kafka service; Close Kafka service first, then close zookeeper service

- start elasticsearch(6.4.3)


