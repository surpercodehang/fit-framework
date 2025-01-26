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
public @interface AssignmentTarget {
    byte byteValue() default 2;

    char charValue() default 'a';

    double doubleValue() default 0.0;

    float floatValue() default 0;

    int integerValue() default 0;

    long longValue() default 0;

    short shortValue() default 0;

    boolean booleanValue() default false;

    String stringValue() default "";

    enum EnumValue {BLUE, RED, GREEN}

    EnumValue fruitColor() default EnumValue.GREEN;

    String[] arrayValue() default {};

    Class classValue();

    NestAssignmentTarget annotationValue();
}
