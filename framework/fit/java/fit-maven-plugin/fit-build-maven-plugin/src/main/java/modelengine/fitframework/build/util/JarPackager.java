/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.build.util;

import modelengine.fitframework.protocol.jar.Jar;
import modelengine.fitframework.protocol.jar.JarEntryLocation;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.StringUtils;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.attribute.FileTime;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 提供 JAR 格式的打包程序。
 *
 * @author 梁济时
 * @since 2023-02-02
 */
public final class JarPackager {
    private static final FileTime NONE_TIME = FileTime.fromMillis(0L);

    private final ZipOutputStream out;
    private final Set<String> directories;

    public JarPackager(ZipOutputStream out) {
        this.out = out;
        this.directories = new HashSet<>();
    }

    public ZipOutputStream out() {
        return this.out;
    }

    /**
     * 确保创建指定的目录。
     *
     * @param fileEntry 表示文件目录的 {@link String}。
     * @throws MojoExecutionException 当创建目录过程发生执行异常时。
     */
    public void ensureDirectory(String fileEntry) throws MojoExecutionException {
        String[] path = StringUtils.split(fileEntry, JarEntryLocation.ENTRY_PATH_SEPARATOR);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < path.length - 1; i++) {
            builder.append(path[i]).append(JarEntryLocation.ENTRY_PATH_SEPARATOR);
            String name = builder.toString();
            if (!this.directories.contains(name)) {
                this.directories.add(name);
                ZipEntry entry = new ZipEntry(name);
                try {
                    this.out.putNextEntry(entry);
                    this.out.closeEntry();
                } catch (IOException ex) {
                    throw new MojoExecutionException(StringUtils.format(
                            "Failed to create directory in JAR. " + "[entry={0}]", name), ex);
                }
            }
        }
    }

    /**
     * 将指定的 {@link Jar.Entry} 打包到当前的文件中。
     *
     * @param entry 表示待打包文件的 {@link Jar.Entry}。
     * @throws MojoExecutionException 当打包过程中发生执行异常时。
     */
    public void packageJarEntry(Jar.Entry entry) throws MojoExecutionException {
        this.packageJarEntry(entry, entry.name());
    }

    /**
     * 按照指定名称将 {@link Jar.Entry} 打包到当前的文件中。
     *
     * @param entry 表示待打包文件的 {@link Jar.Entry}。
     * @param newEntryName 表示文件名的 {@link String}。
     * @throws MojoExecutionException 当打包过程中发生执行异常时。
     */
    public void packageJarEntry(Jar.Entry entry, String newEntryName) throws MojoExecutionException {
        if (entry.directory()) {
            return;
        }
        this.ensureDirectory(newEntryName);
        ZipEntry target = new ZipEntry(newEntryName);
        target.setSize(entry.sizeOfUncompressed());
        target.setCompressedSize(entry.sizeOfCompressed());
        target.setMethod(entry.methodOfCompression().id());
        target.setComment(entry.comment());
        target.setCrc(Integer.toUnsignedLong(entry.crc32()));
        target.setCreationTime(FileTime.fromMillis(0));
        target.setExtra(entry.extra());
        target.setLastAccessTime(FileTime.fromMillis(0));
        target.setLastModifiedTime(FileTime.fromMillis(0));
        try {
            this.out.putNextEntry(target);
            try (InputStream in = entry.read()) {
                IoUtils.copy(in, this.out);
            }
            this.out.closeEntry();
        } catch (IOException ex) {
            throw new MojoExecutionException(StringUtils.format("Failed to package entry in JAR. [entry={0}]",
                    entry.location()), ex);
        }
    }

    /**
     * 将目录添加到当前的 ZIP 文件中。
     *
     * @param entryName 表示目录名称的 {@link String}。
     * @param bytes 表示目录内容的 {@code byte}{@code [}{@code ]}。
     * @throws MojoExecutionException 当添加目录过程中发生执行异常时。
     */
    public void addEntry(String entryName, byte[] bytes) throws MojoExecutionException {
        this.ensureDirectory(entryName);
        ZipEntry entry = new ZipEntry(entryName);
        entry.setLastModifiedTime(NONE_TIME);
        entry.setLastAccessTime(NONE_TIME);
        entry.setCreationTime(NONE_TIME);
        entry.setTime(0L);
        try {
            this.out.putNextEntry(entry);
            this.out.write(bytes);
            this.out.closeEntry();
        } catch (IOException e) {
            throw new MojoExecutionException(StringUtils.format("Failed to add new entry to JAR. [entry={0}]",
                    entryName), e);
        }
    }

    /**
     * 将文件添加到当前的文件的指定的目录中。
     *
     * @param file 表示文件的 {@link File}。
     * @param directory 表示目录的 {@link String}。
     * @throws MojoExecutionException 当添加文件过程中发生执行异常时。
     */
    public void store(File file, String directory) throws MojoExecutionException {
        ZipEntry entry = new ZipEntry(directory + file.getName());
        entry.setCrc(crc32(file));
        entry.setSize(file.length());
        entry.setCompressedSize(file.length());
        entry.setLastModifiedTime(NONE_TIME);
        entry.setLastAccessTime(NONE_TIME);
        entry.setCreationTime(NONE_TIME);
        entry.setMethod(ZipEntry.STORED);
        entry.setTime(0L);
        this.ensureDirectory(entry.getName());
        try {
            this.out.putNextEntry(entry);
            IoUtils.copy(file, this.out);
            this.out.closeEntry();
        } catch (IOException ex) {
            throw new MojoExecutionException(StringUtils.format("Failed to store file into JAR. [file={0}, entry={1}]",
                    FileUtils.path(file),
                    entry.getName()), ex);
        }
    }

    private static long crc32(File file) throws MojoExecutionException {
        CRC32 crc32 = new CRC32();
        byte[] buffer = new byte[IoUtils.DEFAULT_BUFFER_SIZE];
        try (InputStream in = new FileInputStream(file)) {
            while (true) {
                int read = in.read(buffer);
                if (read < 0) {
                    break;
                } else {
                    crc32.update(buffer, 0, read);
                }
            }
        } catch (IOException ex) {
            throw new MojoExecutionException(StringUtils.format("Failed to compute CRC-32 of file. [file={0}]",
                    FileUtils.path(file)), ex);
        }
        return crc32.getValue();
    }

    /**
     * 根据数据流创建新实例。
     *
     * @param out 表示数据流的 {@link ZipOutputStream}。
     * @return 表示创建的新实例的 {@link JarPackager}。
     */
    public static JarPackager of(ZipOutputStream out) {
        return new JarPackager(out);
    }
}
