/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.constant;

import modelengine.fitframework.jvm.classfile.lang.U1;

import java.util.EnumSet;

/**
 * 表示引用的类型。
 *
 * @author 梁济时
 * @since 2022-06-07
 */
public enum ReferenceKind {
    /**
     * 获取字段的值。
     */
    REF_GET_FIELD(U1.of(1), "REF_getField"),

    /**
     * 获取静态字段的值。
     */
    REF_GET_STATIC(U1.of(2), "REF_getStatic"),

    /**
     * 设置字段的值。
     */
    REF_PUT_FIELD(U1.of(3), "REF_putField"),

    /**
     * 设置静态字段的值。
     */
    REF_PUT_STATIC(U1.of(4), "REF_putStatic"),

    /**
     * 执行虚方法。
     */
    REF_INVOKE_VIRTUAL(U1.of(5), "REF_invokeVirtual"),

    /**
     * 执行静态方法。
     */
    REF_INVOKE_STATIC(U1.of(6), "REF_invokeStatic"),

    /**
     * 调用特定方法。（构造方法、私有方法、父类方法等）
     */
    REF_INVOKE_SPECIAL(U1.of(7), "REF_invokeSpecial"),

    /**
     * 调用构造方法。
     */
    REF_NEW_INVOKE_SPECIAL(U1.of(8), "REF_newInvokeSpecial"),

    /**
     * 调用接口方法。
     */
    REF_INVOKE_INTERFACE(U1.of(9), "REF_invokeInterface");

    private final U1 value;
    private final String description;

    ReferenceKind(U1 value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 获取引用类型的值。
     *
     * @return 表示引用类型的值的 {@link U1}。
     */
    public U1 value() {
        return this.value;
    }

    /**
     * 获取引用类型的描述信息。
     *
     * @return 表示描述信息的 {@link String}。
     */
    public String description() {
        return this.description;
    }

    /**
     * 获取具备指定值的引用类型。
     *
     * @param value 表示引用类型的值的 {@link U1}。
     * @return 若存在该值对应的引用类型，则为表示该引用类型的 {@link ReferenceKind}，否则为 {@code null}。
     */
    public static ReferenceKind of(U1 value) {
        EnumSet<ReferenceKind> elements = EnumSet.allOf(ReferenceKind.class);
        for (ReferenceKind element : elements) {
            if (element.value.equals(value)) {
                return element;
            }
        }
        return null;
    }
}
