/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.jar.JarFile;

/**
 * 为 URL（Uniform Resource Locator，统一资源定位符）提供工具方法。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public final class UrlUtils {
    /** 表示路径上的分隔符。 */
    public static final char PATH_SEPARATOR = '/';

    /** {@code 'application/x-www-form-urlencoded'} 规范编码空格为 {@code '+'}。 */
    private static final String FORM_ENCODED_SPACE = "+";

    /** {@code 'RFC 3986'} 规范编码空格为 {@code '%20'}。 */
    private static final String PATH_ENCODED_SPACE = "%20";
    private static final String JAR_FILE_EXTENSION = ".jar";
    private static final String JAVA_HOME = "java.home";
    private static final String DECODED_JAVA_HOME;

    static {
        DECODED_JAVA_HOME = Optional.ofNullable(System.getProperty(JAVA_HOME))
                .map(value -> value.replace("\\", String.valueOf(PATH_SEPARATOR)))
                .map(StringUtils::toLowerCase)
                .map(value -> {
                    try {
                        return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
                    } catch (UnsupportedEncodingException e) {
                        return null;
                    }
                })
                .orElse(null);
    }

    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private UrlUtils() {}

    /**
     * 拼接 URL。
     *
     * @param base 表示原始 URL 的 {@link String}。
     * @param path 表示待追加到原始 URL 后的路径的 {@link String}。
     * @return 表示拼接后的 URL 的 {@link String}。
     */
    public static String combine(String base, String path) {
        if (base == null) {
            return path;
        } else if (path == null) {
            return base;
        } else {
            return StringUtils.trimEnd(base, PATH_SEPARATOR) + PATH_SEPARATOR + StringUtils.trimStart(path,
                    PATH_SEPARATOR);
        }
    }

    /**
     * 对 URL 中的表单部分进行解码，遵守的解码规范为 {@code 'application/x-www-form-urlencoded'}，字符集为
     * {@link StandardCharsets#UTF_8}。
     *
     * @param form 表示待解码的字符串的 {@link String}。
     * @return 表示解码后的字符串的 {@link String}。
     * @throws IllegalArgumentException 当 {@code toDecode} 为 {@code null} 时。
     * @throws IllegalStateException 当系统不支持 UTF-8 编码时。
     * @see URLDecoder#decode(String, String)
     */
    public static String decodeForm(String form) {
        notNull(form, "To decode string cannot be null.");
        try {
            return URLDecoder.decode(form, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unsupported decoding type: UTF-8.", e);
        }
    }

    /**
     * 对 URL 路径解码，遵守的解码规范为 {@code 'RFC 3986'}，字符集为 {@link StandardCharsets#UTF_8}。
     * <p>{@code 'RFC 3986'} 规范将空格编码为 {@code '%20'}，{@code 'application/x-www-form-urlencoded'}
     * 规范将空格编码为 {@code '+'}，{@link URLDecoder#decode(String, String)} 方法支持解码 {@code '+'} 或者 {@code '%20'}
     * 为空格，为了减少重复代码，这里复用 {@link UrlUtils#decodeForm(String)} 方法。</p>
     *
     * @param path 表示待解码的字符串的 {@link String}。
     * @return 表示解码后的字符串的 {@link String}。
     * @throws IllegalArgumentException 当 {@code toDecode} 为 {@code null} 时。
     * @throws IllegalStateException 当系统不支持 UTF-8 编码时。
     * @see URLDecoder#decode(String, String)
     */
    public static String decodePath(String path) {
        return UrlUtils.decodeForm(path);
    }

    /**
     * 对 URL 中的表单部分进行编码，遵守的编码规范为 {@code 'application/x-www-form-urlencoded'}，字符集为
     * {@link StandardCharsets#UTF_8}。
     *
     * @param form 表示待编码的字符串的 {@link String}。
     * @return 表示编码后的字符串的 {@link String}。
     * @throws IllegalArgumentException 当 {@code toEncode} 为 {@code null} 时。
     * @throws IllegalStateException 当系统不支持 UTF-8 编码时。
     * @see URLEncoder#encode(String, String)
     */
    public static String encodeForm(String form) {
        notNull(form, "To encode string cannot be null.");
        try {
            return URLEncoder.encode(form, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unsupported encoding type: UTF-8.", e);
        }
    }

    /**
     * 对 URL 路径进行编码，遵守的编码规范为 {@code 'RFC 3986'}，字符集为 {@link StandardCharsets#UTF_8}。
     *
     * @param path 表示待编码的字符串的 {@link String}。
     * @return 表示编码后的字符串的 {@link String}。
     * @throws IllegalArgumentException 当 {@code toEncode} 为 {@code null} 时。
     * @throws IllegalStateException 当系统不支持 UTF-8 编码时。
     * @see URLEncoder#encode(String, String)
     */
    public static String encodePath(String path) {
        notNull(path, "To encode string cannot be null.");
        try {
            String encodedMessage = URLEncoder.encode(path, StandardCharsets.UTF_8.name());
            return encodedMessage.replace(FORM_ENCODED_SPACE, PATH_ENCODED_SPACE);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unsupported encoding type: UTF-8.", e);
        }
    }

    /**
     * 判断 {@code url} 对象是否存在。
     *
     * @param url 表示指定的对象的 {@link URL}。
     * @return 当 {@code url} 存在，则返回 {@code true}, 否则，返回 {@code false}。
     * @throws IllegalArgumentException 当 {@code url} 为 {@code null} 时。
     * @throws IllegalStateException 当 {@code url} 转换文件失败时。
     */
    public static boolean exists(URL url) {
        notNull(url, "Url cannot be null.");
        try {
            File file = new File(url.toURI());
            return file.exists();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(StringUtils.format("Failed to convert url to file. [url={0}]",
                    url.getPath()), e);
        }
    }

    /**
     * 判断指定的文件是否为 {@code JAR} 文件。
     *
     * @param url 表示待判断的文件的 {@link URL}。
     * @return 如果是 {@code JAR} 文件，则返回 {@code true}，否则，返回 {@code false}。
     * @throws IllegalArgumentException 当 {@code url} 为 {@code null} 时。
     */
    public static boolean isJar(URL url) {
        notNull(url, "Url cannot be null.");
        return url.getPath().endsWith(JAR_FILE_EXTENSION);
    }

    /**
     * 将 {@link URL} 转换为 {@link JarFile}。
     *
     * @param url 表示待转换的对象的 {@link URL}。
     * @return 表示转换后的对象的 {@link JarFile}。
     * @throws IllegalArgumentException 当 {@code url} 为 {@code null} 时。
     * @throws IllegalStateException 当创建 {@link JarFile} 失败时。
     */
    public static JarFile toJarFile(URL url) {
        notNull(url, "Url cannot be null.");
        try {
            return new JarFile(url.toURI().getPath());
        } catch (IOException | URISyntaxException e) {
            throw new IllegalStateException(StringUtils.format("Failed to create jar file. [url={0}]", url.getPath()),
                    e);
        }
    }

    /**
     * 从指定 {@link URL} 中抽取最后一层的 Jar 文件的文件名。
     *
     * @param url 表示指定文件的 {@link URL}。
     * @return 表示抽取出的最后一层的 Jar 文件的文件名的 {@link Optional}{@code <}{@link String}{@code >}。
     */
    public static Optional<String> extractInnerJarNameFromURL(URL url) {
        notNull(url, "Url cannot be null.");
        String urlString = url.toString();
        int lastJarIndex = urlString.lastIndexOf(JAR_FILE_EXTENSION);
        if (lastJarIndex == -1) {
            return Optional.empty();
        }
        int start = urlString.lastIndexOf(PATH_SEPARATOR, lastJarIndex) + 1;
        String innerJarName = urlString.substring(start, lastJarIndex + JAR_FILE_EXTENSION.length());
        return Optional.of(innerJarName);
    }

    /**
     * 判断指定 {@link URL} 是否为 JDK 系统内的资源。
     *
     * @param url 表示指定资源文件的 {@link URL}。
     * @return 如果指定资源是 JDK 系统内的资源，则返回 {@code true}，否则，返回 {@code false}。
     */
    public static boolean isJdkResource(URL url) {
        if (url == null || DECODED_JAVA_HOME == null) {
            return false;
        }
        return decodePath(StringUtils.toLowerCase(url.toString())).contains(DECODED_JAVA_HOME);
    }

    /**
     * 判断指定字符串是否为 {@link URL#toString()}。
     *
     * @param source 表示指定字符串的 {@link String}。
     * @return 如果指定字符串是合法的 URL，则返回 {@code true}，否则，返回 {@code false}。
     */
    public static boolean isUrl(String source) {
        if (StringUtils.isBlank(source)) {
            return false;
        }
        try {
            new URL(source);
        } catch (java.net.MalformedURLException e) {
            return false;
        }
        return true;
    }
}
