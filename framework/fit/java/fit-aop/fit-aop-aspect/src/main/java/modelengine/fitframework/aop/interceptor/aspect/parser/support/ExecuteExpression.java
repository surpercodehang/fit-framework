/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

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

        public ExecutionModel(String accessModifier, String returnType, String classPath, String methodName,
                String paramList) {
            this.accessModifier = accessModifier;
            this.returnType = returnType;
            this.classPath = classPath;
            this.methodName = methodName;
            this.paramList = paramList;
        }

        public String getAccessModifier() {
            return this.accessModifier;
        }

        public String getReturnType() {
            return this.returnType;
        }

        public String getClassPath() {
            return this.classPath;
        }

        public String getMethodName() {
            return this.methodName;
        }

        public String getParamList() {
            return this.paramList;
        }
    }
}
