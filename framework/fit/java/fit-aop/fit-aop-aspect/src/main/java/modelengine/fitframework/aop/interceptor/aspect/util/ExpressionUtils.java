/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.util;

import static modelengine.fitframework.inspection.Validation.isTrue;
import static modelengine.fitframework.inspection.Validation.notEmpty;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * aspect 的表达式工具类。
 *
 * @author 白鹏坤
 * @since 2023-03-17
 */
public class ExpressionUtils {
    private static final String OR = "||";
    private static final String AND = "&&";
    private static final String NOT = "!";
    private static final String LEFT_BRACKET = "(";
    private static final String RIGHT_BRACKET = ")";
    private static final String ARRAY_REGEX = "(?<classType>[^\\[]*)(?<dimension>(\\[\\])+)";
    private static final Pattern ARRAY_PATTERN = Pattern.compile(ARRAY_REGEX);
    private static final String BOOL_REGEX = "!?(true|false)((\\|\\||&&)!?(true|false))*";
    private static final Map<String, Class<?>> BASE_TYPE = MapBuilder.<String, Class<?>>get()
            .put("byte", byte.class)
            .put("char", char.class)
            .put("boolean", boolean.class)
            .put("short", short.class)
            .put("int", int.class)
            .put("long", long.class)
            .put("double", double.class)
            .put("float", float.class)
            .put("void", void.class)
            .build();

    /**
     * 使用特定逻辑运算符分隔切入点表达式。
     * <p>特定逻辑运算符: '&&'，'||'，'！' 这三种。</p>
     * <ul>
     *      <li>输入为 {@code "@args(..) &&this(..)"}，输出为 {@code ["@args(..)", "&&", "this(..)"]}。</li>
     *      <li>输入为 {@code "@args(..)||target(..)"}，输出为  {@code ["@args(..)", "||", "target(..)"]}。</li>
     *      <li>输入为 {@code "!@args(..) || target(..)"}，输出为  {@code ["!", "@args(..)", "||", "target(..)"]}。</li>
     * </ul>
     *
     * @param pointcut 切割切入点表达式的 {@link String}。
     * @return 返回包含切入点表达式关键字与特定逻辑运算符的字符串列表。
     */
    @Nonnull
    public static List<String> expressionSplit(String pointcut) {
        Validation.notNull(pointcut, "Pointcut cannot be null.");
        String str = pointcut;
        String[] arr = str.split("&&|\\|\\||!");
        List<String> list = new ArrayList<>();
        for (String tmp : arr) {
            str = str.substring(tmp.length());
            if (StringUtils.isNotBlank(tmp)) {
                list.add(tmp.trim());
            }
            if (str.startsWith("&&") || str.startsWith("||")) {
                String reg = str.substring(0, 2);
                list.add(reg);
                str = str.substring(2);
            } else if (str.startsWith("!")) {
                String reg = str.substring(0, 1);
                list.add(reg);
                str = str.substring(1);
            }
        }
        return list;
    }

    /**
     * 以正则表达式替换包含 "*" ".." "." 的字符串。
     *
     * @param origin 待替换的字符串。
     * @return 正则替换后的字符串。
     */
    @Nonnull
    public static String expressionReplaceRegex(String origin) {
        Validation.notNull(origin, "the expression cannot be null.");
        // * 替换为  “变量"
        final String starRegex = "[a-zA-Z0-9_]+";
        // .. 替换为  “.变量.变量."
        final String twoDotRegex = "\\.([a-zA-Z0-9_]+\\.)*";
        // . 替换为 ”\.“
        final String oneDotRegex = "\\\\.";
        return origin.replaceAll("\\*", starRegex).replaceAll("\\.\\.", twoDotRegex).replaceAll("\\.", oneDotRegex);
    }

    /**
     * 根据字符串获取相应的类型。
     * <p>支持 8 种基本类型，void，java.lang 包下类型，多维数组，自定义类型。</p>
     * <p>例：int, void, String, int[], {@link modelengine.fitframework.aop.AopInterceptor}。</p>
     *
     * @param content 表示待解析的字符串，自定义类需写全路径的 {@link String}。
     * @param classLoader 表示类加载器的 {@link ClassLoader}。
     * @return 表示相应的类型的 {@link Class}{@code <?>}。
     */
    public static Class<?> getContentClass(String content, ClassLoader classLoader) {
        if (Objects.equals(content, LEFT_BRACKET) || Objects.equals(content, RIGHT_BRACKET)) {
            return null;
        }
        if (BASE_TYPE.containsKey(content)) {
            return BASE_TYPE.get(content);
        }
        ClassLoader currentClassLoader =
                ObjectUtils.getIfNull(classLoader, () -> Thread.currentThread().getContextClassLoader());
        if (content.contains("[")) {
            return getArrayClass(content, currentClassLoader);
        }
        String fullName = content;
        if (!fullName.contains(".")) {
            fullName = "java.lang." + content;
        }
        try {
            return currentClassLoader.loadClass(fullName);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static Class<?> getArrayClass(String content, ClassLoader classLoader) {
        Matcher matcher = ARRAY_PATTERN.matcher(content);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(StringUtils.format("Array format error. [array={0}]", content));
        }
        String arrayType = matcher.group("classType");
        String dimension = matcher.group("dimension");
        Class<?> clazz = ExpressionUtils.getContentClass(arrayType, classLoader);
        List<Integer> dimensionList = new ArrayList<>();
        for (int i = 0; i < dimension.length(); i++) {
            if (dimension.charAt(i) == '[') {
                dimensionList.add(0);
            }
        }
        int[] dimensions = dimensionList.stream().mapToInt(Integer::intValue).toArray();
        return Array.newInstance(clazz, dimensions).getClass();
    }

    /**
     * 布尔表达式计算。
     * <ul>
     *      <li>输入为 {@code ["true"，"&&"，"false"]，输出为 false}。</li>
     *      <li>输入为 {@code ["!"，"true"，"||"，"true"]，输出为 true}。</li>
     *      <li>输入为 {@code ["("，"true"，"&&"，"false"，")"]，输出为 false}。</li>
     * </ul>
     *
     * @param boolExpression 表示输入包含 "!"，"&&"，"||"，"true"，"false"，"(", ")" 这 7 种字符串的 {@link List}。
     * @return 表示布尔表达式计算结果的 {@code boolean}
     */
    public static boolean computeBoolExpression(List<String> boolExpression) {
        notEmpty(boolExpression, "The List cannot be empty.");
        boolean isMatched = boolExpression.stream().allMatch(item -> item.matches("!|&&|\\|\\||true|false|\\(|\\)"));
        Validation.isTrue(isMatched,
                "As bool expression, The List cannot be computed. [boolExpression={0}]",
                boolExpression);
        LinkedList<String> withoutBracketList = new LinkedList<>();
        for (String item : boolExpression) {
            if (RIGHT_BRACKET.equals(item)) {
                LinkedList<String> bracketCompute = new LinkedList<>();
                String last;
                while (!LEFT_BRACKET.equals(last = withoutBracketList.pollLast())) {
                    notNull(last, "'(' and ')' must appear in pairs.");
                    bracketCompute.addFirst(last);
                }
                isTrue(StringUtils.concat(bracketCompute).matches(BOOL_REGEX),
                        "The expression format error. " + "[expression={0}]",
                        boolExpression);
                withoutBracketList.add(computeBoolExpressionWithoutBracket(bracketCompute));
            } else {
                withoutBracketList.add(item);
            }
        }
        isTrue(StringUtils.concat(withoutBracketList).matches(BOOL_REGEX),
                "The expression format error. " + "[expression={0}]",
                boolExpression);
        return Boolean.parseBoolean(computeBoolExpressionWithoutBracket(withoutBracketList));
    }

    /**
     * 计算不包含括号的布尔表达式。
     * <p>调用前应当检查布尔表达式是否符合正确语法。</p>
     *
     * @param boolExpression 表示布尔表达式的 {@link List}。
     * @return 表示布尔值的 {@link String}。
     */
    private static String computeBoolExpressionWithoutBracket(List<String> boolExpression) {
        LinkedList<String> withoutBracketList = new LinkedList<>();
        for (String item : boolExpression) {
            String last = withoutBracketList.peekLast();
            if (NOT.equals(last)) {
                withoutBracketList.pollLast();
                withoutBracketList.add(String.valueOf(!Boolean.parseBoolean(item)));
            } else if (AND.equals(last) || OR.equals(last)) {
                String operator = withoutBracketList.pollLast();
                String left = withoutBracketList.pollLast();
                withoutBracketList.add(String.valueOf(logicAndOrCompute(operator, left, item)));
            } else {
                withoutBracketList.add(item);
            }
        }
        return withoutBracketList.peek();
    }

    private static boolean logicAndOrCompute(String operator, String left, String right) {
        boolean leftBool = Boolean.parseBoolean(left);
        boolean rightBool = Boolean.parseBoolean(right);
        if (OR.equals(operator)) {
            return Boolean.logicalOr(leftBool, rightBool);
        }
        return Boolean.logicalAnd(leftBool, rightBool);
    }
}
