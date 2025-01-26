Ohscript 是一门专为 Java 应用设计的拥有函数式编程特性的脚本语言。本文主要介绍 Ohscript 基本语法。

## 变量和数据类型

### 变量创建

Ohscript 是弱类型语言，变量类型在创建变量时**推导出**；且变量创建后，其类型**不可变**（Number 之间可以自动转换）。

变量命名规则：以小写字母或下划线开头，后接任意数量的字母、数字或下划线。

可以使用 `var` 或 `let` 关键字创建变量：

- `var`：变量可以被修改，但是不能被重新声明（对应 JS 的 `let`）；
- `let`：变量不能被修改，并且不能被重新声明；必须在声明时进行初始化（对应 JS 的 `const`）

```
let test = 1; # 创建为 int
let test = 1.1; # 创建为 float
let test = true; # 创建为 boolean
let test = "test"; # 创建为 String
let test = []; # 创建为 ArrayList
let test = [:]; # 创建为 HashMap
let test = (10,20); # 创建为 tuple
let test = {age:10}; # 创建为 object
```

### 数据类型

支持不同数据类型的常用方法，实现见 `src/main/java/modelengine/fit/ohscript/script/parser/nodes/ScriptNode.java`。

#### Number

支持 `.to_int`，`.to_float`，`.floor`，`.ceil`，`.round`，`.to_str`，实现见方法 `addNumberMethods`。

``` python
let a=1.0; a.to_int() # 1L
let a=1; a.to_float() # 1.0D
let a=1.7; a.floor() # 1L
let a=1.3; a.ceil() # 2L
let a=1.7; a.round() # 2L
let a=1; a.to_str() # "1"
```

| Ohscript       | Java                | 区别                         |
| -------------- | ------------------- | ---------------------------- |
| .to_int()      | Number::longValue   |                              |
| .to_float()    | Number::doubleValue |                              |
| .floor(number) | Math::floor         | Ohscript: long; Java: double |
| .ceil(number)  | Math::ceil          | Ohscript: long; Java: double |
| .round(number) | Math::round         |                              |
| .to_str()      | Number::toString    |                              |

#### String

实现见方法 `addStringMethods`。

| Ohscript                      | Java                                       |
| ----------------------------- | ------------------------------------------ |
| .substr(beginIndex, endIndex) | String::substring                          |
| .replace(target, replacement) | String::replace                            |
| .trim()                       | String::trim                               |
| .len()                        | String::length                             |
| .is_empty()                   | String::isEmpty                            |
| .index_of(str)                | String::indexOf                            |
| .upper()                      | String::toUpperCase                        |
| .lower()                      | String::toLowerCase                        |
| .starts_with(prefix)          | String::startsWith                         |
| .ends_with(suffix)            | String::endsWith                           |
| .contains(str)                | String::contains                           |
| .split(regex)                 | String::split                              |
| .to_num()                     | Double.valueOf(srt) 或者 Long.valueOf(str) |
| .is_num()                     |                                            |

- 支持链式调用

```
# true
let me = "   will zhang   "; me.trim().split(" ")[0].upper().lower().ends_with("ill")
```

#### Array

Ohscript 使用 `[]` 创建数组，`[]` 代表空数组。

- 支持 `.forEach`，`.parallel`，`.map`，`.filter`，实现见方法 `addArrayMethods`。
- 支持 `.size`，`.insert`，`.push`，`.remove`，实现见 `ParserBuilder` 类中的 `loadSystemCode` 方法。

``` python
var b=[1,2,3,4]; b[0]=2; # b: [2, 2, 3, 4]
var b=[[1,[2,3]],4]; b[0][1][0] # 2

var a=[1,2,3,4]; var c=0; a.forEach(i=>{c+=i}); c # 10
var a=[1,2,3,4]; var c=0; warning("parallel starts...."); a.parallel(i=>{lock{c+=i}}); warning("parallel ends...."); c # 10
var a=[1,2,3,4]; var c = a.map(i=>i*10+"abc"); c[1] # "20abc"
var a=[1,2,3,4]; var c = a.filter(i=>i>1); c.size() # 3

var b=[1,2,3,4]; b.size() # 4
var b=[1,2,3,4]; b.insert(1,6); b[1] # 6; b: [1, 6, 2, 3, 4]
var b=[1,2,3,4]; b.push(6); b[4] # 6; b: [1, 2, 3, 4, 6]
var b=[1,2,3,4]; b.remove(1); b.size() # 3; b: [1, 3, 4]
```

#### Map

Ohscript 使用 `[:]` 创建 map，`[:]` 代表空 map。

- 支持 `.put` 和 `.get`，实现见方法 `addMapMethods`。
- 暂不支持 `.forEach`。

``` python
var a=[:]; a.put("name","will"); a.get("name") # "will"
```

#### Tuple

Ohscript 支持元组（tuple）这种数据结构，元组可以将多个不同类型或相同类型的值组合在一起。

语法：`(T1, T2, ..., TN)`

- T1 到 TN 可以是不同类型，用 `,` 连接
- 元组至少是二元以上

``` python
let a=(10,20,30,40); a.0 # 10

let a=(10,20,30,40), (_,b,_,d) = a; b+d # 60

let will = (47,168,"male",("will","zhang")), (age,..,(_,last)) = will; last+age # "zhang47"

let will={name:"test", age:10, getAge:()=>this.age};
let a=(10, will);
a.0 # 10
a.1 # Oh
```

#### 对象

对象中可以定义变量和函数，通过 `.` 访问对象中的成员：

``` python
let will = {
    age:48,
    get:()=>this.age
};
will.age # 48
will.get() # 48
```

### 运算符

支持如下运算符，用法与其他语言一致：

**算术运算符**：

- **普通运算**：加 `+`，减 `-`，乘 `*`，除 `/`
- **自增/自减**：自增 `++`，自减 `--`

**赋值运算符**：`=`，`+=`，`-=`，`*=`，`/=`

**比较运算符**：`>`，`>=`，`<`，`<=`，`==`，`!=`

**逻辑运算符**：`&&`、`||`、`!`

**条件运算符**：`condition ? x : y`

## 调用示例

**pom**：在 `dependencies` 中新增

``` xml
<!-- OhScript -->
<dependency>
    <groupId>modelengine.fit.ohscript</groupId>
    <artifactId>ohscript</artifactId>
    <version>0.0.3.6-SNAPSHOT</version>
</dependency>
```

**main**：

``` java
import modelengine.fit.ohscript.script.engine.OhScript;
import modelengine.fit.ohscript.script.errors.OhPanic;
import modelengine.fit.ohscript.script.interpreter.ASTEnv;

public class TestOhScript {
    private static OhScript ohScript = new OhScript();

    public static void main(String[] args) {
        String ohScriptCode = "let str = \"Hello, world!\"; str";
        ASTEnv env = ohScript.load(ohScriptCode);  // 解析运行时字符串，并创建运行环境
        try {
            Object result = env.execute(); // 执行脚本并获取返回值，结果为 "Hello, world!"
            System.out.println("result: " + result.toString());
        } catch (OhPanic e) {
            System.out.println("error: " + e.getMessage());
        }
    }
}
```

最终打印：`result: Hello, world!`

## 特性

### 返回语句

默认最后一个语句是返回值。如果最后一个语句带上分号就不是返回值，除非显式 `return`：

- `expression`
- `return expression;`

``` python
let a=1; a # 返回 1
let a=1; a; # 无返回值
let a=1; return a; # 返回 1

let a=1; true # 返回布尔值 true
let a=1; a # 返回 1
let a=1, b=2; a+b # 返回 3
func f1(){1}; f1() # 返回 1
var age = 10; let b=1; if(b==1){40}else{30} age # 返回 40
```

### 代码块

Ohscript 中，由一对 `{}` 包围的结构称为块。

代码块拥有返回值，Ohscript 支持 `let xxx={}`：

```
# 运行结果为 20
let age = {let age=10; age+10}; age

# 运行结果为 IGNORE，表明无返回值
let age = {let age=10;}; age
```

### 作用域

如果代码块内外定义了名字相同的变量，优先使用块内变量。

``` python
# 运行结果为 11
let a=1; func f(x){let a=10; a+x} f(a)
```

对象中可以使用 `_` 定义内部变量：

```
# ok，结果为 50
let will = {_age: 48, age: 50}; will.age

# 运行不会报错，如果打开语法检查会提示 ENTITY_MEMBER_ACCESS_DENIED
let will = {_age: 48, age: 50}; will._age
```

### 函数

- 函数定义：支持 `func f(arg1, arg2, ...) {}`、`func (arg1, arg2, ...) {}`（匿名函数）

  - 函数命名与变量命名规则一致
  - 函数参数可以为 0 个
- 函数调用：`f(arg1, arg2, ...)`

  - 如果参数个数为 0，则调用形式为 `f()`

#### lambda 表达式

语法：`(args1, args2, ...)=>expressions` 或 `func(arg1, arg2, ...)=>expressions`

```
# 运行结果为 10
let f1=()=>10;
f1()

# 运行结果为 20
let f1=x=>x+10;
f1(10)

# 运行结果为 10
let f1=(x, y)=>{let z=x+y+1; z};
f1(4, 5)

# 运行结果为 10
let f1=func(x, y)=>{let z=x+y+1; z};
f1(4, 5)
```

#### 柯里化

在函数式编程中，函数可以作为入参、出参。

当传参个数少于函数声明时的参数个数时，返回的是特化函数。

``` python
# 运行结果为 "40abc"
func func1(x,y,z,w) {
    x+y+z+w
}
let r1 = func1(10,20);
let r2 = r1(10);
let r3 = r2("abc");
r3

# 运行结果为 100
func func1(x,y,z,w) {
    x+y+z+w
}
let r1 = func1(20)(30);
let r2 = r1(10);
let r3 = r2(40);
r3
```

#### 闭包

``` python
# 运行结果为 3
func f() {
    var count=1;
    return func () {count+=2; count};
}
let a=f();
a()
```

### import 和 export

`import`：支持导入其他模块的符号。

- `import a as b, f1 from m2;`
- `import * from m2;`

`export`：支持导出该模块的符号供其他模块使用。

- `export a, b;`

``` java
@Test
void test_import_and_export() throws OhPanic {
    this.parserBuilder.begin();
    this.parserBuilder.parseString("m1", "import a as b, f1 from m2; func f2(){b+f1()}; f2()");
    this.parserBuilder.parseString("m2", "var a=100; func f1(){a++}; export a,f1;");
    ASF asf = this.parserBuilder.done();
    ASFEnv env = new ASFEnv(asf);
    assertEquals(201, env.execute("m1"));
}
```

### 循环结构和选择结构

与主流语言用法类似；支持 `continue;` 和 `break;` 控制循环。

**condition 必须是 bool 类型，Ohscript 不能将其它类型转换为 bool 类型。**

#### 条件语句

`condition` 不支持 `&&` 与 `||` 用法。

1. `if (condition) {}`
2. `if (condition) {} else {}`
3. `if (condition_1) {} else if (condition_2) {}`
4. `if (condition_1) {} else if (condition_2) {} else {}`

#### 循环语句

**while 循环**：`while (condition) {}`

**do...while 循环**：`do {} while (condition)`

**for 循环**：`for (var_statement; condition; expression) {}`

- 只支持经典 for 循环用法，且循环变量初始化、循环条件与更新表达式均为必填项
- 循环变量不能直接使用已声明的变量

```
# ok
for (var i=0; i<10; i++){}

# error
var i;
for (i=0; i<10; i++){}
```

**each 循环**（针对 array）：`each (index, i) in expression {}`

``` python
let a=[1,2,3,4]; var c=0; each (b,i) in a {c+=b+i;} c # 16
```

### match 表达式

Ohscript 中的 `match` 类似其他语言中的 `switch`，每个分支以 `|` 开头。

**语法**：

```
match value {
    |match_var_1 match_when_1 => expression_1
    |match_var_2 match_when_2 => expression_2
    |_ => expression_3  # 默认匹配，可选项
}
```

- `match_var`：可以为变量名、元组、数字或字符串
- `match_when`：可选项，为 `if(condition)`
- `_` 相当于 `default`，如果上述分支都不匹配，就执行 `|_ =>` 之后的表达式。

**示例**：

``` python
# 运行结果为 102
let value = 2;
var result = 1;
match value {
    |22 => result=24
    |_=>100+value
}
result

# 运行结果为 3
let a=(-11,2,3);
var result=0;
match a{
    |(b,..,d)if(b>0)=>result=b
    |(..,b,d)if(b==2)=>result=d
    |_=>result=100
}
result
```

### 管道符

Ohscript 中的管道符为 `>>`，用于将一个表达式的结果传递给下一个函数作为输入。

语法：`var >> func`

- `var` 必须为已初始化的变量，或者直接传值（可以为字符串、数字或布尔值）

``` python
let a=1; func f1(x){x+1}; a>>f1 # 结果为 2
func f1(x){x+1}; func f2(x){x+2}; 1>>f1>>f2 # 结果为 4
```

### this

Ohscript 中的 `this` 与 Java 一致，指向对象。

``` python
# 运行结果为 12
let will={
    age:10,
    getAge:func()=>{
        let age=2;
        this.age+age
    }
};
will.getAge()
```

### 对象继承

在 Ohscript 中，`::` 用于**对象继承**，并且可以覆写继承的对象中的方法。

``` python
# 运行结果为58; son.age 为48
let will = {
    age:48,
    add:()=>this.age+2
};
let son = will::{
    add:()=>this.age+10
};
son.add()
```

可以使用 `base` 获取父类对象或方法。

```
# 运行结果为 106
let will = {
    age:48,
    add:()=>this.age+2
};
let son = will::{
    add:()=>this.base.add()+2  # this.age+4
};
let grand_son=son::{
    add:()=>base.base.add()+this.base.add()  # this.age*2 + 6
};
son.age+=2;
grand_son.add()
```

> **注**：如果不加 `this`，内部同名变量优先级更高。以上面的例子为例，如果在 grand_son 的 add 方法中再定义一个变量 `base`，内部 `base` 优先级更高，运行会报错。

```
# error
let grand_son=son::{
    add:()=>{
        let base=10;
        base.base.add()+this.base.add()
    }
};
```

### 解构

Ohscript 支持对元组、对象进行解构。

```
# 结果为 50
let a=(10,20,30,40); let (b,..,d)=a; b+d

# 结果为 20
let will = {age:10, height:20}, (_,height) = will; height
```

### 类型判断

Ohscript 支持使用 `<:` 进行变量的类型判断。

```
let a = 3; a<:number
let a = "someone"; a<:string
let a = (1,2); a<:tuple
let a = []; a<:array
let a = ["name":"will"]; a<:map
let a = {name:"will"}; a<:object
let a = null; a<:null
func f(){}; f<:function
func f1(){}; f1()<:unit
```

### safe

Ohscript 支持异常处理，用法类似 `try-catch`：

``` python
# 会打印日志 result: 100
let result = safe{100};
if(!result.panic_code()) {
    log("result: " + result.get());
} else {
    log("error: " + result.panic_code());
}

# 结果为 106，即 VAR_NOT_FOUND
func f1(){ext::a()}; let result = safe{f1()}; result.panic_code()

# 忽略错误码，强行拿结果，为 null
func f1(){ext::a()}; let result = safe{f1()}; result.get()
```

### lock

Ohscript 支持锁：`lock{}`

> 锁的是代码块，非对象。

```
# 运行结果为 10
let a=[1,2,3,4];
var c=0;
warning("parallel starts....");
a.parallel(i=>{
    lock{c+=i}
});
warning("parallel ends....");
c
```

### async

Ohscript 支持异步处理：

- `async{}`：启动一个异步任务（用法类似 `CompletableFuture.supplyAsync`）
- `.await()`：获取异步任务的结果（用法类似 `future.get`）
- `.then`：处理异步任务完成后的结果（用法类似 `future.thenAccept`）

**示例一：**

```
let promise = async{
    sleep(100);
    log("after sleeping...");
    100 # 为代码块的返回值
};
log("main threading...");
promise.await()
```

日志打印顺序为：

```
main threading...
// 等待 100 ms
after sleeping...
// ohscript 运行结果为 100
```

**示例二：**

```
let promise = async{
    sleep(100);
    log("time is up.");
    100
};
var r = 0;
log("async calling....");
promise.then(
    result=>{
        log("the result is:"+result);
        r = result
    }
);
while(r==0){
    sleep(10);
}
log("get result.");
r
```

日志打印顺序为：

```js
async calling....
// 等待 100 ms
time is up.
the result is:100
get result.
// ohscript 运行结果为 100
```

### 内置工具

在 Ohscript 中，可通过 `ext::util.method()` 调用内置工具。

内置工具的实现在 `OhUtil` 类中，`OhUtil` 作为外部方法映射成 oh 里的 util 工具。

#### 创建新映射和新列表

``` python
var map = ext::util.newMap(); # 创建为 HashMap，且支持 Map 的方法，比如 Map::put、Map::get 等
var list = ext::util.newList(); # 创建为 ArrayList，且支持 List 的方法，比如 List::add、List::get 等
```

#### 日志

Ohscript 的日志实现借助了 fit 框架的日志体系，见 `OhUtil::logPanic`。

在 `ParserBuilder::loadSystemCode` 中封装了 `log`、`warning` 和 `error` 三种级别的日志记录函数，可直接调用。

```
log("log test");
warning("warning test");
error("error test");
```

#### JSON 处理

提供了 `stringToJson`、`jsonToString` 和 `jsonToEntity` 三种 JSON 相关的处理函数。

``` python
# 运行结果为 {"test":"abc"}
let json = ext::util.stringToJson("{'test':'abc'}"); ext::util.jsonToString(json)

# 运行结果为 "abc"
let json = ext::util.stringToJson("{'test':'abc'}"); ext::util.jsonToEntity(json).test

# 使用外部创建的 JSONString，test 为 {"test":"abc"}，运行结果为 {"test":"abc"}
let jsonString = ext::util.stringToJson(ext::test); ext::util.jsonToString(jsonString)

# 支持转义字符后，可使用如下形式，运行结果为 {"test":"abc"}
let jsonString = ext::util.stringToJson("{\"test\":\"abc\"}"); ext::util.jsonToString(jsonString)
```

## Ohscript 与 Java 交互

Ohscript 是一门为 Java 定制的脚本语言。

在 Ohscript 中可以直接使用 Java 宿主程序中的对象，且与 Java 共享该对象；Java 也可以获取 Ohscript 中的对象。

假设宿主应用中有接口 `Human` 和 `Female` 类：

``` java
public interface Human {
    Integer getAge();

    String getName();

    List<Human> makeFriends(List<Human> friends);

    Human getFriend();
}

public class Female implements Human {
    private Integer age = 200;

    private String name = "Elsa";

    private Human friend = null;

    public Female() {
    }

    public Female(Integer age, String name) {
        this.age = age;
        this.name = name;
    }

    @Override
    public Integer getAge() {
        return this.age;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<Human> makeFriends(List<Human> friends) {
        return null;
    }

    @Override
    public Human getFriend() {
        return this.friend;
    }
}
```

### Ohscript 直接使用 Java 宿主程序中的对象

1. `ohScript.grant(key, object)`：向脚本引擎注册一个外部对象，引擎内可直接使用该对象。
2. `ASTEnv env = this.ohScript.load(code)`：加载一段 Ohscript 代码，并返回一个可以执行的环境。
3. `env.execute()`：执行语法树，并进行实际的外部调用。

示例一：

``` java
@Test
void testExternalSourceTarget() throws OhPanic {
    List<String> source = new ArrayList<>(Arrays.asList("will", "zhang"));
    List<String> target = new ArrayList<>();
    this.ohScript.grant("source", source);
    this.ohScript.grant("target", target);
    ASTEnv env = this.ohScript.load("var i=0; while(i<source.size()){target.add1(source.get(i)); i++;} target.get(1)");
    assertEquals("zhang", env.execute());
    // Java 与 ohscript 共享 source 与 target
    assertEquals("zhang", target.get(1));
}
```

**注：​**如果 ohscript 代码内部定义了同名的变量 `source`，则内部变量优先级更高，必须使用 `ext::source` 才能获取外部变量，即：

```
let source = 10; # ohscript 内部变量
let sourceOut = ext::source; # 通过 ext:: 获取外部变量
var i=0; while(i<sourceOut.size()){target.add1(source.get(i)); i++;} target.get(1)
```

示例二：（Ohscript 中的新对象可以覆写原始 Java 类中的方法）

``` java
@Test
void test() throws Exception {
    this.ohScript.grant("Woman", Female.class);
    // 新对象没有覆盖 Female 方法，在 ohscript 中执行
    assertEquals("ivy47", this.ohScript.execute("let ivy = Woman{name:\"ivy\",age:47}; ivy.getName()+ivy.getAge()"));
    // 新对象覆盖 Female 的 getAge 方法，在 ohscript 中执行
    assertEquals("ivy88", this.ohScript.execute(
        "let ivy = Woman{name:\"ivy\",age:47,getAge:()=>88}; ivy.getName()+ivy.getAge()"));
}
```

### Java 宿主程序得到 Ohscript 中的对象

``` java
@Test
void test() throws OhPanic {
    this.ohScript.grant("Female", Female.class);
    ASTEnv env = this.ohScript.load("let person = Female{age:10, name:\"myName\"}; person");
    Female person = ObjectUtils.cast(env.execute());
    assertEquals(10, person.getAge());
    assertEquals("myName", person.getName());
}
```

### Ohscript 调用宿主的静态方法

假设类 `Female` 中有个静态方法 `create`（其余方法省略）：

``` java
public class Female implements Human {
    // ...
    public static Female create(Integer age, String name){
        return new Female(age,name);
    }
}
```

Ohscript 支持调用 Java 静态方法：

``` java
@Test
void test_java_static_call() throws OhPanic {
    this.ohScript.grant("Female", Female.class);
    ASTEnv env = this.ohScript.load("let person = Female.create(10,\"myName\"); person");
    Female person = ObjectUtils.cast(env.execute());
    assertEquals(10, person.getAge());
    assertEquals("myName", person.getName());
}
```

### Ohscript implement Java 接口或类

Ohscript 脚本引擎支持 `implement` Java 接口或类。`implement` 是提供对方法维度的重写或实现。

示例一：implement Interface

``` java
@Test
void test_implement() throws Exception {
    // Ohscript实现了Human，但只实现了 getAge，没有实现 getName
    Human person = ohScript.implement(Human.class, "let person = {name:\"Will\",age:47,getAge:()=>88}; person");
    assertEquals(88, person.getAge());
    // error: No implementation for method: getName
    assertEquals("Will", person.getName());
}
```

示例二：覆盖类中的方法

``` java
@Test
void test_implement() throws Exception {
    this.ohScript.grant("Woman", Female.class);
    // 新对象覆盖 Female 的 getAge 方法，在 Java 中执行
    Human person = this.ohScript.implement(Human.class, "Woman{name:\"ivy\",age:47,getAge:()=>88}");
    assertEquals("ivy", person.getName());
    assertEquals(88, person.getAge());
}
```

### Ohscript 扩展 Java 对象

Ohscript 扩展了一个 Female 对象，使用 Female 的原有成员属性，并且可以覆写原方法。

``` java
@Test
void test_extend() throws Exception {
    Human ivy = this.ohScript.extend(new Female(), "{getAge:()=>300}");
    assertEquals("Elsa", ivy.getName());
    assertEquals(300, ivy.getAge());
}
```

### 读取流中的数据

``` java
@Test
void test_load_file() throws IOException, OhPanic {
    this.parserBuilder.begin();
    AST ast1 = this.parserBuilder.parseFile("service", TestResource.getFilePath("fixture/functions.oh"));
    AST ast2 = this.parserBuilder.parseFile("handler1", TestResource.getFilePath("fixture/caller.oh"));
    ASFEnv env = new ASFEnv(this.parserBuilder.done());
    assertEquals(233, env.execute(ast2));

    AST ast3 = this.parserBuilder.parseFile("handler2", TestResource.getFilePath("fixture/someone_caller.oh"));
    env.link();
    assertEquals("will zhang-47", env.execute(ast3));
}
```
