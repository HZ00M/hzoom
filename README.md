### 简介
本项目指在帮你快速搭建一款分布式，高可用，可伸缩的游戏框架，即时通讯框架。

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
### 技术栈
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
- docker、docker compose
- webflux

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
        +- server 聊天服务器,消息转发
        +- session 本地session,远程session,session管理器
        +- user 分布式用户会话
      +- resource
```
### 技术栈
- springboot 2.x.x 
- spring cloud Greenwich.SR2 
- netty websocket
- jedis、redisson
- curator
- zookeeper 分布式计数器，节点监听器
- protobuf
- webflux

### game-xxx模块（分布式游戏框架）
- game-center-server 游戏中心服务（rest服务中心）
- game-client 游戏模拟客户端服务
```
  src/
    +- main/
      +- java/
        +- client socket客户端
        +- command spring shell 客户端命令模拟器
        +- config retrofit、客户端相关配置类
        +- handler netty处理器，游戏业务处理器
        +- http  retrofit拦截器（请求签名，http请求日志）
      +- resource
```
- game-common 游戏公共模块 （通用的异常 事件 工具类）
- game-gateway-message-starter 游戏内rpc框架
```
  src/
    +- main/
      +- java/
        +- annotation 开启rpc框架注解
        +- channel 模仿netty框架实现的rpc框架，实现项目解耦，去锁化服务
        +- config 自动配置类
        +- context 消息封发，事件分发处理器
        +- handler rpc处理器handler
        +- rpc rpc相关实现类
        +- service 业务服务管理类，网关服务管理类
        += 基于spring cloud stream的消息服务
      +- resource
```
- game-mongo-dao mongo数据库公共项目
- game-network-param http通讯服务
```
  src/
    +- main/
      +- java/
        +- http
          +- common http请求,响应基础类，全局异常类封装
          += request http请求
          += response http响应
        +- message
          +- common rpc消息基础封装
          += request rpc请求
          += response rpc响应
      +- resource
```
- game-socket-gateway 长连接网关服务
```
  src/
    +- main/
      +- java/
        +- config 网关服务配置相关
        +- handler 处理长连接的编解码，分发，限流，心跳
        +- server 网关服务端
      +- resource
```
- game-web-gateway 短连接网关服务
```
  src/
    +- main/
      +- java/
        +- balance 基于spring cloud ribbon实现的基于用户id的负载均衡处理器
        +- common 网关通用工具包
        +- exception 全局异常封装处理
        +- filter 网关限流，token校验，请求分发的定义
      +- resource
```
### 技术栈
- springboot 2.x.x 
- spring cloud Greenwich.SR2 
- netty websocket
- jedis、redisson
- mongo 
- nacos注册中心和nacos配置中心
- protobuf
- docker、docker compose
- webflux

个人开发者，如有问题或建议欢迎讨论交流，联系QQ：1054743949