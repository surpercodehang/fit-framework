/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.support;

import static modelengine.fitframework.protocol.jar.support.Locations.path;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Locale;

/**
 * 为 {@link DataRandomReader} 提供工具方法。
 *
 * @author 梁济时
 * @since 2023-02-22
 */
final class DataRandomReaders {
    /**
     * 表示数据块。
     *
     * @author 梁济时
     * @since 2023-02-14
     */
    static final class Default implements DataRandomReader {
        private final DataLocator locator;
        private final RandomAccessFile access;
        private final boolean root;

        Default(DataLocator locator) throws IOException {
            this(locator, null);
        }

        private Default(DataLocator locator, RandomAccessFile access) throws IOException {
            this.locator = locator;
            if (access == null) {
                this.access = new RandomAccessFile(this.locator.file(), "r");
                this.root = true;
            } else {
                this.access = access;
                this.root = false;
            }
        }

        @Override
        public void close() throws IOException {
            if (this.root) {
                this.access.close();
            }
        }

        @Override
        public long length() {
            return this.locator.length();
        }

        @Override
        public byte[] read(long position, int length) throws IOException {
            validate(position, length, this.locator.length());
            this.access.seek(this.locator.offset() + position);
            byte[] bytes = new byte[length];
            int read = 0;
            while (read < length) {
                int part = this.access.read(bytes, read, length - read);
                if (part < 0) {
                    throw new EOFException(String.format(Locale.ROOT,
                            "No enough data to read. [file=%s, position=%s, length=%d, actual=%d]",
                            path(this.locator.file()), position, length, read));
                } else {
                    read += part;
                }
            }
            return bytes;
        }

        @Override
        public Default sub(long offset, long length) throws IOException {
            if (offset < 0 || offset > this.locator.length()) {
                throw new IllegalArgumentException(String.format(Locale.ROOT,
                        "The offset of a subsection is out of bounds. [offset=%d, data.length=%d]",
                        offset,
                        this.locator.length()));
            }
            if (length < 0 || offset + length > this.locator.length()) {
                throw new IllegalArgumentException(String.format(Locale.ROOT,
                        "The length of a subsection is out of bounds. [offset=%d, length=%d, data.length=%d]",
                        offset,
                        length,
                        this.locator.length()));
            }
            if (offset == 0L && length == this.locator.length()) {
                return this;
            } else {
                DataLocator dataLocator = this.locator.sub(offset, length);
                return new Default(dataLocator, this.access);
            }
        }

        private static void validate(long position, long dataLength) {
            if (position < 0L || position > dataLength) {
                throw new IllegalArgumentException(String.format(Locale.ROOT,
                        "The position of data to read cannot is out of bounds. [position=%d, data.length=%d]",
                        position,
                        dataLength));
            }
        }

        private static void validate(long position, int length, long dataLength) {
            validate(position, dataLength);
            if (position + length > dataLength) {
                throw new IllegalArgumentException(String.format(Locale.ROOT,
                        "No enough data to read. [position=%d, length=%d, data.length=%d]",
                        position,
                        length,
                        dataLength));
            }
        }
    }
}
