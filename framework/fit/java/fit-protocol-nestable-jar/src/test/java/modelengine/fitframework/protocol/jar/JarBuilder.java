/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 用于构建 Jar 文件的工具类。
 *
 * @author 季聿阶
 * @since 2024-03-29
 */
public final class JarBuilder implements Closeable {
    private final ZipOutputStream zip;
    private final Set<String> packages;

    private JarBuilder(ZipOutputStream zip) {
        this.zip = zip;
        this.packages = new HashSet<>();
    }

    private void ensurePackage(String packageName) throws IOException {
        if (packageName == null || this.packages.contains(packageName)) {
            return;
        }
        this.ensurePackage(parent(packageName));
        ZipEntry entry = new ZipEntry(packageName);
        this.zip.putNextEntry(entry);
        this.zip.closeEntry();
        this.packages.add(packageName);
    }

    private static String parent(String entryName) {
        int last = entryName.length() - 1;
        if (entryName.charAt(last) == JarEntryLocation.ENTRY_PATH_SEPARATOR) {
            last--;
        }
        last = entryName.lastIndexOf(JarEntryLocation.ENTRY_PATH_SEPARATOR, last);
        if (last < 0) {
            return null;
        } else {
            return entryName.substring(0, last + 1);
        }
    }

    long add(String entryName, byte[] bytes) throws IOException {
        this.ensurePackage(parent(entryName));
        ZipEntry entry = new ZipEntry(entryName);
        this.zip.putNextEntry(entry);
        this.zip.write(bytes);
        this.zip.closeEntry();
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        return crc32.getValue();
    }

    /**
     * 将给定的字节数组作为一个新的 Jar 条目添加到 Jar 文件中。
     *
     * @param entryName 表示条目名称的 {@link String}。
     * @param bytes 表示要添加的字节数组的 {@code byte[]}。
     * @return 表示条目的 CRC32 值的 {@code long}。
     * @throws IOException 如果发生 I/O 错误。
     */
    public long store(String entryName, byte[] bytes) throws IOException {
        this.ensurePackage(parent(entryName));
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        ZipEntry entry = new ZipEntry(entryName);
        entry.setMethod(ZipEntry.STORED);
        entry.setCrc(crc32.getValue());
        entry.setCompressedSize(bytes.length);
        entry.setSize(bytes.length);
        this.zip.putNextEntry(entry);
        this.zip.write(bytes);
        this.zip.closeEntry();
        return crc32.getValue();
    }

    @Override
    public void close() throws IOException {
        this.zip.close();
    }

    /**
     * 新建一个 JarBuilder 实例，该实例将会将 Jar 文件的内容写入到指定的文件中。
     *
     * @param file 表示指定的文件的 {@link File}。
     * @return 表示 JarBuilder 实例的 {@link JarBuilder}。
     * @throws IOException 如果发生 I/O 错误。
     */
    public static JarBuilder of(File file) throws IOException {
        return new JarBuilder(new ZipOutputStream(Files.newOutputStream(file.toPath())));
    }

    static JarBuilder of(OutputStream out) {
        return new JarBuilder(new ZipOutputStream(out));
    }
}
