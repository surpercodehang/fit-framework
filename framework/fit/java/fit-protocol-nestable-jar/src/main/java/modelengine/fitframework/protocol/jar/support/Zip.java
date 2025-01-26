/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.support;

import modelengine.fitframework.protocol.jar.JarFormatException;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.NoSuchElementException;

/**
 * 用以解析 ZIP 格式的数据。
 *
 * @author 梁济时
 * @since 2022-09-15
 */
final class Zip implements Enumerable<Zip.FileHeader> {
    private static final int LOCAL_HEADER_SIGNATURE = 0x04034b50;
    private static final int LOCAL_HEADER_MINIMUM_LENGTH = 30;
    private static final int LOCAL_HEADER_FILE_NAME_LENGTH_POSITION = 26;
    private static final int LOCAL_HEADER_EXTRA_FIELD_LENGTH_POSITION = 28;

    private final byte[] bytes;
    private final EndRecord end;

    Zip(DataRandomReader reader) throws IOException {
        this.end = EndRecord.load(reader);
        this.bytes = reader.read(this.end.offsetOfCentralDirectory, this.end.lengthOfCentralDirectory);
    }

    /**
     * 获取中心目录中包含记录的数量。
     *
     * @return 表示记录数量的 32 位整数。
     */
    int size() {
        return this.end.numberOfEntries;
    }

    /**
     * 获取归档件的备注信息。
     *
     * @return 表示归档件备注信息的 {@link String}。
     */
    String comment() {
        return this.end.commentOfZipFile;
    }

    /**
     * 返回一个枚举程序，用以遍历包含的所有文件头信息。
     *
     * @return 表示用以遍历文件头信息的枚举程序的 {@link Enumerator}。
     */
    @Override
    public Enumerator<FileHeader> enumerator() {
        return this.new FileHeaderEnumerator();
    }

    /**
     * 为中心目录提供包含的文件头信息的枚举程序。
     *
     * @author 梁济时
     * @since 2022-09-15
     */
    private final class FileHeaderEnumerator implements Enumerator<FileHeader> {
        private int index;
        private int offset;

        /**
         * 初始化 {@link FileHeaderEnumerator} 类的新实例。
         */
        private FileHeaderEnumerator() {
            this.index = 0;
            this.offset = 0;
        }

        @Override
        public boolean more() {
            return this.index < Zip.this.size();
        }

        @Override
        public FileHeader next() throws JarFormatException {
            FileHeader header = FileHeader.load(Zip.this.bytes, this.offset);
            this.offset += header.length();
            this.index++;
            return header;
        }
    }

    /**
     * 为 JAR 的中心目录提供结束记录。
     *
     * @author 梁济时
     * @since 2022-09-16
     */
    private static final class EndRecord {
        private static final int SIGNATURE = 0x06054b50;

        private static final int MINIMUM_LENGTH = 22;
        private static final int MAXIMUM_LENGTH = MINIMUM_LENGTH + 0xffff;

        private static final int NUMBER_OF_ENTRIES_POSITION = 10;
        private static final int LENGTH_OF_CENTRAL_DIRECTORY_POSITION = 12;
        private static final int OFFSET_OF_CENTRAL_DIRECTORY_POSITION = 16;
        private static final int FILE_COMMENT_LENGTH_POSITION = 20;

        private static final int BUFFER_SIZE = 128;

        private final long offsetOfCentralDirectory;
        private final int lengthOfCentralDirectory;
        private final int numberOfEntries;
        private final String commentOfZipFile;

        private EndRecord(int numberOfEntries, int lengthOfCentralDirectory, long offsetOfCentralDirectory,
                String commentOfZipFile) {
            this.numberOfEntries = numberOfEntries;
            this.lengthOfCentralDirectory = lengthOfCentralDirectory;
            this.offsetOfCentralDirectory = offsetOfCentralDirectory;
            this.commentOfZipFile = commentOfZipFile;
        }

        private static final class Zip64 {
            private static final int LOCATOR_LENGTH = 20;
            private static final int LOCATOR_SIGNATURE = 0x07064b50;
            private static final int LOCATOR_ZIP64_OFFSET_POSITION = 8;
            private static final int SIGNATURE = 0x06064b50;
            private static final int MINIMUM_LENGTH = 56;
            private static final int SIZE_POSITION = 4;
            private static final int HEADER_LENGTH = 12;

            private static final int NUMBER_OF_ENTRIES_POSITION = 32;
            private static final int SIZE_OF_CENTRAL_DIRECTORY_POSITION = 40;
            private static final int OFFSET_OF_CENTRAL_DIRECTORY_POSITION = 48;

            private final long offsetOfCentralDirectory;
            private final long lengthOfCentralDirectory;
            private final long numberOfEntries;

            private Zip64(long offsetOfCentralDirectory, long lengthOfCentralDirectory, long numberOfEntries) {
                this.offsetOfCentralDirectory = offsetOfCentralDirectory;
                this.lengthOfCentralDirectory = lengthOfCentralDirectory;
                this.numberOfEntries = numberOfEntries;
            }

            private static Zip64 lookup(DataRandomReader reader, long end) throws IOException {
                long locatorOffset = end - LOCATOR_LENGTH;
                byte[] locatorBytes = reader.read(locatorOffset, LOCATOR_LENGTH);
                int locatorSignature = Bytes.s4(locatorBytes, 0);
                if (locatorSignature != LOCATOR_SIGNATURE) {
                    return null;
                }
                long offsetOfZip64 = Bytes.s8(locatorBytes, LOCATOR_ZIP64_OFFSET_POSITION);
                if (offsetOfZip64 + MINIMUM_LENGTH > locatorOffset) {
                    return null;
                }
                byte[] zip64Bytes = reader.read(offsetOfZip64, MINIMUM_LENGTH);
                int zip64Signature = Bytes.s4(zip64Bytes, 0);
                if (zip64Signature != SIGNATURE) {
                    return null;
                }
                long sizeOfZip64 = Bytes.s8(zip64Bytes, SIZE_POSITION);
                long zip64Length = HEADER_LENGTH + sizeOfZip64;
                if (offsetOfZip64 + zip64Length != locatorOffset) {
                    return null;
                }
                return new Zip64(Bytes.s8(zip64Bytes, OFFSET_OF_CENTRAL_DIRECTORY_POSITION),
                        Bytes.s8(zip64Bytes, SIZE_OF_CENTRAL_DIRECTORY_POSITION),
                        Bytes.s8(zip64Bytes, NUMBER_OF_ENTRIES_POSITION));
            }
        }

        private static EndRecord load(DataRandomReader reader) throws IOException {
            if (reader.length() < MINIMUM_LENGTH) {
                throw new JarFormatException(String.format(Locale.ROOT,
                        "The data block is too small for a JAR. [data=%s]",
                        reader));
            }
            byte[] readBytes = new byte[0];
            for (int size = MINIMUM_LENGTH; size < MAXIMUM_LENGTH; size++) {
                if (size > readBytes.length) {
                    readBytes = read(reader, readBytes);
                }
                int offset = readBytes.length - size;
                if (Bytes.s4(readBytes, offset) != SIGNATURE) {
                    continue;
                }
                int lengthOfComment = Bytes.u2(readBytes, offset + FILE_COMMENT_LENGTH_POSITION);
                if (offset + MINIMUM_LENGTH + lengthOfComment != readBytes.length) {
                    continue;
                }
                Zip64 zip64 = Zip64.lookup(reader, offset);
                int offsetOfComment = offset + MINIMUM_LENGTH;
                String comment = new String(readBytes, offsetOfComment, lengthOfComment, StandardCharsets.UTF_8);
                if (zip64 == null) {
                    return new EndRecord(Bytes.u2(readBytes, offset + NUMBER_OF_ENTRIES_POSITION),
                            Bytes.s4(readBytes, offset + LENGTH_OF_CENTRAL_DIRECTORY_POSITION),
                            Bytes.u4(readBytes, offset + OFFSET_OF_CENTRAL_DIRECTORY_POSITION),
                            comment);
                } else {
                    if (zip64.numberOfEntries > Integer.MAX_VALUE) {
                        throw new JarFormatException(String.format(Locale.ROOT,
                                "Too many entries in the JAR. [data=%s, entries=%d]",
                                reader,
                                zip64.numberOfEntries));
                    }
                    if (zip64.lengthOfCentralDirectory > Integer.MAX_VALUE) {
                        throw new JarFormatException(String.format(Locale.ROOT,
                                "The central directory is too large. [data=%s, size=%d]",
                                reader,
                                zip64.lengthOfCentralDirectory));
                    }
                    return new EndRecord((int) zip64.numberOfEntries,
                            (int) zip64.lengthOfCentralDirectory,
                            zip64.offsetOfCentralDirectory,
                            comment);
                }
            }
            throw new JarFormatException(String.format(Locale.ROOT,
                    "No end of central directory found at the bottom of the data block. [data=%s]",
                    reader));
        }

        private static byte[] read(DataRandomReader reader, byte[] current) throws IOException {
            long offset = reader.length() - current.length - BUFFER_SIZE;
            byte[] readBytes = reader.read(offset, BUFFER_SIZE);
            if (current.length > 0) {
                byte[] total = new byte[readBytes.length + current.length];
                System.arraycopy(readBytes, 0, total, 0, readBytes.length);
                System.arraycopy(current, 0, total, readBytes.length, current.length);
                readBytes = total;
            }
            return readBytes;
        }
    }

    /**
     * 表示中心目录中的文件头信息。
     *
     * @author 梁济时
     * @since 2022-09-19
     */
    static final class FileHeader {
        private static final int SIGNATURE = 0x02014b50;
        private static final int MINIMUM_SIZE = 46;

        private static final int COMPRESSION_METHOD_POSITION = 10;
        private static final int LAST_MODIFICATION_TIME_POSITION = 12;
        private static final int LAST_MODIFICATION_DATE_POSITION = 14;
        private static final int CRC_32_POSITION = 16;
        private static final int COMPRESSED_SIZE_POSITION = 20;
        private static final int UNCOMPRESSED_SIZE_POSITION = 24;
        private static final int FILE_NAME_LENGTH_POSITION = 28;
        private static final int EXTRA_FIELD_LENGTH_POSITION = 30;
        private static final int FILE_COMMENT_LENGTH_POSITION = 32;
        private static final int OFFSET_OF_LOCAL_HEADER_POSITION = 42;

        private final long sizeOfCompressed;
        private final long sizeOfUncompressed;
        private final long offsetOfLocalHeader;
        private final int compressionMethod;
        private final int crc32;
        private final Date timeOfLastModification;

        private final String filename;
        private final byte[] extra;
        private final String comment;
        private final int size;

        /**
         * 使用所在中心目录的数据块和当前文件头的偏移量初始化 {@link FileHeader} 类的新实例。
         *
         * @param sizeOfCompressed 表示压缩后的数据的字节数的 64 位整数。
         * @param sizeOfUncompressed 表示压缩前的数据的字节数的 64 位整数。
         * @param offsetOfLocalHeader 表示本地文件头在整体数据块中的偏移量的 64 位整数。
         * @param compressionMethod 表示压缩算法的 32 位整数。
         * @param crc32 表示数据的 CRC 校验和的 32 位整数。
         * @param timeOfLastModification 表示记录的上次修改时间的 {@link Date}。
         * @param filename 表示文件名称的 {@link String}。
         * @param extra 表示记录的扩展数据的字节数组。
         * @param comment 表示文件的备注的 {@link String}。
         */
        FileHeader(long sizeOfCompressed, long sizeOfUncompressed, long offsetOfLocalHeader, int compressionMethod,
                int crc32, Date timeOfLastModification, String filename, byte[] extra, String comment, int size) {
            this.sizeOfCompressed = sizeOfCompressed;
            this.sizeOfUncompressed = sizeOfUncompressed;
            this.offsetOfLocalHeader = offsetOfLocalHeader;
            this.compressionMethod = compressionMethod;
            this.crc32 = crc32;
            this.timeOfLastModification = timeOfLastModification;
            this.filename = filename;
            this.extra = extra;
            this.comment = comment;
            this.size = size;
        }

        static FileHeader load(byte[] bytes, int offset) throws JarFormatException {
            int signature = Bytes.s4(bytes, offset);
            if (signature != SIGNATURE) {
                throw new JarFormatException(String.format(Locale.ROOT,
                        "Bad signature of central directory file header. [expected=0x%08x, actual=0x%08x]",
                        SIGNATURE,
                        signature));
            }
            int lengthOfFileName = Bytes.u2(bytes, offset + FILE_NAME_LENGTH_POSITION);
            int offsetOfFileName = offset + MINIMUM_SIZE;
            int offsetOfExtraField = offsetOfFileName + lengthOfFileName;
            int lengthOfExtraField = Bytes.u2(bytes, offset + EXTRA_FIELD_LENGTH_POSITION);
            int offsetOfComment = offsetOfExtraField + lengthOfExtraField;
            byte[] extraField = Arrays.copyOfRange(bytes, offsetOfExtraField, offsetOfComment);
            Zip64 zip64 = Zip64.lookup(extraField);
            long readSizeOfCompressed;
            long readSizeOfUncompressed;
            long readOffsetOfLocalHeader;
            if (zip64 == null) {
                readSizeOfCompressed = Bytes.u4(bytes, offset + COMPRESSED_SIZE_POSITION);
                readSizeOfUncompressed = Bytes.u4(bytes, offset + UNCOMPRESSED_SIZE_POSITION);
                readOffsetOfLocalHeader = Bytes.u4(bytes, offset + OFFSET_OF_LOCAL_HEADER_POSITION);
            } else {
                readSizeOfCompressed = zip64.sizeOfCompressed;
                readSizeOfUncompressed = zip64.sizeOfUncompressed;
                readOffsetOfLocalHeader = zip64.offsetOfLocalHeader;
            }
            int lengthOfFileComment = Bytes.u2(bytes, offset + FILE_COMMENT_LENGTH_POSITION);
            int headerSize = MINIMUM_SIZE + lengthOfFileName + lengthOfExtraField + lengthOfFileComment;
            return new FileHeader(readSizeOfCompressed,
                    readSizeOfUncompressed,
                    readOffsetOfLocalHeader,
                    Bytes.u2(bytes, offset + COMPRESSION_METHOD_POSITION),
                    Bytes.s4(bytes, offset + CRC_32_POSITION),
                    new MsDosDateTime(
                            Bytes.u2(bytes, offset + LAST_MODIFICATION_DATE_POSITION),
                            Bytes.u2(bytes, offset + LAST_MODIFICATION_TIME_POSITION)).toDate(),
                    new String(bytes, offsetOfFileName, lengthOfFileName, StandardCharsets.UTF_8),
                    extraField,
                    new String(bytes, offsetOfComment, lengthOfFileComment, StandardCharsets.UTF_8),
                    headerSize);
        }

        /**
         * 获取文件头信息的字节数。
         * <p>可根据该字节数，偏移到下一个文件头。</p>
         *
         * @return 表示文件头信息字节数的 32 位整数。
         */
        int length() {
            return this.size;
        }

        /**
         * 读取文件名。
         *
         * @return 表示文件名的 {@link String}。
         */
        String filename() {
            return this.filename;
        }

        /**
         * 读取压缩后的数据的字节数。
         *
         * @return 表示压缩后的数据的字节数的 64 位整数。
         */
        long sizeOfCompressed() {
            return this.sizeOfCompressed;
        }

        /**
         * 读取压缩前的数据的字节数。
         *
         * @return 表示压缩前的数据的字节数的 64 位整数。
         */
        long sizeOfUncompressed() {
            return this.sizeOfUncompressed;
        }

        /**
         * 获取本地文件头在 JAR 数据块中的偏移量。
         *
         * @return 表示本地文件头偏移量的 64 位整数。
         */
        long offsetOfLocalHeader() {
            return this.offsetOfLocalHeader;
        }

        /**
         * 读取数据的压缩方法。
         *
         * @return 表示压缩方法的 32 位整数。
         */
        int methodOfCompression() {
            return this.compressionMethod;
        }

        /**
         * 读取压缩数据的 CRC-32 校验和。
         *
         * @return 表示 CRC-32 校验和的 32位整数。
         */
        int crc32() {
            return this.crc32;
        }

        /**
         * 读取文件的备注信息。
         *
         * @return 表示文件备注信息的 {@link String}。
         */
        String comment() {
            return this.comment;
        }

        /**
         * 读取文件的上次修改时间。
         *
         * @return 表示上次修改时间的 {@link MsDosDateTime}。
         */
        Date timeOfLastModification() {
            return this.timeOfLastModification;
        }

        /**
         * 读取额外数据。
         *
         * @return 表示额外数据的字节序。
         */
        byte[] extra() {
            return this.extra;
        }

        /**
         * 为中心目录文件头提供额外数据。
         *
         * @author 梁济时
         * @since 2022-09-19
         */
        private static final class Extra {
            private static final int HEADER_SIZE = 4;
            private static final int DATA_SIZE_POSITION = 2;

            private final byte[] bytes;
            private final int offset;
            private final int headerId;
            private final int dataSize;

            /**
             * 使用额外数据所在的数据块及在数据块中的偏移量初始化 {@link Extra} 类的新实例。
             *
             * @param bytes 表示额外数据的字节序。
             * @param offset 表示额外数据在数据块中的偏移量的 32 位整数。
             */
            private Extra(byte[] bytes, int offset) {
                this.bytes = bytes;
                this.offset = offset;
                this.headerId = Bytes.u2(bytes, offset);
                this.dataSize = HEADER_SIZE + Bytes.u2(bytes, offset + DATA_SIZE_POSITION);
            }
        }

        /**
         * 为额外数据的条目提供枚举程序。
         *
         * @author 梁济时
         * @since 2022-09-19
         */
        private static final class ExtraEnumerator implements Enumerator<Extra> {
            private final byte[] bytes;
            private int offset;

            private ExtraEnumerator(byte[] bytes) {
                this.bytes = bytes;
                this.offset = 0;
            }

            @Override
            public boolean more() {
                return this.offset < this.bytes.length;
            }

            @Override
            public Extra next() {
                if (this.offset >= this.bytes.length) {
                    throw new NoSuchElementException(String.format(Locale.ROOT,
                            "No more entry in extra field of central directory file header. [data=%s, offset=%d]",
                            Bytes.hex(this.bytes),
                            this.offset));
                }
                Extra nextExtra = new Extra(this.bytes, this.offset);
                this.offset += nextExtra.dataSize;
                return nextExtra;
            }
        }

        /**
         * 为中心目录的文件头提供 ZIP64 额外数据。
         *
         * @author 梁济时
         * @since 2022-09-19
         */
        private static final class Zip64 {
            private static final int HEADER_ID = 0x0001;

            private static final int UNCOMPRESSED_SIZE_POSITION = 4;
            private static final int COMPRESSED_SIZE_POSITION = 12;
            private static final int OFFSET_OF_LOCAL_HEADER_POSITION = 20;

            private final long sizeOfUncompressed;
            private final long sizeOfCompressed;
            private final long offsetOfLocalHeader;

            /**
             * 使用包含 ZIP64 额外信息的数据块初始化 {@link Zip64} 类的新实例。
             *
             * @param sizeOfUncompressed 表示未经压缩的原始数据的字节数的 64 位整数。
             * @param sizeOfCompressed 表示压缩后的数据的字节数的 64 位整数。
             * @param offsetOfLocalHeader 表示本地文件头的偏移量的 64 位整数。
             */
            private Zip64(long sizeOfUncompressed, long sizeOfCompressed, long offsetOfLocalHeader) {
                this.sizeOfUncompressed = sizeOfUncompressed;
                this.sizeOfCompressed = sizeOfCompressed;
                this.offsetOfLocalHeader = offsetOfLocalHeader;
            }

            /**
             * 从额外数据中查找 ZIP64 额外数据信息。
             *
             * @param data 表示包含中心目录文件头的额外数据信息的数据的字节数组。
             * @return 若存在 ZIP64 额外数据，则为表示该额外数据的 {@link Zip64}，否则为 {@code null}。
             */
            private static Zip64 lookup(byte[] data) {
                ExtraEnumerator enumerator = new ExtraEnumerator(data);
                while (enumerator.more()) {
                    Extra nextExtra = enumerator.next();
                    if (nextExtra.headerId == HEADER_ID) {
                        return new Zip64(Bytes.s8(nextExtra.bytes, nextExtra.offset + UNCOMPRESSED_SIZE_POSITION),
                                Bytes.s8(nextExtra.bytes, nextExtra.offset + COMPRESSED_SIZE_POSITION),
                                Bytes.s8(nextExtra.bytes, nextExtra.offset + OFFSET_OF_LOCAL_HEADER_POSITION));
                    }
                }
                return null;
            }
        }
    }

    /**
     * 表示以 MS-DOS 格式存储的日期时间信息。
     *
     * @author 梁济时
     * @since 2022-09-19
     */
    private static final class MsDosDateTime {
        private final int value;

        private Date date;

        /**
         * 使用表示日期和时间的值初始化 {@link MsDosDateTime} 类的新实例。
         *
         * @param date 表示日期的值的 32 位整数。仅使用其中的低 16 位。
         * @param time 表示时间的值的 32 位整数。仅使用其中的低 16 位。
         */
        private MsDosDateTime(int date, int time) {
            this(((date & 0xffff) << 16) | (time & 0xffff));
        }

        /**
         * 使用表示日期时间的值初始化 {@link MsDosDateTime} 类的新实例。
         *
         * @param value 表示日期时间的值的 32 位整数。
         */
        private MsDosDateTime(int value) {
            this.value = value;
        }

        /**
         * 获取年份。
         *
         * @return 表示年份的 32 位整数。
         */
        int year() {
            return ((value >> 25) & 0x7f) + 1980;
        }

        /**
         * 获取月份。
         *
         * @return 表示月份的 32 位整数。
         */
        int month() {
            return (value >> 21) & 0xf;
        }

        /**
         * 获取日期。
         *
         * @return 表示日期的 32 位整数。
         */
        int day() {
            return (value >> 16) & 0x1f;
        }

        /**
         * 获取小时。
         *
         * @return 表示小时的 32 位整数。
         */
        int hour() {
            return (value >> 11) & 0x1f;
        }

        /**
         * 获取分钟。
         *
         * @return 表示分钟的 32 位整数。
         */
        int minute() {
            return (value >> 5) & 0x3f;
        }

        /**
         * 获取秒数。
         *
         * @return 表示秒数的 32 位整数。
         */
        int second() {
            return (value << 1) & 0x3e;
        }

        /**
         * 返回一个日期对象，包含当前日期时间的值。
         *
         * @return 表示日期时间的 {@link Date}。
         */
        Date toDate() {
            if (this.date == null) {
                Calendar calendar = new GregorianCalendar();
                calendar.set(Calendar.YEAR, this.year());
                calendar.set(Calendar.MONTH, this.month() - 1);
                calendar.set(Calendar.DAY_OF_MONTH, this.day());
                calendar.set(Calendar.HOUR_OF_DAY, this.hour());
                calendar.set(Calendar.MINUTE, this.minute());
                calendar.set(Calendar.SECOND, this.second());
                this.date = calendar.getTime();
            }
            return this.date;
        }
    }

    /**
     * 在输入流中跳过本地文件头。
     * <p>输入流的初始状态，应停留在本地文件头之前。</p>
     *
     * @param in 表示待跳过本地文件头的输入流的 {@link InputStream}。
     * @throws IOException 跳过本地文件头过程发生输入输出异常。
     */
    static void skipLocalHeader(InputStream in) throws IOException {
        byte[] readBytes = new byte[LOCAL_HEADER_MINIMUM_LENGTH];
        int read = 0;
        while (read < LOCAL_HEADER_MINIMUM_LENGTH) {
            int part = in.read(readBytes, read, LOCAL_HEADER_MINIMUM_LENGTH - read);
            if (part < 0) {
                throw new EOFException(String.format(Locale.ROOT,
                        "EOF reached after read %d bytes but total %d bytes required.",
                        read,
                        LOCAL_HEADER_MINIMUM_LENGTH));
            } else {
                read += part;
            }
        }
        long bytesToSkip = measureDynamicDataOfLocalHeader(readBytes);
        long skipped = 0L;
        while (skipped < bytesToSkip) {
            skipped += in.skip(bytesToSkip - skipped);
        }
    }

    /**
     * 度量本地文件头的长度。
     *
     * @param reader 表示用以读取本地文件头所在归档件的数据的读取程序的 {@link DataRandomReader}。
     * @param offset 表示本地文件头在归档件中的偏移量的 64 位整数。
     * @return 表示本地文件头的长度的 32 位整数。
     * @throws IOException 度量过程发生输入输出异常。
     */
    static int measureLocalHeader(DataRandomReader reader, long offset) throws IOException {
        byte[] readBytes = reader.read(offset, LOCAL_HEADER_MINIMUM_LENGTH);
        return LOCAL_HEADER_MINIMUM_LENGTH + measureDynamicDataOfLocalHeader(readBytes);
    }

    /**
     * 通过本地文件头的数据，度量本地文件头除固定长度数据外的动态数据的字节数。
     *
     * @param bytes 表示包含本地文件头数据的字节序。
     * @return 表示动态数据长度的 32 位整数。
     * @throws JarFormatException 字节序中未包含有效的本地文件头数据。
     */
    private static int measureDynamicDataOfLocalHeader(byte[] bytes) throws JarFormatException {
        int signature = Bytes.s4(bytes, 0);
        if (signature != LOCAL_HEADER_SIGNATURE) {
            throw new JarFormatException(String.format(Locale.ROOT,
                    "Bad signature of local file header. [expected=%08x, actual=%08x]",
                    LOCAL_HEADER_SIGNATURE,
                    signature));
        }
        int lengthOfFileName = Bytes.u2(bytes, LOCAL_HEADER_FILE_NAME_LENGTH_POSITION);
        int lengthOfExtraField = Bytes.u2(bytes, LOCAL_HEADER_EXTRA_FIELD_LENGTH_POSITION);
        return lengthOfFileName + lengthOfExtraField;
    }
}
