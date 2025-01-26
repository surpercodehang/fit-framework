/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.data.repository.annotation;

import modelengine.fitframework.util.StringUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 表示返回值的替换信息。
 *
 * @author 季聿阶
 * @since 2024-01-21
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ReturnValue {
    /**
     * 表示需要替换的返回值对象属性的路径。
     *
     * @return 表示返回值对象属性的路径的 {@link String}。
     */
    String path() default StringUtils.EMPTY;
}
