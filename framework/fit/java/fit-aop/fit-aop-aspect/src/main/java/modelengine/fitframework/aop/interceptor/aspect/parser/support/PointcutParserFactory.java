/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.parser.support;

import modelengine.fitframework.aop.interceptor.aspect.parser.ExpressionParser;
import modelengine.fitframework.aop.interceptor.aspect.parser.PointcutParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * 表达式解析器的工厂。
 *
 * @author 郭龙飞
 * @since 2023-03-23
 */
public class PointcutParserFactory {
    private final List<ExpressionParser> expressionList = new ArrayList<>();
    private final Class<?> aspectClass;
    private final PointcutParameter[] parameters;
    private final ClassLoader classLoader;

    private PointcutParserFactory(Class<?> aspectClass, PointcutParameter[] parameters) {
        this.aspectClass = aspectClass;
        this.parameters = parameters;
        this.classLoader = aspectClass.getClassLoader();
    }

    /**
     * 添加表达式解析工具，ReferenceParser 必须最后添加，原因是正则识别，范围比较大，放前面容易误识别。
     *
     * @return 表达式解析工具列表
     */
    public List<ExpressionParser> build() {
        this.expressionList.add(new AndParser());
        this.expressionList.add(new NotParser());
        this.expressionList.add(new OrParser());
        this.expressionList.add(new WithinParser());
        this.expressionList.add(new ExecutionParser(this.classLoader));
        this.expressionList.add(new ArgsParser(this.parameters, this.classLoader));
        this.expressionList.add(new AtAnnotationParser(this.parameters, this.classLoader));
        this.expressionList.add(new AtArgsParser(this.parameters, this.classLoader));
        this.expressionList.add(new AtParamsParser(this.parameters, this.classLoader));
        this.expressionList.add(new AtTargetParser(this.parameters, this.classLoader));
        this.expressionList.add(new AtWithinParser(this.parameters, this.classLoader));
        this.expressionList.add(new TargetParser(this.parameters, this.classLoader));
        this.expressionList.add(new ThisParser(this.parameters, this.classLoader));
        this.expressionList.add(new ReferenceParser(this.aspectClass, this.parameters));
        return this.expressionList;
    }

    /**
     * 创建关键字解析工厂类。
     *
     * @param aspectClass 表示切面定义所在类的类型的 {@link Class}{@code <}{@link Object}{@code >}。
     * @param parameters 表示表达式中所有定义的参数列表的 {@link PointcutParameter}{@code []}。
     * @return 解析工厂类的 {@link PointcutParserFactory}。
     */
    public static PointcutParserFactory create(Class<?> aspectClass, PointcutParameter[] parameters) {
        return new PointcutParserFactory(aspectClass, parameters);
    }
}
