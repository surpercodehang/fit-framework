/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.io.support;

import static modelengine.fitframework.inspection.Validation.between;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.io.RandomAccessor;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * 为 {@link RandomAccessorSubsection} 提供用以读取子片段的装饰器。
 *
 * @author 梁济时
 * @since 2022-07-25
 */
public class RandomAccessorSubsection implements RandomAccessor {
    private final RandomAccessor parent;
    private final long offset;
    private final long size;

    /**
     * 使用父访问程序、偏移量及数据大小初始化 {@link RandomAccessorSubsection} 类的新实例。
     *
     * @param parent 表示父访问程序的 {@link RandomAccessor}。
     * @param offset 表示子片段在父访问程序中的偏移量的64位整数。
     * @param size 表示子片段中包含的数据大小的64位整数。
     * @throws IllegalArgumentException {@code parent} 为 {@code null} 或 {@code offset}、{@code size} 超出限制。
     */
    public RandomAccessorSubsection(RandomAccessor parent, long offset, long size) {
        this.parent = notNull(parent, "The parent accessor of subsection cannot be null.");
        this.offset = between(offset,
                0L,
                parent.size(),
                "The offset is out of bounds. [offset={0}, parent.size={1}]",
                offset,
                this.parent.size());
        this.size = between(size,
                0L,
                this.parent.size() - this.offset,
                "The size is out of bounds. [size={0}, offset={1}, parent.size={2}]",
                size,
                this.offset,
                this.parent.size());
    }

    @Override
    public byte[] read(long offset, int length) throws IOException {
        if (offset < 0 || offset > this.size()) {
            throw new IllegalArgumentException(StringUtils.format(
                    "The offset of data to read is out of bounds. [offset={0}, total={1}]",
                    offset,
                    this.size()));
        }
        if (length < 0 || offset + length > this.size()) {
            throw new IllegalArgumentException(StringUtils.format(
                    "The length of data to read is out of bounds. [length={0}, offset={1}, total={2}]",
                    length,
                    offset,
                    this.size()));
        } else {
            return this.parent.read(this.offset + offset, length);
        }
    }

    @Override
    public long size() {
        return this.size;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.parent, this.offset, this.size});
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof RandomAccessorSubsection) {
            RandomAccessorSubsection another = (RandomAccessorSubsection) obj;
            return Objects.equals(this.parent, another.parent) && this.offset == another.offset
                    && this.size == another.size;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return StringUtils.format("[parent={0}, offset={1}, size={2}]", this.parent, this.offset, this.size);
    }
}
