/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler;

import modelengine.fitframework.inspection.Nullable;
import modelengine.fitframework.pattern.builder.BuilderFactory;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

/**
 * 表示属性值的元数据。
 *
 * @author 邬涨财
 * @since 2022-12-29
 */
public interface PropertyValueMetadata {
    /**
     * 获取属性值的值名。
     *
     * @return 表示属性值的值名的 {@link String}。
     */
    String name();

    /**
     * 获取属性值的位置。
     *
     * @return 表示属性值的位置的 {@link Source}。
     */
    Source in();

    /**
     * 获取属性值的描述信息。
     *
     * @return 表示属性值的描述信息的 {@link String}。
     */
    String description();

    /**
     * 获取属性值的样例值。
     *
     * @return 表示属性值的样例值的 {@link String}。
     */
    String example();

    /**
     * 获取属性值的类型。
     *
     * @return 表示属性值类型的 {@link Type}。
     */
    Type type();

    /**
     * 获取属性值是否必须的属性值。
     *
     * @return 表示属性值是否必须的属性值的 {@code boolean}。
     */
    boolean isRequired();

    /**
     * 获取属性值的默认值属性。
     *
     * @return 表示属性值的默认值属性的 {@link Object}。
     */
    Object defaultValue();

    /**
     * 获取属性值的具体元素。
     *
     * @return 表示属性值的具体元素的 {@link AnnotatedElement}。
     */
    @Nullable
    AnnotatedElement element();

    /**
     * {@link PropertyValueMetadata} 的构建器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置属性值名。
         *
         * @param name 表示待设置的属性值名的 {@link Builder}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder name(String name);

        /**
         * 向当前构建器中设置属性值的位置。
         *
         * @param in 表示待设置的属性值的位置的 {@link Source}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder in(Source in);

        /**
         * 向当前构建器中设置属性值的描述信息。
         *
         * @param description 表示待设置的属性值的描述信息的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder description(String description);

        /**
         * 向当前构建器中设置属性值的样例值。
         *
         * @param example 表示待设置的属性值的样例值的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder example(String example);

        /**
         * 向当前构建器中设置属性值的类型。
         *
         * @param type 表示待设置的属性值的类型的 {@link Type}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder type(Type type);

        /**
         * 向当前构建器中设置属性值是否必须的属性值。
         *
         * @param isRequired 表示待设置的属性值是否必须的 {@code boolean}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder isRequired(boolean isRequired);

        /**
         * 向当前构建器中设置属性值的默认值。
         *
         * @param defaultValue 表示待设置的属性值的默认值的 {@link Object}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder defaultValue(Object defaultValue);

        /**
         * 向当前构建器中设置属性值的具体元素。
         *
         * @param element 表示待设置的属性值的具体元素的 {@link AnnotatedElement}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder element(AnnotatedElement element);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link PropertyValueMetadata}。
         */
        PropertyValueMetadata build();
    }

    /**
     * 获取 {@link PropertyValueMetadata} 的构建器。
     *
     * @return 表示 {@link PropertyValueMetadata} 的构建器的 {@link Builder}。
     */
    static Builder builder() {
        return builder(null);
    }

    /**
     * 获取 {@link PropertyValueMetadata} 的构建器，同时将指定对象的值进行填充。
     *
     * @param value 表示指定对象的 {@link PropertyValueMetadata}。
     * @return 表示 {@link PropertyValueMetadata} 的构建器的 {@link Builder}。
     */
    static Builder builder(PropertyValueMetadata value) {
        return BuilderFactory.get(PropertyValueMetadata.class, Builder.class).create(value);
    }
}
