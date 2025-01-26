/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.jvm.classfile.AccessFlag;
import modelengine.fitframework.util.LazyLoader;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示修饰符。
 *
 * @author 梁济时
 * @since 2023-01-13
 */
public enum Modifier {
    /**
     * 表示修饰符：public。
     */
    PUBLIC(AccessFlag.ACC_PUBLIC, "public"),

    /**
     * 表示修饰符：private。
     */
    PRIVATE(AccessFlag.ACC_PRIVATE, "private"),

    /**
     * 表示修饰符：protected。
     */
    PROTECTED(AccessFlag.ACC_PROTECTED, "protected"),

    /**
     * 表示修饰符：static。
     */
    STATIC(AccessFlag.ACC_STATIC, "static"),

    /**
     * 表示修饰符：final。
     */
    FINAL(AccessFlag.ACC_FINAL, "final"),

    /**
     * 表示修饰符：synchronized。
     */
    SYNCHRONIZED(AccessFlag.ACC_SYNCHRONIZED, "synchronized"),

    /**
     * 表示修饰符：volatile。
     */
    VOLATILE(AccessFlag.ACC_VOLATILE, "volatile"),

    /**
     * 表示修饰符：transient。
     */
    TRANSIENT(AccessFlag.ACC_TRANSIENT, "transient"),

    /**
     * 表示修饰符：native。
     */
    NATIVE(AccessFlag.ACC_NATIVE, "native"),

    /**
     * 表示修饰符：interface。
     */
    INTERFACE(AccessFlag.ACC_INTERFACE, "interface"),

    /**
     * 表示修饰符：abstract。
     */
    ABSTRACT(AccessFlag.ACC_ABSTRACT, "abstract"),

    /**
     * 表示修饰符：strict。
     */
    STRICT(AccessFlag.ACC_STRICT, "strict"),

    /**
     * 表示修饰符：synthetic。
     */
    SYNTHETIC(AccessFlag.ACC_SYNTHETIC, "synthetic"),

    /**
     * 表示修饰符：annotation。
     */
    ANNOTATION(AccessFlag.ACC_ANNOTATION, "annotation"),

    /**
     * 表示修饰符：enum。
     */
    ENUM(AccessFlag.ACC_ENUM, "enum"),

    /**
     * 表示修饰符：module。
     */
    MODULE(AccessFlag.ACC_MODULE, "module");

    private final AccessFlag flag;
    private final String descriptor;

    Modifier(AccessFlag flag, String descriptor) {
        this.flag = flag;
        this.descriptor = descriptor;
    }

    /**
     * 返回一个字符串，用以描述当前的修饰符。
     *
     * @return 表示描述当前修饰符的字符串的 {@link String}。
     */
    public String describe() {
        return this.descriptor;
    }

    private static final LazyLoader<Map<AccessFlag, Modifier>> MODIFIERS = new LazyLoader<>(Modifier::loadModifiers);

    private static Map<AccessFlag, Modifier> loadModifiers() {
        return Stream.of(values()).collect(Collectors.toMap(modifier -> modifier.flag, Function.identity()));
    }

    /**
     * 获取指定访问标记所对应的修饰符。
     *
     * @param flag 表示访问标记的 {@link AccessFlag}。
     * @return 表示访问标记所对应的修饰符的 {@link Modifier}。
     * @throws IllegalArgumentException 没有 {@code flag} 所对应的修饰符。
     */
    public static Modifier of(AccessFlag flag) {
        notNull(flag, "The access flag to convert to modifier cannot be null.");
        Modifier modifier = MODIFIERS.get().get(flag);
        if (modifier == null) {
            throw new IllegalArgumentException("Unknown access flag: " + flag);
        } else {
            return modifier;
        }
    }
}
