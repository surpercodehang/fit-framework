<div style="text-align: center;"><span style="font-size: 40px"><b>API设计</b></span></div>

# 异常体系

FIT 编程框架内的异常是能够跨语言跨进程传递的，核心异常有 3 个，分别是`FitException`、`DegradableException`
和`RetryableException`，他们的关系如下：

``` plantuml
@startuml
skinparam roundcorner 15
skinparam sequenceArrowThickness 2
skinparam defaultFontName Mono

hide empty members

class FitException {
- code : int
- properties : Map<String, String>
+ FitException(String message)
+ FitException(int code, String message)
+ FitException(int code, Throwable cause)
+ FitException(int code, String message, Throwable cause)
+ getCode() : int
+ getProperties() : Map<String, String>
+ setProperty(String key, String value) : void
+ setProperties(Map<String, String>) : void
+ associateGenericable(String genericableId) : void
+ associateFitable(String genericableId, String fitableId) : void
+ associatedGenericableId() : String
+ associatedFitableId() : String
}
class DegradableException {
+ DegradableException(String message)
+ DegradableException(int code, String message)
+ DegradableException(int code, Throwable cause)
+ DegradableException(int code, String message, Throwable cause)
+ degradationKey() : String
+ degradationKey(String key) : void
}
class RetryableException {
+ RetryableException(String message)
+ RetryableException(int code, String message)
+ RetryableException(int code, Throwable cause)
+ RetryableException(int code, String message, Throwable cause)
}

RetryableException -u-|> DegradableException
DegradableException -u-|> FitException
FitException -u-|> RuntimeException

@enduml
```

- `FitException`：FIT 系统中最基础的异常类，可以携带异常码、异常信息和自定义属性集。

> 建议所有服务如果需要显式地抛出异常，其异常一定继承自该异常，可以在跨进程调用中恢复异常类型和信息，但不包括调用堆栈。

- `DegradableException`：FIT 系统中可以降级的异常类，为`FitException`的子类，可以关联调用的`Genericable`的信息，其信息存储在自定义属性集中。

> 当系统识别到异常为可降级异常后，会搜索降级服务并进行调用。

- `RetryableException`：FIT 系统中可以重试的异常类，为`DegradableException`的子类。

> 当系统识别到异常为可重试异常后，在远程调用的场景下，会优先（相比降级）尝试重试，重试次数也是可以配置的。

## 异常码划分

- 非用户异常：`0x7F000000`~`0x7FFFFFFF`，由 FIT 系统保留。
- 用户异常：除上述保留异常码以外的部分，用户可自定义。

### 编程框架异常码

- `0x7F000000`~`0x7F0FFFFF`

| 异常码          |        异常类型         | 说明    |
|--------------|:-------------------:|-------|
| `0x7F000000` |    FitException     | 基础异常  |
| `0x7F000001` | DegradableException | 可降级异常 |
| `0x7F000002` | RetryableException  | 可重试异常 |

### 一般错误码

- `0x7F010000` ~ `0x7F01FFFF`

| 异常码          |                 异常类型                 | 说明           |
|--------------|:------------------------------------:|--------------|
| `0x7F010000` |   RouterRetrievalFailureException    | 获取动态路由器失败    |
| `0x7F010001` |     GenericableNotFoundException     | 无法找到服务       |
| `0x7F010002` | ClientLocalExecutorNotFoundException | 客户端获取本地执行器失败 |
| `0x7F010003` | ServerLocalExecutorNotFoundException | 服务端获取本地执行器失败 |
| `0x7F010007` |    AsyncTaskNotAcceptedException     | 异步任务未提交      |
| `0x7F010008` |      AsyncTaskNotFoundException      | 异步任务未找到      |
| `0x7F010009` |    AsyncTaskNotCompletedException    | 异步任务未完成      |
| `0x7F010010` |     AsyncTaskExecutionException      | 异步任务执行失败     |

#### 动态路由异常码

- `0x7F020000` ~ `0x7F02FFFF`

| 异常码          |           异常类型           | 说明                        |
|--------------|:------------------------:|---------------------------|
| `0x7F020000` | TooManyFitablesException | 动态路由后的实现超过 1 个，无法精确定位服务实现 |
| `0x7F020001` | FitableNotFoundException | 动态路由后没有找到任何实现，无法定位服务实现    |

#### 负载均衡异常码

- `0x7F030000` ~ `0x7F03FFFF`

| 异常码          |          异常类型           | 说明                       |
|--------------|:-----------------------:|--------------------------|
| `0x7F030000` | TargetNotFoundException | 负载均衡后没有找到任何地址，无法精确定位服务位置 |

#### 通信异常

- `0x7F040000` ~ `0x7F04FFFF`

| 异常码          |       异常类型       | 说明                     |
|--------------|:----------------:|------------------------|
| `0x7F040000` | ClientException  | 远程通信客户端发生的异常，导致信息发送失败  |
| `0x7F040001` | TimeoutException | 远程通信客户端发生超时异常，导致信息发送失败 |

#### 序列化异常

- `0x7F050000` ~ `0x7F05FFFF`

| 异常码          |          异常类型          | 说明         |
|--------------|:----------------------:|------------|
| `0x7F050000` | SerializationException | 序列化过程发生的异常 |

#### 响应式异常

- `0x7F060000` ~ `0x7F06FFFF`

| 异常码          |       异常类型        | 说明           |
|--------------|:-----------------:|--------------|
| `0x7F060000` | FlowableException | 响应式编程框架发生的异常 |

### 注册中心异常码

- `0x7F100000` ~ `0x7F1FFFFF`

### 配置中心异常码

- `0x7F200000` ~ `0x7F2FFFFF`

### 其他

- `0x7FF00000` ~ `0x7FFFFFFF`

| 异常码          |           异常类型            | 说明       |
|--------------|:-------------------------:|----------|
| `0x7FF00000` | CapacityOverflowException | 容量溢出发生异常 |

## 异常转换机制

每一个异常类上需要有一个 `@ErrorCode` 注解，表明该异常的异常码。 在异常序列化和反序列化过程中，需要将异常类型序列化为对应的异常码，再由异常码反序列化为对应的异常。

> 如果没有找到异常码，则统一序列化为 `FitException` 对应的 `0x7F000000`。

### 进程内异常直接透传

当插件为聚合部署时，异常仅在进程内传递，此时异常的传递机制和 Java 语言保持同步，没有特殊处理。

### 跨进程异常序列化及反序列化

当插件为离散部署时，异常需要跨进程透传，此时先进行序列化：

1. 找到异常的异常码，将其设置到 FIT 传输协议的元数据中；
2. 找到异常的消息内容，将其设置到 FIT 传输协议的元数据中；
3. 找到异常的自定义属性集（如有），将其设置到 FIT 传输协议的元数据中的 TLV 扩展字段内，对应的标签为 `1`。

其次再进行反序列化：

1. 从 FIT 传输协议的元数据中找到异常码，再找到对应的异常类，如果找不到，则统一反序列化为 `FitException`；
2. 从 FIT 传输协议的元数据中找到异常消息，将其设置到异常类中；
3. 从 FIT 传输协议的元数据中的 TLV 扩展字段内，找到标签为 `1` 的数据值，反序列后将其设置到 `FitException` 中。
