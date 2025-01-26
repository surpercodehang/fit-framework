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
 * 表示对象可以被缓存。
 * <p>缓存的逻辑是：
 * <ul>
 *     <li>如果没有缓存，则调用指定对象，获取结果之后将结果缓存；</li>
 *     <li>如果有缓存，则直接使用缓存作为结果返回。</li>
 * </ul></p>
 *
 * @author 季聿阶
 * @since 2022-12-13
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {
    /**
     * 获取缓存实例的名字列表。
     *
     * @return 表示缓存实例名字列表的 {@link String}{@code []}。
     * @see #name()
     */
    @Forward(annotation = Cacheable.class, property = "name") String[] value() default {};

    /**
     * 获取缓存实例的名字列表。
     *
     * @return 表示缓存实例名字列表的 {@link String}{@code []}。
     */
    String[] name() default {};

    /**
     * 获取缓存对象的键的样式。
     * <p>该方法为<b>动态</b>获取缓存键的方法：
     * <ul>
     *     <li>如果键为 {@code ""}，则表示使用默认的缓存键生成方法。</li>
     *     <li>如果键为 {@code "#paramName"}，则表示使用参数名为 {@code 'paramName'} 的参数信息作为缓存的键。</li>
     * </ul></p>
     * <p>默认缓存键生成方法为：
     * <ul>
     *     <li>如果没有参数，则使用 {@link modelengine.fitframework.util.StringUtils#EMPTY} 的信息作为键；</li>
     *     <li>如果只有一个参数，则使用唯一的参数的信息作为键；</li>
     *     <li>如果有超过一个参数，则使用所有参数的整合信息作为键。</li>
     * </ul>
     * </p>
     *
     * @return 表示缓存对象键的样式的 {@link String}。
     */
    String key() default "";
}
