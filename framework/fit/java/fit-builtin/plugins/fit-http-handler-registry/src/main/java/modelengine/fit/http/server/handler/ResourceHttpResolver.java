/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler;

import modelengine.fitframework.protocol.jar.Jar;
import modelengine.fitframework.protocol.jar.JarEntryLocation;
import modelengine.fitframework.protocol.jar.JarLocation;
import modelengine.fitframework.resource.Resource;
import modelengine.fitframework.resource.support.ClassLoaderResourceResolver;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 表示资源的 http 请求解析器。
 *
 * @author 邬涨财
 * @since 2024-01-18
 */
public class ResourceHttpResolver extends AbstractFileHttpResolver<Resource> {
    private static final String TYPE = "resource";

    @Override
    protected Resource getFile(String actualPath, ClassLoader classLoader) {
        ClassLoaderResourceResolver resourceResolver = new ClassLoaderResourceResolver(classLoader);
        try {
            Resource[] resources = resourceResolver.resolve(actualPath);
            if (resources.length > 0) {
                return resources[0];
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to get resource.", e);
        }
        return null;
    }

    @Override
    protected boolean isFileValid(Resource resource) {
        return resource != null;
    }

    @Override
    protected InputStream getInputStream(Resource resource) throws IOException {
        return resource.read();
    }

    @Override
    protected long getLength(Resource resource, String actualPath, InputStream inputStream) throws IOException {
        URL url = resource.url();
        if (StringUtils.equalsIgnoreCase(url.getProtocol(), JarLocation.JAR_PROTOCOL)) {
            return Jar.from(JarEntryLocation.parse(url).jar()).entries().get(actualPath).sizeOfUncompressed();
        } else if (StringUtils.equalsIgnoreCase(url.getProtocol(), JarLocation.FILE_PROTOCOL)) {
            return inputStream.available();
        } else {
            throw new UnsupportedOperationException(StringUtils.format("Failed to get url length. [url={0}]",
                    url.toExternalForm()));
        }
    }

    @Override
    protected String getFileName(Resource resource) {
        return resource.filename();
    }

    @Override
    protected String getType() {
        return TYPE;
    }
}
