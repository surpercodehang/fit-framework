/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.aop.util;

import modelengine.fitframework.inspection.Validation;

import java.util.Objects;
import java.util.Optional;

/**
 * 类加载器相关的工具类。
 *
 * @author 邬涨财
 * @since 2023-03-25
 */
public class ClassLoaderUtils {
    /**
     * 获得两个类加载器公共的子类加载器。
     * <p>该方法查找两个类加载器是否在同一链上，若在同一链上，返回其中的子类加载器。若不在同一条链上，返回空。</p>
     * <pre>例如：
     *    classLoader：A -> B -> C -> D，此处箭头指向类加载器的父节点，即 A.getParent() == B，
     *    class1.classLoader = A，
     *    class2.classLoader = C，
     *    getCommonChildClassLoader(class1, class2) 返回值为 A。
     * </pre>
     *
     * @param class1 表示第一个类加载器的 {@link Class}{@code <?>}。
     * @param class2 表示第二个类加载器的 {@link Class}{@code <?>}。
     * @return 表示获取到的公共子类加载器的 {@link Optional}{@code <}{@link ClassLoader}{@code >}。
     */
    public static Optional<ClassLoader> getCommonChildClassLoader(Class<?> class1, Class<?> class2) {
        Validation.notNull(class1, "The first class cannot not be null when get child class loader.");
        Validation.notNull(class2, "The second class cannot not be null when get child class loader.");
        ClassLoader classLoader1 = class1.getClassLoader();
        ClassLoader classLoader2 = class2.getClassLoader();
        if (classLoader1 == null) {
            return Optional.of(classLoader2);
        }
        if (classLoader2 == null) {
            return Optional.of(classLoader1);
        }
        ClassLoader tmp = classLoader1;
        while (tmp != null) {
            if (Objects.equals(tmp, classLoader2)) {
                return Optional.of(classLoader1);
            }
            tmp = tmp.getParent();
        }
        tmp = classLoader2;
        while (tmp != null) {
            if (Objects.equals(tmp, classLoader1)) {
                return Optional.of(classLoader2);
            }
            tmp = tmp.getParent();
        }
        return Optional.empty();
    }
}