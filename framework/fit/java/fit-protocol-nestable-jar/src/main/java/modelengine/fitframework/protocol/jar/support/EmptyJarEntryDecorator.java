/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.support;

import modelengine.fitframework.protocol.jar.CompressionMethod;
import modelengine.fitframework.protocol.jar.Jar;
import modelengine.fitframework.protocol.jar.JarEntryLocation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * 为 {@link Jar.Entry} 提供空的装饰程序。
 *
 * @author 梁济时
 * @since 2023-02-21
 */
class EmptyJarEntryDecorator implements Jar.Entry {
    private final Jar.Entry decorated;

    /**
     * 使用被装饰的归档记录初始化 {@link EmptyJarEntryDecorator} 类的新实例。
     *
     * @param decorated 表示被装饰的归档记录的 {@link Jar.Entry}。
     * @throws IllegalArgumentException {@code decorated} 为 {@code null}。
     */
    EmptyJarEntryDecorator(Jar.Entry decorated) {
        if (decorated == null) {
            throw new IllegalArgumentException("The decorated JAR entry cannot be null.");
        } else {
            this.decorated = decorated;
        }
    }

    /**
     * 获取被装饰的归档记录。
     *
     * @return 表示被装饰的归档记录的 {@link Jar.Entry}。
     */
    final Jar.Entry decorated() {
        return this.decorated;
    }

    @Override
    public Jar jar() {
        return this.decorated.jar();
    }

    @Override
    public String name() {
        return this.decorated.name();
    }

    @Override
    public CompressionMethod methodOfCompression() {
        return this.decorated.methodOfCompression();
    }

    @Override
    public int crc32() {
        return this.decorated.crc32();
    }

    @Override
    public long offsetOfLocalHeader() {
        return this.decorated.offsetOfLocalHeader();
    }

    @Override
    public long sizeOfCompressed() {
        return this.decorated.sizeOfCompressed();
    }

    @Override
    public long sizeOfUncompressed() {
        return this.decorated.sizeOfUncompressed();
    }

    @Override
    public Date timeOfLastModification() {
        return this.decorated.timeOfLastModification();
    }

    @Override
    public byte[] extra() {
        return this.decorated.extra();
    }

    @Override
    public String comment() {
        return this.decorated.comment();
    }

    @Override
    public boolean directory() {
        return this.decorated.directory();
    }

    @Override
    public JarEntryLocation location() {
        return this.decorated.location();
    }

    @Override
    public InputStream read() throws IOException {
        return this.decorated.read();
    }

    @Override
    public Jar asJar() throws IOException {
        return this.decorated.asJar();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.getClass(), this.decorated});
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            EmptyJarEntryDecorator another = (EmptyJarEntryDecorator) obj;
            return Objects.equals(this.decorated, another.decorated);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.decorated.toString();
    }
}
