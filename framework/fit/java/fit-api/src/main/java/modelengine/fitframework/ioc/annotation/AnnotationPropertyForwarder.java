/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * 为注解提供转发程序。
 * <p>当需要扩展该接口时，需要借助Java SPI机制：</p>
 * <ul>
 *     <li>首先，实现该接口</li>
 *     <li>然后，在JAR包中的 {@code META-INF/services} 目录下增加
 *     {@code modelengine.fitframework.ioc.annotation.AnnotationPropertyForwarder} 文件</li>
 *     <li>最后，将实现类的全名写入到该文件中，若存在多个实现类，每个实现类单独一行</li>
 * </ul>
 * <note>实现类所在的JAR需要与 {@code fit-ioc} 的JAR在相同的 {@link ClassLoader} 中被加载。</note>
 *
 * @author 梁济时
 * @since 2022-05-03
 */
public interface AnnotationPropertyForwarder {
    /**
     * 获取指定注解属性方法定义的转发程序。
     *
     * @param propertyMethod 表示注解属性方法的 {@link Method}。
     * @return 若定义了转发程序，则表示该转发程序的 {@link Optional}{@code <}{@link AnnotationProperty}{@code >}。否则为
     * {@link Optional#empty()}。
     */
    Optional<AnnotationPropertyForward> forward(Method propertyMethod);
}
