/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.globalization;

import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * 为 {@link ResourceBundle.Control} 提供用以 {@link ResourceBundleStringResource} 的实现。
 *
 * @author 梁济时
 * @since 2022-11-22
 */
final class MessageBundleControl extends ResourceBundle.Control {
    private static final String PROPERTIES_FORMAT = "java.properties";
    private static final String RESOURCE_SUFFIX = "properties";
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final Charset charset;

    /**
     * 使用配置使用的编码初始化 {@link MessageBundleControl} 类的新实例。
     *
     * @param encoding 表示配置使用的编码的 {@link String}。
     */
    MessageBundleControl(String encoding) {
        this.charset = charset(encoding);
    }

    private static Charset charset(String encoding) {
        String actual = StringUtils.trim(encoding);
        if (StringUtils.isEmpty(actual)) {
            return DEFAULT_CHARSET;
        }
        try {
            return Charset.forName(actual);
        } catch (UnsupportedCharsetException ignored) {
            return DEFAULT_CHARSET;
        }
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
            throws IllegalAccessException, InstantiationException, IOException {
        if (StringUtils.equalsIgnoreCase(PROPERTIES_FORMAT, format)) {
            String bundleName = this.toBundleName(baseName, locale);
            String resourceName = this.toResourceName(bundleName, RESOURCE_SUFFIX);
            InputStream in = getInputStream(loader, resourceName, reload);
            if (in == null) {
                return null;
            }
            // 因为上面获取的输入流可能为 null，所以无法直接写入 try，将其赋值给新变量确保能够自动关闭。
            try (InputStream closableIn = in;
                 InputStreamReader reader = new InputStreamReader(closableIn, this.charset)) {
                return new PropertyResourceBundle(reader);
            }
        }
        return super.newBundle(baseName, locale, format, loader, reload);
    }

    private static InputStream getInputStream(ClassLoader loader, String resourceName, boolean isReloadRequired)
            throws IOException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<InputStream>) () -> getInputStream0(loader,
                    resourceName, isReloadRequired));
        } catch (PrivilegedActionException e) {
            // 因为 getInputStream0 方法仅会抛出 IOException，所以需要将 PrivilegedActionException 进行转换。
            throw ObjectUtils.<IOException>cast(e.getException());
        }
    }

    private static InputStream getInputStream0(ClassLoader loader, String resourceName, boolean isReloadRequired)
            throws IOException {
        if (isReloadRequired) {
            URL url = loader.getResource(resourceName);
            URLConnection connection;
            if (url == null) {
                return null;
            } else if ((connection = url.openConnection()) == null) {
                return null;
            } else {
                connection.setUseCaches(false);
                return connection.getInputStream();
            }
        } else {
            return loader.getResourceAsStream(resourceName);
        }
    }
}
