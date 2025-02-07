# FIT 框架：AOP 插件化，实现真正的业务无感

在软件开发领域，AOP（面向切面编程）作为一种强大的编程范式，能够有效地将切面关注点（如日志记录、性能监控、安全控制等）与核心业务逻辑分离，提升代码的模块化和可维护性。然而，传统 AOP 框架，例如 Spring AOP，通常需要在业务中显式地依赖切面模块，这在一定程度上增加了代码的复杂性和耦合度。

FIT 框架的出现，为 AOP 的应用带来了全新的思路。它先进的插件化开发理念，使 AOP 切面不仅可以像 Spring AOP 一样，采用传统的模块（插件）内 AOP 开发方式，也可以将 AOP 功能封装成独立的插件，让 AOP 功能与业务模块彻底解耦，当 AOP 插件与业务插件同时部署时，业务模块无需添加任何依赖，也不会感知到 AOP 模块逻辑，即可享受 AOP 带来的便利。

## FIT 框架 AOP 插件化的实现原理

FIT 框架通过字节码增强技术，在运行时动态地将 AOP 逻辑织入到目标方法中。具体来说，这个过程发生在创建 Bean 时，FIT 框架会首先部署 AOP 插件，然后在业务插件部署时，将 AOP 逻辑织入匹配到的目标方法中。由于整个过程是在运行时完成的，因此用户无需修改代码即可享受 AOP 功能。
> 值得注意的是，当在插件内实现 AOP 逻辑时，该 AOP 功能仅对同一插件有效，因此 FIT 框架支持整个插件的热插拔；而将 AOP 封装为插件以后，AOP 插件影响范围为全局，若需要对 AOP 插件进行热插拔，则所有 Bean 都需要重新创建，相当于一次重启，因此从实现复杂度和操作安全性上考虑，FIT 不支持对包含全局切面的插件热插拔。

## 快速入门插件化 AOP 日志管理

### 准备环境

进入项目地址 FIT-Framework，下载项目代码，根据入门指南快速部署你的 FIT 环境，并学习如何基于 FIT 框架新建属于你自己的插件！

### 目录构建

请根据以下目录创建工程，本目录参考了 FIT 工程目录最佳实践。

```
aop-log-plugin              -- 总目录
+- plugins                  -- 插件目录，在该目录下创建各类插件
│  +- plugin-log            -- 日志功能插件，在该插件内实现 AOP 逻辑
│  │  +- src
│  │  +- pom.xml
│  │  \- ...
│  +- plugin-simple-mvc     -- 一个简单的 MVC 功能插件，在该插件内实现业务逻辑
│  │  +- src
│  │  +- pom.xml
│  │  \- ...
│  \- ...
+- pom.xml
+- README.xml
\- ...
```

### simple-mvc 插件

在`simple-mvc`插件内，我们实现一个简单的 MVC 功能：

定义服务：

``` java
public interface MyService {
    void doSomething();
}
```

定义服务相关的实现，需要打上`@Component`注解将其注册为`Bean`：

``` java
@Component
public class MyServiceImpl implements MyService {
    @Override
    public void doSomething() {
        System.out.println("do something");
    }
}
```

定义一个简单的 HTTP 控制器，需要打上`@Component`注解将其注册为`Bean`：

``` java
@Component
public class MyController {
    private final MyService myService;

    public MyController(MyService myService) {
        this.myService = myService;
    }

    @GetMapping(path = "/hello")
    public void hello() {
        this.myService.doSomething();
    }
}
```

这样，一个简单的 MVC 插件就实现了，当该插件部署后，访问`http://localhost:8080/hello`即可调用到指定服务。

> 关于插件开发的相关 pom 配置及 yml 配置，请到项目内进行参考，此处不过多赘述。
> 关于如何启动 FIT 框架及部署插件，请参考项目的快速入门指南，此处不过多赘述。

### log 插件

在`log`插件内，我们通过`@Aspect`注解来定义一个日志管理的切面，同时打上`@Component`注解使之注册为一个`Bean`，通过`@Around("@annotation(modelengine.fit.http.annotation.GetMapping)")`来拦截`GetMapping`注解，当执行到含该注解的方法时，该切面会自动拦截方法并执行相关逻辑。在本例中，切面拦截目标方法后实现了简单的日志功能。

``` java
@Aspect(scope = Scope.GLOBAL)
@Component
public class LoggingAspect {
    private static final Logger logger = Logger.get(LoggingAspect.class);

    @Around("@annotation(modelengine.fit.http.annotation.GetMapping)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        // 获取方法信息
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        // 记录方法开始日志
        logger.info("===> {}.{}() 开始执行", className, methodName);
        try {
            // 执行目标方法
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            // 记录方法结束日志
            logger.info("<=== {}.{}() 执行成功 | 耗时: {}ms", className, methodName, executionTime);
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("<=== {}.{}() 执行失败 | 耗时: {}ms | 异常: {}",
                    className,
                    methodName,
                    executionTime,
                    e.getMessage(),
                    e);
            throw e;
        }
    }
}
```

实现了这样一个 AOP 日志管理插件后，将`log`插件和`simple-mvc`插件同时部署到用户插件目录中，启动 FIT，此时访问`http://localhost:8080/hello` ，则可以在`Bash`中看到如下日志：

```bash
[yyyy-MM-dd 00:00:00.000] [INFO ] [netty-request-assembler-thread-0] [modelengine.fit.example.LoggingAspect] ===> modelengine.fit.example.controller.MyController.hello() 开始执行
do something
[yyyy-MM-dd 00:00:00.000] [INFO ] [netty-request-assembler-thread-0] [modelengine.fit.example.LoggingAspect] <=== modelengine.fit.example.controller.MyController.hello() 执行成功 | 耗时: 1809ms
```