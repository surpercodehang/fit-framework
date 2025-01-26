/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.serialization.tlv;

import static modelengine.fitframework.inspection.Validation.isTrue;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 表示用于校验静态私有变量值无重复的检查器。
 * <p>需要校验静态私有变量值无重复的类需要继承本类，并且在其静态初始化块中调用 {@link TagValuesChecker#validate(Class)} 方法。</p>
 *
 * @author 何天放
 * @since 2024-05-13
 */
public abstract class TagValuesChecker {
    /**
     * 校验静态私有变量值是否重复，并且在存在重复时抛出异常。
     *
     * @param clazz 表示待校验的类的 {@link Class}{@code <?>}。
     */
    protected static void validate(Class<?> clazz) {
        notNull(clazz, "The class to check cannot be null.");
        isTrue(TagValuesChecker.class.isAssignableFrom(clazz),
                "The class to check is not TagValuesChecker. [class={0}]",
                clazz.getName());
        Set<Integer> values = new HashSet<>();
        for (Field field : findConstants(clazz)) {
            int value = ObjectUtils.cast(ReflectionUtils.getField(null, field));
            if (values.contains(value)) {
                throw new IllegalStateException(StringUtils.format(
                        "The private static field conflict. [field={0}, value={1}]",
                        field.getName(),
                        value));
            }
            values.add(value);
        }
    }

    private static List<Field> findConstants(Class<?> clazz) {
        return Arrays.stream(ReflectionUtils.getDeclaredFields(clazz, true))
                .filter(field -> Modifier.isPrivate(field.getModifiers()) && Modifier.isStatic(field.getModifiers())
                        && Modifier.isFinal(field.getModifiers()) && Objects.equals(field.getType(), int.class))
                .collect(Collectors.toList());
    }
}
