/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.plugin.dynamic.mvn.support;

import static modelengine.fitframework.inspection.Validation.isTrue;
import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.plugin.dynamic.mvn.MavenArtifactDownloader;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.protocol.jar.Jar;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * 表示 {@link MavenArtifactDownloader} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-09-17
 */
@Component
public class DefaultMavenArtifactDownloader implements MavenArtifactDownloader {
    private static final char SEPARATOR_URL = '/';

    private final HttpClassicClientFactory factory;
    private final String url;
    private final File root;

    public DefaultMavenArtifactDownloader(HttpClassicClientFactory factory, @Value("${repository-url}") String url,
            @Value("${directory}") String directory) {
        this.factory = notNull(factory, "The http classic client factory cannot be null.");
        notBlank(url, "The maven repository url cannot be blank. [config='plugin.fit.dynamic.plugin.repository-url']");
        if (url.endsWith(String.valueOf(SEPARATOR_URL))) {
            this.url = url;
        } else {
            this.url = url + SEPARATOR_URL;
        }
        this.root = new File(notBlank(directory,
                "The directory to monitor cannot be blank. [config='plugin.fit.dynamic.plugin.directory']"));
        isTrue(this.root.isDirectory(), "The directory to monitor must be a directory. [directory={0}]", directory);
    }

    @Override
    public File download(String groupId, String artifactId, String version) throws IOException {
        String actualUrl =
                this.url + groupId.replace('.', SEPARATOR_URL) + SEPARATOR_URL + artifactId + SEPARATOR_URL + version
                        + SEPARATOR_URL + artifactId + "-" + version + Jar.FILE_EXTENSION;
        HttpClassicClientRequest request = this.factory.create().createRequest(HttpRequestMethod.GET, actualUrl);
        try (HttpClassicClientResponse<Object> exchange = request.exchange()) {
            File downloaded = new File(this.root, artifactId + "-" + version);
            try (OutputStream out = Files.newOutputStream(downloaded.toPath())) {
                byte[] bytes = exchange.entityBytes();
                out.write(bytes);
                out.flush();
            }
            return downloaded;
        }
    }
}


