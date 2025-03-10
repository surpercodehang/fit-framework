/*
 * Copyright (c) 2024-2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fitframework.aop.interceptor.aspect.parser.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * execute 表达式正则匹配。
 *
 * @author 郭龙飞
 * @since 2023-03-10
 */
public class ExecuteExpression {
    private static final String ACCESS_MODIFIER = "(?<accessModifier>((public|private|protected|\\*)(\\s+))?)";
    private static final String RETURN_TYPE;
    private static final String CLASS_PATH = "(?<classPath>(((\\*?\\w+\\*?|\\*)\\.\\.?)*(\\*?\\w+\\*?|\\*)(\\.))?)";
    private static final String METHOD_NAME = "(?<methodName>(\\*?\\w+\\*?|\\*))";
    private static final String PARAM_LIST;
    private static final String EXECUTION;
    private static final String BASIC_TYPE = "(byte|char|boolean|short|int|long|double|float)";
    private static final String PARAM_TYPE;
    private static final Pattern PATTERN;

    static {
        // 参数类型：基础类型 + Object + 数组类型
        PARAM_TYPE = "(" + BASIC_TYPE + "|[\\w\\.]*)(\\[\\])*";
        // 返回值类型：void  + 参数类型
        RETURN_TYPE = "(?<returnType>(void|" + PARAM_TYPE + "|\\*))";
        // 参数列表
        PARAM_LIST = "(?<paramList>(.*))";
        // execution表达式
        EXECUTION = "\\s*" + ACCESS_MODIFIER + RETURN_TYPE + "\\s+" + CLASS_PATH + METHOD_NAME + "\\(\\s*" + PARAM_LIST
                + "\\s*\\)\\s*" + "\\s*";
        PATTERN = Pattern.compile(EXECUTION);
    }

    static ExecutionModel parse(String pointcut) {
        notNull(pointcut, "The pointcut cannot be null.");
        Matcher matcher = PATTERN.matcher(pointcut);
        if (matcher.matches()) {
            String accessModifier = matcher.group("accessModifier");
            String returnType = matcher.group("returnType");
            String classPath = StringUtils.trimEnd(matcher.group("classPath"), '.');
            String methodName = matcher.group("methodName");
            String paramList = matcher.group("paramList");
            return new ExecutionModel(accessModifier, returnType, classPath, methodName, paramList);
        }
        throw new IllegalArgumentException(StringUtils.format("Execution grammar format error. [execution={0}]",
                pointcut));
    }

    /**
     * 表达式解析结果。
     */
    public static class ExecutionModel {
        private final String accessModifier;
        private final String returnType;
        private final String classPath;
        private final String methodName;
        private final String paramList;

        /**
         * 使用指定的访问修饰符、返回类型、类路径、方法名和参数列表初始化 {@link ExecutionModel} 的新实例。
         *
         * @param accessModifier 表示访问修饰符的 {@link String}。
         * @param returnType 表示返回类型的 {@link String}。
         * @param classPath 表示类路径的 {@link String}。
         * @param methodName 表示方法名的 {@link String}。
         * @param paramList 表示参数列表的 {@link String}。
         */
        public ExecutionModel(String accessModifier, String returnType, String classPath, String methodName,
                String paramList) {
            this.accessModifier = accessModifier;
            this.returnType = returnType;
            this.classPath = classPath;
            this.methodName = methodName;
            this.paramList = paramList;
        }

        /**
         * 获取访问修饰符。
         *
         * @return 表示访问修饰符的 {@link String}。
         */
        public String getAccessModifier() {
            return this.accessModifier;
        }

        /**
         * 获取返回类型。
         *
         * @return 表示返回类型的 {@link String}。
         */
        public String getReturnType() {
            return this.returnType;
        }

        /**
         * 获取类路径。
         *
         * @return 表示类路径的 {@link String}。
         */
        public String getClassPath() {
            return this.classPath;
        }

        /**
         * 获取方法名。
         *
         * @return 表示方法名的 {@link String}。
         */
        public String getMethodName() {
            return this.methodName;
        }

        /**
         * 获取参数列表。
         *
         * @return 表示参数列表的 {@link String}。
         */
        public String getParamList() {
            return this.paramList;
        }
    }
}
