<div style="text-align: center;"><span style="font-size: 40px"><b>插件体系架构</b></span></div>

[TOC]

# 元数据

``` xml
<?xml version="1.0" encoding="utf-8"?>
<plugin>
  <group>fit.hakuna</group>
  <name>http-server</name>
  <version>1.0.0</version>
</plugin>
```

# ClassLoader结构

![avatar](https://cloudmodelingapi.tools.huawei.com/cloudmodelingdrawiosvr/d/svg/2AFE)

FIT体系结构中使用的ClassLoader主要有三个：
- `SharedClassLoader`，用以加载公共JAR包，如FIT对外API、Genericable及结构体定义等。
- `FrameworkClassLoader`，用以加载FIT框架实现及所使用的JAR包。
- `PluginClassLoader`，用以加载一个插件的实现及所使用的JAR包。

其中`SharedClassLoader`的`parent`为启动FIT运行时环境的`ClassLoader`，`FrameworkClassLoader`和`PluginClassLoader`的`parent`为`SharedClassLoader`。

> **ClassLoader隔离的目的**
>
> - **JAR隔离**，不同插件使用的三方包互不影响，避免插件间的兼容性问题。
> - **Bean隔离**，不同插件的Bean从各自的`ClassLoader`中扫描，在`ClassLoader`层面对扫描范围进行隔离。

# 插件目录结构

- *plugin-root*
  - **lib**
  - **third-party**
  - **shared**
    - **genericables**
    - **structures**

## `lib`目录

存储插件编译的JAR包，目录及子目录中的所有JAR将被加载到`PluginClassLoader`。

## `third-party`目录

存储插件所使用的三方包，目录及子目录中的所有JAR将被加载到`PluginClassLoader`。

## `shared`目录

存储公共JAR，目录及子目录中的所有JAR将被加载到`SharedClassLoader`。

### `shared/genericables`目录

存储插件使用或实现的`Genericable`的JAR包。

### `shared/structures`目录

存储插件使用或实现的`Genericable`中使用的结构体的JAR包。

# 整体类图

![avatar](https://cloudmodelingapi.tools.huawei.com/cloudmodelingdrawiosvr/d/34hm)

# Samples

## Plugin Metadata File

``` xml
<?xml version="1.0" encoding="utf-8"?>
<plugin name="my-plugin">
    <category>user</category>
    <level>5</level>
</plugin>
```