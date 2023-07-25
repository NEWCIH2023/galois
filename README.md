# galois

## 介绍

一个开源的springboot热部署工具，基于JavaAgent + ASM方式，在项目启动阶段，动态获取SpringBoot框架及MyBatis框架的重点对象，并通过监听项目本地文件变更，来实现实体或MyBatis的Mapper热更新。支持MyBatis、SpringBoot等框架的代码热部署。
Java版本上，目前仅支持JDK 8版本。因为使用了JDK自带的ASM框架，因此暂无适配其它Java版本。

## 软件架构

+ 文件变动监听服务
    + 监听java、xml、class等类型文件变更，将变更交由各自的热部署服务处理
+ 实体热部署服务
    + 通过ASM工具，在项目启动阶段，修改关键类的实现（如MyBatis、SpringBoot相关上下文类），获取到关键的配置对象或上下文对象，之后重新解析监听到的文件变更
      + 如java文件变更，则重新编译并redefine这个类，使其动态更新成新的类。该步骤通过java agent提供的redefine功能实现
      + 如mybatis的xml文件变更，则清空mybatis的相关缓存，并重新解析这个mapper配置文件，使其动态更新对应的statement语句

## 支持情况

| 框架                      |  支持情况  | 版本 |
|:------------------------|:------:|:--:|
| Java 代码                 | ✘(实验性) | 8  |
| SpringBoot Controller接口 | ✘(实验性) |    |
| SpringBoot XML配置文件      |   ✘    |    |
| MyBatis XML配置文件         |   ✔    |    |
| MyBatis 注解              |   ✘    |    |

## 使用说明

+ 额外扩展：[给你的JVM装一个插件DCEVM](https://blog.csdn.net/NEWCIH/article/details/129093034?spm=1001.2014.3001.5501)
+ 配置控制台日志Logger，可以直观看到galois的运行状态
```xml
<logger name="io.liuguangsheng.galois" level="INFO"/>
```
+ 打包可用jar包
  + 更新完maven依赖后，能在 `Plugins` 找到 `assembly`，需要通过 `assembly:assembly` 来打包可用jar包，可用jar包的名称为 `galois-jar-with-dependencies.jar`，位于 `target`目录下
  
+ 配置项目JVM参数
  + 项目的启动参数中，添加 -javaagent:/XXX/galois.jar (jar包的绝对路径)

+ 如何使用
  + 通过上述步骤完成项目配置后，galois将正式可用，可以通过SpringBoot项目启动的控制台日志看出来，如打印 galois 的logo字符串
  + 项目启动后，修改mybatis的xml配置文件，控制台将打印如 "Reload mybatis mapper by xml file XXX.xml success. " 字样，此时表明该mybatis的mapper文件已经动态更新