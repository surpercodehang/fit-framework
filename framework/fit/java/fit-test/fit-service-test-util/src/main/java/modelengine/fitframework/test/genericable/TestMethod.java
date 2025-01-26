/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.test.genericable;

import modelengine.fitframework.pattern.builder.BuilderFactory;

import java.lang.reflect.Method;

/**
 * 表示测试方法的信息。
 *
 * @author 季聿阶
 * @since 2022-09-10
 */
public interface TestMethod {
    /**
     * 获取测试方法的显示名称。
     *
     * @return 表示测试方法的显示名称的 {@link String}。
     */
    String displayName();

    /**
     * 获取测试方法。
     *
     * @return 表示测试方法的 {@link Method}。
     */
    Method method();

    /**
     * {@link TestMethod} 的构建器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置测试方法的显示名称。
         *
         * @param displayName 表示待设置的测试方法的显示名称的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder displayName(String displayName);

        /**
         * 向当前构建器中设置测试方法。
         *
         * @param method 表示待设置的测试方法的 {@link Method}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder method(Method method);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link TestMethod}。
         */
        TestMethod build();
    }

    /**
     * 获取 {@link TestMethod} 的构建器。
     *
     * @return 表示 {@link TestMethod} 的构建器的 {@link Builder}。
     */
    static Builder builder() {
        return builder(null);
    }

    /**
     * 获取 {@link TestMethod} 的构建器，同时将指定对象的值进行填充。
     *
     * @param value 表示指定对象的 {@link TestMethod}。
     * @return 表示 {@link TestMethod} 的构建器的 {@link Builder}。
     */
    static Builder builder(TestMethod value) {
        return BuilderFactory.get(TestMethod.class, Builder.class).create(value);
    }
}
