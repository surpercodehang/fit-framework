/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static modelengine.fitframework.inspection.Validation.notNull;

import java.net.URL;
import java.security.CodeSource;

/**
 * 为类型提供工具方法。
 *
 * @author 季聿阶
 * @since 2021-02-24
 */
public class ClassUtils {
    /**
     * 表示包的分隔符。
     */
    public static final char PACKAGE_SEPARATOR = '.';

    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private ClassUtils() {}

    /**
     * 判断目标类型是否是指定类型的父类或本身。
     *
     * @param targetClass 表示目标类型的 {@link Class}{@code <}{@link Object}{@code >}。
     * @param fromClass 表示指定类型的 {@link Class}{@code <}{@link Object}{@code >}。
     * @return 表示判断结果的 {@code boolean}。
     */
    public static boolean isAssignableFrom(Class<?> targetClass, Class<?> fromClass) {
        notNull(fromClass, "The fromClass cannot be null.");
        if (targetClass == null) {
            return false;
        }
        return targetClass.isAssignableFrom(fromClass);
    }

    /**
     * 判断指定类型是否是一个由 JVM 创建出来的（而非用户定义的）类型，比如说 Lambda 表达式。
     *
     * @param clazz 表示指定类型的 {@link Class}{@code <}{@link Object}{@code >}。
     * @return 如果是一个由 JVM 创建出来的（而非用户定义的）类型，则返回 {@code true}，否则，返回 {@code false}。
     */
    public static boolean isLambda(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return (clazz.getSuperclass() == Object.class) && (clazz.getInterfaces().length > 0) && clazz.isSynthetic()
                && clazz.getName().contains("$$Lambda");
    }

    /**
     * 定位指定类型的保护域。
     *
     * @param clazz 表示待定位保护域的类型的 {@link Class}。
     * @return 表示保护域的位置的 {@link URL}。
     * @throws IllegalArgumentException 当 {@code clazz} 为 {@code null} 时。
     * @throws IllegalStateException 当没有 {@link CodeSource} 与类型的保护域关联时。
     */
    public static URL locateOfProtectionDomain(Class<?> clazz) {
        notNull(clazz, "The class to locate protection domain cannot be null.");
        CodeSource source = clazz.getProtectionDomain().getCodeSource();
        if (source == null) {
            throw new IllegalStateException(StringUtils.format("No code source related to the class. [class={0}]",
                    clazz.getName()));
        }
        return source.getLocation();
    }

    /**
     * 尝试使用指定的类加载器加载指定的类。
     *
     * @param loader 表示类加载器的 {@link ClassLoader}。
     * @param className 表示类的全限定名的 {@link String}。
     * @return 表示加载结果的 {@link Class}{@code <}{@link Object}{@code >}。
     */
    public static Class<?> tryLoadClass(ClassLoader loader, String className) {
        notNull(loader, "The class loader to load class cannot be null.");
        notNull(className, "The name of class to load cannot be null.");
        try {
            return loader.loadClass(className);
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }
}
