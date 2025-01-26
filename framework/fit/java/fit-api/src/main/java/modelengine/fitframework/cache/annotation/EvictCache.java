/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.cache.annotation;

import modelengine.fitframework.annotation.Forward;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示将对应的缓存删除。
 *
 * @author 季聿阶
 * @since 2022-12-13
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EvictCache {
    /**
     * 获取缓存实例的名字列表。
     *
     * @return 表示缓存实例名字列表的 {@link String}{@code []}。
     * @see #name()
     */
    @Forward(annotation = EvictCache.class, property = "name") String[] value() default {};

    /**
     * 获取缓存实例的名字列表。
     *
     * @return 表示缓存实例名字列表的 {@link String}{@code []}。
     */
    String[] name() default {};

    /**
     * 获取缓存对象的键的样式。
     *
     * @return 表示缓存对象键的样式的 {@link String}。
     * @see Cacheable#key()
     */
    String key() default "";
}
