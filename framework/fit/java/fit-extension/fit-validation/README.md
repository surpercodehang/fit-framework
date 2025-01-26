<div style="text-align: center;"><span style="font-size: 40px"><b>Validation Architecture</b></span></div>

# 核心类图

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

class ValidationHandler {
  - validator : <color:blue><u>Validator</u></color>
  --
  - handle(ProceedingJoinPoint joinPoint) : Object
  ===
  This class as the entry of the validation capability and automatically scans the validator annotation <b>during startup</b>.
  Therefore, the handle method is <b>private</b> and is not provided externally.
}

interface ConstraintValidator {
  + {abstract} initialize(Annotation constraintAnnotation) : void
  + {abstract} isValid(T value) : void
  ===
  Represents a constraint validator that validates a specific constraint.
  The specific constraint is represented by an annotation.
}  
annotation Constraint 
Constraint .left.> ConstraintValidator

interface ConstraintViolation {
   + {abstract} message() : String
   + {abstract} message(String value) : void
   + {abstract} propertyName() : String
   + {abstract} propertyName(String value) : void 
   + {abstract} propertyValue() : Object
   + {abstract} propertyValue(Object value) : void
   ===
   This class is used to save data verification error information.
}
interface Validator {
   + {abstract} validate(Object object, Class<?>... groups) : Set<<color:blue><u>ConstraintViolation</u></color>>
   ===
   Provides the ability to verify that an object complies with a specific constraint. such as non-null, non-empty so on.
}
Validator ..> ConstraintViolation
Validator .right.> ConstraintValidator
ValidationHandler o-- Validator
@enduml
```

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

annotation Constraint 

interface ConstraintValidator {
  + {abstract} initialize(Annotation constraintAnnotation) : void
  + {abstract} isValid(T value) : void
  ===
  Represents a constraint validator that validates a specific constraint.
  The specific constraint is represented by an annotation.
}  

Constraint .left.> ConstraintValidator

class NotBlankValidator {
   + initialize(<color:blue><u>NotBlank</u></color> constraintAnnotation) : void
   + isValid(Object value) : boolean
}
NotBlankValidator .up.|> ConstraintValidator
class NotEmptyValidator {
   + initialize(<color:blue><u>NotEmpty</u></color> constraintAnnotation) : void
   + isValid(Object value) : boolean
}
NotEmptyValidator .up.|> ConstraintValidator
class RangeValidator {
   + initialize(<color:blue><u>Range</u></color> constraintAnnotation) : void
   + isValid(Long value) : boolean
}
RangeValidator .up.|> ConstraintValidator

annotation NotBlank 
NotBlank .up.> NotBlankValidator
annotation NotEmpty 
NotEmpty .up.> NotEmptyValidator
annotation Range 
Range .up.> RangeValidator

@enduml
```

# 核心类

### ValidationHandler

`ValidationHandler`类作为校验能力的入口，通过`AOP`的方式来解析。框架启动时，ValidationHandler类的handle方法会扫描@Validated注解，对有该注解的类或者参数进行解析：如果是类上含有该注解，
则对这个类的所有方法进行判断，判断方法参数是否含有约束注解，如果有，对该方法进行AOP 注入；如果是参数上含有该注解，则对该参数所属的方法进行注入。

### ConstraintValidator

表示约束验证器，对某个特定的约束进行验证。当前实现的`ConstraintValidator`的有：

- NotBlankValidator: 判断该字符串是否不为`null`，且不是空白字符。
- NotEmptyValidator：判断该实体是否不为`null`，且不是空对象。
- RangeValidator：判断该数字是否在所属区间范围内。

### ConstraintViolation

`ConstraintViolation`主要用于保存数据校验的错误信息。当用户使用约束注解进行校验时，如果校验出错，`ConstraintViolation`
里就保存了校验出错的字段、出错原因等信息。通过处理这个异常，可以将错误信息返回给客户端，让其得知请求传递的数据有误，从而进行相应的修正。

### 约束注解

每个`ConstraintValidator`都有其对应的约束注解供用户使用，如`NotBlankValidator`对应`NotBlank`注解。注解除了特定属性外，都含有`message`、`group`属性

- message: 当约束条件不满足时，打印出的异常信息
- group:
  用于分组约束。通过分组约束可以对校验进行更精确化控制。如果在类级别和方法级别同时使用分组，那么方法级别的分组将覆盖类级别的分组。

考虑一下例子： 首先，需要定义一个分组接口：

``` java
public interface GroupA {}

public interface GroupB {}
```

然后，在需要进行校验的类中，使用`@Validated`注解指定需要校验的分组：

``` java
public class User {
    @NotBlank(message = "用户名不能为空", groups = {GroupA.class})
    private String username;

    @NotBlank(message = "密码不能为空", groups = {GroupB.class})
    private String password;

    // getter and setter
}
```

在Controller中，使用`@Validated`注解指定需要校验的分组：

``` java
public class UserController {
    @PostMapping("/login")
    public String login(@Validated(GroupA.class) User user) {
        // ...
    }

    @PostMapping("/register")
    public String register(@Validated(GroupB.class) User user) {
        // ...
    }
}
```

这样，在`login`方法中，只会校验`username`字段，而在`register`方法中，只会校验`password`字段。

