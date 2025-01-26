/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven;

/**
 * {@link ResolvedDependencyPrinter} 的接口声明。
 *
 * @author 梁济时
 * @since 2020-10-09
 */
public interface ResolvedDependencyPrinter {
    /**
     * 打印解析后的依赖信息。
     *
     * @param dependency 表示依赖信息的 {@link ResolvedDependencyPrinter}。
     */
    void print(ResolvedDependency dependency);
}
