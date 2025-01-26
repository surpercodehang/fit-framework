/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc;

/**
 * 为 {@link DependencyResolvingResult} 提供解析失败的结果。
 *
 * @author 梁济时
 * @since 2022-06-27
 */
public final class DependencyResolvingFailureResult implements DependencyResolvingResult {
    /**
     * 获取解析结果的唯一实例。
     */
    public static final DependencyResolvingFailureResult INSTANCE = new DependencyResolvingFailureResult();

    /**
     * 隐藏默认构造方法，避免单例对象被实例化。
     */
    private DependencyResolvingFailureResult() {}

    @Override
    public boolean resolved() {
        return false;
    }

    @Override
    public Object get() {
        return null;
    }

    @Override
    public String toString() {
        return "non-dependency";
    }
}
