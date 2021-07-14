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
- quartz
- wkhtmltopdf
- Caffeine Cache
- 七牛云对象存储
- ...

## 模块

- 首页
- 注册登陆模块
- 用户个人信息
- 帖子
- 消息模块
- 管理员模块，统计数据(redis 高级数据结构 HyperLogLog Bitmap)
- 搜索功能
- 热帖排行功能
    - 热帖排行算法：`log(精华分(75) + 评论数 * 10 + 点赞数 * 2) + (发布时间 - 创立纪元) `  
    - 使用 quartz 做定时任务计算，测试中计算时间为 5 min 
- 生成长图


## 项目亮点

- 前缀树过滤敏感词
- Caffeine Cache 对首页热门帖子查询优化

使用 Jmeter （70个线程）进行抗压测试：

优化之前：吞吐量为 17.5/sec

<img src="https://tva1.sinaimg.cn/large/008i3skNgy1gs7opwq1d0j31rw0oq0yd.jpg" alt="image-20210707001552584" style="zoom:50%;" align="left"/>



优化过后：吞吐量达到 111.8/sec

<img src="https://tva1.sinaimg.cn/large/008i3skNgy1gs7ozveadjj31ro0ow0yc.jpg" alt="image-20210707002229751" style="zoom:50%;" />



    
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


