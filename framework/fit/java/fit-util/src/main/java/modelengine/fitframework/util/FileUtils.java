/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.jvm.classfile.ClassFile;
import modelengine.fitframework.util.support.Unzip;
import modelengine.fitframework.util.support.Zip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为文件提供工具方法。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public final class FileUtils {
    /** 表示文件默认的字符集。 */
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private static final String JAR_FILE_EXTENSION = ".jar";

    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private FileUtils() {}

    /**
     * 对文件进行标准化。
     *
     * @param file 表示待标准化的文件的 {@link File}。
     * @return 若源文件为 {@code null}，则返回 {@code null}；否则返回标准化后的文件的 {@link File}。
     * @throws IllegalStateException 当标准化失败时。
     */
    public static File canonicalize(File file) {
        if (file == null) {
            return null;
        }
        try {
            return file.getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalStateException(StringUtils.format("Fail to canonicalize file. [file={0}]", file.getPath()),
                    e);
        }
    }

    /**
     * 根据文件名获取标准化的文件。
     *
     * @param fileName 表示文件的名字的 {@link String}。
     * @return 若文件名为 {@code null}，则返回 {@code null}；否则返回获取的标准化后的文件的 {@link File}。
     * @throws IllegalStateException 当标准化失败时。
     */
    public static File canonicalize(String fileName) {
        if (fileName == null) {
            return null;
        }
        return canonicalize(new File(fileName));
    }

    /**
     * 获取基于指定文件的指定路径的子文件。
     *
     * @param parent 表示基准文件的 {@link File}。
     * @param paths 表示基于基准文件的相对路径的 {@link String}{@code []}。
     * @return 表示基于基准文件的子文件的 {@link File}。
     */
    public static File child(File parent, String... paths) {
        if (paths == null) {
            return parent;
        }
        File child = parent;
        for (String path : paths) {
            if (path != null) {
                child = new File(child, path);
            }
        }
        return child;
    }

    /**
     * 删除指定文件。
     *
     * @param file 表示待删除文件的 {@link File}。
     * @throws IllegalStateException 当 {@code file} 无法删除时。
     */
    public static void delete(File file) {
        if (file == null) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = ObjectUtils.nullIf(file.listFiles(), new File[0]);
            for (File subFile : files) {
                delete(subFile);
            }
        }
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            throw new IllegalStateException(StringUtils.format("Failed to delete file. [file={0}]", file.getPath()), e);
        }
    }

    /**
     * 删除指定路径的文件。
     *
     * @param path 表示待删除的文件的路径的 {@link String}。
     * @throws IllegalStateException 当 {@code path} 所代表的文件无法删除时。
     */
    public static void delete(String path) {
        delete(new File(path));
    }

    /**
     * 获取文件标准化后的文件系统深度。
     *
     * @param file 表示待获取文件系统深度的文件的 {@link File}。
     * @return 返回文件的深度的 {@code int}。
     */
    public static int depth(File file) {
        File canonical = FileUtils.canonicalize(file);
        int depth = 0;
        while (canonical != null) {
            canonical = canonical.getParentFile();
            depth++;
        }
        return depth;
    }

    /**
     * 确保指定目录存在。
     *
     * @param directory 表示目标目录的 {@link File}。
     * @throws IOException 创建目录失败。
     * @throws IllegalArgumentException {@code directory} 为 {@code null}。
     * @throws IllegalStateException {@code directory} 存在且是一个文件而非文件夹。
     */
    public static void ensureDirectory(File directory) throws IOException {
        notNull(directory, "The directory to ensure cannot be null.");
        if (!directory.exists()) {
            ensureDirectory(directory.getParentFile());
            Files.createDirectory(directory.toPath());
            return;
        }
        if (!directory.isDirectory()) {
            throw new IllegalStateException(StringUtils.format("The directory to ensure is a file. [file={0}]",
                    directory.getPath()));
        }
    }

    /**
     * 计算文件的扩展名。
     *
     * @param filename 表示文件名的 {@link String}。
     * @return 若文件存在扩展名，则为表示该扩展名的 {@link String}，否则为 {@link StringUtils#EMPTY}。
     * @throws IllegalArgumentException {@code filename} 为 {@code null}。
     */
    public static String extension(String filename) {
        notNull(filename, "The filename to get extension cannot be null.");
        int index = filename.lastIndexOf('.');
        if (index < 0) {
            return StringUtils.EMPTY;
        }
        return filename.substring(index);
    }

    /**
     * 忽略文件的扩展名。
     *
     * @param filename 表示指定文件的名字的 {@link String}。
     * @return 表示忽略扩展名后的文件名的 {@link String}。
     */
    public static String ignoreExtension(String filename) {
        notNull(filename, "The filename to ignore extension cannot be null.");
        int index = filename.lastIndexOf('.');
        if (index < 0) {
            return filename;
        } else {
            return filename.substring(0, index);
        }
    }

    /**
     * 将指定统一资源定位符 {@link URL} 转换为文件。
     *
     * @param url 表示待转换的指定 {@link URL}。
     * @return 表示转换后的文件的 {@link File}。
     */
    public static File file(URL url) {
        if (url == null) {
            return null;
        }
        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(StringUtils.format("To uri failed. [url={0}]", url), e);
        }
        return canonicalize(new File(uri));
    }

    /**
     * 判断指定路径是否为绝对路径。
     *
     * @param path 表示待判断的文件路径的 {@link String}。
     * @return 是绝对路径，则返回 {@code true}，否则，返回 {@code false}。
     */
    public static boolean isAbsolute(String path) {
        if (path == null) {
            return false;
        }
        File file = new File(path);
        return file.isAbsolute();
    }

    /**
     * 判断指定的文件是否为 {@code JAR} 文件。
     *
     * @param file 表示待判断的文件的 {@link File}。
     * @return 表示判断结果的 {@code boolean}。
     */
    public static boolean isJar(File file) {
        return file.getName().endsWith(JAR_FILE_EXTENSION);
    }

    /**
     * 判断指定的文件是否为 {@code CLASS} 文件。
     *
     * @param file 表示待判断的文件的 {@link File}。
     * @return 表示判断结果的 {@code boolean}。
     */
    public static boolean isClass(File file) {
        return file.getName().endsWith(ClassFile.FILE_EXTENSION);
    }

    /**
     * 列出指定文件夹的所有子文件。
     *
     * @param file 表示指定文件夹的 {@link File}。
     * @return 表示列出的所有子文件的 {@link List}{@code <}{@link File}{@code >}。
     */
    public static List<File> list(File file) {
        if (file == null || !file.isDirectory()) {
            return Collections.emptyList();
        }
        File[] subFiles = ObjectUtils.nullIf(file.listFiles(), new File[0]);
        return Stream.of(subFiles).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 获取指定文件的标准化路径。
     *
     * @param file 表示待获取路径的文件的 {@link File}。
     * @return 表示文件的路径的 {@link String}。
     * @throws IllegalStateException 当标准化失败时。
     */
    public static String path(File file) {
        if (file == null) {
            return null;
        }
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalStateException(StringUtils.format("Failed to canonicalize file. [file={0}]",
                    file.getPath()), e);
        }
    }

    /**
     * 遍历指定的文件或目录，列出其所包含的所有子文件。
     *
     * @param file 表示待遍历的文件或目录的 {@link File}。
     * @return 表示遍历结果的所有文件及目录的 {@link Stream}{@code <}{@link File}{@code >}。
     */
    public static Stream<File> traverse(File file) {
        if (file == null) {
            return Stream.empty();
        }
        File canonical = FileUtils.canonicalize(file);
        Stream<File> stream = Stream.of(canonical);
        if (canonical.isDirectory()) {
            stream = Stream.concat(stream, traverse(canonical.listFiles()));
        }
        return stream;
    }

    private static Stream<File> traverse(File[] files) {
        if (ArrayUtils.isEmpty(files)) {
            return Stream.empty();
        }
        return Arrays.stream(files).flatMap(FileUtils::traverse);
    }

    /**
     * 将指定打包文件进行解包。
     *
     * @param zipFile 表示待解包的指定文件的 {@link File}。
     * @return 表示解包后的文件的 {@link Unzip}。
     */
    public static Unzip unzip(File zipFile) {
        return unzip(zipFile, null);
    }

    /**
     * 将指定打包文件进行解包。
     *
     * @param zipFile 表示待解包的指定文件的 {@link File}。
     * @param charset 表示解包的字符集的 {@link Charset}。
     * @return 表示解包后的文件的 {@link Unzip}。
     */
    public static Unzip unzip(File zipFile, Charset charset) {
        return new Unzip(zipFile, charset);
    }

    /**
     * 将指定文件进行打包。
     *
     * @param zipFile 表示打包后的指定文件的 {@link File}。
     * @return 表示打包后的文件的 {@link Zip}。
     */
    public static Zip zip(File zipFile) {
        return zip(zipFile, null);
    }

    /**
     * 将指定文件进行打包。
     *
     * @param zipFile 表示打包后的指定文件的 {@link File}。
     * @param charset 表示打包的字符集的 {@link Charset}。
     * @return 表示打包后的文件的 {@link Zip}。
     */
    public static Zip zip(File zipFile, Charset charset) {
        return new Zip(zipFile, charset);
    }

    /**
     * 获取指定文件的 URL。
     *
     * @param file 表示待获取 URL 的文件的 {@link File}。
     * @return 表示文件的 URL 的 {@link URL}。
     * @throws IllegalArgumentException 当 {@code file} 为 {@code null} 时。
     */
    public static URL urlOf(File file) {
        notNull(file, "The file to obtain URL cannot be null.");
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            // never occurs
            throw new IllegalStateException(e);
        }
    }

    /**
     * 从文件中读取文本信息。
     *
     * @param file 表示包含输入文件的 {@link File}。
     * @return 表示从文件中读取到的文本信息的 {@link String}。
     * @throws IOException 当读取过程发生输入输出异常时。
     */
    public static String content(File file) throws IOException {
        return content(file, null);
    }

    /**
     * 从文件中读取文本信息。
     *
     * @param file 表示包含输入文件的 {@link File}。
     * @param charset 表示文本的字符集的 {@link Charset}。
     * @return 表示从文件中读取到的文本信息的 {@link String}。
     * @throws IOException 当读取过程发生输入输出异常时。
     */
    public static String content(File file, Charset charset) throws IOException {
        notNull(file, "The file to read cannot be null.");
        try (InputStream in = Files.newInputStream(Paths.get(file.getCanonicalPath()))) {
            return IoUtils.content(in, charset);
        }
    }
}
