/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.plugin.dynamic.mvn;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static modelengine.fitframework.inspection.Validation.isTrue;
import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.getIfNull;

import modelengine.fit.http.annotation.DeleteMapping;
import modelengine.fit.http.annotation.DocumentIgnored;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.annotation.ResponseStatus;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.protocol.jar.Jar;
import modelengine.fitframework.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示动态插件的控制器。
 *
 * @author 季聿阶
 * @since 2023-09-17
 */
@Component
@DocumentIgnored
@RequestMapping(path = "/plugins")
public class PluginController {
    private final MavenArtifactDownloader downloader;
    private final File root;

    public PluginController(MavenArtifactDownloader downloader, @Value("${directory}") String directory) {
        this.downloader = notNull(downloader, "The maven artifact downloader cannot be null.");
        this.root = new File(notBlank(directory,
                "The directory to monitor cannot be blank. [config='plugin.fit.dynamic.plugin.directory']"));
        isTrue(this.root.isDirectory(), "The directory to monitor must be a directory. [directory={0}]", directory);
    }

    /**
     * 获取已经加载的动态插件的文件名列表。
     *
     * @return 表示已经加载的动态插件的文件名列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    @GetMapping
    public List<String> getDynamicPlugins() {
        return Stream.of(getIfNull(this.root.list(), () -> new String[0])).collect(Collectors.toList());
    }

    /**
     * 添加一个动态插件。
     *
     * @param groupId 表示动态插件的分组名的 {@link String}。
     * @param artifactId 表示动态插件的名字的 {@link String}。
     * @param version 表示动态插件的版本号的 {@link String}。
     */
    @PostMapping
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void addDynamicPlugin(@RequestParam(name = "groupId") String groupId,
            @RequestParam(name = "artifactId") String artifactId, @RequestParam(name = "version") String version) {
        notBlank(groupId, "No query param. [key=groupId]");
        notBlank(artifactId, "No query param. [key=artifactId]");
        notBlank(version, "No query param. [key=version]");
        try {
            File downloaded = this.downloader.download(groupId, artifactId, version);
            Files.move(downloaded.toPath(),
                    new File(downloaded.getParentFile(), downloaded.getName() + Jar.FILE_EXTENSION).toPath(),
                    ATOMIC_MOVE);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 移除一个动态插件。
     *
     * @param groupId 表示动态插件的分组名的 {@link String}。
     * @param artifactId 表示动态插件的名字的 {@link String}。
     * @param version 表示动态插件的版本号的 {@link String}。
     */
    @DeleteMapping
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public void removeDynamicPlugin(@RequestParam(name = "groupId") String groupId,
            @RequestParam(name = "artifactId") String artifactId, @RequestParam(name = "version") String version) {
        notBlank(groupId, "No query param. [key=groupId]");
        notBlank(artifactId, "No query param. [key=artifactId]");
        notBlank(version, "No query param. [key=version]");
        File file = new File(this.root, artifactId + "-" + version + Jar.FILE_EXTENSION);
        FileUtils.delete(file);
    }
}
