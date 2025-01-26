/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.type.support;

import modelengine.fitframework.type.TypeMatcher;
import modelengine.fitframework.type.annotation.MatchTypes;

import java.lang.reflect.Type;

/**
 * 为对象类型和期望类型都为 {@link Class} 的情况提供匹配判定程序。
 *
 * @author 梁济时
 * @since 2020-10-29
 */
@MatchTypes(current = Class.class, expected = Class.class, factory = ClassMatcher.Factory.class)
public class ClassMatcher extends AbstractTypeMatcher<Class<?>, Class<?>> {
    /**
     * 使用对象类型和上下文信息初始化 {@link ClassMatcher} 类的新实例。
     *
     * @param currentType 表示对象类型的 {@link Class}。
     * @param context 表示上下文信息的 {@link TypeMatcher.Context}。
     */
    public ClassMatcher(Class<?> currentType, TypeMatcher.Context context) {
        super(currentType, context);
    }

    @Override
    protected boolean match0(Class<?> expectedType) {
        return expectedType.isAssignableFrom(this.getCurrentType());
    }

    /**
     * 为创建 {@link ClassMatcher} 实例提供工厂。
     *
     * @author 梁济时
     * @since 2020-10-29
     */
    public static class Factory implements TypeMatcher.Factory {
        @Override
        public TypeMatcher create(Type currentType, TypeMatcher.Context context) {
            return new ClassMatcher((Class<?>) currentType, context);
        }
    }
}
