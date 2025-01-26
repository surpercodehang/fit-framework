/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.test;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * jvm 模块测试文件
 *
 * @author 郭龙飞
 * @since 2023-01-04
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NestAssignmentTarget {
    int integerValue() default 0;
}
