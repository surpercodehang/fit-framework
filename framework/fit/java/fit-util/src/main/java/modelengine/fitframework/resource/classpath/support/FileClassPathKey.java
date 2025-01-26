/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource.classpath.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.io.virtualization.VirtualDirectory;
import modelengine.fitframework.resource.classpath.ClassPath;
import modelengine.fitframework.resource.classpath.ClassPathKey;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Objects;

/**
 * {@link DefaultClassPath} 提供 {@code file} {@link URI#getScheme() scheme} 的实现。
 *
 * @author 梁济时
 * @since 2022-07-27
 */
public final class FileClassPathKey implements ClassPathKey {
    private final File file;

    /**
     * 使用文件初始化 {@link FileClassPathKey} 类的新实例。
     *
     * @param file 表示文件的 {@link File}。
     * @throws IllegalArgumentException {@code file} 为 {@code null}，或无法被标准化。
     */
    public FileClassPathKey(File file) {
        try {
            this.file = notNull(file, "The file of classpath key cannot be null.").getCanonicalFile();
        } catch (IOException ex) {
            throw new IllegalArgumentException(StringUtils.format(
                    "The file of classpath key is not canonical. [file={0}]",
                    FileUtils.path(file)));
        }
    }

    /**
     * 获取类路径的文件。
     *
     * @return 表示文件的 {@link File}。
     */
    public File file() {
        return this.file;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {FileClassPathKey.class, this.file});
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof FileClassPathKey) {
            FileClassPathKey another = (FileClassPathKey) obj;
            return Objects.equals(this.file, another.file);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return FileUtils.path(this.file);
    }

    @Override
    public ClassPath create() throws IOException {
        VirtualDirectory directory;
        if (file.isDirectory()) {
            directory = VirtualDirectory.of(file);
        } else {
            throw new UnsupportedOperationException("Does not support zip directory");
        }
        return new DefaultClassPath(this, directory);
    }
}
