/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.io.virtualization.local;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.io.virtualization.VirtualDirectory;
import modelengine.fitframework.io.virtualization.VirtualFile;
import modelengine.fitframework.util.FileUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为 {@link VirtualDirectory} 提供基于本地文件系统的实现。
 *
 * @author 梁济时
 * @since 2022-06-06
 */
public class LocalVirtualDirectory implements VirtualDirectory {
    private final File directory;

    /**
     * 使用文件系统中的目录实例初始化 {@link LocalVirtualDirectory} 类的新实例。
     *
     * @param directory 表示文件系统中目录实例的 {@link File}。
     * @throws IllegalArgumentException {@code directory} 为 {@code null}，或不存在或不是一个目录。
     */
    public LocalVirtualDirectory(File directory) {
        Validation.notNull(directory, "The data of virtual directory cannot be null.");
        if (!directory.exists()) {
            throw new IllegalArgumentException("The directory does not exist.");
        } else if (!directory.isDirectory()) {
            throw new IllegalArgumentException(StringUtils.format(
                    "The file system element is not a directory. [element={0}]",
                    FileUtils.path(directory)));
        } else {
            this.directory = directory;
        }
    }

    @Override
    public String name() {
        return this.directory.getName();
    }

    @Override
    public String path() {
        return FileUtils.path(this.directory);
    }

    @Override
    public VirtualDirectory parent() {
        File parent = this.directory.getParentFile();
        return ObjectUtils.mapIfNotNull(parent, VirtualDirectory::of);
    }

    @Override
    public Collection<VirtualDirectory> children() {
        return Optional.ofNullable(this.directory.listFiles())
                .map(LocalVirtualDirectory::directories)
                .orElse(Collections.emptyList());
    }

    @Override
    public Optional<VirtualDirectory> child(String name) {
        File child = new File(this.directory, name);
        if (!child.exists() || !child.isDirectory()) {
            return Optional.empty();
        } else {
            return Optional.of(VirtualDirectory.of(child));
        }
    }

    @Override
    public Optional<VirtualDirectory> child(String... path) {
        return child(this, path, path.length);
    }

    @Override
    public Collection<VirtualFile> files() {
        return Optional.ofNullable(this.directory.listFiles())
                .map(LocalVirtualDirectory::files)
                .orElse(Collections.emptyList());
    }

    @Override
    public Optional<VirtualFile> file(String name) {
        File child = new File(this.directory, name);
        if (!child.exists() || !child.isFile()) {
            return Optional.empty();
        } else {
            return Optional.of(VirtualFile.of(child));
        }
    }

    @Override
    public Optional<VirtualFile> file(String... path) {
        int directoryPathLength = path.length - 1;
        return child(this, path, directoryPathLength).flatMap(directory -> directory.file(path[directoryPathLength]));
    }

    @Override
    public URL url() {
        return FileUtils.urlOf(this.directory);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof LocalVirtualDirectory) {
            LocalVirtualDirectory another = (LocalVirtualDirectory) obj;
            return another.directory.equals(this.directory);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {LocalVirtualDirectory.class, this.directory});
    }

    @Override
    public String toString() {
        return this.path();
    }

    private static Collection<VirtualDirectory> directories(File[] files) {
        return Stream.of(files).filter(File::isDirectory).map(VirtualDirectory::of).collect(Collectors.toList());
    }

    private static Collection<VirtualFile> files(File[] files) {
        return Stream.of(files).filter(File::isFile).map(VirtualFile::of).collect(Collectors.toList());
    }

    private static Optional<VirtualDirectory> child(VirtualDirectory system, String[] path, int length) {
        Optional<VirtualDirectory> current = Optional.of(system);
        for (int i = 0; i < length && current.isPresent(); i++) {
            current = current.get().child(path[i]);
        }
        return current;
    }
}
