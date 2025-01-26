# 用户使用手册

### `MethodInterceptor` 执行顺序

对于被定义在同一个切面类中的增强方法，将根据它们的增强类型按以下顺序分配优先级，从最高到最低优先级：

1. `@Around`
2. `@Before`
3. `@After`
4. `@AfterReturning`
5. `@AfterThrowing`

当不论是否在同一个切面类中的同类型增强方法，想要在同一个连接点执行时，默认的执行顺序是未定义的。例如:

``` java
public class TestAspect1 {
    /**
     * 表示一个方法前置拦截器
     */
    public void before1() {
    }
}
``` 

``` java
public class TestAspect2 {
    /**
     * 表示一个方法前置拦截器
     */
    public void before2() {
    }
}
``` 

在默认情况下如果以上两个不同切面的同一类型的拦截器在同一个连接点执行时，会有两种且都合理的情况：

1. `before1()` 先执行， `before2()`后执行
2. `before2()` 先执行， `before1()`后执行

但在一些特殊场景下，要求两者之间有严格的执行顺序，此时可以通过设置优先级来控制执行顺序，通过`modelengine.fitframework.annotation.Order`注解实现。例如：

``` java

@Order(1)
public class TestAspect1 {
    /**
     * 表示一个方法前置拦截器
     */
    public void before1() {
    }
}
``` 

``` java

@Order(-1)
public class TestAspect2 {
    /**
     * 表示一个方法前置拦截器
     */
    public void before2() {
    }
}
``` 

通过在类上添加 `@Order` 注解可以对执行顺序进行控制。给定两个 `@Order` 注解，从 `Order.value()` 返回值越低，优先级越高。

此外，`@Order` 注解同样可以添加在方法上，考虑下面一种特殊的场景：

两个切面类 `A`, `B`， 要求 `A` 中除了前置拦截器的其他拦截器的优先级要高于 `B`。
``` java

@Order(-1)
public class TestAspectA {
    /**
     * 表示一个方法前置拦截器
     */
    @Order(2)
    public void before1() {
    }

    /**
     * 表示一个方法环绕拦截器
     */
    public void around1() {
    }
}
``` 

``` java

@Order(1)
public class TestAspectB {
    /**
     * 表示一个方法前置拦截器
     */
    public void before2() {
    }

    /**
     * 表示一个方法环绕拦截器
     */
    public void around2() {
    }
}
```
对于增强方法若要通过 `@Order` 获取优先级，首先从 `method` 的 `@Order` 注解中获取，倘若没有则从 `class` 的 `@Order` 注解中获取。如果都没有，则为 `@Order` 注解的默认值。