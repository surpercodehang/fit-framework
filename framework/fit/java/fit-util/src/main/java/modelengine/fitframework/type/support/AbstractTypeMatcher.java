/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.type.support;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.type.TypeMatcher;
import modelengine.fitframework.util.ObjectUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 为类型匹配程序提供基类。
 *
 * @author 梁济时
 * @since 2020-10-29
 */
public abstract class AbstractTypeMatcher<C extends Type, E extends Type> implements TypeMatcher {
    private final C currentType;

    private final TypeMatcher.Context context;

    /**
     * 使用对象类型和上下文信息初始化 {@link AbstractTypeMatcher} 类的新实例。
     *
     * @param currentType 表示对象类型的 {@link Type}。
     * @param context 表示上下文信息的 {@link TypeMatcher.Context}。
     */
    public AbstractTypeMatcher(C currentType, TypeMatcher.Context context) {
        this.currentType = Validation.notNull(currentType, "The object type to match cannot be null.");
        this.context = Validation.notNull(context, "The context to match cannot be null.");
    }

    public C getCurrentType() {
        return this.currentType;
    }

    public Context getContext() {
        return this.context;
    }

    @Override
    public boolean match(Type expectedType) {
        return this.match0(ObjectUtils.cast(expectedType));
    }

    /**
     * 检查是否可以匹配到指定的期望类型。
     *
     * @param expectedType 表示期望的类型的 {@link Type}。
     * @return 若可以匹配到期望类型，则为 {@code true}；否则为 {@code false}。
     */
    protected abstract boolean match0(E expectedType);

    /**
     * 列出指定类型的所有父类型。
     *
     * @param clazz 表示当前类型的 {@link Class}。
     * @return 表示所有父类型的列表的 {@link List}{@code <}{@link Type}{@code >}。
     */
    protected List<Type> listSuperTypes(Class<?> clazz) {
        Type[] interfaces = clazz.getGenericInterfaces();
        Type superClass = clazz.getGenericSuperclass();
        if (superClass == null) {
            return Arrays.asList(interfaces);
        }
        List<Type> superTypes = new ArrayList<>(interfaces.length + 1);
        superTypes.addAll(Arrays.asList(interfaces));
        superTypes.add(superClass);
        return superTypes;
    }

    /**
     * 匹配指定类型的父类型是否匹配期望的类型。
     * <p>若其任意父类型可以匹配到期望类型，则表示可以匹配到期望类型。</p>
     *
     * @param clazz 表示当前类型的 {@link Class}。
     * @param expectedType 表示期望的类型的 {@link Type}。
     * @return 若存在可以匹配到期望类型的父类型，则为 {@code true}；否则为 {@code false}。
     */
    protected boolean matchSuperTypes(Class<?> clazz, Type expectedType) {
        TypeMatcher.Context superContext = this.getSuperContext();
        return this.listSuperTypes(clazz)
                .stream()
                .anyMatch(superType -> TypeMatcher.match(superType, expectedType, superContext));
    }

    /**
     * 获取父类的匹配上下文信息。
     *
     * @return 表示父类的匹配上下文信息的 {@link Context}。
     */
    protected TypeMatcher.Context getSuperContext() {
        return this.getContext();
    }
}
