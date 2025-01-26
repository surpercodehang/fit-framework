/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.tool;

/**
 * 表示创建工具的工厂。
 *
 * @author 王攀博
 * @since 2024-04-23
 */
public interface ToolFactory {
    /**
     * 获取工厂支持的工具类型。
     * <p>工具类型通过工具的标签体现。</p>
     *
     * @return 表示工厂支持的工具类型的 {@link String}。
     */
    String type();

    /**
     * 创建一个工具。
     *
     * @param itemInfo 表示工具的基本信息的 {@link Tool.Info}。
     * @param metadata 表示工具元数据信息的 {@link Tool.Metadata}。
     * @return 表示创建的工具的 {@link Tool}。
     * @throws IllegalArgumentException 当 {@code itemInfo} 或 {@code metadata} 为 {@code null} 时。
     */
    Tool create(Tool.Info itemInfo, Tool.Metadata metadata);
}
