/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.resource.Resource;
import modelengine.fitframework.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

/**
 * 为 {@link Resource} 提供基于文件的实现。
 *
 * @author 梁济时
 * @since 2022-12-20
 */
public class FileResource implements Resource {
    private final File file;
    private final String filename;
    private volatile URL url;

    /**
     * 为指定的文件创建资源。
     *
     * @param file 表示资源文件的 {@link File}。
     * @throws IllegalArgumentException {@code file} 为 {@code null}。
     */
    public FileResource(File file) {
        this.file = notNull(file, "The file of a resource cannot be null.");
        this.filename = this.file.getName();
        this.url = FileUtils.urlOf(this.file);
    }

    public File file() {
        return this.file;
    }

    @Override
    public String filename() {
        return this.filename;
    }

    @Override
    public URL url() throws MalformedURLException {
        if (this.url == null) {
            this.url = this.file.toURI().toURL();
        }
        return this.url;
    }

    @Override
    public InputStream read() throws IOException {
        return new FileInputStream(this.file);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            FileResource another = (FileResource) obj;
            return Objects.equals(this.file, another.file);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.getClass(), this.file});
    }

    @Override
    public String toString() {
        return FileUtils.path(this.file);
    }
}
