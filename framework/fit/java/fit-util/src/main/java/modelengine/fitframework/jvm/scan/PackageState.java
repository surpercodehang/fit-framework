/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.scan;

import modelengine.fitframework.jvm.scan.support.DefaultPackageState;

/**
 * 为类型的自动扫描提供包的状态管理。
 *
 * @author 梁济时
 * @since 2023-02-01
 */
public interface PackageState {
    /**
     * 获取一个值，该值指示指定包是否已经被扫描。
     *
     * @param basePackage 表示待检查的包的 {@link String}。
     * @return 若包已经被扫描，则为 {@code true}，否则为 {@code false}。
     */
    boolean get(String basePackage);

    /**
     * 设置一个标记，指示指定的包已经被扫描。
     *
     * @param basePackage 表示待标记的包的 {@link String}。
     */
    void set(String basePackage);

    /**
     * 创建一个包状态的新实例。
     *
     * @return 表示包状态的新实例的 {@link PackageState}。
     */
    static PackageState create() {
        return new DefaultPackageState();
    }
}
