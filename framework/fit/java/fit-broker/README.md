<div style="text-align: center;"><span style="font-size: 40px"><b>FIT 代理架构设计</b></span></div>

[TOC]

# 核心逻辑

`FIT` 代理模块承载了 `FIT` 编程框架的核心逻辑，代理分为服务代理（`Genericable`）和服务实现代理（`Fitable`），服务代理即表示接口，服务实现代理即表示接口的实现。

# 核心类图

## 调用链路

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

interface BrokerClient {
  + {abstract} getRouter(Class<?> genericableClass) : Router
  + {abstract} getRouter(String genericableId, Method genericableMethod) : Router
}
class DefaultBrokerClient {
  - routerFactory : <color:blue><u>RouterFactory</u></color>
  + getRouter(Class<?> genericableClass) : <color:blue><u>Router</u></color>
  + getRouter(String genericableId, Method genericableMethod) : <color:blue><u>Router</u></color>
}
DefaultBrokerClient .l.|> BrokerClient

interface Router {
  + route() : <color:blue><u>Invoker</u></color>
  + {abstract} route(Router.Filter filter) : <color:blue><u>Invoker</u></color>
}
BrokerClient .d.> Router
class DefaultRouter {
  - invokerFactory : <color:blue><u>InvokerFactory</u></color>
  ..
  - genericableId : String
  - genericableMethod : Method
  __
  + route(Router.Filter filter) : <color:blue><u>Invoker</u></color>
}
DefaultRouter .r.|> Router

interface RouterFactory {
  + {abstract} create(String genericableId, Method genericableMethod)
}
RouterFactory .l.> Router : create

class DefaultRouterFactory {
  - invokerFactory : <color:blue><u>InvokerFactory</u></color>
  + create(String genericableId, Method genericableMethod) : <color:blue><u>Router</u></color>
}
DefaultRouterFactory .u.|> RouterFactory

interface Invoker {
  + {abstract} filter(Invoker.Filter filter) : <color:blue><u>Invoker</u></color>
  + {abstract} filterWith(List<UniqueFitableId> ids) : <color:blue><u>Invoker</u></color>
  + {abstract} filterWithSpecifiedEnvironment(String environment) : <color:blue><u>Invoker</u></color>
  + {abstract} unicast() : <color:blue><u>Invoker</u></color>
  + {abstract} multicast(BinaryOperator<Object> accumulator) : <color:blue><u>Invoker</u></color>
  + {abstract} retry(int maxCount) : <color:blue><u>Invoker</u></color>
  + {abstract} timeout(long timeout, TimeUnit timeoutUnit) : <color:blue><u>Invoker</u></color>
  + {abstract} ignoreDegradation() : <color:blue><u>Invoker</u></color>
  + {abstract} invoke(Object... args) : Object
}
Router ..d..> Invoker
class DefaultInvoker {
  - genericableRepository : <color:blue><u>GenericableRepository</u></color>
  - genericableId : String
  - genericableMethod : Method
  - contextBuilder : InvocationContext.Builder
  ..
  - filter : Invoker.Filter
  - filterWith : List<UniqueFitableId>
  - accumulator : BinaryOperator<Object>
  __
  + filter(Invoker.Filter filter) : <color:blue><u>Invoker</u></color>
  + filterWith(List<UniqueFitableId> ids) : <color:blue><u>Invoker</u></color>
  + filterWithSpecifiedEnvironment(String environment) : <color:blue><u>Invoker</u></color>
  + unicast() : <color:blue><u>Invoker</u></color>
  + multicast(BinaryOperator<Object> accumulator) : <color:blue><u>Invoker</u></color>
  + retry(int maxCount) : <color:blue><u>Invoker</u></color>
  + timeout(long timeout, TimeUnit timeoutUnit) : <color:blue><u>Invoker</u></color>
  + ignoreDegradation() : <color:blue><u>Invoker</u></color>
  + invoke(Object... args) : Object
}
DefaultInvoker .u.|> Invoker

interface InvokerFactory {
  + {abstract} create(String genericableId, Method genericableMethod, Router.Filter filter) : <color:blue><u>Invoker</u></color>
}
InvokerFactory .l.> Invoker : create

class DefaultInvokerFactory {
  - genericableFactory : <color:blue><u>GenericableFactory</u></color>
  - fitableFactory : <color:blue><u>FitableFactory</u></color>
  ..
  - localExecutorRepository : <color:blue><u>LocalExecutorRepositoryComposite</u></color>
  - localGenericableRepository : <color:blue><u>LocalGenericableRepository</u></color>
  - genericableRepository : <color:blue><u>GenericableRepository</u></color>
  ..
  - workerId : String
  - host : String
  - appName : String
  - environmentPrioritySequence : Deque<String>
  __
  + create(String genericableId, Method genericableMethod, Router.Filter filter) : <color:blue><u>Invoker</u></color>
  + onBeanContainerInitialized(BeanContainer container) : void
  + get(UniqueFitableId id) : Optional<<color:blue><u>LocalExecutor</u></color>>
  + get(Plugin plugin, boolean isMicro) : List<<color:blue><u>LocalExecutor</u></color>>
}
DefaultInvokerFactory .u.|> InvokerFactory
note right of DefaultInvokerFactory::onBeanContainerInitialized
  After <color:blue>BeanContainer</color> initialized:
  1. Get <b>pluginName</b> from <b>container</b>.
  2. Create <color:red><b>localExecutorRepository</b></color> and install it.
  3. Create <color:green><b>localGenericableRepository</b></color> and install it.
  4. The installed <color:green><b>localGenericableRepository</b></color>
  observes the installed <color:red><b>localExecutorRepository</b></color> on
  <color:blue><b>LocalExecutor</b></color> registered.
  5. Resolve <color:blue><b>LocalExecutor</b></color>s in local executor repository.
end note

interface LocalExecutorFactory {
  + {abstract} get(UniqueFitableId id) : Optional<<color:blue><u>LocalExecutor</u></color>>
  + {abstract} get(Plugin plugin, boolean isMicro) : List<<color:blue><u>LocalExecutor</u></color>>
}
interface BeanContainerInitializedObserver {
  + {abstract} onBeanContainerInitialized(BeanContainer container) : void
}
DefaultInvokerFactory .d.|> LocalExecutorFactory
DefaultInvokerFactory .d.|> BeanContainerInitializedObserver

interface GenericableRepository {
  + {abstract} name() : String
  + {abstract} get(String id, String version) : Optional<<color:blue><u>Genericable</u></color>>
}
DefaultInvoker "1" o-d-> "1" GenericableRepository : contains

class DefaultGenericableRepository {
  - name : String
  - genericables : Map<UniqueGenericableId, <color:blue><u>ConfigurableGenericable</u></color>>
  - genericableFactory : <color:blue><u>GenericableFactory</u></color>
  - fitableFactory : <color:blue><u>FitableFactory</u></color>
  + name() : String
  + get(String id, String version) : Optional<<color:blue><u>Genericable</u></color>>
  + onLocalExecutorRegistered(UniqueFitableId id, <color:blue><u>LocalExecutor</u></color> executor) : void
}
note left of DefaultGenericableRepository::onLocalExecutorRegistered
  After <color:blue><b>LocalExecutor</b></color> registered:
  1. Create a <color:blue><b>ConfigurableFitable</b></color>.
  2. Associate it with an existing
  <color:blue><b>ConfigurableGenericable</b></color>.
  <b>OR</b>
  1. Create a <color:blue><b>ConfigurableFitable</b></color>.
  2. Associate it with a new
  <color:blue><b>ConfigurableGenericable</b></color>.
  3. Add this <color:blue><b>ConfigurableGenericable</b></color>
  into current genericable repository.
end note
DefaultGenericableRepository ...u...|> GenericableRepository

interface LocalExecutorRegisteredObserver {
  + {abstract} onLocalExecutorRegistered(UniqueFitableId id, <color:blue><u>LocalExecutor</u></color> executor) : void
}
DefaultGenericableRepository .d.|> LocalExecutorRegisteredObserver

class PriorityGenericableRepository {
  - repositories : List<<color:blue><u>GenericableRepository</u></color>>
  + name() : String
  + get(String id, String version) : Optional<<color:blue><u>Genericable</u></color>>
}
PriorityGenericableRepository .u.|> GenericableRepository

class LocalGenericableRepository {
  - genericableFactory : <color:blue><u>GenericableFactory</u></color>
  - fitableFactory : <color:blue><u>FitableFactory</u></color>
  ..
  - rootRepository : <color:blue><u>GenericableRepository</u></color>
  - pluginRepositories : List<<color:blue><u>GenericableRepository</u></color>>
  - lock : ReadWriteLock
  __
  + name() : String
  + get(String id, String version) : Optional<<color:blue><u>Genericable</u></color>>
  + install(<color:blue><u>GenericableRepository</u></color> repository) : void
  + uninstall(<color:blue><u>GenericableRepository</u></color> repository) : void
}
LocalGenericableRepository .u.|> GenericableRepository

interface GenericableMetadata {
  + {abstract} id() : String
  + {abstract} version() : String
  + {abstract} name() : String
  + {abstract} type() : GenericableType
  + {abstract} method() : GenericableMethod
  + {abstract} route() : Route
  + {abstract} tags() : Tags
  + {abstract} fitables() : List<? extends <color:blue><u>FitableMetadata</u></color>>
  + {abstract} toUniqueId() : UniqueGenericableId
}
interface Genericable {
  + {abstract} fitables() : List<<color:blue><u>Fitable</u></color>>
  + {abstract} execute(InvocationContext context, Object[] args) : Object
}
interface ConfigurableGenericable {
  + {abstract} name(String name) : <color:blue><u>ConfigurableGenericable</u></color>
  + {abstract} type(GenericableType type) : <color:blue><u>ConfigurableGenericable</u></color>
  + {abstract} method(Method method) : <color:blue><u>ConfigurableGenericable</u></color>
  + {abstract} route(String defaultFitableId) : <color:blue><u>ConfigurableGenericable</u></color>
  + {abstract} tags(Set<String> tags) : <color:blue><u>ConfigurableGenericable</u></color>
  + {abstract} appendTag(String tag) : <color:blue><u>ConfigurableGenericable</u></color>
  + {abstract} removeTag(String tag) : <color:blue><u>ConfigurableGenericable</u></color>
  + {abstract} clearTags() : <color:blue><u>ConfigurableGenericable</u></color>
  + {abstract} fitables(List<<color:blue><u>Fitable</u></color>> fitables) : <color:blue><u>ConfigurableGenericable</u></color>
  + {abstract} appendFitable(<color:blue><u>Fitable</u></color> fitable) : <color:blue><u>ConfigurableGenericable</u></color>
  + {abstract} clearFitables() : <color:blue><u>ConfigurableGenericable</u></color>
}
Genericable -u-|> GenericableMetadata
ConfigurableGenericable -u-|> Genericable
DefaultGenericableRepository "1" o--d--> "n" ConfigurableGenericable : contains
DefaultGenericableRepository -[hidden]> ConfigurableGenericable

interface GenericableFactory {
  + {abstract} create(String id, String version) : <color:blue><u>ConfigurableGenericable</u></color>
  + {abstract} create(UniqueGenericableId id) : <color:blue><u>ConfigurableGenericable</u></color>
}
class DefaultGenericableFactory {
  - dynamicRouter : <color:blue><u>DynamicRouter</u></color>
  + create(String id, String version) : <color:blue><u>ConfigurableGenericable</u></color>
  + create(UniqueGenericableId id) : <color:blue><u>ConfigurableGenericable</u></color>
}
DefaultGenericableFactory .u.|> GenericableFactory
GenericableFactory .u.> ConfigurableGenericable : create

interface FitableMetadata {
  + {abstract} id() : String
  + {abstract} version() : String
  + {abstract} aliases() : Aliases
  + {abstract} tags() : Tags
  + {abstract} degradationFitableId() : String
  + {abstract} genericable() : <color:blue><u>GenericableMetadata</u></color>
  + {abstract} toUniqueId() : UniqueFitableId
}
interface Fitable {
  + {abstract} genericable() : <color:blue><u>Genericable</u></color>
  + {abstract} execute(InvocationContext context, Object[] args) : Object
}
interface ConfigurableFitable {
  + {abstract} aliases(Set<String> aliases) : <color:blue><u>ConfigurableFitable</u></color>
  + {abstract} appendAlias(String alias) : <color:blue><u>ConfigurableFitable</u></color>
  + {abstract} removeAlias(String alias) : <color:blue><u>ConfigurableFitable</u></color>
  + {abstract} clearAliases() : <color:blue><u>ConfigurableFitable</u></color>
  + {abstract} tags(Set<String> tags) : <color:blue><u>ConfigurableFitable</u></color>
  + {abstract} appendTag(String tag) : <color:blue><u>ConfigurableFitable</u></color>
  + {abstract} removeTag(String tag) : <color:blue><u>ConfigurableFitable</u></color>
  + {abstract} clearTags() : <color:blue><u>ConfigurableFitable</u></color>
  + {abstract} degradationFitableId(String degradationFitableId) : <color:blue><u>ConfigurableFitable</u></color>
  + {abstract} genericable(<color:blue><u>Genericable</u></color> genericable) : <color:blue><u>ConfigurableFitable</u></color>
}
Fitable -u-|> FitableMetadata
ConfigurableFitable -u-|> Fitable
GenericableMetadata "1" o-r-> "n" FitableMetadata : contains
Genericable "1" o-r-> "n" Fitable : contains
ConfigurableGenericable -[hidden]> ConfigurableFitable

interface FitableFactory {
  + {abstract} create(String id, String version) : <color:blue><u>ConfigurableFitable</u></color>
  + {abstract} create(UniqueFitableId id) : <color:blue><u>ConfigurableFitable</u></color>
}
class DefaultFitableFactory {
  - container : BeanContainer
  - loadBalancer : <color:blue><u>LoadBalancer</u></color>
  - serializationService : SerializationService
  + create(String id, String version) : <color:blue><u>ConfigurableFitable</u></color>
  + create(UniqueFitableId id) : <color:blue><u>ConfigurableFitable</u></color>
}
DefaultFitableFactory .u.|> FitableFactory
FitableFactory .l.> ConfigurableFitable : create

class DefaultGenericable {
  - dynamicRouter : <color:blue><u>DynamicRouter</u></color>
  - retryableExecutor : <color:blue><u>GenericableExecutor</u></color>
  - degradableExecutor : <color:blue><u>GenericableExecutor</u></color>
  ..
  - id : String
  - version : String
  - name : String
  - type : GenericableType
  - method : ConfigurableGenericableMethod
  - route : ConfigurableRoute
  - tags : ConfigurableTags
  - fitables : List<<color:blue><u>Fitable</u></color>>
  - uniqueId : UniqueGenericableId
  __
  + id() : String
  + version() : String
  + name() : String
  + type() : GenericableType
  + method() : GenericableMethod
  + route() : Route
  + tags() : Tags
  + fitables() : List<<color:blue><u>Fitable</u></color>>
  + toUniqueId() : UniqueGenericableId
  + execute(InvocationContext context, Object[] args) : Object
  + name(String name) : <color:blue><u>ConfigurableGenericable</u></color>
  + type(GenericableType type) : <color:blue><u>ConfigurableGenericable</u></color>
  + method(Method method) : <color:blue><u>ConfigurableGenericable</u></color>
  + route(String defaultFitableId) : <color:blue><u>ConfigurableGenericable</u></color>
  + tags(Set<String> tags) : <color:blue><u>ConfigurableGenericable</u></color>
  + appendTag(String tag) : <color:blue><u>ConfigurableGenericable</u></color>
  + removeTag(String tag) : <color:blue><u>ConfigurableGenericable</u></color>
  + clearTags() : <color:blue><u>ConfigurableGenericable</u></color>
  + fitables(List<<color:blue><u>Fitable</u></color>> fitables) : <color:blue><u>ConfigurableGenericable</u></color>
  + appendFitable(<color:blue><u>Fitable</u></color> fitable) : <color:blue><u>ConfigurableGenericable</u></color>
  + clearFitables() : <color:blue><u>ConfigurableGenericable</u></color>
}
DefaultGenericable ..u..|> ConfigurableGenericable

interface DynamicRouter {
  + {abstract} route(<color:blue><u>Genericable</u></color> genericable, InvocationContext context, Object[] args) : List<<color:blue><u>Fitable</u></color>>
}
DefaultGenericable "1" o-l-> "1" DynamicRouter : contains

class DefaultDynamicRouter {
  + route(<color:blue><u>Genericable</u></color> genericable, InvocationContext context, Object[] args) : List<<color:blue><u>Fitable</u></color>>
}
DefaultDynamicRouter .u.|> DynamicRouter

interface GenericableExecutor {
  + {abstract} execute(List<<color:blue><u>Fitable</u></color>> fitables, InvocationContext context, Object[] args) : Object
}
DefaultGenericable "1" o--d--> "n" GenericableExecutor : contains

class MulticastGenericableExecutor {
  - executor : <color:blue><u>GenericableExecutor</u></color>
  + execute(List<<color:blue><u>Fitable</u></color>> fitables, InvocationContext context, Object[] args) : Object
}
MulticastGenericableExecutor .u.|> GenericableExecutor

abstract class AbstractUnicastGenericableExecutor {
  + execute(List<<color:blue><u>Fitable</u></color>> fitables, InvocationContext context, Object[] args) : Object
  # {abstract} execute(<color:blue><u>Fitable</u></color> fitable, InvocationContext context, Object[] args) : Object
}
AbstractUnicastGenericableExecutor .u..|> GenericableExecutor

class UnicastGenericableExecutor {
  # execute(<color:blue><u>Fitable</u></color> fitable, InvocationContext context, Object[] args) : Object
}
UnicastGenericableExecutor -u-|> AbstractUnicastGenericableExecutor

class DegradableGenericableExecutor {
  - executor : <color:blue><u>GenericableExecutor</u></color>
  # execute(<color:blue><u>Fitable</u></color> fitable, InvocationContext context, Object[] args) : Object
}
DegradableGenericableExecutor -u--|> AbstractUnicastGenericableExecutor

class RetryableGenericableExecutor {
  - executor : <color:blue><u>GenericableExecutor</u></color>
  # execute(<color:blue><u>Fitable</u></color> fitable, InvocationContext context, Object[] args) : Object
}
RetryableGenericableExecutor -u--|> AbstractUnicastGenericableExecutor

class DefaultFitable {
  - container : BeanContainer
  - loadBalancer : <color:blue><u>LoadBalancer</u></color>
  - localExecutorFactoryLoader : LazyLoader<<color:blue><u>LocalExecutorFactory</u></color>>
  - remoteExecutor : <color:blue><u>FitableExecutor</u></color>
  - multicastExecutor : <color:blue><u>FitableExecutor</u></color>
  ..
  - id : String
  - version : String
  - aliases : ConfigurableAliases
  - tags : ConfigurableTags
  - degradationFitableId : String
  - genericable : <color:blue><u>Genericable</u></color>
  - uniqueIdLoader : LazyLoader<UniqueFitableId>
  __
  + id() : String
  + version() : String
  + aliases() : Aliases
  + tags() : Tags
  + degradationFitableId() : String
  + genericable() : <color:blue><u>Genericable</u></color>
  + toUniqueId() : UniqueFitableId
  + execute(InvocationContext context, Object[] args) : Object
  + aliases(Set<String> aliases) : <color:blue><u>ConfigurableFitable</u></color>
  + appendAlias(String alias) : <color:blue><u>ConfigurableFitable</u></color>
  + removeAlias(String alias) : <color:blue><u>ConfigurableFitable</u></color>
  + clearAliases() : <color:blue><u>ConfigurableFitable</u></color>
  + tags(Set<String> tags) : <color:blue><u>ConfigurableFitable</u></color>
  + appendTag(String tag) : <color:blue><u>ConfigurableFitable</u></color>
  + removeTag(String tag) : <color:blue><u>ConfigurableFitable</u></color>
  + clearTags() : <color:blue><u>ConfigurableFitable</u></color>
  + degradationFitableId(String degradationFitableId) : <color:blue><u>ConfigurableFitable</u></color>
  + genericable(<color:blue><u>Genericable</u></color> genericable) : <color:blue><u>ConfigurableFitable</u></color>
}
DefaultFitable .u.|> ConfigurableFitable
DefaultGenericable -[hidden]> DefaultFitable

interface LoadBalancer {
  + {abstract} balance(<color:blue><u>Fitable</u></color> fitable, InvocationContext context, Object[] args) : List<Target>
}
DefaultFitable "1" o-r-> "1" LoadBalancer : contains

class DefaultLoadBalancer {
  + balance(<color:blue><u>Fitable</u></color> fitable, InvocationContext context, Object[] args) : List<Target>
}
DefaultLoadBalancer .u.|> LoadBalancer

interface FitableExecutor {
  + {abstract} execute(<color:blue><u>Fitable</u></color> fitable, List<Target> targets, InvocationContext context, Object[] args) : Object
}
DefaultFitable "1" o--d--> "n" FitableExecutor : contains
class MulticastFitableExecutor {
  - container : BeanContainer
  - remoteFitableExecutor : <color:blue><u>FitableExecutor</u></color>
  - localExecutorFactoryLoader : LazyLoader<<color:blue><u>LocalExecutorFactory</u></color>>
  + execute(<color:blue><u>Fitable</u></color> fitable, List<Target> targets, InvocationContext context, Object[] args) : Object
}
MulticastFitableExecutor .u.|> FitableExecutor

abstract class AbstractUnicastFitableExecutor {
  + execute(<color:blue><u>Fitable</u></color> fitable, List<Target> targets, InvocationContext context, Object[] args) : Object
  # {abstract} execute(<color:blue><u>Fitable</u></color> fitable, Target target, InvocationContext context, Object[] args) : Object
}
AbstractUnicastFitableExecutor .u..|> FitableExecutor

class LocalFitableExecutor {
  - id : UniqueFitableId
  - isMicro : boolean
  - metadata : BeanMetadata
  - targetLoader : LazyLoader<Object>
  - method : Method
  + id() : UniqueFitableId
  + metadata() : BeanMetadata
  + isMicro() : boolean
  + method() : Method
  + execute(Object[] args) : Object
  # execute(<color:blue><u>Fitable</u></color> fitable, Target target, InvocationContext context, Object[] args) : Object
}
LocalFitableExecutor -u--|> AbstractUnicastFitableExecutor

class RemoteFitableExecutor {
  - container : BeanContainer
  - serializationService : SerializationService
  # execute(<color:blue><u>Fitable</u></color> fitable, Target target, InvocationContext context, Object[] args) : Object
}
RemoteFitableExecutor -u-|> AbstractUnicastFitableExecutor

interface LocalExecutorRepository {
  + {abstract} registry() : LocalExecutorRepository.Registry
  + {abstract} name() : String
  + {abstract} executors() : Set<<color:blue><u>LocalExecutor</u></color>>
  + {abstract} executors(UniqueGenericableId id) : Set<<color:blue><u>LocalExecutor</u></color>>
  + {abstract} executor(UniqueFitableId id) : Optional<<color:blue><u>LocalExecutor</u></color>>
}
DefaultFitable "1" o--d--> "1" LocalExecutorRepository : contains
FitableExecutor -[hidden]> LocalExecutorRepository

class LocalExecutorRepositoryComposite {
  - rootRepository : <color:blue><u>LocalExecutorRepository</u></color>
  - pluginRepositories : List<<color:blue><u>LocalExecutorRepository</u></color>>
  - lock : ReadWriteLock
  + registry() : LocalExecutorRepository.Registry
  + name() : String
  + executors() : Set<<color:blue><u>LocalExecutor</u></color>>
  + executors(UniqueGenericableId id) : Set<<color:blue><u>LocalExecutor</u></color>>
  + executor(UniqueFitableId id) : Optional<<color:blue><u>LocalExecutor</u></color>>
  + install(<color:blue><u>LocalExecutorRepository</u></color> repository) : void
  + uninstall(<color:blue><u>LocalExecutorRepository</u></color> repository) : void
  + getRootRepository() : <color:blue><u>LocalExecutorRepository</u></color>
  + getChildren() : List<<color:blue><u>LocalExecutorRepository</u></color>>
  + getChild(String name) : Optional<<color:blue><u>LocalExecutorRepository</u></color>>
}
LocalExecutorRepositoryComposite .u.|> LocalExecutorRepository

class DefaultLocalExecutorRepository {
  - name : String
  - localExecutors : Map<String, Map<String, Map<String, Map<String, <color:blue><u>LocalExecutor</u></color>>>>>
  - lock : ReadWriteLock
  - registeredObservers : List<<color:blue><u>LocalExecutorRegisteredObserver</u></color>>
  + observeLocalExecutorRegistered(<color:blue><u>LocalExecutorRegisteredObserver</u></color> observer) : void
  + registry() : LocalExecutorRepository.Registry
  + name() : String
  + executors() : Set<<color:blue><u>LocalExecutor</u></color>>
  + executors(UniqueGenericableId id) : Set<<color:blue><u>LocalExecutor</u></color>>
  + executor(UniqueFitableId id) : Optional<<color:blue><u>LocalExecutor</u></color>>
}
DefaultLocalExecutorRepository .u..|> LocalExecutorRepository

interface LocalExecutor {
  + {abstract} id() : UniqueFitableId
  + {abstract} metadata() : BeanMetadata
  + {abstract} isMicro() : boolean
  + {abstract} method() : Method
  + {abstract} execute(Object[] args) : Object
}
DefaultLocalExecutorRepository "1" o--> "n" LocalExecutor : contains
LocalFitableExecutor .u.|> LocalExecutor

@enduml
```
