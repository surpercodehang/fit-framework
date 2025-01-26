/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.support.EmptyInputStream;

import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;
import java.util.function.Function;

/**
 * 为输入输出流提供工具方法。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public class IoUtils {
    /** 表示默认的缓冲区大小。 */
    public static final int DEFAULT_BUFFER_SIZE = 1024;

    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private IoUtils() {}

    /**
     * 关闭指定的可关闭对象，并忽略关闭过程中可能产生的输入输出异常。
     *
     * @param closeableObject 表示待关闭的对象的 {@link Closeable}。
     */
    public static void close(Closeable closeableObject) {
        if (closeableObject != null) {
            try {
                closeableObject.close();
            } catch (IOException ignored) {
                // Ignore exception when close resources.
            }
        }
    }

    /**
     * 从源文件中拷贝数据到目标文件。
     * <p>拷贝过程将使用 {@link #DEFAULT_BUFFER_SIZE 默认的缓冲区大小}。</p>
     * <p>拷贝完成后，目标文件将会被覆盖。</p>
     *
     * @param inputFile 表示输入文件的 {@link File}。
     * @param outputFile 表示输出文件的 {@link File}。
     * @throws IllegalArgumentException 当 {@code inputFile} 为 {@code null} 或不存在时。
     * @throws IllegalArgumentException 当 {@code outputFile} 为 {@code null} 时。
     * @throws IOException 当拷贝过程中发生异常时。
     * @see #copy(InputStream, OutputStream, int, long)
     */
    public static void copy(File inputFile, File outputFile) throws IOException {
        copy(inputFile, outputFile, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 从源文件中拷贝数据到目标文件。
     * <p>拷贝完成后，目标文件将会被覆盖。</p>
     *
     * @param inputFile 表示输入文件的 {@link File}。
     * @param outputFile 表示输出文件的 {@link File}。
     * @param bufferSize 表示拷贝过程中使用的缓冲区的大小的 {@code int}。
     * @throws IllegalArgumentException 当 {@code inputFile} 为 {@code null} 或不存在时。
     * @throws IllegalArgumentException 当 {@code outputFile} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code bufferSize} 不是一个正整数时。
     * @throws IOException 当拷贝过程中发生异常时。
     * @see #copy(InputStream, OutputStream, int, long)
     */
    public static void copy(File inputFile, File outputFile, int bufferSize) throws IOException {
        validateInputFile(inputFile);
        try (InputStream in = new FileInputStream(inputFile)) {
            copy(in, outputFile, bufferSize, inputFile.length());
        }
    }

    /**
     * 从源文件中拷贝数据到输出流。
     * <p>拷贝过程将使用 {@link #DEFAULT_BUFFER_SIZE 默认的缓冲区大小}。</p>
     *
     * @param inputFile 表示输入文件的 {@link File}。
     * @param out 表示输出流的 {@link OutputStream}。
     * @throws IllegalArgumentException 当 {@code inputFile} 为 {@code null} 或不存在时。
     * @throws IllegalArgumentException 当 {@code out} 为 {@code null} 时。
     * @throws IOException 当拷贝过程中发生异常时。
     * @see #copy(InputStream, OutputStream, int, long)
     */
    public static void copy(File inputFile, OutputStream out) throws IOException {
        copy(inputFile, out, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 从源文件中拷贝数据到输出流。
     *
     * @param inputFile 表示输入文件的 {@link File}。
     * @param out 表示输出流的 {@link OutputStream}。
     * @param bufferSize 表示拷贝过程中使用的缓冲区的大小的 {@code int}。
     * @throws IllegalArgumentException 当 {@code inputFile} 为 {@code null} 或不存在时。
     * @throws IllegalArgumentException 当 {@code out} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code bufferSize} 不是一个正整数时。
     * @throws IOException 当拷贝过程中发生异常时。
     * @see #copy(InputStream, OutputStream, int, long)
     */
    public static void copy(File inputFile, OutputStream out, int bufferSize) throws IOException {
        validateInputFile(inputFile);
        try (InputStream in = new FileInputStream(inputFile)) {
            copy(in, out, bufferSize, inputFile.length());
        }
    }

    /**
     * 从输入流中拷贝数据到文件。
     * <p>拷贝过程将使用 {@link #DEFAULT_BUFFER_SIZE 默认的缓冲区大小}。</p>
     * <p>拷贝完成后，目标文件将会被覆盖。</p>
     *
     * @param in 表示输入流的 {@link InputStream}。
     * @param outputFile 表示输出文件的 {@link File}。
     * @throws IllegalArgumentException 当 {@code in} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code outputFile} 为 {@code null} 时。
     * @throws IOException 当拷贝过程中发生异常时。
     * @see #copy(InputStream, OutputStream, int)
     */
    public static void copy(InputStream in, File outputFile) throws IOException {
        copy(in, outputFile, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 从输入流中拷贝数据到文件。
     * <p>拷贝完成后，目标文件将会被覆盖。</p>
     *
     * @param in 表示输入流的 {@link InputStream}。
     * @param outputFile 表示输出文件的 {@link File}。
     * @param bufferSize 表示拷贝过程中使用的缓冲区的大小的 {@code int}。
     * @throws IllegalArgumentException 当 {@code in} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code outputFile} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code bufferSize} 不是一个正整数时。
     * @throws IOException 当拷贝过程中发生异常时。
     * @see #copy(InputStream, OutputStream, int)
     */
    public static void copy(InputStream in, File outputFile, int bufferSize) throws IOException {
        validateOutputFile(outputFile);
        try (OutputStream out = new FileOutputStream(outputFile, false)) {
            copy(in, out, bufferSize);
        }
    }

    /**
     * 从输入流中拷贝数据到文件。
     * <p>拷贝完成后，目标文件将会被覆盖。</p>
     *
     * @param in 表示输入流的 {@link InputStream}。
     * @param outputFile 表示输出文件的 {@link File}。
     * @param bufferSize 表示拷贝过程中使用的缓冲区的大小的 {@code int}。
     * @param length 表示待拷贝数据的长度的 {@code long}。
     * @throws IllegalArgumentException 当 {@code in} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code outputFile} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code bufferSize} 不是一个正整数时。
     * @throws IllegalArgumentException 当 {@code length} 是一个负整数时。
     * @throws IOException 当拷贝过程中发生异常时。
     * @see #copy(InputStream, OutputStream, int, long)
     */
    public static void copy(InputStream in, File outputFile, int bufferSize, long length) throws IOException {
        validateOutputFile(outputFile);
        try (OutputStream out = new FileOutputStream(outputFile, false)) {
            copy(in, out, bufferSize, length);
        }
    }

    /**
     * 从输入流中拷贝数据到文件。
     * <p>拷贝过程将使用 {@link #DEFAULT_BUFFER_SIZE 默认的缓冲区大小}。</p>
     * <p>拷贝完成后，目标文件将会被覆盖。</p>
     *
     * @param in 表示输入流的 {@link InputStream}。
     * @param outputFile 表示输出文件的 {@link File}。
     * @param length 表示待拷贝数据的长度的 {@code long}。
     * @throws IllegalArgumentException 当 {@code in} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code outputFile} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code length} 是一个负整数时。
     * @throws IOException 当拷贝过程中发生异常时。
     * @see #copy(InputStream, OutputStream, int, long)
     */
    public static void copy(InputStream in, File outputFile, long length) throws IOException {
        copy(in, outputFile, DEFAULT_BUFFER_SIZE, length);
    }

    /**
     * 从输入流中拷贝数据到输出流。
     * <p>拷贝过程将使用 {@link #DEFAULT_BUFFER_SIZE 默认的缓冲区大小}。</p>
     *
     * @param in 表示输入流的 {@link InputStream}。
     * @param out 表示输出流的 {@link OutputStream}。
     * @throws IllegalArgumentException 当 {@code in} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code out} 为 {@code null} 时。
     * @throws IOException 当拷贝过程中发生异常时。
     * @see #copy(InputStream, OutputStream, int)
     */
    public static void copy(InputStream in, OutputStream out) throws IOException {
        copy(in, out, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 从输入流中拷贝数据到输出流。
     *
     * @param in 表示输入流的 {@link InputStream}。
     * @param out 表示输出流的 {@link OutputStream}。
     * @param bufferSize 表示拷贝过程中使用的缓冲区的大小的 {@code int}。
     * @throws IllegalArgumentException 当 {@code in} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code out} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code bufferSize} 不是一个正整数时。
     * @throws IOException 当拷贝过程中发生异常时。
     * @see #copy(InputStream, OutputStream, int)
     */
    public static void copy(InputStream in, OutputStream out, int bufferSize) throws IOException {
        validateInputStream(in);
        validateOutputStream(out);
        validateBufferSize(bufferSize);
        byte[] buffer = new byte[bufferSize];
        int read;
        while ((read = in.read(buffer, 0, bufferSize)) >= 0) {
            out.write(buffer, 0, read);
        }
    }

    /**
     * 从输入流中拷贝数据到输出流。
     *
     * @param in 表示输入流的 {@link InputStream}。
     * @param out 表示输出流的 {@link OutputStream}。
     * @param bufferSize 表示拷贝过程中使用的缓冲区的大小的 {@code int}。
     * @param length 表示待拷贝数据的长度的 {@code long}。
     * @throws IllegalArgumentException 当 {@code in} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code out} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code bufferSize} 不是一个正整数时。
     * @throws IllegalArgumentException 当 {@code length} 是一个负整数时。
     * @throws IOException 当拷贝过程中发生异常时。
     * @see #copy(InputStream, OutputStream, int, long)
     */
    public static void copy(InputStream in, OutputStream out, int bufferSize, long length) throws IOException {
        validateInputStream(in);
        validateOutputStream(out);
        validateBufferSize(bufferSize);
        validateLength(length);
        long read = 0L;
        byte[] buffer = new byte[bufferSize];
        while (read < length) {
            int part = in.read(buffer, 0, (int) Math.min(length - read, bufferSize));
            out.write(buffer, 0, part);
            read += part;
        }
    }

    /**
     * 从输入流中拷贝数据到输出流。
     * <p>拷贝过程将使用 {@link #DEFAULT_BUFFER_SIZE 默认的缓冲区大小}。</p>
     *
     * @param in 表示输入流的 {@link InputStream}。
     * @param out 表示输出流的 {@link OutputStream}。
     * @param length 表示待拷贝数据的长度的 {@code long}。
     * @throws IllegalArgumentException 当 {@code in} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code out} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code length} 是一个负整数时。
     * @throws IOException 当拷贝过程中发生异常时。
     * @see #copy(InputStream, OutputStream, int, long)
     */
    public static void copy(InputStream in, OutputStream out, long length) throws IOException {
        copy(in, out, DEFAULT_BUFFER_SIZE, length);
    }

    /**
     * 从指定类的类加载器中读取指定名称的资源，并将其加载为一个属性集。
     *
     * @param clazz 表示待加载资源的类型的 {@link Class}{@code <}{@link Object}{@code >}。
     * @param resourceKey 表示资源的键的 {@link String}。
     * @return 表示加载到的属性集的 {@link Properties}。
     * @throws IllegalArgumentException 当 {@code clazz} 为 {@code null} 时，或 {@code resourceKey} 为 {@code null} 或空白字符串时。
     * @throws IllegalStateException 当不存在指定名称的资源，或读取资源失败时。
     * @see #properties(ClassLoader, String)
     */
    public static Properties properties(Class<?> clazz, String resourceKey) {
        Properties properties = new Properties();
        try (InputStream in = resource(clazz, resourceKey)) {
            properties.load(in);
        } catch (IOException e) {
            throw new IllegalStateException(StringUtils.format(
                    "Failed to read properties from embedded resource. [resourceKey={0}]",
                    resourceKey), e);
        }
        return properties;
    }

    /**
     * 从指定类的类加载器中使用指定编码读取指定名称的资源，并将其加载为一个属性集。
     *
     * @param clazz 表示待加载资源的类型的 {@link Class}{@code <}{@link Object}{@code >}。
     * @param resourceKey 表示资源的键的 {@link String}。
     * @param charset 表示资源编码的 {@link String}。
     * @return 表示加载到的属性集的 {@link Properties}。
     * @throws IllegalArgumentException 当 {@code clazz} 为 {@code null} 时，或 {@code resourceKey} 为 {@code null} 或空白字符串时。
     * @throws IllegalStateException 当不存在指定名称的资源，或读取资源失败时。
     * @see #properties(ClassLoader, String)
     */
    public static Properties properties(Class<?> clazz, String resourceKey, Charset charset) {
        Properties properties = new Properties();
        try (InputStream in = resource(clazz, resourceKey);
             InputStreamReader reader = new InputStreamReader(in, charset)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new IllegalStateException(StringUtils.format(
                    "Failed to read properties from embedded resource. [resourceKey={0}, charset={1}]",
                    resourceKey, charset), e);
        }
        return properties;
    }

    /**
     * 从指定类加载器中读取指定名称的资源，并将其加载为一个属性集。
     *
     * @param loader 表示类加载器的 {@link ClassLoader}。
     * @param resourceKey 表示资源的键的 {@link String}。
     * @return 表示加载到的属性集的 {@link Properties}。
     * @throws IllegalArgumentException 当 {@code loader} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code resourceKey} 为 {@code null} 或空白字符串时。
     * @throws IllegalStateException 当不存在指定名称的资源时。
     * @throws IllegalStateException 当读取资源失败时。
     * @see Properties#load(InputStream)
     */
    public static Properties properties(ClassLoader loader, String resourceKey) {
        Properties properties = new Properties();
        try (InputStream in = resource(loader, resourceKey)) {
            properties.load(in);
        } catch (IOException e) {
            throw new IllegalStateException(StringUtils.format(
                    "Failed to read properties from embedded resource. [resourceKey={0}]",
                    resourceKey), e);
        }
        return properties;
    }

    /**
     * 使用指定类加载指定资源，并读取其中存储的文本信息。
     * <pre>
     * src
     * +- main
     * |  \- java
     * |     \- resources
     * |        \- demo
     * |           \- hello.txt
     * +- test
     * |  \- java
     * \- README.md
     * </pre>
     * <p>样例中的 {@code hello.txt} 文件对应 {@code resourceName} 参数值为 {@code /demo/hello.txt}。</p>
     *
     * @param clazz 表示用以加载资源的类的 {@link Class}{@code <}{@link Object}{@code >}。
     * @param resourceName 表示待加载资源的名称的 {@link String}。
     * @return 表示从资源文件中读取的文本内容的 {@link String}。
     * @throws IllegalArgumentException 当 {@code clazz} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code resourceName} 为 {@code null} 或空白字符串时。
     * @throws IllegalStateException 当指定资源不存在时。
     * @throws IOException 当读取过程发生输入输出异常时。
     */
    public static String content(Class<?> clazz, String resourceName) throws IOException {
        notNull(clazz, "The coordinate class to read resource cannot be null.");
        return readResource(clazz::getResourceAsStream, resourceName);
    }

    /**
     * 使用指定类加载器加载指定资源，并读取其中存储的文本信息。
     * <pre>
     * src
     * +- main
     * |  \- java
     * |     \- resources
     * |        \- demo
     * |           \- hello.txt
     * +- test
     * |  \- java
     * \- README.md
     * </pre>
     * <p>样例中的 {@code hello.txt} 文件对应 {@code resourceName} 参数值为 {@code demo/hello.txt}。</p>
     *
     * @param classLoader 表示用以加载资源的类加载器的 {@link ClassLoader}。
     * @param resourceName 表示待加载资源的名称的 {@link String}，不能以 {@code /} 开头。
     * @return 表示从资源文件中读取的文本内容的 {@link String}。
     * @throws IllegalArgumentException 当 {@code classLoader} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code resourceName} 为 {@code null} 或空白字符串时。
     * @throws IllegalStateException 当指定资源不存在时。
     * @throws IOException 当读取过程发生输入输出异常时。
     */
    public static String content(ClassLoader classLoader, String resourceName) throws IOException {
        notNull(classLoader, "The coordinate class loader to read resource cannot be null.");
        return readResource(classLoader::getResourceAsStream, resourceName);
    }

    /**
     * 从输入流中读取文本信息。
     *
     * @param in 表示包含文本信息的输入流的 {@link InputStream}。
     * @return 表示从输入流中读取到的文本信息的 {@link String}。
     * @throws IllegalArgumentException 当 {@code in} 为 {@code null} 时。
     * @throws IOException 当读取过程发生输入输出异常时。
     */
    public static String content(InputStream in) throws IOException {
        return content(in, null);
    }

    /**
     * 从输入流中读取文本信息。
     *
     * @param in 表示包含文本信息的输入流的 {@link InputStream}。
     * @param length 表示文本信息的长度的 {@code int}。
     * @return 表示从输入流中读取到的文本信息的 {@link String}。
     * @throws IllegalArgumentException 当 {@code in} 为 {@code null} 时。
     * @throws IOException 当读取过程发生输入输出异常时。
     */
    public static String content(InputStream in, int length) throws IOException {
        notNull(in, "The input stream to read cannot be null.");
        byte[] bytes = read(in, length);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 从输入流中读取文本信息。
     *
     * @param in 表示包含文本信息的输入流的 {@link InputStream}。
     * @param charset 表示文本的字符集的 {@link Charset}。
     * @return 表示从流中读取到的文本信息的 {@link String}。
     * @throws IllegalArgumentException 当 {@code in} 为 {@code null} 时。
     * @throws IOException 当读取过程发生输入输出异常时。
     */
    public static String content(InputStream in, Charset charset) throws IOException {
        notNull(in, "The input stream to read cannot be null.");
        Charset actualCharset = ObjectUtils.nullIf(charset, StandardCharsets.UTF_8);
        byte[] bytes = read(in);
        return new String(bytes, actualCharset);
    }

    /**
     * 获取一个空的驶入流。
     *
     * @return 表示空的输入流的 {@link InputStream}。
     */
    public static InputStream emptyInputStream() {
        return EmptyInputStream.INSTANCE;
    }

    /**
     * 将十六进制的字符串转为字节序表现形式。
     *
     * @param hexString 表示十六进制的字符串的 {@link String}。
     * @return 表示字节序的 {@code byte[]}。
     */
    public static byte[] fromHexString(String hexString) {
        if (StringUtils.isBlank(hexString)) {
            return new byte[0];
        }
        String actual = hexString.trim();
        if (actual.length() % 2 == 1) {
            actual = '0' + actual;
        }
        byte[] bytes = new byte[actual.length() >> 1];
        int index = 0;
        for (int i = 0; i < actual.length(); i++) {
            char high = actual.charAt(i++);
            char low = actual.charAt(i);
            int value = (hexValue(high) << 4) | hexValue(low);
            bytes[index++] = (byte) value;
        }
        return bytes;
    }

    /**
     * 从输入流中读取指定长度的数据。
     * <p>当未能读取到指定长度的数据时会持续阻塞，并累积读入的数据。</p>
     *
     * @param in 表示输入流的 {@link InputStream}。
     * @param length 表示待读取数据的长度的 {@code int}。
     * @return 表示从输入流中读取到的数据的 {@code byte[]}。
     * @throws IllegalArgumentException 当 {@code in} 为 {@code null} 或者 {@code length} 小于 0。
     * @throws IOException 读取过程发生输入输出异常。
     */
    public static byte[] read(InputStream in, int length) throws IOException {
        notNull(in, "The input stream to read cannot be null.");
        Validation.greaterThanOrEquals(length, 0, "The length of data to read cannot be negative.");
        if (length < 1) {
            return new byte[0];
        }
        byte[] buffer = new byte[length];
        int read = 0;
        do {
            int current = in.read(buffer, read, length - read);
            if (current < 0) {
                throw new IOException(StringUtils.format("Failed to read from input stream: no enough available bytes. "
                        + "[expectedLength={0}, actualLength={1}]", length, read));
            } else {
                read += current;
            }
        } while (read < length);
        return buffer;
    }

    /**
     * 从指定的输入流中读取所有数据。
     *
     * @param in 表示待读取数据的输入流的 {@link InputStream}。
     * @return 表示从输入流中读取到的数据的 {@code byte[]}。
     * @throws IllegalArgumentException {@code in} 为 {@code null}。
     * @throws IOException 读取过程发生输入输出异常。
     */
    public static byte[] read(InputStream in) throws IOException {
        notNull(in, "The input stream to read cannot be null.");
        Queue<byte[]> buffers = new LinkedList<>();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int total = 0;
        while (true) {
            int read = in.read(buffer);
            if (read < 0) {
                break;
            } else if (read > 0) {
                buffers.add(Arrays.copyOf(buffer, read));
                total += read;
            } else {
                ThreadUtils.sleep(0);
            }
        }
        byte[] bytes = new byte[total];
        int offset = 0;
        while (!buffers.isEmpty()) {
            byte[] current = buffers.poll();
            System.arraycopy(current, 0, bytes, offset, current.length);
            offset += current.length;
        }
        return bytes;
    }

    /**
     * 获取指定的资源。
     *
     * @param clazz 表示用以加载资源的类的 {@link Class}{@code <}{@link Object}{@code >}。
     * @param key 表示资源的键的 {@link String}。
     * @return 表示用以读取资源的输入流的 {@link InputStream}。
     * @throws IllegalArgumentException 当 {@code clazz} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code key} 为 {@code null} 或空白字符串时。
     * @throws IllegalStateException 当不存在指定键的资源时。
     * @see ClassLoader#getResourceAsStream(String)
     */
    public static InputStream resource(Class<?> clazz, String key) {
        notNull(clazz, "The class to load resource cannot be null.");
        Validation.notBlank(key, "The key of resource to load cannot be blank.");
        InputStream stream = clazz.getResourceAsStream(key);
        if (stream == null) {
            throw new IllegalStateException(StringUtils.format(
                    "The embedded resource with specific key not found. [key={0}]",
                    key));
        } else {
            return stream;
        }
    }

    /**
     * 从指定的类加载器中获取指定的资源。
     *
     * @param loader 表示资源所属的类加载器的 {@link ClassLoader}。
     * @param key 表示资源的键的 {@link String}。
     * @return 表示用以读取资源的输入流的 {@link InputStream}。
     * @throws IllegalArgumentException 当 {@code loader} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code key} 为 {@code null} 或空白字符串时。
     * @throws IllegalStateException 当不存在指定键的资源时。
     * @see ClassLoader#getResourceAsStream(String)
     */
    public static InputStream resource(ClassLoader loader, String key) {
        notNull(loader, "The loader to load resource cannot be null.");
        Validation.notBlank(key, "The key of resource to load cannot be blank.");
        InputStream stream = loader.getResourceAsStream(key);
        if (stream == null) {
            throw new IllegalStateException(StringUtils.format(
                    "The embedded resource with specific key not found. [key={0}]",
                    key));
        } else {
            return stream;
        }
    }

    /**
     * 将字节序转为字符串表现形式。
     *
     * @param bytes 表示字节序 {@code byte[]}。
     * @return 表示字符串 {@link String}。
     */
    public static String toHexString(byte[] bytes) {
        if (bytes == null) {
            return StringUtils.EMPTY;
        }
        StringBuilder builder = new StringBuilder(bytes.length << 1);
        for (byte aByte : bytes) {
            String text = Integer.toHexString(Byte.toUnsignedInt(aByte));
            if (text.length() == 1) {
                builder.append('0');
            }
            builder.append(text);
        }
        return builder.toString();
    }

    private static int hexValue(char ch) {
        if (ch >= '0' && ch <= '9') {
            return ch - '0';
        }
        if (ch >= 'a' && ch <= 'f') {
            return ch - 'a' + 10;
        }
        if (ch >= 'A' && ch <= 'F') {
            return ch - 'A' + 10;
        }
        throw new IllegalStateException(StringUtils.format(
                "Char is out of range, legal char range is [0-9A-Fa-f]. [ch='{0}']",
                ch));
    }

    /**
     * 通过指定方法获取输入流，然后从输入流中读取指定资源的文本内容。
     *
     * @param inSupplier 表示指定方法的 {@link Function}{@code <}{@link String}{@code , }{@link InputStream}{@code >}。
     * @param resourceName 表示指定资源名称的 {@link String}。
     * @return 表示读取到的指定资源的文本内容的 {@link String}。
     * @throws IllegalArgumentException 当 {@code resourceName} 为 {@code null} 或空白字符串时。
     * @throws IllegalStateException 当指定资源不存在时。
     * @throws IOException 当读取过程发生输入输出异常时。
     */
    private static String readResource(Function<String, InputStream> inSupplier, String resourceName)
            throws IOException {
        Validation.notBlank(resourceName, "The name of resource to read cannot be null.");
        try (InputStream in = inSupplier.apply(resourceName)) {
            notNull(in, () -> new IllegalStateException("The input stream to read cannot be null."));
            return content(in);
        }
    }

    /**
     * 校验缓冲区大小。
     * <p>缓冲区大小应该大于 0。</p>
     *
     * @param bufferSize 表示缓冲区大小的 {@code int}。
     * @throws IllegalArgumentException 当 {@code bufferSize} 不是正整数时。
     */
    private static void validateBufferSize(int bufferSize) {
        Validation.greaterThan(bufferSize, 0, "The size of buffer to copy data must be positive.");
    }

    /**
     * 校验输入文件。
     * <p>输入文件不能为 {@code null}，且存在。</p>
     *
     * @param inputFile 表示待校验的输入文件的 {@link File}。
     * @throws IllegalArgumentException 当输入文件为 {@code null}，或者不存在时。
     */
    private static void validateInputFile(File inputFile) {
        notNull(inputFile, "The input file to copy data cannot be null.");
        Validation.isTrue(inputFile.exists(), "The input file to copy data not exists.");
    }

    /**
     * 校验输入流。
     * <p>输入流不能为 {@code null}。</p>
     *
     * @param in 表示输入流的 {@link InputStream}。
     * @throws IllegalArgumentException 当输入流为 {@code null} 时。
     */
    private static void validateInputStream(InputStream in) {
        notNull(in, "The input stream to copy data cannot be null.");
    }

    /**
     * 校验长度。
     * <p>长度应该大于等于 0。</p>
     *
     * @param length 表示待校验长度的 {@code long}。
     * @throws IllegalArgumentException 当长度为负数时。
     */
    private static void validateLength(long length) {
        Validation.greaterThanOrEquals(length, 0, "The length of data to copy cannot be negative.");
    }

    /**
     * 校验输出文件。
     * <p>输出文件不能为 {@code null}。</p>
     *
     * @param outputFile 表示待校验的输出文件的 {@link File}。
     * @throws IllegalArgumentException 当输入文件为 {@code null} 时。
     */
    private static void validateOutputFile(File outputFile) {
        notNull(outputFile, "The output file to copy data cannot be null.");
    }

    /**
     * 校验输出流。
     * <p>输出流不能为 {@code null}。</p>
     *
     * @param out 表示待校验的输出流的 {@link OutputStream}。
     * @throws IllegalArgumentException 当输出流为 {@code null} 时。
     */
    private static void validateOutputStream(OutputStream out) {
        notNull(out, "The output stream to copy data cannot be null.");
    }

    /**
     * 从指定的随机访问文件中的指定位置读取内容。
     *
     * @param file 表示指定随机访问文件的 {@link RandomAccessFile}。
     * @param position 表示文件指定位置的 {@code long}。
     * @param length 表示需要读取的内容长度的 {@code long}。
     * @return 表示读取到的文件内容的 {@code byte[]}。
     * @throws IOException 当发生 I/O 异常时。
     * @throws IllegalArgumentException 当 {@code file} 为 {@code null} 时。
     */
    public static byte[] read(RandomAccessFile file, long position, int length) throws IOException {
        notNull(file, "The file cannot be null.");
        file.seek(position);
        return read(file, length);
    }

    /**
     * 从指定的随机访问文件中的开头读取内容。
     *
     * @param file 表示指定随机访问文件的 {@link RandomAccessFile}。
     * @param length 表示需要读取的内容长度的 {@code long}。
     * @return 表示读取到的文件内容的 {@code byte[]}。
     * @throws IOException 当发生 I/O 异常时。
     * @throws IllegalArgumentException 当 {@code file} 为 {@code null} 时。
     */
    public static byte[] read(RandomAccessFile file, int length) throws IOException {
        notNull(file, "The file cannot be null.");
        byte[] bytes = new byte[length];
        fill(file, bytes, 0, length);
        return bytes;
    }

    /**
     * 从指定的随机访问文件中的开始位置，读取内容，填充到指定字节数组中。
     *
     * @param file 表示指定随机访问文件的 {@link RandomAccessFile}。
     * @param bytes 表示待填充的字节数组的 {@code byte[]}。
     * @throws IOException 当发生 I/O 异常时。
     * @throws IllegalArgumentException 当 {@code file} 为 {@code null} 时。
     */
    public static void fill(RandomAccessFile file, byte[] bytes) throws IOException {
        fill(file, bytes, 0, bytes.length);
    }

    /**
     * 从指定的随机访问文件中的指定位置，读取指定长度的内容，填充到指定字节数组中。
     *
     * @param file 表示指定随机访问文件的 {@link RandomAccessFile}。
     * @param bytes 表示待填充的字节数组的 {@code byte[]}。
     * @param offset 表示随机访问文件读取内容开始的位置的 {@code int}。
     * @param length 表示待读取内容的长度的 {@code int}。
     * @throws IOException 当发生 I/O 异常时。
     * @throws IllegalArgumentException 当 {@code file} 为 {@code null} 时。
     */
    public static void fill(RandomAccessFile file, byte[] bytes, int offset, int length) throws IOException {
        notNull(file, "The file cannot be null.");
        int read = 0;
        while (read < length) {
            int part = file.read(bytes, offset + read, length - read);
            if (part < 0) {
                throw new EOFException("No more data to read.");
            } else {
                read += part;
            }
        }
    }
}