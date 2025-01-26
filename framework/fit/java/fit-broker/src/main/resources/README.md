# Fit Broker Client 核心接口

## 动态路由依赖注入

``` plantuml
@startuml

hide empty members

skinparam backgroundColor #EEEBDC
skinparam roundcorner 20
skinparam sequenceArrowThickness 2
skinparam ClassFontName Consolas

skinparam class {
BackgroundColor<<REFERENCE>> LightGray
BackgroundColor<<EXTENSION CAPABILITIES>> Pink
}

package ioc {
  interface DependencyResolver {
  + {abstract} resolve(BeanDependency dependency) : Optional<ValueSupplier>
  }
  class DynamicRoutingDependencyResolver {
  - resolver : DependencyResolver
  + resolve(BeanDependency dependency) : Optional<ValueSupplier>
  }
}

package aop {
  class MicroDynamicRoutingInterceptor {}
  class AccessibleMethodMatcher {}
}

package api {
  interface BrokerClient {}
  interface Router {}
  interface Invoker {}
  interface Router.Filter {}
  interface Invoker.Filter {}
}

package broker-client {
  class DefaultBrokerClient {}
  class DefaultRouter {}
  class DefaultInvoker {}
  
  interface ProxyFactory {}
  class DefaultProxyFactory {}
  interface Proxy {}
  class LoadBalanceProxy {}
  abstract class AbstractProxy {}
  class LocalProxy {}
  abstract class AbstractRemoteProxy {}
  class GenericRemoteProxy {}
  class ConcreteRemoteProxy {}
  abstract class ProxyDecorator {}
  class DegradationDecorator {}
  class RetryDecorator {}
  
  interface InvocationStrategy {}
  abstract class AbstractInvocationStrategy {}
  class UnicastInvocationStrategy {}
  class MulticastInvocationStrategy {}
  class LocalOnlyUnicastInvocationStrategy {}
  
}


DependencyResolver <|-- DynamicRoutingDependencyResolver
DynamicRoutingDependencyResolver ..> MicroDynamicRoutingInterceptor
DynamicRoutingDependencyResolver ..> AccessibleMethodMatcher
MicroDynamicRoutingInterceptor --> BrokerClient
BrokerClient ..> Router
Router ..> Router.Filter
Router ..> Invoker
Invoker ..> Invoker.Filter
BrokerClient <|.. DefaultBrokerClient
Router <|.. DefaultRouter
Invoker <|.. DefaultInvoker

ProxyFactory <|.. DefaultProxyFactory

DefaultInvoker ..> LoadBalanceProxy

InvocationStrategy <|.. AbstractInvocationStrategy
AbstractInvocationStrategy <|-- UnicastInvocationStrategy
AbstractInvocationStrategy <|-- MulticastInvocationStrategy
AbstractInvocationStrategy <|-- LocalOnlyUnicastInvocationStrategy

LoadBalanceProxy ..> InvocationStrategy
AbstractInvocationStrategy ..> ProxyFactory

Proxy <|.. AbstractProxy
AbstractProxy <|-- LocalProxy
AbstractProxy <|-- AbstractRemoteProxy
AbstractRemoteProxy <|-- GenericRemoteProxy
AbstractRemoteProxy <|-- ConcreteRemoteProxy
Proxy <|.. ProxyDecorator
ProxyDecorator <|-- DegradationDecorator
ProxyDecorator <|-- RetryDecorator
Proxy <|.. LoadBalanceProxy

ProxyFactory ..> LocalProxy
ProxyFactory ..> GenericRemoteProxy
ProxyFactory ..> ConcreteRemoteProxy

@enduml
```

## Macro 服务代理

``` plantuml
@startuml

hide empty members

skinparam backgroundColor #EEEBDC
skinparam roundcorner 20
skinparam sequenceArrowThickness 2
skinparam ClassFontName Consolas

skinparam class {
BackgroundColor<<REFERENCE>> LightGray
BackgroundColor<<EXTENSION CAPABILITIES>> Pink
}

interface Proxy {
}
class LoadBalanceProxy {}
abstract class AbstractProxy {}
class LocalProxy {}
abstract class AbstractRemoteProxy {}
class GenericRemoteProxy {}
class ConcreteRemoteProxy {}
abstract class ProxyDecorator {}
class DegradationDecorator {}
class RetryDecorator {}

Proxy <|.. AbstractProxy
AbstractProxy <|-- LocalProxy
AbstractProxy <|-- AbstractRemoteProxy
AbstractRemoteProxy <|-- GenericRemoteProxy
AbstractRemoteProxy <|-- ConcreteRemoteProxy
Proxy <|.. ProxyDecorator
ProxyDecorator <|-- DegradationDecorator
ProxyDecorator <|-- RetryDecorator
Proxy <|.. LoadBalanceProxy

@enduml
```