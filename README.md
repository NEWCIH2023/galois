# galois

## 介绍

一个开源的springboot热部署工具，基于Java Agent方式。目前支持MyBatis、SpringBoot等框架的代码热部署。
Java版本上，目前仅支持JDK 8版本。因为使用了JDK自带的ASM框架，因此暂无适配其它Java版本。

## 软件架构

+ 文件变动监听服务
    + 监听java、xml、class等文件变更，将变更交由各自的热部署服务处理
+ 实体热部署服务
    + 通过ASM工具，在项目启动阶段，修改关键类的实现（如MyBatis、SpringBoot相关上下文类），使其支持热更新服务

## 支持情况

| 框架                      | 支持情况 | 版本 |
|:------------------------|:----:|:--:|
| Java 代码                 |  ✔   | 8  |
| SpringBoot Controller接口 |  ✔   |    |
| SpringBoot XML配置文件      |  ✘   |    |
| MyBatis XML配置文件         |  ✔   |    |
| MyBatis 注解              |  ✘   |    |

## 使用说明

+ [给你的JVM装一个插件DCEVM](https://blog.csdn.net/NEWCIH/article/details/129093034?spm=1001.2014.3001.5501)
+ 配置控制台日志Logger
```xml
<logger name="io.liuguangsheng.galois" level="INFO"/>
```

+ 配置项目JVM参数
    + 项目的启动参数中，添加 -javaagent:/XXX/galois.jar (jar包的绝对路径)

+ 如何打包可用jar包
  + 更新完maven依赖后，能在 `Plugins` 找到 `assembly`，需要通过 `assembly:assembly` 来打包可用jar包
