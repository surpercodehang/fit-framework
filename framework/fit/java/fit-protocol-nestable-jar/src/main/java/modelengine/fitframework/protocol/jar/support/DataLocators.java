/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.support;

import static modelengine.fitframework.protocol.jar.support.Locations.path;

import java.io.File;
import java.io.FilePermission;
import java.security.Permission;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

/**
 * 为 {@link DataLocator} 提供工具方法。
 *
 * @author 梁济时
 * @since 2023-02-22
 */
final class DataLocators {
    /**
     * 为 {@link DataLocator} 提供默认实现。
     *
     * @author 梁济时
     * @since 2023-02-21
     */
    static final class Default implements DataLocator {
        private static final String FILE_PERMISSION_ACTION = "read";

        private final File file;
        private final long offset;
        private final long length;
        private final Permission permission;

        /**
         * 使用目标文件初始化 {@link Default} 类的新实例。
         *
         * @param file 表示目标文件的 {@link File}。
         * @throws IllegalArgumentException {@code file} 为 {@code null} 或不存在或不是一个常规文件。
         */
        Default(File file) {
            this(validate(file), 0L, file.length(), null);
        }

        private Default(File file, long offset, long length, Permission permission) {
            this.file = file;
            this.offset = offset;
            this.length = length;
            if (permission == null) {
                this.permission = new FilePermission(path(this.file), FILE_PERMISSION_ACTION);
            } else {
                this.permission = permission;
            }
        }

        private static File validate(File file) {
            if (file == null) {
                throw new IllegalArgumentException("The file of a data locator cannot be null.");
            } else if (!file.exists()) {
                throw new IllegalArgumentException(String.format(Locale.ROOT,
                        "The file of data locator does not exist. [path=%s]", path(file)));
            } else if (!file.isFile()) {
                throw new IllegalArgumentException(String.format(Locale.ROOT,
                        "THe file of data locator is not a regular file. [path=%s]", path(file)));
            } else {
                return file;
            }
        }

        @Override
        public File file() {
            return this.file;
        }

        @Override
        public long offset() {
            return this.offset;
        }

        @Override
        public long length() {
            return this.length;
        }

        @Override
        public Permission permission() {
            return this.permission;
        }

        @Override
        public DataLocator sub(long offset, long length) {
            if (offset < 0 || offset > this.length) {
                throw new IllegalArgumentException(String.format(Locale.ROOT,
                        "The offset of sub data locator is out of bounds. [offset=%d, data.length=%d]",
                        offset, this.length));
            } else if (length < 0 || offset + length > this.length) {
                throw new IllegalArgumentException(String.format(Locale.ROOT,
                        "The length of sub data locator is out of bounds. [offset=%d, length=%d, data.length=%d]",
                        offset, length, this.length));
            } else {
                return new Default(this.file, this.offset + offset, length, this.permission);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (obj != null && obj.getClass() == this.getClass()) {
                Default another = (Default) obj;
                return Objects.equals(this.file, another.file)
                        && this.offset == another.offset && this.length == another.length;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new Object[] {this.getClass(), this.file, this.offset, this.length});
        }

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "%s?offset=%d&length=%d",
                    path(this.file), this.offset, this.length);
        }
    }
}
