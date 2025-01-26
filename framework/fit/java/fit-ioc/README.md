<div style="text-align: center;"><span style="font-size: 40px"><b>FIT IoC 容器架构设计</b></span></div>

[TOC]

# BeanContainer核心类图

![avatar](https://cloudmodelingapi.tools.huawei.com/cloudmodelingdrawiosvr/d/svg/2CuG)

`BeanContainer`作为`Bean`容器的核心接口，通过管理`BeanFactory`提供对`Bean`的管理能力。

为了使`BeanContainer`接口的对外呈现更加聚焦，通过提供`BeanRegistry`和`BeanContainer$Resolver`接口，对部分功能进行整合。
- `BeanRegistry`，整合`Bean`注册相关能力。
- `BeanContainer$Resolver`，整合`BeanContainer`中使用的各种解析程序。

# 解析程序

`BeanContainer`中共使用三个解析程序，为`BeanContainer`提供功能封装和扩展能力。

## BeanResolver

`BeanResolver`用以封装`Bean`解析的相关能力，例如：
- 判断一个类型、方法是否表示`Bean`
- 判断一个参数是否应当进行依赖注入
- 解析`Bean`类型中定义的方法是否用以依赖注入、初始化或释放资源等
- 检查`Bean`是否定义了所依赖的`Bean`所在的包
- 检查指定的`Bean`是否作为工厂使用

`BeanContainer`体系结构中，通过扩展`BeanResolver`来扩展所支持的用法。在`DefaultBeanResolver`中，支持通过以下注解来定义`Bean`：
- `FitableSuite`，定义在类型上，用以表示这个类型将作为`Bean`来被`BeanContainer`管理
- `Fitable`，定义在类型或方法上，用以表示类型或方法作为`Bean`来被`BeanContainer`管理
- `Initialize`，定义在方法上，表示该方法用以初始化`Bean`
- `Destroy`，定义在方法上，表示该方法用以释放`Bean`所占用的资源
- `Fit`，定义在字段、方法或参数上，表示需要进行依赖注入

## DependencyResolver

`DependencyResolver`用以解析`Bean`依赖。

`Bean`依赖的解析可通过**名称**和**类型**来作为依据进行解析，其中优先使用名称进行解析。

即当提供了名称时，将完全通过名称来解析依赖，即使解析不到，也不会根据类型来进行解析。

> 当按类型解析时，如果所需的类型是`List`或`Map`，则会进行特殊的处理。
> 
> 当所需类型为`List`时，`DependencyResolver`会找到所有符合`List`泛型参数的类型的`Bean`，并返回这些`Bean`按优先级排序后的列表。
> 
> 当所需类型为`Map`，且键为`String`类型时，`DependencyResolver`会找到所有符合`Map`泛型参数中值类型的`Bean`，并返回这些`Bean`以名称为键的、按优先级排序的映射。

### 构造方法参数依赖解析过程

![avatar](https://cloudmodelingapi.tools.huawei.com/cloudmodelingdrawiosvr/d/svg/2DZP)

## AnnotationMetadataResolver

`AnnotationMetadata`描述注解的元数据，用以提供**注解转发**能力，在使用上与`AnnotatedElement`接口提供的能力类似。

> 注解转发为注解提供类似于继承的能力，其主要能力可简要理解为两个方面：
> - 当定义了注解A时，即认为定义了注解B
> - 当设置了注解A的X属性时，即认为设置了注解B的Y属性
> 例如：
> ``` java
> public @interface Fitable {}
> 
> @Fitable
> public @interface FitableSuite {}
> ```
> 因为在`@FitableSuite`注解上定义了`@Fitable`注解，因此认为当一个类上定义了`@FitableSuite`注解时，也认为这个类上定义了`@Fitable`注解。

`AnnotationMetadataResolver`用以解析`AnnotatedElement`的注解元数据。

### 核心类图

![avatar](https://cloudmodelingapi.tools.huawei.com/cloudmodelingdrawiosvr/d/2CxX)

### 接口说明

- `AnnotationMetadataResolver`，注解元数据解析程序的对外接口，用以从某个`AnnotatedElement`中解析注解元数据
- `AnnotationMetadata`，表示注解的元数据，使用方式与`AnnotatedElement`类似
- `AnnotationProperty`，用以描述指定注解的指定名称的属性，为`AnnotationPropertyForwarder`提供转发到的注解的属性信息
- `AnnotationPropertyForwarder`，定义注解转发方法，用以解析指定注解方法所需转发到的注解的属性
- `AnnotationEliminator`，用以排除某些注解，避免不必要的注解处理，同时也可以避免因系统注解导致的各种问题（如`Retention`等）

`AnnotationMetadataResolver`系统可通过`AnnotationPropertyForwarder`和`AnnotationEliminator`提供扩展能力。

### 解析流程

![avatar](https://cloudmodelingapi.tools.huawei.com/cloudmodelingdrawiosvr/d/svg/2D4m)

### 注解树

![avatar](https://cloudmodelingapi.tools.huawei.com/cloudmodelingdrawiosvr/d/2DPL)

#### AnnotationTree

注解转发模型中，可通过在注解上定义其他注解，形成转发逻辑。依次逻辑可知，对于任意`AnnotatedElement`，其注解转发模型，都为`AnnotatedElement`定义的注解开始，形成的树形结构。

`AnnotationTree`即为此树形结构建模。

#### AnnotationTreeNode

`AnnotationTreeNode`为`AnnotationTree`定义其中的节点，节点呈现树形结构，每个节点表示一个注解定义。

#### AnnotationTreeNodeProperty

`AnnotationTreeNodeProperty`为`AnnotationTreeNode`提供注解的属性信息定义。

#### AnnotationTreeNodePropertySource

为注解属性提供数据来源的定义，用以支撑注解的属性转发能力。

# 生命周期

## 容器生命周期

![avatar](https://cloudmodelingapi.tools.huawei.com/cloudmodelingdrawiosvr/d/2CKG)

### 实例化容器

实例化容器过程将创建`BeanContainer`实例，后续可在此以装饰器形式对`BeanContainer`进行扩展。在此阶段后，将确定最终使用的`BeanContainer`实例。

### 初始化容器

初始化容器过程，主要进行`Bean`的注册，从而形成以`Bean`为最小单元的软件架构（`Bean`与`Bean`之间的关系）。

注册过程按顺序完成以下`Bean`的注册：
- 系统`Bean`，如`BeanContainer`自身。
- 自动扫描形成的`Bean`。
- 应用程序入口`Bean`，如被`ComponentScan`修饰的`Bean`。

在完成`Bean`注册后，将通知所有`BeanContainerInitializedObserver`观察者。

### 启动容器

启动容器过程将实例化所有单例模式（`Singleton Pattern`）的`Bean`。之后将通知所有`BeanContainerStartedObserver`观察者。

### 提供服务

在进入到该阶段后，容器将拒绝所有新的`Bean`注册行为，以确保容器的稳定。

## Bean生命周期

![avatar](https://cloudmodelingapi.tools.huawei.com/cloudmodelingdrawiosvr/d/2DRB)

`BeanCreator`、`BeanInjector`、`BeanInitializer`、`BeanMapper`和`BeanDestroyer`作为功能性接口，其中每个接口表示生命周期的一个阶段：
- `BeanCreator`，为`Bean`提供创建能力，用以实例化`Bean`。
- `BeanInjector`，为`Bean`提供依赖注入的能力，主要用以注入依赖的`Bean`。
- `BeanInitializer`，为`Bean`提供初始化能力，主要用以资源的初始化。
- `BeanMapper`，为`Bean`提供映射能力，用以支撑`BeanSupplier`特性。
- `BeanDestroyer`，为`Bean`提供销毁能力，主要用以资源的释放。

`BeanLifecycle`继承了各生命周期的阶段性接口，从而提供了完整的生命周期定义。

### 整合能力

`DefaultBeanLifecycle`通过整合各生命周期功能接口，从而形成最终的`BeanLifecycle`实例，即提供了将生命周期的各个阶段整合成完整生命周期的能力。

![avatar](https://cloudmodelingapi.tools.huawei.com/cloudmodelingdrawiosvr/d/2DRT)

通过这样的设计，同时为生命周期的各个阶段的功能性接口应用组合模式，从而允许将原子实现组成复杂逻辑，最终形成生命周期的完整实例。以`BeanInjector`为例：

![avatar](https://cloudmodelingapi.tools.huawei.com/cloudmodelingdrawiosvr/d/svg/2DRa)

首先提供`BeanInjector`的原子逻辑实现：
- `FieldBeanInjector`提供基于字段的注入逻辑，每个`FieldBeanInjector`实例可为一个`Bean`提供对指定一个字段的注入能力
- `MethodBeanInjector`提供基于方法的注入逻辑，每个`MethodBeanInjector`实例可为一个`Bean`提供对指定一个方法的注入能力

然后再通过使用组合模式，通过`BeanInjectorComposite`提供`BeanInjector`的组合能力，可以将一个`Bean`中的多个字段注入、方法注入聚合成一个`BeanInjectorComposite`以对外呈现，最终用以形成`BeanLifecycle`实例。

`BeanCreator`、`BeanInitializer`、`BeanDestroyer`也通过相同的模式实现。

### 拦截能力

`BeanLifecycleInterceptor`提供对生命周期的拦截能力。

![avatar](https://cloudmodelingapi.tools.huawei.com/cloudmodelingdrawiosvr/d/svg/2DRh)

`InterceptedBeanLifecycle`使用装饰器模式，结合一个`BeanLifecycleInterceptor`对一个`BeanLifecycle`进行装饰，继续提供`BeanLifecycle`的能力。当有多个拦截器时，每个拦截器都会形成一层装饰。

`BeanFactory`创建`Bean`时，首先在所属的`BeanContainer`中获取所有可用的`BeanLifecycleInterceptor`，并检查每个拦截器是否可用于装饰当前`Bean`，如果可以装饰当前`Bean`，则通过`InterceptedBeanLifecycle`进行装饰，最终形成拦截后的`BeanLifecycle`，并基于此提供的生命周期形成最终的`Bean`实例。

# 异常结构

``` plantuml
hide empty members

skinparam backgroundColor #EEEBDC
skinparam roundcorner 20
skinparam sequenceArrowThickness 2
skinparam ClassFontName Consolas

left to right direction

class IocException
class DependencyException extends IocException
class DependencyNotFoundException extends DependencyException
class DependencyDefinitionException extends DependencyException
class CircularDependencyException extends DependencyException
class UnresolvableDependencyException extends DependencyException

class AmbiguousBeanException extends IocException
class BeanCreationException extends IocException
class BeanDefinitionException extends IocException
class BeanNotFoundException extends IocException
```