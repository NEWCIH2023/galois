# galois

## 介绍

一个开源的springboot项目热部署工具，基于JavaAgent +
ASM方式，在项目启动阶段，动态获取SpringBoot框架及MyBatis框架的重点对象，并通过监听项目本地文件变更，来实现实体或MyBatis的Mapper
热更新。支持MyBatis、SpringBoot等框架的代码热部署。
Java版本上，目前仅实测支持JDK 8版本。因为使用了JDK自带的ASM框架，因此其它Java版本暂未测试，理论上都是通用的。

## 软件架构

+ 文件变动监听服务
    + 监听java、xml、class等类型文件变更，将变更交由各自的热部署服务处理
    + 对项目的编译输出目录进行了过滤，即仅监听项目里面其它路径的文件更改变动
+ 对象热部署服务
    + 通过ASM工具，在项目启动阶段，修改关键类的实现（如MyBatis、SpringBoot相关上下文类），获取到关键的配置对象或上下文对象，之后重新解析监听到的文件变更的文件
        + 如java文件变更，则重新编译并redefine这个类，使其动态更新成新的类。该步骤通过java agent提供的redefine功能实现
        + 如mybatis的xml文件变更，则清空mybatis的相关缓存，并重新解析这个mapper配置文件，使其动态更新对应的statement语句

## Java框架支持情况

| 框架         | 支持情况 | 版本 |
|:-----------|:----:|:--:|
| Java 代码    |  ❌   | 8  |
| MyBatis    |  ✅   |    |
| SpringBoot |  ❌   |    |

## 使用说明

+ 额外扩展：[给你的JVM装一个插件DCEVM](https://blog.csdn.net/NEWCIH/article/details/129093034?spm=1001.2014.3001.5501)
  ，这是一个最高支持到JDK 8的JVM插件，用来拓展idea刷新class变动的能力，使其支持属性级别，方法级别的热更新能力。该项目`DCEVM`
  仅是一个推荐工具，与该开源项目并无直接关系，不是必须要求。
+ 打包可用jar包
    + 更新完maven依赖后，通过 `mvn clean package`
      来打包可用jar包，可用jar包的名称为 `galois-jar-with-dependencies.jar`，位于 `target`目录下
+ 配置项目JVM参数
    + 项目的启动参数`-vm`中，添加 `-javaagent:/XXX/galois.jar` (jar包的绝对路径)，以Windows系统举例，配置为
      `-javaagent:N:\IdeaProjects\galois\target\galois-jar-with-dependencies.jar`
    + 可选参数说明
      + **galois.includes**
        + 指定galois仅扫描的路径，多个路径通过 **;** 隔开，默认galois扫描整个工程路径
        + 举例：`-javaagent:N:\IdeaProjects\galois\target\galois-jar-with-dependencies.jar -Dgalois.
          includes=N:\IdeaProjects\galois\target\;N:\IdeaProjects\galois\demo\ `
      + **galois.excludes**
        + 指定galois排除扫描的路径，多个路径通过 **;** 隔开
        + *注意：excludes策略总是优先于includes策略*
+ 配置你的项目的控制台日志Logger，可以直观看到galois的运行状态
   ```xml
   <logger name="io.liuguangsheng.galois" level="INFO"/>
   ```   
+ 备注
    + 通过上述步骤完成项目配置后，galois将正式可用，可以通过SpringBoot项目启动的控制台日志看出来，将会在SpringBoot
      的Banner下面打印 **:: Galois ::    (vXXXX-SNAPSHOT)** 字样
    + 项目启动后，修改mybatis的xml配置文件，控制台将打印如 **Reload mybatis mapper by xml file XXX.xml success.**
      字样，此时表明该mybatis的mapper文件已经动态更新

## 项目详情

+ MyBatis
    + 为什么是热部署整个`namespace`，而不是单个mapper文件更新后就热部署单个mapper文件？
        + 这是因为，MyBatis在解析一个mapper的时候，遇到`<include
          refid=XXXX>`这种标签，会直接在解析阶段，就读取对应refid的内容，并解析成`sqlSource`
          属性，而不是一直保持引用，在每次执行时动态解析。所以，如果仅仅更改了a.
          xml一个mapper文件，就只热部署a.xml一个mapper文件的话，会导致a.xml里面被其它mapper通过`refid`引用的片段，无法得到更新。因此需要热部署整个`namespace`

