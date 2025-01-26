/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.plugin.dynamic.mvn;

import java.io.File;
import java.io.IOException;

/**
 * 表示插件下载器。
 * <p>该插件为标准的 Jar 包。</p>
 *
 * @author 季聿阶
 * @since 2023-09-17
 */
public interface MavenArtifactDownloader {
    /**
     * 下载指定坐标的 Jar 文件，下载到本地形成一个去掉 {@link modelengine.fitframework.protocol.jar.Jar#FILE_EXTENSION} 的文件。
     *
     * @param groupId 表示 Jar 的分组名的 {@link String}。
     * @param artifactId 表示 Jar 的名字的 {@link String}。
     * @param version 表示 Jar 的版本号的 {@link String}。
     * @return 表示下载下来的 Jar 文件的 {@link File}。
     * @throws IOException 当下载过程发生异常时。
     */
    File download(String groupId, String artifactId, String version) throws IOException;
}
