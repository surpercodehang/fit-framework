/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.interceptor.aspect.parser.support;

import modelengine.fitframework.aop.interceptor.aspect.parser.model.PointcutSupportedType;
import modelengine.fitframework.aop.interceptor.aspect.util.ExpressionUtils;

import java.lang.reflect.Method;

/**
 * 解析切点表达式中关键字 within 的解析器。
 * <P>用于匹配 within 表达式中包路径以及子包下任意类任意方法，支持通配符。</p>
 *
 * @author 郭龙飞
 * @since 2023-03-14
 */
public class WithinParser extends BaseParser {
    @Override
    protected PointcutSupportedType parserType() {
        return PointcutSupportedType.WITHIN;
    }

    @Override
    protected Result createConcreteParser(String content) {
        return new WithinResult(content);
    }

    class WithinResult extends BaseParser.BaseResult {
        public WithinResult(String content) {
            super(content, null);
        }

        @Override
        public boolean couldMatch(Class<?> beanClass) {
            String content = this.content().toString();
            final String replaceRegex = ExpressionUtils.expressionReplaceRegex(content);
            return beanClass.getName().matches(replaceRegex);
        }

        @Override
        public boolean match(Method method) {
            return true;
        }

        @Override
        public boolean isBinding() {
            return false;
        }
    }
}
