/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.beans;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ReflectionUtils;

import java.util.Set;

/**
 * Bean 的工具类。
 *
 * @author 季聿阶
 * @since 2023-02-07
 */
public class BeanUtils {
    /**
     * 将来源对象的属性拷贝到目标对象中去。
     *
     * @param source 表示来源对象的 {@link Object}。
     * @param target 表示目标对象的 {@link Object}。
     * @throws IllegalArgumentException 当 {@code source} 或 {@code target} 为 {@code null} 时。
     */
    public static void copyProperties(Object source, Object target) {
        notNull(source, "The source object cannot be null.");
        notNull(target, "The target object cannot be null.");
        BeanAccessor srcAccessor = BeanAccessor.of(source.getClass());
        BeanAccessor dstAccessor = BeanAccessor.of(target.getClass());
        Set<String> properties = CollectionUtils.intersect(srcAccessor.properties(), dstAccessor.properties());
        properties.remove("class");
        for (String property : properties) {
            Object value = srcAccessor.get(source, property);
            dstAccessor.set(target, property, value);
        }
    }

    /**
     * 提供目标类型，将来源对象的属性拷贝到目标对象中去。
     *
     * @param source 表示来源对象的 {@link Object}。
     * @param type 表示目标对象的 {@link Class}。
     * @return 表示目标对象的实例。
     * @throws IllegalArgumentException 当 {@code source} 或 {@code target} 为 {@code null} 时。
     */
    public static <T> T copyProperties(Object source, Class<T> type) {
        notNull(type, "The target type cannot be null.");
        Object target = ReflectionUtils.instantiate(type);
        BeanUtils.copyProperties(source, target);
        return cast(target);
    }
}
