<div style="text-align: center;"><span style="font-size: 40px"><b>FIT事务管理</b></span></div>

[TOC]

# 事务管理程序

事务管理程序，为调用方提供获取事务的入口。调用方可根据事务的元数据获取一个事务实例，如下图所示。

![avatar](https://cloudmodelingapi.tools.huawei.com/cloudmodelingdrawiosvr/d/2Xco)

其中：
- `TransactionManager`，管理整个事务体系，主要提供获取事务的能力。
- `TransactionMetadata`，事务的元数据定义信息，如事务的隔离级别、传播策略、超时时间等。
- `Transaction`，定义事务的属性和行为，例如事务是否处于活动状态，提交、回滚事务等。

# 传播控制

事务的传播控制用以定义如何使用事务，如指定在事务中执行、不在事务中执行、在嵌套事务中执行等。

| 传播方式 | 已存在事务 | 不存在事务 |
|---|---|---|
|`REQUIRED`|加入已有事务|创建新事务|
|`SUPPORTS`|加入已有事务|无事务执行|
|`MANDATORY`|加入已有事务|抛出异常|
|`REQUIRES_NEW`|创建新事务|创建新事务|
|`NOT_SUPPORTED`|无事务执行|无事务执行|
|`NEVER`|抛出异常|无事务执行|
|`NESTED`|创建嵌套事务|创建新事务|

# 详细设计

## 核心类图

![avatar](https://cloudmodelingapi.tools.huawei.com/cloudmodelingdrawiosvr/d/2Xd9)

## 事务传播策略

### `REQUIRED`

表示需要一个事务。如果当前未在事务中，那么则启动一个事务，否则加入到现有事务。

# 遗留问题

## 1. 操作不生效

`InheritedTransaction`在回滚时，实际并未发生回滚行为，导致事务中的内容最终会被提交。

应考虑使用`Savepoint`进行替代。

## 2. 数据已生效但提交失败

`IndependentTransaction`中，在实际提交事务后（已执行`Connection.commit()`方法），若在执行`Connection.setAutoCommit(boolean)`、`Connection.close()`方法失败时，此时事务中的SQL已提交到数据库，但是事务最终会失败，出现事务失败但数据已提交的情况。