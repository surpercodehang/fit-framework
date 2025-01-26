/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.test;

import java.util.function.Predicate;

/**
 * jvm 模块测试文件
 *
 * @since 2023-01-04
 */
@AssignmentTarget(
        byteValue = 3,
        charValue = 'c',
        doubleValue = 12.1,
        floatValue = 4,
        integerValue = 23,
        longValue = 5,
        shortValue = 6,
        booleanValue = true,
        stringValue = "stringValue",
        fruitColor = AssignmentTarget.EnumValue.BLUE,
        arrayValue = {"a", "b"},
        classValue = String.class,
        annotationValue = @NestAssignmentTarget(integerValue = 1)
)
public class AttributeTarget implements Comparable {
    /**
     * 定义一个静态常量，用于测试。
     */
    public static final int INT_TEST = 10;

    /**
     * 定义一个字节类型的变量，用于测试。
     */
    public final byte byteTest = 0;

    /**
     * 定义一个字符类型的变量，用于测试。
     */
    public final char charTest = 'a';

    /**
     * 定义一个长整型的变量，用于测试。
     */
    protected final long longTest = 11L;

    /**
     * 定义一个短整型的变量，用于测试。
     */
    protected final short shortTest = 13;

    /**
     * 定义一个布尔类型的变量，用于测试。
     */
    protected final boolean boolTest = true;
    private final double doubleTest = 12.1d;
    private final float floatTest = 11.2f;
    private final String strTest = "aaaa";
    private final String[] arrTest = {"a", "b"};
    private final Predicate invokeDynamic = (ele) -> true;

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    private Object test() {
        return INT_TEST;
    }
}
