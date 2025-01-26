/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.support;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.FunctionUtils;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 为压缩成 {@code .zip} 格式的文件提供工具。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-10-05
 */
public class Zip extends AbstractZip<Zip> {
    private static final String ENTRY_SEPARATOR = "/";

    private final List<File> targets;

    private Predicate<File> predicate;

    /**
     * 构造一个新的 {@link Zip} 实例。
     *
     * @param zipFile 表示压缩文件的 {@link File}。
     * @param charset 表示压缩文件的字符集 {@link Charset}。
     */
    public Zip(File zipFile, Charset charset) {
        super(zipFile, charset);
        this.targets = new ArrayList<>();
    }

    /**
     * 添加打包的目标文件。
     *
     * @param target 表示打包的目标文件的 {@link File}。
     * @return 表示打包的文件类的 {@link Zip}。
     */
    public Zip add(File target) {
        Validation.notNull(target, "The file to zip cannot be null.");
        this.targets.add(target);
        return this;
    }

    /**
     * 添加一个对打包文件内容项的过滤条件。
     *
     * @param predicate 表示打包过滤条件的 {@link Predicate}{@code <}{@link File}{@code >}。
     * @return 表示打包的文件类的 {@link Zip}。
     */
    public Zip filter(Predicate<File> predicate) {
        this.predicate = FunctionUtils.and(this.predicate, predicate);
        return this;
    }

    @Override
    public void start() throws IOException {
        this.deleteZipFile();
        try (FileOutputStream out = new FileOutputStream(this.file());
             ZipOutputStream zip = new ZipOutputStream(out, this.charset())) {
            for (File target : this.targets) {
                this.zip(zip, target);
            }
        }
    }

    private void deleteZipFile() throws IOException {
        if (this.file().exists()) {
            if (this.override()) {
                Files.delete(this.file().toPath());
            } else {
                throw new IOException(StringUtils.format("The output file of zip already exists. [name={0}]",
                        this.file().getName()));
            }
        }
    }

    private boolean filter(File file) {
        return FunctionUtils.test(this.predicate, file, true);
    }

    private String getEntryName(File target) {
        return target.isDirectory() ? target.getName() + ENTRY_SEPARATOR : target.getName();
    }

    private void zip(ZipOutputStream zip, File target) throws IOException {
        this.zip(zip, target, this.getEntryName(target));
    }

    private void zip(ZipOutputStream zip, File target, String entryName) throws IOException {
        if (!this.filter(target)) {
            return;
        }
        BasicFileAttributeView view =
                Files.getFileAttributeView(target.toPath(), BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
        BasicFileAttributes attributes = view.readAttributes();
        zip.putNextEntry(new ZipEntry(entryName).setCreationTime(attributes.creationTime())
                .setLastModifiedTime(attributes.lastModifiedTime())
                .setLastAccessTime(attributes.lastAccessTime()));
        if (target.isDirectory()) {
            zip.closeEntry();
            this.zip(zip, FileUtils.list(target), entryName);
            return;
        }
        try {
            IoUtils.copy(target, zip);
        } finally {
            zip.closeEntry();
        }
    }

    private void zip(ZipOutputStream zip, List<File> targets, String path) throws IOException {
        for (File target : targets) {
            this.zip(zip, target, path + this.getEntryName(target));
        }
    }
}
