/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Objects;

/**
 * 为 {@link URLStreamHandler} 提供 JAR 协议的实现。
 *
 * @author 梁济时
 * @since 2022-09-25
 */
public class Handler extends URLStreamHandler {
    private static final String JAR_PROTOCOL_PREFIX = JarLocation.JAR_PROTOCOL + JarLocation.PROTOCOL_SEPARATOR;

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        return new NestableJarUrlConnection(url);
    }

    @Override
    protected void parseURL(URL url, String spec, int start, int limit) {
        String actual = spec.substring(start, limit);
        if (spec.regionMatches(true, 0, JAR_PROTOCOL_PREFIX, 0, JAR_PROTOCOL_PREFIX.length())
                || url.getFile() == null || url.getFile().isEmpty()) {
            this.setFile(url, actual);
            return;
        }
        String currentFile = url.getFile();
        if (actual.charAt(0) == JarEntryLocation.ENTRY_PATH_SEPARATOR) {
            int index = currentFile.lastIndexOf(JarLocation.URL_PATH_SEPARATOR);
            if (index > -1) {
                String file = currentFile.substring(0, index + JarLocation.URL_PATH_SEPARATOR.length());
                file += actual.substring(1);
                this.setFile(url, file);
            } else {
                this.setFile(url, actual.substring(1));
            }
            return;
        }
        if (currentFile.charAt(currentFile.length() - 1) == JarEntryLocation.ENTRY_PATH_SEPARATOR) {
            this.setFile(url, currentFile + actual);
            return;
        }
        int index = currentFile.lastIndexOf(JarEntryLocation.ENTRY_PATH_SEPARATOR);
        if (index > -1) {
            String file = currentFile.substring(0, index + 1);
            file += actual;
            this.setFile(url, file);
        } else {
            this.setFile(url, actual);
        }
    }

    private void setFile(URL url, String file) {
        this.setURL(url, JarLocation.JAR_PROTOCOL, null, -1, null, null, file, null, null);
    }

    @Override
    protected boolean sameFile(URL url1, URL url2) {
        try {
            JarLocation location1 = JarLocation.parse(url1);
            JarLocation location2 = JarLocation.parse(url2);
            return Objects.equals(location1, location2);
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    @Override
    protected int hashCode(URL url) {
        try {
            JarLocation location = JarLocation.parse(url);
            return location.hashCode();
        } catch (IllegalArgumentException ignored) {
            return url.toExternalForm().hashCode();
        }
    }
}
