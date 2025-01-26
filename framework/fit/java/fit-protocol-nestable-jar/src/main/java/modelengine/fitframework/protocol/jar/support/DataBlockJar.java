/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.support;

import modelengine.fitframework.protocol.jar.CompressionMethod;
import modelengine.fitframework.protocol.jar.Jar;
import modelengine.fitframework.protocol.jar.JarEntryLocation;
import modelengine.fitframework.protocol.jar.JarFormatException;
import modelengine.fitframework.protocol.jar.JarLocation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Permission;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * 为 {@link Jar} 提供基类。
 *
 * @author 梁济时
 * @since 2023-01-12
 */
final class DataBlockJar implements Jar {
    private final JarLocation location;
    private final DataLocator locatorOfData;
    private final String comment;
    private final JarEntryCollection entries;

    private DataBlockJar(JarLocation location, DataLocator locatorOfData, DataRandomReader reader) throws IOException {
        this.location = location;
        this.locatorOfData = locatorOfData;
        Zip cd = new Zip(reader);
        this.comment = cd.comment();
        this.entries = new JarEntryCollection(cd.size());
        Enumerator<Zip.FileHeader> enumerator = cd.enumerator();
        while (enumerator.more()) {
            Zip.FileHeader header = enumerator.next();
            Entry entry = this.new Entry(header);
            entries.add(entry);
        }
    }

    final DataLocator locatorOfData() {
        return this.locatorOfData;
    }

    @Override
    public final JarLocation location() {
        return this.location;
    }

    @Override
    public final Permission permission() {
        return this.locatorOfData.permission();
    }

    @Override
    public final EntryCollection entries() {
        return this.entries;
    }

    @Override
    public final String comment() {
        return this.comment;
    }

    @Override
    public String toString() {
        return this.location().toString();
    }

    static DataBlockJar load(File file) throws IOException {
        JarLocation jarLocation = JarLocation.custom().file(file).build();
        DataLocator dataLocator = DataLocator.of(jarLocation.file());
        try (DataRandomReader reader = DataRandomReader.from(dataLocator)) {
            return new DataBlockJar(jarLocation, dataLocator, reader);
        }
    }

    /**
     * 为 {@link Jar.Entry} 提供基于数据块的实现。
     *
     * @author 梁济时
     * @since 2023-01-12
     */
    private final class Entry implements Jar.Entry {
        private final String name;
        private final CompressionMethod methodOfCompression;
        private final int crc32;
        private final long sizeOfCompressed;
        private final long sizeOfUncompressed;
        private final Date timeOfLastModification;
        private final byte[] extra;
        private final String comment;
        private final long offsetOfLocalHeader;
        private final JarEntryLocation location;

        Entry(Zip.FileHeader header) {
            this.name = header.filename();
            this.methodOfCompression = CompressionMethod.fromId(header.methodOfCompression());
            this.crc32 = header.crc32();
            this.sizeOfCompressed = header.sizeOfCompressed();
            this.sizeOfUncompressed = header.sizeOfUncompressed();
            this.timeOfLastModification = header.timeOfLastModification();
            this.extra = header.extra();
            this.comment = header.comment();
            this.offsetOfLocalHeader = header.offsetOfLocalHeader();

            this.location = JarEntryLocation.custom().jar(DataBlockJar.this.location()).entry(this.name).build();
        }

        @Override
        public DataBlockJar jar() {
            return DataBlockJar.this;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public CompressionMethod methodOfCompression() {
            return this.methodOfCompression;
        }

        @Override
        public int crc32() {
            return this.crc32;
        }

        @Override
        public long sizeOfCompressed() {
            return this.sizeOfCompressed;
        }

        @Override
        public long sizeOfUncompressed() {
            return this.sizeOfUncompressed;
        }

        @Override
        public Date timeOfLastModification() {
            return this.timeOfLastModification;
        }

        @Override
        public byte[] extra() {
            return Arrays.copyOf(this.extra, this.extra.length);
        }

        @Override
        public String comment() {
            return this.comment;
        }

        @Override
        public long offsetOfLocalHeader() {
            return this.offsetOfLocalHeader;
        }

        @Override
        public boolean directory() {
            return !this.name().isEmpty()
                    && this.name().charAt(this.name().length() - 1) == JarEntryLocation.ENTRY_PATH_SEPARATOR;
        }

        @Override
        public JarEntryLocation location() {
            return this.location;
        }

        @Override
        public InputStream read() throws IOException {
            // 性能敏感场景，IDEA 提示无情重构为 Files.newInputStream(Path path)，该重构性能劣化严重，禁止该重构。
            InputStream in = new FileInputStream(DataBlockJar.this.locatorOfData().file());
            try {
                long bytesToSkip = DataBlockJar.this.locatorOfData.offset() + this.offsetOfLocalHeader;
                long skipped = 0L;
                while (skipped < bytesToSkip) {
                    skipped += in.skip(bytesToSkip - skipped);
                }
                Zip.skipLocalHeader(in);
                in = new LimitedInputStream(in, this.sizeOfCompressed());
                switch (this.methodOfCompression()) {
                    case NONE:
                        return in;
                    case DEFLATED:
                        return new JarInflaterInputStream(in, this.sizeOfCompressed());
                    default:
                        throw new JarFormatException(String.format(Locale.ROOT,
                                "Unsupported method of compression. [method=%s]",
                                this.methodOfCompression().code()));
                }
            } catch (IOException | RuntimeException e) {
                try {
                    in.close();
                } catch (IOException | RuntimeException suppressed) {
                    e.addSuppressed(suppressed);
                }
                throw e;
            }
        }

        @Override
        public Jar asJar() throws IOException {
            if (this.directory()) {
                return new NestedDirectoryJar(this);
            } else if (Objects.equals(this.methodOfCompression, CompressionMethod.NONE)) {
                try (DataRandomReader data = DataRandomReader.from(DataBlockJar.this.locatorOfData)) {
                    long offset = this.offsetOfLocalHeader;
                    offset += Zip.measureLocalHeader(data, offset);
                    long length = this.sizeOfCompressed;
                    DataLocator locator = DataBlockJar.this.locatorOfData.sub(offset, length);
                    try (DataRandomReader sub = data.sub(offset, length)) {
                        return new DataBlockJar(this.location.asJar(), locator, sub);
                    }
                }
            } else {
                throw new JarFormatException(String.format(Locale.ROOT,
                        "The entry to be used as a JAR cannot be compressed. [entry=%s, compressionMethod=%s]",
                        this.toString(),
                        this.methodOfCompression.code()));
            }
        }

        @Override
        public String toString() {
            return this.location.toString();
        }
    }
}
