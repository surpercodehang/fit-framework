/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.test.annotation;

import modelengine.fitframework.test.adapter.north.junit5.FitExtension;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 支持 Fit 测试框架的指定注解。
 * <p>当前注解支持 Junit5 使用。</p>
 * <p> Junit5 案例：</p>
 * <pre>
 *  1:   @FitTestWithJunit(classes = {xxx.class})
 *  2:   public class DemoTest {
 *  3:   }
 * </pre>
 *
 * @author 邬涨财
 * @since 2023-01-17
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(FitExtension.class)
public @interface FitTestWithJunit {
    /**
     * 获取需要注入到容器中的组件类型的数组。
     *
     * @return 表示需要注入到容器中的组件类型数组的 {@link Class}{@code <?>[]}。
     */
    Class<?>[] includeClasses() default {};

    /**
     * 获取不需要注入到容器中的组件类型的数组。
     *
     * @return 表示不需要注入到容器中的组件类型数组的 {@link Class}{@code <?>[]}。
     */
    Class<?>[] excludeClasses() default {};
}
