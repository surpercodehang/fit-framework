<div style="text-align: center;"><span style="font-size: 40px"><b>FIT工具集</b></span></div>

[TOC]

# 通用树

提供通用的树形结构定义。

如下图所示，通用树的主体部分包含树（`Tree`）、树节点（`TreeNode`）及树节点集合（`TreeNodeCollection`）三部分内容。

![avatar](https://cloudmodelingapi.tools.huawei.com/cloudmodelingdrawiosvr/d/2QKO)

其中：

- `Tree`，是通用树组件直接对外呈现的接口，调用方可通过该接口中定义的`create`静态方法创建通用树实例。
- `TreeNode`，表示树中的节点，可通过`get`、`set`方法来获取或设置节点表述的值。
- `TreeNodeCollection`，表示`TreeNode`的集合，在`Tree`和`TreeNode`中使用。当在树中使用时，表示树中根节点的集合；在`TreeNode`中使用时，表示节点中包含子节点的集合。

在整体上，`Tree`中通过`roots`方法来定义树中包含的所有根节点，其中每个根节点是一个`TreeNode`实例。`TreeNode`本身通过`parent`和`children`方法来形成最终的树形结构。

同时，`Tree`中封装了一系列方法，用以提供基于路径的操作方法，以便于调用方使用。

## 示例

``` java
import modelengine.fitframework.model.tree.Tree;
import modelengine.fitframework.model.tree.TreeNode;

public class Demo {
    public static void main(String[] args) {
        Tree<Boolean> tree = Tree.create('.');
        tree.put("modelengine.fit", true);
        tree.put("modelengine.fitframework.demo", true);
        print(tree);
        // modelengine=null
        //   fit=true
        //   fitframework=null
        //     demo=true
    }

    private static void print(Tree<?> tree) {
        tree.dfs(node -> {
            TreeNode<?> parent = node.parent();
            while (parent != null) {
                System.out.print("  ");
                parent = parent.parent();
            }
            System.out.printf("%s=%s%n", node.name(), node.get());
        });
    }
}
```

# 资源

`Resource`用以定义一个资源，这个资源可以用来描述`ClassPath`中包含的嵌入资源，也可以用以描述一个外部资源。

``` plantuml
hide empty members

skinparam backgroundColor #EEEBDC
skinparam roundcorner 20
skinparam sequenceArrowThickness 2
skinparam ClassFontName Consolas

interface ResourceTree {
+ {abstract} location() : URL
+ {abstract} roots() : <color:blue><u>ResourceTree$NodeCollection</u></color>
+ {abstract} traverse(Consumer<<color:blue><u>ResourceTree$FileNode</u></color>>) : void
+ {abstract} lookup(String[]) : <color:blue><u>ResourceTree$Node</u></color>
+ {static} fromUrl(URL) : <color:blue><u>ResourceTree</u></color>
+ {static} fromJar(<color:blue><u>Jar</u></color>) : <color:blue><u>ResourceTree</u></color>
}

interface ResourceTree$NodeCollection {
+ {abstract} count() : int
+ {abstract} get(String) : <color:blue><u>ResourceTree:Node</u></color>
+ {abstract} get(String, Class) : <color:blue><u>ResourceTree:Node</u></color>
+ {abstract} get(int) : <color:blue><u>ResourceTree:Node</u></color>
+ {abstract} stream() : Stream<<color:blue><u>ResourceTree:Node</u></color>>
+ {abstract} toList() : List<<color:blue><u>ResourceTree:Node</u></color>>
}

ResourceTree *-right-> ResourceTree$NodeCollection : has

interface ResourceTree$Node {
+ {abstract} name() : String
+ {abstract} path() : String
+ {abstract} directory() : <color:blue><u>ResourceTree$DirectoryNode</u></color>
}

ResourceTree$NodeCollection *-right-> ResourceTree$Node : contains

interface ResourceTree$DirectoryNode {
+ {abstract} children() : <color:blue><u>ResourceTree:NodeCollection</u></color>
+ {abstract} traverse(Consumer<<color:blue><u>ResourceTree:Node</u></color>>) : void
+ {abstract} traverse(Predicate<<color:blue><u>ResourceTree:Node</u></color>>, Consumer<<color:blue><u>ResourceTree:Node</u></color>>) : void
}

ResourceTree$DirectoryNode -up-|> ResourceTree$Node
ResourceTree$DirectoryNode *-up-> ResourceTree$NodeCollection : has
ResourceTree$Node ..> ResourceTree$DirectoryNode : refers

interface ResourceTree$FileNode {
+ {abstract} url() : URL
+ {abstract} read() : InputStream
}

ResourceTree$FileNode -up-|> ResourceTree$Node

interface ClassPath {
+ {abstract} loader() : ClassLoader
+ {abstract} url() : URL
+ {abstract} resources() : Tree
+ {abstract} resolve(Pattern<String>) : List<<color:blue><u>ClassPathResource</u></color>>
+ {static} fromClassLoader(ClassLoader) : List<<color:blue><u>ClassPath</u></color>>
}
note top on link
A <color:blue><b>ClassPath</b></color> stores its contained
<color:blue><b>Resource</b></color>s in a tree structure that
can be visited by <color:blue><b>resources()</b></color>
method. Any <color:blue><b>Resource</b></color> is stored
in property <color:blue><b>tag()</b></color> of <color:blue><b>TreeNode</b></color>.
The property may returns null if no
<color:blue><b>Resource</b></color> related to the node.
end note

abstract AbstractClassPath {
- loader : ClassLoader
- url : URL
+ AbstractClassPath(ClassLoader, URL)
+ loader() : ClassLoader
+ url() : URL
+ resolve(Pattern<String>) : List<<color:blue><u>ClassPathResource</u></color>>
+ equals(Object) : boolean
+ hashCode() : int
+ toString() : String
}
AbstractClassPath .up.|> ClassPath

class DirectoryClassPath {
- root : File
- resources : Tree
+ DirectoryClassPath(ClassLoader, URL, File)
+ resources() : Tree
}
DirectoryClassPath -up-|> AbstractClassPath

class JarClassPath {
- resources : Tree
+ JarClassPath(ClassLoader, URL, JarLocation)
+ resources() : Tree
}
JarClassPath -up-|> AbstractClassPath

interface ResourceResolver {
+ {abstract} resolve(String) : <color:blue><u>Resource</u></color>[]
}

class ClassLoaderResourceResolver {
- loader : ClassLoader
- classPaths : List<<color:blue><u>ClassPath</u></color>>
+ ClassPathResourceResolver(ClassLoader)
+ resolve(String) : <color:blue><u>Resource</u></color>[]
}
ClassLoaderResourceResolver .up.|> ResourceResolver
ClassLoaderResourceResolver *-> ClassPath
note top on link
A <color:blue><b>ResourceResolver</b></color> resolve
<color:blue><b>Resource</b></color>s by <color:blue><b>ClassPath</b></color>s.
end note
```

```text


interface ClassScanner<<(F,#BF00BF)>> {
+ {abstract} scan(Consumer<<color:blue><u>ClassDeclaration</u></color>>) : void
+ {static} custom() : <color:blue><u>ClassScanner$Builder</u></color>
}

interface ClassScanner$Builder {
+ {abstract} loader(ClassLoader) : <color:blue><u>ClassScanner$Builder</u></color>
+ {abstract} basePackages(String[]) : <color:blue><u>ClassScanner$Builder</u></color>
+ {abstract} filter(Predicate<<color:blue><u>ClassDeclaration</u></color>>) : <color:blue><u>ClassScanner$Builder</u></color>
+ {abstract} build() : <color:blue><u>ClassScanner</u></color>
}
ClassScanner$Builder .right.> ClassScanner : instantiate

class DefaultClassScanner {
- classLoader : ClassLoader
- basePackages : Set<String>
- filter : Predicate<<color:blue><u>ClassDeclaration</u></color>>
+ DefaultClassScanner(ClassLoader, String[], Predicate<<color:blue><u>ClassDeclaration</u></color>>)
+ scan(Consumer<<color:blue><u>ClassDeclaration</u></color>>) : void
}
DefaultClassScanner .up.|> ClassScanner
DefaultClassScanner *-down-> ClassPath
```

## 通配符匹配

### 问题描述

资源匹配过程通过给定的字符串判断指定资源是否符合要求。

#### 概念描述

- *资源路径*，用以描述一个资源的全限定名
- *路径节点*，*资源路径*以分隔符 `/` 可分隔成为的若干个部分，其中每个部分为一个*路径节点*

资源匹配过程可通过*通配符*匹配多个资源。支持的通配符有：

- `*`，表示在*路径节点*名称中匹配 0 个或多个任意字符
- `**`，表示在*资源路径*中匹配 0 个或多个任意*路径节点*

> 当存在连续多个 `*` 时，多余的 `*` 将被折叠。
>
> 当一个路径节点中包含连续的多个 `*` 时，若这个节点的所有字符都为 `*`，那么则认为其需要匹配任意节点。否则匹配任意字符。
>
> 例如：
>
> - `****` 将被折叠为 `**`
> - `x***` 将被折叠为 `x*`

### 算法实现

![avatar](https://cloudmodelingapi.tools.huawei.com/cloudmodelingdrawiosvr/d/2LjE)

### 抽象建模

![avatar](https://cloudmodelingapi.tools.huawei.com/cloudmodelingdrawiosvr/d/svg/2Mwl)

### 样例

- **字符串模式匹配**

``` java
import modelengine.fitframework.util.wildcard.CharSequencePattern;
import modelengine.fitframework.util.wildcard.Pattern;

public class Main {
    public static void main(String[] args) {
        // pattern 使用 ? 匹配单个字符，使用 * 匹配多个字符
        CharSequencePattern pattern = Pattern.forCharSequence("h*l?");
        boolean ret = pattern.matches("hello");
        System.out.println(ret); // 输出：true
    }
}
```

- **路径模式匹配**

``` java
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.wildcard.Pattern;
import modelengine.fitframework.util.wildcard.SymbolSequence;
import modelengine.fitframework.util.wildcard.SymbolType;

public class Main {
    public static void main(String[] args) {
        Pattern<String> pattern = Pattern.custom()
                .pattern(SymbolSequence.fromArray(new String[] {"opt", "**", "fit.yaml"}))
                .symbol()
                .classifier(symbol -> {
                    if ("**".equals(symbol)) {
                        return SymbolType.MULTIPLE_WILDCARD;
                    } else {
                        return SymbolType.NORMAL;
                    }
                }) // 当为 ** 时，匹配任意数量的路径
                .symbol()
                .matcher(StringUtils::equalsIgnoreCase) // 忽略大小写匹配
                .build();
        SymbolSequence<String> value = SymbolSequence.fromArray(new String[] {"opt", "fit", "framework", "FIT.yaml"});
        boolean ret = pattern.matches(value);
        System.out.println(ret); // 输出：true
    }
}
```

- **树形结构匹配**

``` java
import modelengine.fitframework.util.wildcard.Pattern;
import modelengine.fitframework.util.wildcard.SymbolSequence;
import modelengine.fitframework.util.wildcard.SymbolType;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) throws IOException {
        Pattern<String> pattern = Pattern.custom()
                .pattern(SymbolSequence.fromArray(new String[] {"opt", "**", "fit.yaml"}))
                .symbol()
                .classifier(symbol -> {
                    if ("**".equals(symbol)) {
                        return SymbolType.MULTIPLE_WILDCARD;
                    } else {
                        return SymbolType.NORMAL;
                    }
                }) // 当为 ** 时，匹配任意数量的路径
                .symbol()
                .matcher(StringUtils::equalsIgnoreCase) // 忽略大小写匹配
                .build();
        List<File> roots = Collections.singletonList(new File("/opt").getCanonicalFile());
        Function<File, List<File>> childrenMapper =
                file -> Optional.ofNullable(file.listFiles()).map(Arrays::asList).orElse(Collections.emptyList());
        Function<File, String> symbolMapper = File::getName;
        List<File> matched = pattern.match(roots, childrenMapper, symbolMapper);
        for (File file : matched) {
            System.out.println(file.getPath());
        }
    }
}
```

# Jar In Jar

JRE在解析URL时，需要去获取对应协议的`URLStreamHandler`实例，来对URL进行处理。在扩展Jar In Jar能力时，我们需要重写JRE对`jar`协议的支持。

**即实现自己的针对`jar`协议的`URLStreamHandler`，并使其在URL解析过程中生效。**

扩展URL支持的协议有多种方式，但是较为通用的方法为通过`java.protocol.handler.pkgs`系统属性，主要的原因是这种扩展方式被支持的较早，所支持的JDK版本更多。

在通过`java.protocol.handler.pkgs`系统属性来获取`URLStreamHandler`时，主要过程为：
- 获取`java.protocol.handler.pkgs`系统属性的值，并使用`|`字符进行分割。
- 分割后的每个元素被认为是一个`package`名，在该包下的协议名称的子包中查找名为`Handler`的类型
- 通过默认构造方法实例化`Handler`类，并将其强转为`URLStreamHandler`使用

JDK预置的`URLStreamHandler`都定义在`sun.net.www.protocol`包下，以其作为参考，我们将使用`modelengine.fitframework.protocol`包作为对URL协议的支持。

因此，我们将通过`modelengine.fitframework.protocol.jar.Handler`类型，提供对`jar`协议的扩展。

## 架构优化

在进行`ResourceResolver`的性能优化时，发现了一些问题：

### Jar In Jar性能优化

Jar In Jar解析过程耗时较多，其原因集中在读取JAR文件数据的过程中。

原始的实现逻辑中最终通过同一个`DataBlock`来对文件的数据进行读取，为了应对并发访问的问题，在每次读取时都会对`seek`和`read`操作进行加锁，以按预期读取到文件中指定位置的数据。在此过程中，即使只有单线程读取，也会在每次读取时进行**入锁**和**出锁**操作，性能较差。

为了解决该问题，决定在每次需要对一个内容进行数据读取时，使用独立的对象进行，以避免并发访问过程的相互影响，从而避免并发控制导致的性能损耗。

同时，因为不同内容的读取过程使用了独立了IO对象，因此也就没有必要在根JAR中保持一个用以读取的`DataBlock`来承载所有内容的读取工作，因此同时将`Jar`和`JarEntry`进行托管处理，消除其中所持有的非托管资源。将非托管资源的建立汇聚到相应的实例方法调用中。

优化后的类图如下：

``` plantuml
hide empty members

skinparam backgroundColor #EEEBDC
skinparam roundcorner 20
skinparam sequenceArrowThickness 2
skinparam ClassFontName Consolas

interface Jar {
+ {abstract} url() : URL
+ {abstract} permission() : Permission
+ {abstract} entries() : <color:blue><u>Jar$EntryCollection</u></color>
+ {abstract} comment() : String
+ {static} load(File) : <color:blue><u>Jar</u></color>
}

interface Jar$EntryCollection {
+ {abstract} size() : int
+ {abstract} get(int) : <color:blue><u>Jar$Entry</u></color>
+ {abstract} get(String) : <color:blue><u>Jar$Entry</u></color>
}
Jar *-right-> Jar$EntryCollection

interface Jar$Entry {
+ {abstract} jar() : Jar
+ {abstract} name() : String
+ {abstract} methodOfCompression() : <color:blue><u>CompressionMethod</u></color>
+ {abstract} crc32() : int
+ {abstract} offset() : long
+ {abstract} sizeOfCompressed() : long
+ {abstract} sizeOfUncompressed() : long
+ {abstract} timeOfLastModification() : Date
+ {abstract} extra() : byte[]
+ {abstract} comment() : String
+ {abstract} directory() : boolean
+ {abstract} asJar() : <color:blue><u>NestedJar</u></color>
}
Jar$EntryCollection *-right-> Jar$Entry

enum CompressionMethod {
+ {static} NONE
+ {static} DEFLATED
}
Jar$Entry .right.> CompressionMethod : with

interface NestedJar {
+ {abstract} entry() : <color:blue><u>Jar$Entry</u></color>
+ {abstract} parent() : <color:blue><u>Jar</u></color>
}
NestedJar -right-|> Jar

abstract AbstractJar {
- location : <color:blue><u>DataLocation</u></color>
- comment : String
- entries : <color:blue><u>DefaultJarEntryCollection</u></color>
+ AbstractJar(<color:blue><u>DataLocation</u></color>)
  location() : <color:blue><u>DataLocation</u></color>
+ file() : File
+ entries() : <color:blue><u>Jar$EntryCollection</u></color>
+ comment() : String
+ url() : URL
}
AbstractJar .up.|> Jar

package ".ZIP File Format Specification" as ZIP {
class EndOfCentralDirectory {
- offsetOfCentralDirectory : long
- sizeOfCentralDirectory : long
- numberOfEntries : long
- commentOfZipFile : String
- EndOfCentralDirectory(long, long, long, String)
+ offsetOfCentralDirectory() : long
+ sizeOfCentralDirectory() : long
+ numberOfEntries() : long
+ commentOfZipFile() : String
+ {static} load(FileRandomAccessor) : <color:blue><u>EndOfCentralDirectory</u></color>
}
AbstractJar .down.> EndOfCentralDirectory : use

class CentralDirectory {
- access : FileRandomAccessor
- offsetOfCentralDirectory : long
- sizeOfCentralDirectory : long
- numberOfEntries : int
- CentralDirectory(FileRandomAccessor, long, long, int)
+ enumerator() : Enumerator<<color:blue><u>CentralDirectoryFileHeader</u></color>>
+ {static} of(<color:blue><u>EndOfCentralDirectory</u></color>, FileRandomAccessor) : <color:blue><u>CentralDirectory></u></color>
}
AbstractJar .down.> CentralDirectory : use

class CentralDirectoryFileHeader {
- sizeOfCompressed : long
- sizeOfUncompressed : long
- offsetOfLocalHeader : long
- methodOfCompression : int
- crc32 : int
- filename : String
- extra : byte[]
- comment : String
- size : int
+ CentralDirectoryFileHeader(long, long, long, int,\n    int, MsDosDateTime, String, byte[], String, int)
+ sizeOfCompressed() : long
+ sizeOfUncompressed() : long
+ offsetOfLocalHeader() : long
+ methodOfCompression() : int
+ crc32() : int
+ filename() : String
+ extra() : byte[]
+ comment() : String
+ size() : int
}
AbstractJar .down.> CentralDirectoryFileHeader : use
CentralDirectory .> CentralDirectoryFileHeader : enumerate

class LocalFileHeader {
- size : long
- LocalFileHeader(long)
+ size() : long
+ {static} load(FileRandomAccessor, long) : <color:blue><u>LocalFileHeader</u></color>
}
AbstractJar .down.> LocalFileHeader : use
EndOfCentralDirectory -[hidden]> CentralDirectory
}

class RootJar {
- permission : Permission
+ RootJar(File)
+ permission() : Permission
}
RootJar -left-|> AbstractJar

class NestedFileJar {
- entry : <color:blue><u>Jar$Entry</u></color>
+ NestedFileJar(<color:blue><u>DefaultJarEntry</u></color>)
+ permission() : Permission
+ entry() : <color:blue><u>Jar$Entry</u></color>
+ parent() : <color:blue><u>Jar</u></color>
}
NestedFileJar -right-|> AbstractJar
NestedFileJar .up.|> NestedJar

class NestedDirectoryJar {
- entry : <color:blue><u>Jar$Entry</u></color>
- entries : <color:blue><u>DefaultJarEntryCollection</u></color>
+ NestedDirectoryJar(<color:blue><u>Jar$Entry</u></color>)
+ entry() : <color:blue><u>Jar$Entry</u></color>
+ parent() : <color:blue><u>Jar</u></color>
+ file() : File
+ permission() : Permission
+ entries() : <color:blue><u>Jar$EntryCollection</u></color>
+ comment() : String
+ url() : URL
+ toString() : String
}
NestedDirectoryJar .up.|> NestedJar
NestedDirectoryJar -[hidden]> NestedFileJar

class DefaultJarEntryCollection {
- keys : List<String>
- entries : Map<String, <color:blue><u>Jar$Entry</u></color>>
+ DefaultJarEntryCollection(int)
  add(<color:blue><u>Jar$Entry</u></color>) : void
+ size() : int
+ get(int) : <color:blue><u>Jar$Entry</u></color>
+ get(String) : <color:blue><u>Jar$Entry</u></color>
+ stream() : Stream<<color:blue><u>Jar$Entry</u></color>>
+ iterator() : Iterator<<color:blue><u>Jar$Entry</u></color>>
+ toString() : String
}
DefaultJarEntryCollection .down.|> Jar$EntryCollection

class DefaultJarEntry {
- jar : <color:blue><u>Jar</u></color>
- name : String
- compressionMethod : <color:blue><u>CompressionMethod</u></color>
- crc32 : int
- sizeOfCompressed : long
- sizeOfUncompressed : long
- lastModificationTime : Date
- extra : byte[]
- comment : String
- offset : long
- location: <color:blue><u>DataLocation</u></color>
+ DefaultJarEntry(Jar, String, CompressionMethod, int,\n    long, long, Date, byte[], String, long, DataLocation)
  location() : <color:blue><u>DataLocation</u></color>
+ jar() : <color:blue><u>Jar</u></color>
+ name() : String
+ methodOfCompression() : <color:blue><u>CompressionMethod</u></color>
+ crc32() : int
+ offset() : long
+ sizeOfCompressed() : long
+ sizeOfUncompressed() : long
+ timeOfLastModification() : Date
+ extra() : byte[]
+ comment() : String
+ directory() : boolean
+ asJar() : <color:blue><u>NestedJar</u></color>
}
DefaultJarEntry .down.|> Jar$Entry
note right of DefaultJarEntry::"asJar()"
When <color:blue><i>directory()</i></color> is true, then return
an instance of <color:blue><b>NestedDirectoryJar</b></color>.
Otherwise return an instance of
<color:blue><b>NestedFileJar</b></color>.
end note
```

## JVM能力扩展

### 类型解析

### 类型扫描

``` plantuml
hide empty members

skinparam backgroundColor #EEEBDC
skinparam roundcorner 20
skinparam sequenceArrowThickness 2
skinparam ClassFontName Consolas

interface ClassScanner {
+ {abstract} scan(String[]) : void
+ {static} create(Consumer<<color:blue><u>ClassDeclaration</u></color>>) : <color:blue><u>ClassScanner</u></color>
}

interface ClassScanner$Callback {
+ {abstract} onClassDetected(<color:blue><u>ClassScanner</u></color>, <color:blue><u>ClassDeclaration</u></color>) : void

}
ClassScanner .right.> ClassScanner$Callback : callback

interface ClassScanner$Builder {
+ {abstract} addClassLoader(ClassLoader) : <color:blue><u>ClassScanner$Builder</u></color>
+ {abstract} addCallback(<color:blue><u>ClassScanner$Callback</u></color>) : <color:blue><u>ClassScanner$Builder</u></color>
+ {abstract} build() : <color:blue><u>ClassScanner</u></color>
}
ClassScanner$Builder .right.> ClassScanner : create

class DefaultClassScanner {
- scanners : Map<URL, <color:blue><u>ClassPathScanner</u></color>>
+ DefaultClassScanner(ClassLoader[], <color:blue><u>ClassScanner$Callback</u></color>)
+ scan(String[]) : void
}
DefaultClassScanner .up.|> ClassScanner

abstract ClassPathScanner {
- classPath : URL
+ ClassPathScanner(URL)
+ {abstract} scan(<color:blue><u>ClassScanner$Callback</u></color>) : 
+ {static} fromUrl(URL) : <color:blue><u>ClassPathScanner</u></color>
}
DefaultClassScanner *-> ClassPathScanner : each url

class DirectoryClassPathScanner {
}
DirectoryClassPathScanner -up-|> ClassPathScanner

class JarClassPathScanner {
}
JarClassPathScanner -up-|> ClassPathScanner
```