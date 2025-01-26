/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.protocol.jar.JarEntryLocation;
import modelengine.fitframework.resource.Resource;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

/**
 * 为 {@link Resource} 提供基于 {@link URL} 的实现。
 *
 * @author 梁济时
 * @since 2023-01-11
 */
public class UrlResource implements Resource {
    private final URL url;
    private volatile String filename;

    /**
     * 使用资源的定位符初始化 {@link UrlResource} 类的新实例。
     *
     * @param url 表示资源定位符的 {@link URL}。
     * @throws IllegalArgumentException {@code url} 为 {@code null}。
     */
    public UrlResource(URL url) {
        this.url = notNull(url, "The URL of resource cannot be null.");
        this.filename = name(this.url);
    }

    private static String name(URL url) {
        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException ex) {
            throw new IllegalStateException(StringUtils.format(
                    "Failed to compute URI of URL. [url={0}]",
                    url.toExternalForm()), ex);
        }
        String full = uri.getSchemeSpecificPart();
        return full.substring(full.lastIndexOf(JarEntryLocation.ENTRY_PATH_SEPARATOR) + 1);
    }

    @Override
    public String filename() {
        if (this.filename == null) {
            this.filename = name(this.url);
        }
        return this.filename;
    }

    @Override
    public URL url() {
        return this.url;
    }

    @Override
    public InputStream read() throws IOException {
        return this.url.openStream();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            UrlResource another = (UrlResource) obj;
            return Objects.equals(this.url, another.url);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.getClass(), this.url});
    }

    @Override
    public String toString() {
        return this.url.toExternalForm();
    }
}
