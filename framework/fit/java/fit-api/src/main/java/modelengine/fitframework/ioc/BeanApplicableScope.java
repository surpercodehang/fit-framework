/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc;

/**
 * 为 Bean 提供可用范围的定义。
 *
 * @author 梁济时
 * @since 2022-08-30
 */
public enum BeanApplicableScope implements Comparable<BeanApplicableScope> {
    /**
     * 表示可以在任何地方被使用。
     */
    ANYWHERE(Integer.MAX_VALUE),

    /**
     * 表示只能在当前容器或其子容器中被使用。
     */
    CHILDREN(1),

    /**
     * 表示只能在当前容器中使用。
     */
    CURRENT(0),

    /**
     * 表示Bean对在何处使用并不敏感。
     * <p>与 {@link #ANYWHERE} 不同的是，{@link #ANYWHERE} 明确地描述了可以在任何地方使用。</p>
     */
    INSENSITIVE(Integer.MIN_VALUE);

    private final int value;

    BeanApplicableScope(int value) {
        this.value = value;
    }

    /**
     * 获取当前范围的值。
     * <p>值越大的范围越大。</p>
     *
     * @return 表示范围的值的 {@code int}。
     */
    public int value() {
        return this.value;
    }
}
