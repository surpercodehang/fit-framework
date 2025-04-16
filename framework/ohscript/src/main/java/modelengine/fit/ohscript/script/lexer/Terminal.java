/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.lexer;

import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.NodeType;
import modelengine.fit.ohscript.util.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ohScript语法中所有的token类型，词法分析阶段生成的基本符号（tokens），它们是语法树的叶子节点，不可再分割
 * token都关联了一个正则表达式，用来匹配input string
 * 匹配成功的token type会结合匹配的字符串形成一个Token对象
 *
 * @author 张群辉
 * @since 2023-05-01
 */
public enum Terminal implements NodeType, Serializable {
    /**
     * 输入结束标记
     * <p>表示源代码解析结束的特殊标记</p>
     */
    END("\\$"),

    /******************************
     *   Multi-character tokens   *
     ******************************/

    /**
     * 自增运算符
     * <p>匹配 {@code ++} 操作符，用于变量自增操作</p>
     */
    PLUS_PLUS("\\+\\+"),

    /**
     * 箭头函数符号
     * <p>匹配 {@code ->} 符号，用于lambda表达式参数列表与函数体的分隔</p>
     */
    MINUS_ARROW("->"),

    /**
     * 自减运算符
     * <p>匹配 {@code --} 操作符，用于变量自减操作</p>
     */
    MINUS_MINUS("--"),

    /**
     * 不等于比较符
     * <p>匹配 {@code !=} 操作符，进行非严格不等性判断</p>
     */
    BANG_EQUAL("!="),

    /**
     * 相等比较符
     * <p>匹配 {@code ==} 操作符，进行严格相等性判断</p>
     */
    EQUAL_EQUAL("=="),

    /**
     * 大于等于比较符
     * <p>匹配 {@code >=} 操作符，进行数值或字符序列的边界判断</p>
     */
    GREATER_EQUAL(">="),

    /**
     * 小于等于比较符
     * <p>匹配 {@code <=} 操作符，进行数值或字符序列的边界判断</p>
     */
    LESS_EQUAL("<="),

    /**
     * 类型推导符号
     * <p>匹配 {@code =>} 符号，用于泛型类型推导和模式匹配</p>
     */
    EQUAL_GREATER("=>"),

    /**
     * 右位移操作符
     * <p>匹配 {@code >>} 符号，用于二进制右位移运算</p>
     */
    GREATER_GREATER(">>"),

    /**
     * 泛型类型约束符
     * <p>匹配 {@code <:} 符号，声明泛型参数的上界约束</p>
     */
    TYPE_OF("<:"),

    /**
     * 类型扩展符号
     * <p>匹配 {@code :>} 符号，用于类型扩展声明</p>
     */
    EXTEND_TO(":>"),

    /**
     * 精确类型匹配符
     * <p>匹配 {@code =:} 符号，用于严格类型匹配检查</p>
     */
    EXACT_TYPE_OF("=:"),

    /**
     * 逻辑与操作符
     * <p>匹配 {@code &&} 符号，进行短路逻辑与运算</p>
     */
    AND_AND("&&"),

    /**
     * 逻辑或操作符
     * <p>匹配 {@code ||} 符号，进行短路逻辑或运算</p>
     */
    OR_OR("\\|\\|"),

    /**
     * 复合加法赋值符
     * <p>匹配 {@code +=} 操作符，进行数值累加赋值操作</p>
     */
    PLUS_EQUAL("\\+="),

    /**
     * 复合减法赋值符
     * <p>匹配 {@code -=} 操作符，进行数值递减赋值操作</p>
     */
    MINUS_EQUAL("-="),

    /**
     * 复合乘法赋值符
     * <p>匹配 {@code *=} 操作符，进行数值乘积赋值操作</p>
     */
    STAR_EQUAL("\\*="),

    /**
     * 复合除法赋值符
     * <p>匹配 {@code /=} 操作符，进行数值除法赋值操作</p>
     */
    SLASH_EQUAL("\\/="),
    /**
     * 注释标识符
     * <p>匹配 {@code #} 开头的单行注释</p>
     */
    COMMENT("#.*"),

    /**
     * 位取反操作符
     * <p>匹配 {@code ~} 符号，进行二进制位取反运算</p>
     */
    WAVE("~"),

    /**
     * 字符串类型标识
     * <p>匹配 {@code _string_} 系统类型关键字</p>
     */
    STRING_TYPE("_string_"),

    /**
     * 数值类型标识
     * <p>匹配 {@code _number_} 系统类型关键字</p>
     */
    NUMBER_TYPE("_number_"),

    /**
     * 布尔类型标识
     * <p>匹配 {@code _bool_} 系统类型关键字</p>
     */
    BOOL_TYPE("_bool_"),

    /**
     * 对象类型标识
     * <p>匹配 {@code _object_} 系统类型关键字</p>
     */
    OBJECT_TYPE("_object_"),

    /**
     * 函数类型标识
     * <p>匹配 {@code _function_} 系统类型关键字，用于函数类型声明和类型检查</p>
     */
    FUNCTION_TYPE("_function_"), // 不同于func函数声明，这是函数扩展关键字

    /**
     * 数组类型标识
     * <p>匹配 {@code _array_} 系统类型关键字，用于数组类型声明和元素访问校验</p>
     */
    ARRAY_TYPE("_array_"),

    /**
     * 空数组声明符
     * <p>匹配 {@code []} 结构，声明空数组字面量</p>
     */
    ARRAY("\\[\\s*\\]"),

    /**
     * 映射类型标识
     * <p>匹配 {@code _map_} 系统类型关键字</p>
     */
    MAP_TYPE("_map_"),

    /**
     * 空映射声明符
     * <p>匹配 {@code [ : ]} 结构，声明空键值对集合</p>
     */
    MAP("\\[\\s*\\:\\s*\\]"),

    /**
     * 实体定义起始符
     * <p>匹配 {@code {.}} 结构，标识类/结构体定义的语法起始位置</p>
     */
    ENTITY_BODY_BEGIN("\\{(\\s|\\n|\\r)*\\."),

    /**
     * Lambda表达式起始符
     * <p>匹配匿名函数声明，如 {@code param => ...}</p>
     */
    LAMBDA_START("(?<!func)(\\b[a-z]\\w*\\s*|\\(\\s*(\\b[a-z]\\w*\\s*,\\s*)*(\\b([a-z]\\w*)\\s*)?\\)\\s*)=>"),
    
    /*****************************
     *   Single-character tokens *
     *****************************/

    /**
     * 左圆括号
     * <p>匹配 {@code (} 符号，用于函数调用、表达式分组和参数列表声明</p>
     */
    LEFT_PAREN("\\("),

    /**
     * 右圆括号
     * <p>匹配 {@code )} 符号，闭合函数调用和表达式分组结构</p>
     */
    RIGHT_PAREN("\\)"),

    /**
     * 左花括号
     * <p>匹配 {@code {}} 符号，用于代码块和对象字面量的开始</p>
     */
    LEFT_BRACE("\\{"),

    /**
     * 右花括号
     * <p>匹配 {@code }} 符号，闭合代码块和对象字面量</p>
     */
    RIGHT_BRACE("\\}"),

    /**
     * 左方括号
     * <p>匹配 {@code [} 符号，用于数组和映射声明</p>
     */
    LEFT_BRACKET("\\["),

    /**
     * 右方括号
     * <p>匹配 {@code ]} 符号，闭合数组和映射声明</p>
     */
    RIGHT_BRACKET("\\]"),

    /**
     * 逗号分隔符
     * <p>匹配 {@code ,} 符号，用于参数和元素分隔</p>
     */
    COMMA("\\,"),

    /**
     * 范围操作符
     * <p>匹配 {@code ..} 符号，用于区间范围声明</p>
     */
    DOT_DOT("\\.\\."),

    /**
     * 成员访问符
     * <p>匹配 {@code .} 符号，用于对象成员访问</p>
     */
    DOT("\\."),

    /**
     * 模式匹配默认分支
     * <p>匹配 {@code |_ =>} 结构，声明模式匹配的默认分支</p>
     */
    MATCH_ELSE("\\|\\s*_\\s*\\=\\>"),

    /**
     * 减号/负号操作符
     * <p>匹配 {@code -} 符号，用于减法运算或负值表示</p>
     */
    MINUS("-"),

    /**
     * 逻辑非操作符
     * <p>匹配 {@code !} 符号，进行布尔值取反和条件反转操作</p>
     */
    BANG("!"),

    /**
     * 赋值操作符
     * <p>匹配 {@code =} 符号，用于变量赋值和默认参数声明</p>
     */
    EQUAL("="),

    /**
     * 大于比较符
     * <p>匹配 {@code >} 符号，用于数值大小比较和泛型参数声明</p>
     */
    GREATER(">"),

    /**
     * 小于比较符
     * <p>匹配 {@code <} 符号，用于数值大小比较和泛型参数声明</p>
     */
    LESS("<"),

    /**
     * 加号操作符
     * <p>匹配 {@code +} 符号，用于加法运算和字符串连接操作</p>
     */
    PLUS("\\+"),

    /**
     * 分号结束符
     * <p>匹配 {@code ;} 符号，标识语句结束和代码块分隔</p>
     */
    SEMICOLON("\\;"),

    /**
     * 除法操作符
     * <p>匹配 {@code /} 符号，进行数值除法运算和正则表达式分隔</p>
     */
    SLASH("\\/"),

    /**
     * 乘法操作符
     * <p>匹配 {@code *} 符号，进行数值乘法和通配符匹配</p>
     */
    STAR("\\*"),

    /**
     * 按位与操作符
     * <p>匹配 {@code &} 符号，进行二进制按位与运算</p>
     */
    AND("&"),

    /**
     * 按位或操作符
     * <p>匹配 {@code |} 符号，进行二进制按位或运算</p>
     */
    OR("\\|"),

    /**
     * 取模操作符
     * <p>匹配 {@code %} 符号，进行数值取模运算</p>
     */
    MOD("\\%"),

    /**
     * 幂运算操作符
     * <p>匹配 {@code ^} 符号，进行数值幂运算</p>
     */
    POWER("\\^"),

    /**
     * 三元条件符
     * <p>匹配 {@code ?} 符号，用于条件表达式分支选择</p>
     */
    QUESTION("\\?"),

    /**
     * 遍历操作符
     * <p>匹配 {@code each} 关键字，用于集合元素的迭代操作</p>
     */
    EACH("each", true),
    
    /*****************************
     *   Keywords tokens         *
     *****************************/

    /**
     * 安全调用标识
     * <p>匹配 {@code safe} 关键字，声明空安全执行代码块</p>
     */
    SAFE("safe", true),

    /**
     * 异步上下文标记
     * <p>匹配 {@code async} 关键字，标识异步执行的代码块</p>
     */
    ASYNC("async", true),

    /**
     * 同步锁标记
     * <p>匹配 {@code lock} 关键字，用于并发临界区保护</p>
     */
    LOCK("lock", true),

    /**
     * 常量声明符
     * <p>匹配 {@code let} 关键字，声明不可变常量</p>
     */
    LET("let", true),

    /**
     * 变量声明符
     * <p>匹配 {@code var} 关键字，声明可变变量</p>
     */
    VAR("var", true),

    /**
     * 条件判断起始符
     * <p>匹配 {@code if} 关键字，声明条件分支结构</p>
     */
    IF("if", true),

    /**
     * 条件否定分支符
     * <p>匹配 {@code else} 关键字，声明条件语句的否定分支</p>
     */
    ELSE("else", true),

    /**
     * 循环结构标识
     * <p>匹配 {@code while} 关键字，声明前测试循环结构</p>
     */
    WHILE("while", true),

    /**
     * 后测试循环标识
     * <p>匹配 {@code do} 关键字，声明后测试循环结构</p>
     */
    DO("do", true),

    /**
     * 循环控制符
     * <p>匹配 {@code for} 关键字，声明迭代循环结构</p>
     */
    FOR("for", true),

    /**
     * 成员遍历符
     * <p>匹配 {@code in} 关键字，用于集合迭代操作</p>
     */
    IN("in", true),

    /**
     * 模式匹配标识
     * <p>匹配 {@code match} 关键字，声明模式匹配结构</p>
     */
    MATCH("match", true),

    /**
     * 循环中断符
     * <p>匹配 {@code break} 关键字，终止当前循环结构</p>
     */
    BREAK("break", true),

    /**
     * 循环跳转符
     * <p>匹配 {@code continue} 关键字，跳至循环下次迭代</p>
     */
    CONTINUE("continue", true),

    /**
     * 函数返回符
     * <p>匹配 {@code return} 关键字，从函数返回结果值</p>
     */
    RETURN("return", true),

    /**
     * 模块导入符
     * <p>匹配 {@code import} 关键字，声明外部模块依赖</p>
     */
    IMPORT("import", true),

    /**
     * 模块导出标识
     * <p>匹配 {@code export} 关键字，声明模块导出项和可见性控制</p>
     */
    EXPORT("export"),

    /**
     * 命名空间声明符
     * <p>匹配 {@code namespace} 关键字，用于声明代码的命名空间</p>
     */
    NAME_SPACE("namespace", true),

    /**
     * 表单定义标识
     * <p>匹配 {@code form} 关键字，用于声明数据表单结构</p>
     */
    FORM("form", true),

    /**
     * 上下文绑定符
     * <p>匹配 {@code with} 关键字，用于指定代码块的执行上下文</p>
     */
    WITH("with", true),

    /**
     * 布尔真值常量
     * <p>匹配 {@code true} 关键字，表示布尔类型的真值</p>
     */
    TRUE("true", true),

    /**
     * 布尔假值常量
     * <p>匹配 {@code false} 关键字，表示布尔类型的假值</p>
     */
    FALSE("false", true),

    /**
     * 函数声明标识
     * <p>匹配 {@code func} 关键字，用于函数定义</p>
     */
    FUNC("func", true),

    /**
     * 实体类型声明符
     * <p>匹配 {@code entity} 关键字，用于声明自定义数据类型</p>
     */
    ENTITY("entity", true),

    /**
     * 元组类型标识
     * <p>匹配 {@code _tuple} 关键字，用于声明固定长度的异构数据集合</p>
     */
    TUPLE("_tuple", true),

    /**
     * 来源声明符
     * <p>匹配 {@code from} 关键字，用于指定数据或模块的来源</p>
     */
    FROM("from", true),

    /**
     * 别名声明符
     * <p>匹配 {@code as} 关键字，用于为导入项指定别名</p>
     */
    AS("as", true),

    /**
     * 空行占位符
     * <p>匹配仅包含换行的空行</p>
     */
    ENTER("\\S* \\n"),

    /**
     * 协议前缀标识
     * <p>匹配 {@code ext::}、{@code http::} 等协议前缀</p>
     */
    OH("(ext|http|fit)::"),

    /*****************************
     *   literals tokens         *
     *****************************/

    /**
     * 类型扩展起始符
     * <p>匹配 {@code ::{}} 结构，标识类型扩展声明的语法起始</p>
     */
    EXTEND("(\\:\\:\\{)"),

    /**
     * 带冒号标识符
     * <p>匹配 {@code id:} 结构，用于对象属性和映射键的声明</p>
     */
    ID_COLON("((?<!\\?)\\b[a-zA-Z_]\\w*\\s*\\:(?!\\:))"),

    /**
     * 大写标识符
     * <p>匹配首字母大写的标识符，用于类型名称和常量声明</p>
     */
    UPPER_ID("(\\b[A-Z]\\w*)"),

    /**
     * 常规标识符
     * <p>匹配小写字母开头的标识符，用于变量和函数名声明</p>
     */
    ID("(\\b[a-z_]\\w*)"),

    /**
     * 数值字面量
     * <p>匹配整型和浮点型数值，支持负数和科学计数法表示</p>
     */
    NUMBER("(?:\\b|(?<=[\\=\\<\\>])\\s*-\\s*)\\d+(?:\\.\\d+)?(?![\\.])\\b"),

    /**
     * 带冒号字符串
     * <p>匹配 {@code "key":} 结构，用于JSON风格映射键的声明</p>
     */
    STRING_COLON("(\\\"[^\"]*\\\"\\s*\\:(?!\\:))"),

    /**
     * 字符串字面量
     * <p>匹配双引号包裹的字符串，支持转义字符处理</p>
     */
    STRING("(\\\"(?:[^\"\\\\]|\\\\.)*\\\")"),

    /*****************************
     *   Others tokens           *
     *****************************/

    /**
     * 冒号分隔符
     * <p>匹配 {@code :} 符号，用于类型声明和三元表达式分隔</p>
     */
    COLON("\\:"),

    /**
     * 行结束标记
     * <p>匹配行结束位置，用于自动分号插入(ASI)机制</p>
     */
    EOL("$"),

    /**
     * 未知符号
     * <p>匹配无法识别的字符序列，触发语法错误提示</p>
     */
    UNKNOWN("\\w+|\\S"),

    /**
     * 空产生式标记
     * <p>匹配空产生式，用于语法分析中的epsilon转换</p>
     */
    EPSILON("ε"),

    /**
     * 空单元类型标识
     * <p>表示无返回值的空单元类型，用于函数无返回值声明</p>
     */
    UNIT("(ε)");

    private static final long serialVersionUID = -3083743187191599816L;

    private final String regex;

    private final boolean isKeyWord;

    Terminal(String regex, boolean isKeyWord) {
        this.regex = regex;
        this.isKeyWord = isKeyWord;
    }

    Terminal(String regex) {
        this(regex, false);
    }

    /**
     * 根据名字或者正则表达式获取Terminal枚举对象
     *
     * @param name 名字或者正则表达式
     * @return 对应的Terminal枚举对象，如果没有找到则返回null
     */
    public static Terminal valueFrom(String name) {
        Terminal terminal;
        try {
            terminal = Terminal.valueOf(name);
        } catch (Exception e) {
            terminal = null;
        }

        if (terminal == null) {
            for (Terminal value : Terminal.values()) {
                if (value.regex.equals(name)) {
                    return value;
                }
            }
            return null;
        }
        return terminal;
    }

    /**
     * 构建所有的枚举对象的正则表达式
     *
     * @return 以“|”连接的多条正则表达式
     */
    private static String buildRegex() {
        StringBuilder builder = null;
        Terminal[] types = Terminal.values();
        for (Terminal type : types) {
            if (builder == null) {
                builder = new StringBuilder();
                builder.append(type.regex());
            } else {
                builder.append("|").append(type.regex());
            }
        }
        assert builder != null;
        return builder.toString();
    }

    /**
     * 给定一行源码，构建token列表
     *
     * @param line 源码行
     * @param lineNum 行数，用于记录token与源码的位置关系
     * @return 构建的token列表
     */
    public static List<Token> match(String line, int lineNum) {
        List<Token> tokens = new ArrayList<>();
        Pattern pattern = Pattern.compile(Terminal.buildRegex());
        String trimmed = line.replaceAll("\\?\\s+", "?");
        Matcher matcher = pattern.matcher(trimmed);
        while (matcher.find()) {
            tokens.add(buildToken(matcher, lineNum));
        }
        return tokens;
    }

    /**
     * 整个正则匹配的情况下，找到特定的终结符，构建出token
     *
     * @param matcher 整个正则匹配的结果
     * @param lineNum 行数
     * @return 构建的token
     */
    private static Token buildToken(Matcher matcher, int lineNum) {
        Terminal[] types = Terminal.values();
        for (Terminal type : types) {
            String name = type.tokenName();
            String value = matcher.group(name);
            if (value != null) {
                if (type == STRING) {
                    value = value.replaceAll("\\\\\"", "\"");
                }
                return new Token(type, value, lineNum, matcher.start(name), matcher.end(name));
            }
        }
        return null;
    }

    /**
     * 获取token的正则表达式
     *
     * @return token的正则表达式
     */
    protected String regex() {
        if (this.isKeyWord) {
            return String.format("(?<%s>(?<![\\w\\.])%s(?![\\w\\.]))", this.tokenName(), this.regex);
        } else {
            return String.format("(?<%s>%s)", this.tokenName(), this.regex);
        }
    }

    /**
     * 判断是否为关键字
     *
     * @return 是否为关键字
     */
    public boolean isKeyWord() {
        return this.isKeyWord;
    }

    /**
     * 获取token的名字，去掉下划线
     *
     * @return token的名字
     */
    public String tokenName() {
        String name = this.name();
        return name.replace(Constants.UNDER_LINE, "");
    }

    /**
     * 获取token的文本表示，去掉正则表达式的转义字符
     *
     * @return token的文本表示
     */
    public String text() {
        return this.regex.replace("\\", "");
    }

    @Override
    public TerminalNode parse() {
        return new TerminalNode(this);
    }
}
