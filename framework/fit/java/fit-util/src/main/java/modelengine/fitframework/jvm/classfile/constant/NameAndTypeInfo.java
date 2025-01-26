/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.constant;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.jvm.classfile.Constant;
import modelengine.fitframework.jvm.classfile.ConstantPool;
import modelengine.fitframework.jvm.classfile.lang.U1;
import modelengine.fitframework.jvm.classfile.lang.U2;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * 表示名称类型常量。
 * <ul>
 *     <li><b>Tag: </b>12</li>
 *     <li><b>class file format: </b>45.3</li>
 *     <li><b>Java SE: </b>1.0.2</li>
 * </ul>
 *
 * @author 梁济时
 * @since 2022-06-07
 */
public final class NameAndTypeInfo extends Constant {
    /**
     * 表示常量的标签。
     */
    public static final U1 TAG = U1.of(12);

    private final U2 nameIndex;
    private final U2 descriptorIndex;

    /**
     * 使用所属常量池及包含常量数据的输入流初始化 {@link NameAndTypeInfo} 类的新实例。
     *
     * @param pool 表示所属常量池的 {@link ConstantPool}。
     * @param in 表示包含常量数据的输入流的 {@link InputStream}。
     * @throws IllegalArgumentException {@code pool} 或 {@code in} 为 {@code null}。
     * @throws IOException 读取过程发生输入输出异常。
     */
    public NameAndTypeInfo(ConstantPool pool, InputStream in) throws IOException {
        super(pool, TAG);
        Validation.notNull(in, "The input stream to read data cannot be null.");
        this.nameIndex = U2.read(in);
        this.descriptorIndex = U2.read(in);
    }

    /**
     * 获取名称在常量池中的索引。
     * <p>常量池中该索引位置的条目必然是一个 {@link Utf8Info CONSTANT_Utf8_info}。</p>
     *
     * @return 表示常量池中索引的 {@link U2}。
     */
    public U2 nameIndex() {
        return this.nameIndex;
    }

    /**
     * 获取类型描述符在常量池中的索引。
     * <p>常量池中该索引位置的条目必然是一个 {@link Utf8Info CONSTANT_Utf8_info}。</p>
     *
     * @return 表示常量池中索引的 {@link U2}。
     */
    public U2 descriptorIndex() {
        return this.descriptorIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof NameAndTypeInfo) {
            NameAndTypeInfo another = (NameAndTypeInfo) obj;
            return another.pool() == this.pool() && Objects.equals(this.nameIndex, another.nameIndex) && Objects.equals(
                    this.descriptorIndex,
                    another.descriptorIndex);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                this.pool(), NameAndTypeInfo.class, this.nameIndex, this.descriptorIndex
        });
    }

    @Override
    public String toString() {
        return StringUtils.format("[tag={0}, name_index={1}, descriptor_index={2}]",
                this.tag(), this.nameIndex(), this.descriptorIndex());
    }

    @Override
    public void write(OutputStream out) throws IOException {
        super.write(out);
        this.nameIndex().write(out);
        this.descriptorIndex().write(out);
    }
}
