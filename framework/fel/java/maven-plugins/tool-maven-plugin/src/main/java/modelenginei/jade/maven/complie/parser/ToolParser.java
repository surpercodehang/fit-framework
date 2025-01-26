/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelenginei.jade.maven.complie.parser;

import modelenginei.jade.maven.complie.entity.ToolEntity;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * 工具解析接口。
 *
 * @author 杭潇
 * @since 2024-06-28
 */
public interface ToolParser {
    /**
     * 基于 class 文件路径将工具注解的方法解析成 {@link ToolEntity}。
     *
     * @param classFile 表示给定的 class 文件路径的 {@link Path}。
     * @return 表示解析完之后的 {@link List}{@code <}{@link ToolEntity}{@code >}。
     */
    List<ToolEntity> parseTool(File classFile);
}
