/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.parser.support;

import modelengine.fitframework.aop.interceptor.aspect.parser.ExpressionParser;
import modelengine.fitframework.aop.interceptor.aspect.parser.model.PointcutSupportedType;
import modelengine.fitframework.aop.interceptor.aspect.util.ExpressionUtils;
import modelengine.fitframework.util.LazyLoader;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * 切点表达式解析器的通用抽象实现。
 *
 * @author 白鹏坤
 * @since 2023-04-06
 */
public abstract class BaseParser implements ExpressionParser {
    /**
     * 逻辑运算符类型集合。
     */
    public static final Set<String> logicReferenceTypes = new HashSet<>();

    static {
        logicReferenceTypes.add(PointcutSupportedType.AND.getValue());
        logicReferenceTypes.add(PointcutSupportedType.OR.getValue());
        logicReferenceTypes.add(PointcutSupportedType.NOT.getValue());
        logicReferenceTypes.add(PointcutSupportedType.LEFT_BRACKET.getValue());
        logicReferenceTypes.add(PointcutSupportedType.RIGHT_BRACKET.getValue());
    }

    /**
     * 获取解析器类型。
     *
     * @return 返回解析器类型的 {@link PointcutSupportedType}。
     */
    protected abstract PointcutSupportedType parserType();

    /**
     * 根据解析内容，创建具体的解析器。
     *
     * @param content 解析器待解析内容的 {@link String}。
     * @return 返回解析结果的 {@link Result}。
     */
    protected abstract Result createConcreteParser(String content);

    @Override
    public boolean couldParse(String expression) {
        return expression.startsWith(this.parserType().getValue());
    }

    @Override
    public Result parse(String expression) {
        String replacedContent = expression;
        if (!logicReferenceTypes.contains(expression)) {
            replacedContent =
                    ExpressionParser.getParseContent(expression, this.parserType().getValue()).replaceAll(" ", "");
        }
        return this.createConcreteParser(replacedContent);
    }

    abstract class BaseResult implements Result {
        /**
         * 解析器待解析的内容。
         */
        protected final String content;
        private final ClassLoader classLoader;
        private final LazyLoader<Boolean> isBindingLoader;

        public BaseResult(String content, ClassLoader classLoader) {
            this.content = content;
            this.classLoader = classLoader;
            this.isBindingLoader =
                    new LazyLoader<>(() -> ExpressionUtils.getContentClass(this.content, this.classLoader) == null);
        }

        @Override
        public boolean couldMatch(Class<?> beanClass) {
            return false;
        }

        @Override
        public boolean match(Method method) {
            return false;
        }

        @Override
        public boolean isBinding() {
            return this.isBindingLoader.get();
        }

        @Override
        public PointcutSupportedType type() {
            return BaseParser.this.parserType();
        }

        @Override
        public Object content() {
            return this.content;
        }
    }
}
