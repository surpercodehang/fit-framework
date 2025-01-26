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
    END("\\$"),
    // more than one char token
    PLUS_PLUS("\\+\\+"),
    MINUS_ARROW("->"),
    MINUS_MINUS("--"),
    BANG_EQUAL("!="),
    EQUAL_EQUAL("=="),
    GREATER_EQUAL(">="),
    LESS_EQUAL("<="),
    EQUAL_GREATER("=>"),
    GREATER_GREATER(">>"),
    TYPE_OF("<:"),
    EXTEND_TO(":>"),
    EXACT_TYPE_OF("=:"),
    AND_AND("&&"),
    OR_OR("\\|\\|"),
    PLUS_EQUAL("\\+="),
    MINUS_EQUAL("-="),
    STAR_EQUAL("\\*="),
    SLASH_EQUAL("\\/="),
    COMMENT("#.*"),
    WAVE("~"),
    STRING_TYPE("_string_"),
    NUMBER_TYPE("_number_"),
    BOOL_TYPE("_bool_"),
    OBJECT_TYPE("_object_"),
    FUNCTION_TYPE("_function_"), // 不同于func函数声明，这是函数扩展关键字
    ARRAY_TYPE("_array_"),
    ARRAY("\\[\\s*\\]"),
    MAP_TYPE("_map_"),
    MAP("\\[\\s*\\:\\s*\\]"),
    ENTITY_BODY_BEGIN("\\{(\\s|\\n|\\r)*\\."),
    LAMBDA_START("(?<!func)(\\b[a-z]\\w*\\s*|\\(\\s*(\\b[a-z]\\w*\\s*,\\s*)*(\\b([a-z]\\w*)\\s*)?\\)\\s*)=>"),
    // single char token
    LEFT_PAREN("\\("),
    RIGHT_PAREN("\\)"),
    LEFT_BRACE("\\{"),
    RIGHT_BRACE("\\}"),
    LEFT_BRACKET("\\["),
    RIGHT_BRACKET("\\]"),
    COMMA("\\,"),
    DOT_DOT("\\.\\."),
    DOT("\\."),
    MATCH_ELSE("\\|\\s*_\\s*\\=\\>"),
    MINUS("-"),
    BANG("!"),
    EQUAL("="),
    GREATER(">"),
    LESS("<"),
    PLUS("\\+"),
    SEMICOLON("\\;"),
    SLASH("\\/"),
    STAR("\\*"),
    AND("&"),
    OR("\\|"),
    MOD("\\%"),
    POWER("\\^"),
    QUESTION("\\?"),
    EACH("each", true),
    // keywords
    SAFE("safe", true),
    ASYNC("async", true),
    LOCK("lock", true),
    LET("let", true),
    VAR("var", true),
    IF("if", true),
    ELSE("else", true),
    WHILE("while", true),
    DO("do", true),
    FOR("for", true),
    IN("in", true),
    MATCH("match", true),
    BREAK("break", true),
    CONTINUE("continue", true),
    RETURN("return", true),
    IMPORT("import", true),
    EXPORT("export"),
    NAME_SPACE("namespace", true),
    FORM("form", true),
    WITH("with", true),
    TRUE("true", true),
    FALSE("false", true),
    FUNC("func", true),
    ENTITY("entity", true),
    TUPLE("_tuple", true),
    FROM("from", true),
    AS("as", true),
    ENTER("\\S* \\n"),
    OH("(ext|http|fit)::"),
    // literals
    EXTEND("(\\:\\:\\{)"),
    ID_COLON("((?<!\\?)\\b[a-zA-Z_]\\w*\\s*\\:(?!\\:))"),
    UPPER_ID("(\\b[A-Z]\\w*)"),
    ID("(\\b[a-z_]\\w*)"),
    NUMBER("(?:\\b|(?<=[\\=\\<\\>])\\s*-\\s*)\\d+(?:\\.\\d+)?(?![\\.])\\b"),
    STRING_COLON("(\\\"[^\"]*\\\"\\s*\\:(?!\\:))"),
    STRING("(\\\"(?:[^\"\\\\]|\\\\.)*\\\")"),
    // others
    COLON("\\:"),
    EOL("$"),
    UNKNOWN("\\w+|\\S"),
    EPSILON("ε"),
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
