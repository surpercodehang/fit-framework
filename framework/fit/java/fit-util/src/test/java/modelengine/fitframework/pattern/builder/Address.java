/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.pattern.builder;

/**
 * 测试用的基本对象的接口。
 *
 * @author 季聿阶
 * @since 2022-06-22
 */
public interface Address {
    /**
     * 获取测试属性。
     *
     * @return 表示测试属性的 {@link String}。
     */
    String host();

    /**
     * 获取测试属性。
     *
     * @return 表示测试属性的 {@code int}。
     */
    int port();

    /**
     * 测试用的基本对象的构建器。
     */
    interface Builder {
        /**
         * 构建测试属性。
         *
         * @param host 表示待构建的属性的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder host(String host);

        /**
         * 构建测试属性。
         *
         * @param port 表示待构建的属性的 {@code int}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder port(int port);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link Address}。
         */
        Address build();
    }

    /**
     * 获取测试对象的构建器。
     *
     * @return 表示测试对象的构建器的 {@link Builder}。
     */
    static Builder builder() {
        return builder(null);
    }

    /**
     * 获取测试对象的构建器，同时将指定对象的值进行填充。
     *
     * @param address 表示指定对象的 {@link Address}。
     * @return 表示测试对象的构建器的 {@link Builder}。
     */
    static Builder builder(Address address) {
        return BuilderFactory.get(Address.class, Builder.class).create(address);
    }

    /**
     * 获取携带当前测试对象的属性值的新的构建器。
     *
     * @return 表示携带当前测试对象属性值的构建器的 {@link Builder}。
     */
    default Builder copy() {
        return builder(this);
    }
}
