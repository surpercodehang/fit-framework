/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.security.Permission;
import java.util.Locale;

/**
 * 为 {@link JarURLConnection} 提供基于 {@link NestableJarFile} 的实现。
 *
 * @author 梁济时
 * @since 2022-09-25
 */
public class NestableJarUrlConnection extends JarURLConnection {
    private static final String MIME_TYPE = "application/java-archive";
    private static final URL EMPTY_URL;

    static {
        String file = JarLocation.FILE_PROTOCOL + JarLocation.PROTOCOL_SEPARATOR + JarLocation.URL_PATH_SEPARATOR;
        try {
            EMPTY_URL = new URL(JarLocation.JAR_PROTOCOL, null, -1, file, new URLStreamHandler() {
                @Override
                protected URLConnection openConnection(URL u) {
                    return null;
                }
            });
        } catch (MalformedURLException ex) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "Failed to build empty JAR url. [error=%s]",
                    ex.getMessage()), ex);
        }
    }

    private final URL url;
    private final URL jarFileUrl;
    private final String entryName;
    private NestableJarFile jar;
    private NestableJarFile.Entry entry;

    /**
     * 使用待连接的 JAR 的 URL 初始化 {@link NestableJarUrlConnection} 类的新实例。
     *
     * @param url 表示待连接的 JAR 的 {@link URL}。
     * @throws MalformedURLException {@code url} 的格式不正确。
     */
    protected NestableJarUrlConnection(URL url) throws MalformedURLException {
        super(EMPTY_URL);
        this.url = url;

        String file = url.getFile();
        if (file.regionMatches(true,
                file.length() - JarLocation.URL_PATH_SEPARATOR.length(),
                JarLocation.URL_PATH_SEPARATOR,
                0,
                JarLocation.URL_PATH_SEPARATOR.length())) {
            this.jarFileUrl = url;
            this.entryName = null;
        } else {
            int index = file.lastIndexOf(JarLocation.URL_PATH_SEPARATOR);
            if (index < 0) {
                throw new MalformedURLException(String.format(Locale.ROOT,
                        "The URL does not specify a entry in JAR. [url=%s]",
                        url.toExternalForm()));
            }
            index += JarLocation.URL_PATH_SEPARATOR.length();
            this.entryName = file.substring(index);
            file = file.substring(0, index);
            this.jarFileUrl = new URL(JarLocation.JAR_PROTOCOL, null, -1, file);
        }
    }

    @Override
    public URL getJarFileURL() {
        return this.jarFileUrl;
    }

    @Override
    public String getEntryName() {
        return this.entryName;
    }

    @Override
    public URL getURL() {
        return this.url;
    }

    @Override
    public NestableJarFile getJarFile() throws IOException {
        this.connect();
        return this.jar;
    }

    @Override
    public NestableJarFile.Entry getJarEntry() throws IOException {
        this.connect();
        if (this.entry == null) {
            throw new FileNotFoundException(String.format(Locale.ROOT,
                    "JAR entry not found. [entry=%s, jar=%s]",
                    this.entryName,
                    this.jar));
        }
        return this.entry;
    }

    @Override
    public void connect() throws IOException {
        if (this.connected) {
            return;
        }
        Jar actualJar = Jar.from(this.jarFileUrl);
        this.jar = new NestableJarFile(actualJar);
        this.entry = this.jar.getEntry(this.entryName);
        super.connected = true;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        this.connect();
        return this.getJarEntry().getInputStream();
    }

    @Override
    public boolean getDoInput() {
        return true;
    }

    @Override
    public void setDoInput(boolean doInput) {
        if (!doInput) {
            throw new IllegalStateException("The doInput is always be true.");
        }
    }

    @Override
    public boolean getDoOutput() {
        return false;
    }

    @Override
    public void setDoOutput(boolean doOutput) {
        if (doOutput) {
            throw new IllegalStateException("The doOutput is always be false.");
        }
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new IOException("Cannot write data into a nested JAR.");
    }

    @Override
    public String getContentType() {
        return MIME_TYPE;
    }

    @Override
    public int getConnectTimeout() {
        return 0;
    }

    @Override
    public long getContentLengthLong() {
        if (this.entry == null) {
            return -1;
        }
        return this.entry.getSize();
    }

    @Override
    public Permission getPermission() throws IOException {
        this.connect();
        return this.jar.getPermission();
    }

    @Override
    public int getReadTimeout() {
        return 0;
    }

    @Override
    public long getExpiration() {
        return 0;
    }

    @Override
    public long getDate() {
        return 0;
    }

    @Override
    public long getLastModified() {
        if (this.entry == null || this.entry.getLastModifiedTime() == null) {
            return 0;
        }
        return this.entry.getLastModifiedTime().toMillis();
    }
}
