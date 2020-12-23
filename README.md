### 简介
本项目指在帮你快速搭建一款分布式，高可用，可伸缩的游戏框架，即时通讯框架。

### 技术栈
- 并发设计模式：活跃对象、保护性暂挂、半同步半异步、不可变对象、流水线、二阶段挂起、线程特有存储、主从模式等
- springboot 2.x.x 
- spring cloud Greenwich.SR2 
- elasticsearch7.x.x
- netty websocket
- jedis、redisson
- mybatis
- xxljob
- curator
- 工作流 activiti 6.0.0
- 基于注解的多数据源，读写分离
- shardingsphere 分库分表
- zookeeper 分布式计数器，节点监听器
- nacos注册中心和nacos配置中心
- protobuf
- docker、docker compose
- webflux

### starter-core模块（核心自动配置模块）
```
src/
  +- main/
    +- java/
      +- concurrent 并发设计模式学习参考
      +- elasticsearch elasticsearch自动配置类：封装核心api
      +- netty
        +- web websocket自动配置类:封装websocket，基于注解帮助你快速开发WebSocket服务器
      +- redis redis自动配置类：封装核心api，分布式锁，redis工具类
      +- sqlgen 动态sql语句，封装crud
      +- xxl  xxl-job自动配置类
      +- zookeeper zk自动配置类，封装核心api
      +- resource
```

### cloud-xxx模块（微服务脚手架）


- cloud-common 公共项目
- cloud-config-client 配置中心测试 
- cloud-config-server 配置中心
- cloud-elastic-service es测试
- cloud-eureka-client 
```
  src/
    +- main/
      +- java/
        +- activiti activiti工作流参考模块
        +- concurrent 并发测试工具类
        +- datasouce 多数据源，主从分离，读写分离
        +- dbtool 数据库表差异比较工具包
        +- interceptor mybatis分页插件参考
        +- sharding shardingsphere分库分表使用参考
        +- zookeeper zk自动配置类，封装核心api
        +- websocket websocket自动配置测试
        +- xxl 分布式调度测试
      +- resource
```
- cloud-eureka-consumer 
- cloud-eureka-service eureka服务发现中心
- cloud-zuul-service zuul服务发现中心

### im-xxx模块（即时通讯）
- im-client 模拟即时通讯客户端
- im-common 即使通讯公共包
- im-web 即使通讯网关
- im-server 即时通讯服务器
```
  src/
    +- main/
      +- java/
        +- distributed zk分布式计数器，分布式节点，节点路由
        +- handler 服务器处理器，包含消息转发处理器，心跳包处理器，登入登出处理器，异常处理器等
        +- processor 消息处理器
        +- server 聊天服务器
        +- session 本地session,远程session,session管理器
        +- user 分布式用户会话
      +- resource
```
    