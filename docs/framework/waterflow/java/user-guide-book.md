本文主要介绍waterflow声明式语法，用以描述waterflow声明式语法中的关键字及其使用。
声明式语法主要包含如下几个类型的关键字，在余下的内容中，将展开做详细介绍：

* 流程创建
* 数据处理
* 流程控制
* 流程运行

## 流程创建

### Flows.create

**功能**：

创建一个新的处理流程对象，这里创建了流程的开始节点。

**方法签名**：

``` java
Start<D, D, D, ProcessFlow<D>> create()
Start<D, D, D, ProcessFlow<D>> create(FlowContextRepo repo, FlowContextMessenger messenger, FlowLocks locks)
```

**参数**：

`D`: 表示处理的数据类型
`repo`: 表示指定用于暂存流转数据上下文的对象
`messenger`: 表示用于流程间传递数据的对象
`locks`: 表示流程运行过程用于控制并发的锁对象管理

**返回值**：

流程的开始节点

**示例**：

``` java
ProcessFlow<TestData> flow = Flows.<TestData>create();
```

### Flows.mono

**功能**：

生成一个包含单一元素的流，通常用于仅需处理单个对象的场景。它创建一个流并立即提供该单一元素。

**方法签名**：

``` java
DataStart<D, D, D> mono(D data)
```

**参数**：

`D`: 表示处理的数据类型
`data`: 单一元素的实例

**返回值**：

数据前置的流对象

**示例**：

``` java
Flows.mono(new Person().name("Alice").age(30));
```

### Flows.flux

**功能**：

生成包含多个元素的流，用于处理一组数据或集合。它接收一个集合并将其作为流源传递给下游操作。

**方法签名**：

``` java
DataStart<D, D, D> flux(D... data)
```

**参数**：

`D`: 表示处理的数据类型
`data`: 包含多个元素的集合或数据集

**返回值**：

数据前置的流对象

**示例**：

``` java
String[] data = new String[]{"a", "b", "c"};
Flows.flux(data);
```

### Flows.source

**功能**：

使用自定义逻辑生成数据流。它接收一个数据发射器，用于产生数据并发送给流的消费者。

**方法签名**：

``` java
DataStart<D, D, D> source(Emitter<D, FlowSession> emitter)
```

**参数**：

`D`: 表示处理的数据类型
`emitter`: 负责发射数据的对象

**返回值**：

数据前置的流对象

**示例**：

``` java
Emitter<Integer, FlowSession> emitter = new Emitter<Integer, FlowSession>() {
    private EmitterListener<Integer, FlowSession> handler;

    @Override
    public void register(EmitterListener<Integer, FlowSession> handler) {
        this.handler = handler;
    }

    @Override
    public void emit(Integer data, FlowSession trance) {
        this.handler.handle(data, trance);
    }
};
Flows.source(emitter)
        .just(data -> System.out.println(data.toString())) // 输出：1 2
        .offer(); // 完成数据前置流的连接，后续通过emitter发射数据才能正常运行
emitter.emit(1);
emitter.emit(2);
```

### close

**功能**：

结束流程定义返回流对象，并可添加执行收集或最终处理逻辑回调。通常用于将处理结果输出或存储结果。

**方法签名**：

``` java
F close()
F close(Operators.Just<Callback<FlowContext<O>>> callback)
F close(Operators.Just<Callback<FlowContext<O>>> callback, Operators.ErrorHandler<Object> errHandler)
```

**参数**：

`F`: 推导出的流程对象类型
`callback`: 接收流程处理结果的回调
`errHandler`: 接收流程中的异常处理回调

**返回值**：

流对象

**示例一**：

``` java
Flows.<Integer>create()
        .map(i -> i + 1)
        .close(result -> System.out.println(result.get().getData())) // 输出：2
        .offer(1);
```

**示例二**：

``` java
Flows.<Integer>create()
        .map(i -> {
            if (i == 1) {
                throw new IllegalArgumentException("illegal param");
            }
            return i + 1;
        }).close(result -> System.out.println(result.get().getData()), (exception, retryable, contexts) -> {
            // exception为异常信息，contexts为发生异常的数据上下文信息，retryable为控制重试的对象
            System.out.println("ex: " + exception.getMessage() + ", data: " + contexts.get(0).getData()); // 输出：ex: illegal param, data: 1
            // 可以自己根据实际场景控制发起重试
            // contexts.forEach(context -> context.setStatus(READY));
            // retryable.retry(contexts);
        })
        .offer(1);
```

## 流程控制

### id

**功能**：

为当前操作节点分配唯一标识符，以便后续在 matchTo 等条件中引用。

**方法签名**：

``` java
State<O, D, I, F> id(String id)
```

**参数**：

`O`: 表示节点的输出数据类型
`D`: 表示节点对应流初始数据类型
`I`: 表示输入数据类型
`F`: 表示流的类型
`id`: 节点的标识符名称。

**返回值**：

当前操作的节点

**示例**：

``` java
// 为just(data -> System.out.println(data.toString()))节点指定id为justPrint
Flows.<Integer>create()
        .just(data -> System.out.println(data.toString())).id("justPrint");
```

### conditions

**功能**：

定义流程的分支，类似于流程图中的菱形节点。可以接`match`和`matchTo`关键字，二者顺序不受限制，可混合使用，最终通过`others`关键字终结分支的定义。

#### match

**功能**：

创建一个分支，在满足该分支条件时执行逻辑，在使用`conditions`开启条件分支后可使用。

**方法签名**：

``` java
MatchHappen<O, D, I, F> match(Operators.Whether<I> whether, Operators.BranchProcessor<O, D, I, F> processor)
```

**参数**：

`O`: 表示节点的输出数据类型
`D`: 表示节点对应流初始数据类型
`I`: 表示输入数据类型
`F`: 表示流的类型
`whether`: 判断分支是否成立的条件函数
`processor`: 处理符合条件的数据的操作逻辑

**返回值**：

条件节点对象

**示例**：

``` java
Flows.<Integer>create()
        .conditions()
        .match(i -> i < 5, node -> node.map(i -> "branch1"))
        .others(node -> node.map(i -> "branch else"))
        .just(System.out::println) // 输出：branch1
        .close()
        .offer(3);
```

**注意**：

> `conditions`后的第一个`match`语句的数据类型决定了该分支的最终数据类型。因此，后续的所有`match`及带参的`others`分支的数据类型必须与第一个`match`语句的数据类型保持一致。

#### matchTo

**功能**：

在满足条件时跳转到指定节点。

**方法签名**：

``` java
MatchToHappen<D, I, F> matchTo(Operators.Whether<I> whether, Operators.BranchToProcessor<D, I, F> navigator)
```

**参数**：

`D`: 表示节点对应流初始数据类型
`I`: 表示输入数据类型
`F`: 表示流的类型
`whether`: 判断分支是否成立的条件函数
`navigator`: 跳转到指定节点的逻辑

**返回值**：

条件节点对象

**示例**：

``` java
// 条件满足时，跳转到该流程中节点id为mapNode的节点继续执行
Flows.<Integer>create()
        .map(i -> i + 3).id("mapNode")
        .conditions()
        .matchTo(i -> i < 5, node -> node.to("mapNode"))
        .others(node -> node.map(i -> "branch else"))
        .just(System.out::println) // 输出：branch else
        .close()
        .offer(2);
```

#### to

**功能**：

指定要跳转到的目标节点，仅可用于matchTo中，其他地方使用将导致死循环。

**方法签名**：

``` java
void to(String id)
```

**参数**：

`id`: 目标节点的标识符。

#### others

**功能**：

定义默认操作，用于处理未满足其他条件的数据，可以看作`else`分支。

**方法签名**：

``` java
// 无参时，没有命中前序match条件的数据将被丢弃
State<O, D, O, F> others()
// 兜底处理没有命中前序match条件的数据 类似default
State<O, D, O, F> others(Operators.BranchProcessor<O, D, I, F> processor)
```

**参数**：

`O`: 表示节点的输入和输出数据类型
`D`: 表示节点对应流初始数据类型
`I`: 表示输入数据类型
`F`: 表示流的类型
`processor`: 处理默认数据的操作逻辑。

**返回值**：

将所有条件分支聚合的节点对象

**示例**：

``` javaFlows.create()
        .conditions()
        .match(i -> i < 5, node -> node.map(i -> "branch1"))
        .others(node -> node.map(i -> "branch else"))
        .just(System.out::println) // 输出：branch else
        .close()
        .offer(5);
```

### parallel

**功能**：

将数据并行处理，后面可以使用`fork`创建独立的分支，使每个分支可以独立操作数据流，最后可以通过`join`再合并结果。

**方法签名**：

``` java
Parallel<D, O, F> parallel()
```

**参数**：

`D`: 表示节点对应流初始数据类型
`O`: 表示节点的输入数据类型
`F`: 表示流的类型

**返回值**：

并行节点对象

**示例**：

``` java
Flows.<Integer>create()
        .parallel()
        .fork(node -> node.map(i -> i * 10)) // 10
        .fork(node -> node.map(i -> i * 100)) // 100
        .join(() -> 0, (acc, i) -> acc + i) // 0 + 10 + 100
        .just(System.out::println) // 输出：110
        .close()
        .offer(1);
```

#### fork

**功能**：

从并行节点中创建一个新的分支。可以在该分支上执行独立的操作逻辑。用于在并行操作中引入多个不同的路径。

**方法签名**：

``` java
Fork<O, D, I, F> fork(Operators.BranchProcessor<O, D, I, F> processor)
```

**参数**：

`D`: 表示节点对应流初始数据类型
`O`: 表示节点的输出数据类型
`I`: 表示输入数据类型
`F`: 表示流的类型
`processor`: 定义该分支操作的逻辑。

**返回值**：

并行节点对象

**示例**：

``` java
Flows.<Integer>create()
        .parallel()
        .fork(node -> node.map(i -> i * 10)) // 10
        .fork(node -> node.map(i -> i * 100)) // 100
        .join(() -> 0, (acc, i) -> acc + i) // 0 + 10 + 100
        .just(System.out::println) // 输出：110
        .close()
        .offer(1);
```

#### join

**功能**：

合并并行处理的多个分支结果。可以将累积结果和每个分支的结果组合成最终结果。

**方法签名**：

``` java
State<R, D, O, F> join(Supplier<R> init, Operators.Reduce<O, R> processor)
```

**参数**：

`D`: 表示节点对应流初始数据类型
`O`: 表示节点的输入数据类型
`R`: 聚合后输出的数据类型
`F`: 表示流的类型
`initial`: 合并结果的初始值
`processor`: 用于合并数据的逻辑函数

**返回值**：

聚合节点对象

**示例**：

``` java
Flows.<Integer>create()
        .parallel()
        .fork(node -> node.map(i -> i * 10)) // 10
        .fork(node -> node.map(i -> i * 100)) // 100
        .join(() -> 0, (acc, i) -> acc + i) // 0 + 10 + 100
        .just(System.out::println) // 输出：110
        .close()
        .offer(1);
```

### block

**功能**：

将流程阻塞，直到符合`BlockToken`条件的情况下恢复。通常用于控制数据流的执行节奏。

**方法签名**：

``` java
State<O, D, O, F> block(BlockToken<O> block)
```

**参数**：

`D`: 表示节点对应流初始数据类型
`O`: 表示节点的输入和输出数据类型
`F`: 表示流的类型
`token`: 控制阻塞和恢复逻辑的`BlockToken`对象。

**返回值**：

`block`节点对象

**示例**：

``` java
BlockToken<Integer> block = new BlockToken<Integer>() {
    @Override
    public boolean verify(Integer data) {
        // 这里可以对数据增加过滤条件，不满足的数据在后续resume时不会往后续节点流转
        return true;
    }
};
Flows.<Integer>create()
        .just(i -> System.out.println(i)) // 输出：1
        .block(block)
        .just(i -> System.out.println(i + 1)) // 不会执行
        .close()
        .offer(1);
```

### resume

**功能**：

恢复被阻塞的流程。在`BlockToken`中使用以继续流程的执行。

**方法签名**：

``` java
void resume()
```

**示例**：

``` java
BlockToken<Integer> block = new BlockToken<Integer>() {
    @Override
    public boolean verify(Integer data) {
        // 这里可以对数据增加过滤条件，不满足的数据在后续resume时不会往后续节点流转
        return true;
    }
};
ProcessFlow<Integer> flow = Flows.<Integer>create()
        .just(i -> System.out.println(i)) // 输出：1
        .block(block)
        .just(i -> System.out.println(i + 1)) // resume后输出：2
        .close()
flow.offer(1);
Thread.sleep(1000); // 这里为了方便示例，直接使用sleep方式等待数据执行到block节点
block.resume(); // 此时停留在block节点的数据才会往后续节点执行
```

## 数据处理

### just

**功能**：

用于在不改变流程流向的情况下对数据执行某种操作，通常是修改数据或进行计算。

**方法签名**：

``` java
State<O, D, O, F> just(Operators.Just<O> processor)
```

**参数**：

`D`: 表示节点对应流初始数据类型
`O`: 表示节点的输入数据类型
`processor`: 处理数据的操作逻辑

**返回值**：

当前`just`操作创建的节点对象

**示例**：

``` java
ProcessFlow<Integer> flow = Flows.<Integer>create()
        .just(i -> System.out.println(i)) // 输出：1
        .close()
        .offer(1);
```

### map

**功能**：

对数据进行转换或映射。通常传入一个映射函数，将数据转换为新的形态。

**方法签名**：

``` java
State<R, D, O, F> map(Operators.Map<O, R> processor)
// 包含上下文的map处理
State<R, D, O, F> map(Operators.ProcessMap<O, R> processor)
```

**参数**：

`D`: 表示节点对应流初始数据类型
`O`: 表示当前节点的输入数据类型
`R`: `map`后新产生的数据类型
`F`: 表示流的类型
`processor`: 数据映射或转换的逻辑

**返回值**：

当前`map`操作创建的节点对象

**示例一**：

``` java
Flows.<Integer>create()
        .map(i -> "num=" + i.toString())
        .just(i -> System.out.println(i)) // 输出：num=1
        .close()
        .offer(1);
```

**示例二**：

``` java
Flows.<Integer>create()
        .map((value, ctx) -> {
            Integer value1 = ctx.getState("userKey1"); // 从上下文获取流程运行实例的共享数据
            ctx.setState("newKey", 1); // 可以对上下文添加新的共享数据
            return value + value1;
        })
        .close();
```

### flatMap

**功能**：

将输入映射为多个输出流，并将这些流展平为一个单一的输出流。适合处理将集合或流展开并继续操作的情况。

**方法签名**：

``` java
State<R, D, O, F> flatMap(Operators.FlatMap<O, R> processor)
```

**参数**：

`D`: 表示节点对应流初始数据类型
`O`: 表示当前节点的输入数据类型
`R`: 新产生的数据类型
`F`: 表示流的类型
`processor`: 用于将单个输入映射为多个输出流的函数

**返回值**：

当前`flatMap`操作创建的节点对象

**示例**：

``` java
Flows.<Integer>create()
        .flatMap(i -> {
            Integer[] data = new Integer[i];
            for (int order = 0; order < data.length; ++order) {
                data[order] = order;
            }
            return Flows.flux(data);
        })
        .just(System.out::println) // 输出：0 1
        .close()
        .offer(2);
```

### produce

**功能**：

生成或转换数据的步骤，通常用于从输入生成一组输出。它接收一个函数，将数据集合转换为新的数据集合。

**方法签名**：

``` java
State<List<R>, D, ?, F> produce(Operators.Produce<O, R> processor)
```

**参数**：

`D`: 表示节点对应流初始数据类型
`O`: 表示当前节点的输入数据类型
`R`: 新产生的数据类型
`F`: 表示流的类型
`processor`: 将输入转换为输出的函数

**返回值**：

当前`produce`操作创建的节点对象

**示例**：

``` java
Flows.<Integer>create()
        .produce(list -> list.stream()
                .limit(2) // 只保留前两个数据
                .map(value -> "num=" + value.toString())
                .collect(Collectors.toList()))
        .just(System.out::println) // 输出：[num=1, num=2]
        .close()
        .offer(new Integer[] {1, 2, 3, 4, 5});
```

### buffer

**功能**：

将输入数据累积到一个集合中，当集合满足某些条件（例如大小或时间）时，集合会被发送到下一个处理阶段。类似于`window`，但所有数据会被收集到一个缓冲区内再处理。一般情况结合`window`使用。

**方法签名**：

``` java
State<List<O>, D, O, F> buffer()
```

**参数**：

`D`: 表示节点对应流初始数据类型
`O`: 表示当前节点的输入数据类型
`F`: 表示流的类型

> `buffer`后数据的输出类型变成`List<O>`

**返回值**：

当前`buffer`操作创建的节点对象

**示例**：

``` java
Flows.<Integer>create()
        .window(inputs -> inputs.size() == 3)
        .buffer()
        .just(System.out::println) // 输出：[1, 2, 3] [4, 5, 6]
        .close()
        .offer(new Integer[] {1, 2, 3, 4, 5, 6});
```

### keyBy

**功能**：

根据给定的键提取器将数据分组。通常在数据处理流程的早期使用，以确保后续操作针对相同的键进行。

**方法签名**：

``` java
State<Tuple<R, O>, D, O, F> keyBy(Operators.Map<O, R> keyGetter)
```

**参数**：

`D`: 表示节点对应流初始数据类型
`O`: 表示当前节点的输入数据类型
`F`: 表示流的类型
`R`: `key`的类型
`keyGetter`: 用于提取键的函数

> 该节点输出的数据类型为`Tuple<R, O>`, 其中`R`对应指定的键值, `first`为`key`, `second`为数据

**返回值**：

当前操作创建的节点对象

**示例**：

``` java
Flows.<Integer>create()
        .keyBy(i -> i) // 相同数据分组
        .reduce(() -> 0, (acc, data) -> { // 同组数据求和
            return acc + data.second();
        })
        .just(data -> System.out.println(data.toString())) // 输出：2 4
        .close()
        .offer(new Integer[] {1, 2, 1, 2});
```

### window <span id="window"></span>

**功能**：

定义一个窗口函数，用于将数据划分成窗口，以适应特定的处理逻辑。可以使用各种条件作为窗口边界，例如时间、大小等。

**方法签名**：

``` java
State<O, D, O, F> window(Operators.Window<O> window)
```

**参数**：

`D`: 表示节点对应流初始数据类型
`O`: 表示当前节点的输入数据类型，`window`节点的输入输出类型一致
`F`: 表示流的类型
`window`: 用于定义窗口边界的逻辑函数

**返回值**：

当前操作创建的节点对象

**示例**：

``` java
Flows.<Integer>create()
        .window(inputs -> inputs.size() == 2); // 通过大小控制窗口大小，每2个一个窗口
        .reduce(() -> 0, (acc, data) -> { // 窗口内求和
            return acc + data.second();
        })
        .just(data -> System.out.println(data.toString())) // 输出：3 3
        .close()
        .offer(new Integer[] {1, 2, 1, 2});
```

### reduce

**功能**：

对数据进行聚合。通过将累加器和当前数据传递给聚合函数，可实现将窗口或组的数据压缩为单一结果。

**方法签名**：

``` java
State<R, D, O, F> reduce(Supplier<R> init, Operators.Reduce<O, R> processor)
// processor中能够对上下文操作的方法
State<R, D, O, F> reduce(Supplier<R> init, Operators.ProcessReduce<O, R> processor)
```

**参数**：

`D`: 表示节点对应流初始数据类型
`O`: 表示当前节点的输入数据类型
`F`: 表示流的类型
`R`: 聚合结果的类型，对应该节点的输出数据类型
`initial`: 累加器的初始值
`processor`: 聚合函数，将累加器和当前数据聚合为新值

**返回值**：

当前操作创建的节点对象

**示例一**：

``` java
Flows.<Integer>create()
        .reduce(() -> 0, (acc, data) -> {
            return acc + data;
        })
        .just(data -> System.out.println(data.toString())) // 输出：10
        .close()
        .offer(new Integer[] {1, 2, 3, 4});
```

**示例二**：

结合上下文传递共享数据

``` java
FlowSession flowSession = new FlowSession();
// 设置上下文数据
flowSession.setState("key1", 10);
Flows.<Integer>create()
        .reduce(() -> 0, (acc, data, ctx) -> {
            return acc + data + (Integer) ctx.getState("key1"); // 消费上下文中的数据
        })
        .just(data -> System.out.println(data.toString())) // 输出：50
        .close()
        .offer(new Integer[] {1, 2, 3, 4}, flowSession);
```

> 1. 默认情况下`offer`一次的数据会作为一批数据进行reduce聚合为一个结果
> 2. 使用`window`或`keyBy`后会改变上面的默认行为，按照实际的规则来执行。对于`window`则是按照新的窗口中的数据作为一批数据，如果存在`keyBy`则会进一步将数据按照指定的键分批

## 流程运行

### offer

**功能**：

将数据传入流程并根据定义的流程节点进行处理。

**方法签名**：

``` java
// 通过mono、flux、source创建的flow无需参数
String offer()
// 通过create创建的flow通过offer提供单个数据
String offer(T data)
// 通过create创建的flow通过offer提供一批数据
String offer(T[] data)
// 通过create创建的flow通过offer将数据投递到指定节点，id为指定的节点id
String offer(String id, Object data)
// 通过create创建的flow通过offer提供单个数据，同时指定FlowSession信息。同一个session中的数据可以共享session中的数据和保证数据在一个session下
String offer(T data, FlowSession session)
```

**参数**：

`T`: 投递的数据类型
`data`: 投递的数据
`id`: 节点id, 通过`.id("xx")`指定的节点`id`
`session`: 流程运行实例的共享信息

```
手动指定session时，保证投递的数据在同一个session下
同一个session中的数据可以共享session中的数据
```

**返回值**：

该次运行数据的唯一`traceId`标识

**示例**：

``` java
ProcessFlow<Integer> flow = Flows.<Integer>create()
        .reduce(() -> 0, (acc, data) -> {
            return acc + data;
        })
        .just(data -> System.out.println(data.toString())) // 输出：3 10
        .close()：
flow.offer(new Integer[] {1, 2});
flow.offer(new Integer[] {1, 2, 3, 4});
```

**注意**：

> 1. `offer`时只投递数据，实际异步执行，如果需要同步等待结果，需要在对应节点的业务处理代码中使用信号量等机制来控制

## 关键定义

### FlowSession

1. 每个`FlowSession`对象都有一个唯一标识，通过`id()`方法可以获取。
2. `FlowSession`对象提供`session`级别的上下文操作，可在同一个`session`下共享数据。

#### 上下文操作

`FlowSession`实现了`StateContext`接口，提供上下文操作，可在同一个`session`下共享数据。

接口定义如下：

``` java
public interface StateContext {
    /**
     * 获取指定key的上下文数据
     *
     * @param key 指定key
     * @return 上下文数据
     */
    <R> R getState(String key);

    /**
     * 设置上下文数据
     *
     * @param key 指定key
     * @param value 待设置的上下文数据
     */
    void setState(String key, Object value);
}
```

### FlowContext

包含关键数据如下：

1. 用户`offer`的数据
2. 所属的`FlowSession`

> 1. 在一些能够操作`FlowContext`的节点上，可以通过`.getData()`获取用户自己的数据
> 2. `FlowContext`实现了`StateContext`接口，可以通过`getState`和`setState`操作上下文数据，zh而部分数据实际操作的是所属的`FlowSession`中的上下文数据

### Operators

数据处理器类型，对应节点中可传入的数据操作方法签名的种类，不同节点使用不同的`Operator`

#### Just

只对数据处理，并不产生新数据的过程。

使用节点：`just`

``` java
public interface Just<T> {
    /**
     * process
     *
     * @param input 节点的入参数据
     */
    void process(T input);
}
```

#### ProcessJust

只对数据处理，并不产生新数据的过程。该方法为入参支持携带`session KV`状态数据。

使用节点：`just`

```
public interface ProcessJust<T> {
    /**
     * process
     *
     * @param input 节点的入参数据
     * @param context KV 上下文，session级别共享的上下文数据
     */
    void process(T input, StateContext context);
}
```

#### Map

节点处理数据 1:1 过程，可返回新的数据类型。

使用节点：`map`

``` java
public interface Map<T, R> {
    /**
     * process
     *
     * @param input 节点的入参数据
     * @return R 节点返回的新的类型的数据
     */
    R process(T input);
}
```

#### ProcessMap

节点处理数据 1:1 过程，可返回新的数据类型。该方法为入参支持携带`session KV`状态数据。

使用节点：`map`

``` java
public interface ProcessMap<T, R> {
    /**
     * process
     *
     * @param input 节点的入参数据
     * @param context KV 上下文，session级别共享的上下文数据
     * @return R 节点返回的新的类型的数据
     */
    R process(T input, StateContext context);
}
```

#### FlatMap

节点处理数据1:M的过程。即进入一条数据，可以产生M个数据处理。

使用节点：`flatMap`

``` java
public interface FlatMap<T, R> {
    /**
     * process
     *
     * @param input 节点的入参数据
     * @return 数据前置的流，用于发射的新数据
     */
    DataStart<R, R, ?> process(T input);
}
```

#### Reduce

节点将M个数据聚合为一个的处理过程。

使用节点：`reduce`

``` java
public interface Reduce<T, R> {
    /**
     * process
     *
     * @param input 节点的入参数据
     * @param acc 累加的值
     * @return R 节点返回的新的类型的数据
     */
    R process(R acc, T input);
}
```

#### ProcessReduce

节点将M个数据聚合为一个的处理过程,。该方法为入参支持携带`session KV`状态数据。

使用节点：`reduce`

``` java
public interface ProcessReduce<T, R> {
    /**
     * process
     *
     * @param input 节点的入参数据
     * @param acc 累加的值
     * @param context KV 上下文，session级别共享的上下文数据
     * @return R
     */
    R process(R acc, T input, StateContext context);
}
```

#### Produce

节点将M个数据处理为N个数据的处理过程。

使用节点：`produce`

``` java
public interface Produce<T, R> {
    /**
     * process
     *
     * @param input 节点的入参数据
     * @return List<R> 节点返回的新的类型的数据
     */
    List<R> process(List<T> input);
}
```

#### Window

提供对`window`结束的判定，用于将数据按照窗口分批。

使用节点：[`window`](#window)

``` java
public interface Window<T> {
    /**
     * 判断窗口是否完结
     *
     * @param inputs 输入的数据，当前累积在该窗口的数据
     * @return 是否完结，返回true时表示该窗口的数据可以往后续节点发送
     */
    boolean fulfilled(List<T> inputs);
}
```

#### ErrorHandler

异常信息处理的接口定义。

使用节点：`close`、`error`

``` java
public interface ErrorHandler<T> {
    /**
     * handle
     *
     * @param exception 异常对象
     * @param retryable 重试操作对象
     * @param contexts 出错对应的数据
     */
    void handle(Exception exception, Retryable<T> retryable, List<FlowContext<T>> contexts);
}
```

#### Whether

条件判定的接口定义。

使用节点：`match`、`matchTo`

``` java
public interface Whether<T> {
    /**
     * is
     *
     * @param input 输入的数据
     * @return boolean 是否通过判定
     */
    boolean is(T input);
}
```

#### BranchProcessor

分支处理的接口定义。

使用节点：`match`、`matchTo`、`fork`

``` java
public interface BranchProcessor<O, D, I, F extends Flow<D>> {
    /**
     * process
     *
     * @param node 当前分支的节点操作，可基于该节点继续新增用户的处理逻辑节点
     * @return State<O, D, ?, F> 该分支中新建的最后一个节点信息
     */
    State<O, D, ?, F> process(State<I, D, I, F> node);
}

// matchTo使用，用于将数据流向其它节点
public interface BranchToProcessor<D, I, F extends Flow<D>> {
    /**
     * process
     *
     * @param node 当前分支的节点操作，可基于该节点的`to`方法控制后续流向的节点
     */
    void process(State<I, D, I, F> node);
}
```

> 1. 分支处理中，需要用户自己定义自己的一系列处理逻辑，所以这里的入参是一个节点对象。这样就可以基于该节点继续追加各种操作节点。
> 2. 对于`matchTo`的场景，需要用户在基于`node`创建的最后一个节点上通过`.to("nodeId")`的方式控制数据流向该流程中的节点。
