/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile;

import modelengine.fitframework.jvm.classfile.lang.U2;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 表示访问标识。
 *
 * @author 梁济时
 * @since 2022-06-09
 */
public enum AccessFlag {
    /**
     * {@code public}。
     */
    ACC_PUBLIC(0x0001),

    /**
     * {@code private}。
     */
    ACC_PRIVATE(0x0002),

    /**
     * {@code protected}。
     */
    ACC_PROTECTED(0x0004),

    /**
     * {@code static}。
     */
    ACC_STATIC(0x0008),

    /**
     * {@code final}。
     */
    ACC_FINAL(0x0010),

    /**
     * {@code synchronized}。
     */
    ACC_SYNCHRONIZED(0x0020),

    /**
     * {@code volatile}。
     */
    ACC_VOLATILE(0x0040),

    /**
     * {@code transient}。
     */
    ACC_TRANSIENT(0x0080),

    /**
     * {@code native}。
     */
    ACC_NATIVE(0x0100),

    /**
     * {@code interface}。
     */
    ACC_INTERFACE(0x0200),

    /**
     * {@code abstract}。
     */
    ACC_ABSTRACT(0x0400),

    /**
     * {@code strict}。
     */
    ACC_STRICT(0x0800),

    /**
     * {@code synthetic}。
     */
    ACC_SYNTHETIC(0x1000),

    /**
     * {@code annotation}。
     */
    ACC_ANNOTATION(0x2000),

    /**
     * {@code enum}。
     */
    ACC_ENUM(0x4000),

    /**
     * {@code module}。
     */
    ACC_MODULE(0x8000);

    private final U2 value;

    AccessFlag(int value) {
        this.value = U2.of(value);
    }

    /**
     * 获取访问标记的值。
     *
     * @return 表示访问标记的值的 {@link U2}。
     */
    public U2 value() {
        return this.value;
    }

    /**
     * 获取指定值中所定义的方法标记的列表。
     *
     * @param value 表示访问标记的值的 {@link U2}。
     * @return 表示定义的访问标记的 {@link Set}{@code <}{@link AccessFlag}{@code >}。
     */
    public static Set<AccessFlag> of(U2 value) {
        return EnumSet.allOf(AccessFlag.class).stream()
                .filter(flag -> flag.value.and(value).equals(flag.value))
                .collect(Collectors.toSet());
    }
}
