/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.descriptor;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 表示方法描述符。
 *
 * @author 梁济时
 * @since 2022-10-20
 */
public class MethodDescriptor {
    private final List<ClassDescriptor> parameterTypes;
    private final ClassDescriptor returnType;

    /**
     * 使用参数类型列表及返回值类型初始化 {@link MethodDescriptor} 类的新实例。
     *
     * @param parameterTypes 表示参数类型列表的 {@link List}{@code <}{@link ClassDescriptor}{@code >}。
     * @param returnType 表示返回值类型的 {@link ClassDescriptor}。
     */
    private MethodDescriptor(List<ClassDescriptor> parameterTypes, ClassDescriptor returnType) {
        this.parameterTypes = Collections.unmodifiableList(parameterTypes);
        this.returnType = returnType;
    }

    /**
     * 获取参数类型列表。
     *
     * @return 表示参数类型列表的 {@link List}{@code <}{@link ClassDescriptor}{@code >}。
     */
    public List<ClassDescriptor> parameterTypes() {
        return this.parameterTypes;
    }

    /**
     * 获取返回值类型。
     *
     * @return 表示返回值类型的 {@link ClassDescriptor}。
     */
    public ClassDescriptor returnType() {
        return this.returnType;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        for (ClassDescriptor descriptor : this.parameterTypes) {
            builder.append(descriptor.term());
        }
        builder.append(')').append(this.returnType.term());
        return builder.toString();
    }

    /**
     * 从指定字符序中解析方法描述符。
     *
     * @param chars 表示包含方法描述符信息的字符序的 {@link CharSequence}。
     * @return 表示解析到的方法描述符的 {@link MethodDescriptor}。
     * @throws IllegalArgumentException {@code chars} 为 {@code null} 或未包含有效的描述符信息。
     */
    public static MethodDescriptor parse(CharSequence chars) {
        notNull(chars, "The string to read method descriptor cannot be null.");
        if (chars.length() < 3) {
            throw new IllegalArgumentException(StringUtils.format(
                    "The text to parse method descriptor is too short. [text={0}]",
                    chars));
        }
        int parameterEnd;
        if (chars.charAt(0) != '(' || (parameterEnd = endOfParameters(chars)) < 0
                || parameterEnd == chars.length() - 1) {
            throw new IllegalArgumentException(StringUtils.format("Invalid format of method descriptor. [text={0}]",
                    chars));
        }
        List<ClassDescriptor> classDescriptors = new ClassDescriptor.Parser(chars, 1, parameterEnd).parseAll();
        if (classDescriptors.stream().anyMatch(descriptor -> descriptor instanceof ClassDescriptor.Void)) {
            throw new IllegalArgumentException(StringUtils.format(
                    "The void type cannot be used in parameters. [text={0}]",
                    chars));
        }
        List<ClassDescriptor> returnTypes = new ClassDescriptor.Parser(chars, parameterEnd + 1).parseAll();
        if (returnTypes.size() != 1) {
            throw new IllegalArgumentException(StringUtils.format(
                    "The return type must contain 1 and only 1 type. [text={0}]",
                    chars));
        }
        return new MethodDescriptor(classDescriptors, returnTypes.get(0));
    }

    /**
     * 创建一个方法描述符。
     *
     * @param parameterTypes 表示参数描述符列表的 {@link List}{@code <}{@link ClassDescriptor}{@code >}。
     * @param returnType 表示方法的返回类型的 {@link ClassDescriptor}。
     * @return 表示方法描述符的 {@link MethodDescriptor}。
     */
    public static MethodDescriptor create(List<ClassDescriptor> parameterTypes, ClassDescriptor returnType) {
        return new MethodDescriptor(parameterTypes, returnType);
    }

    private static int endOfParameters(CharSequence chars) {
        for (int i = 1; i < chars.length(); i++) {
            if (chars.charAt(i) == ')') {
                return i;
            }
        }
        return -1;
    }
}
