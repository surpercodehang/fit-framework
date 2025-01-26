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
 * 表示字符串常量。
 * <ul>
 *     <li><b>Tag: </b>8</li>
 *     <li><b>class file format: </b>45.3</li>
 *     <li><b>Java SE: </b>1.0.2</li>
 * </ul>
 *
 * @author 梁济时
 * @since 2022-06-07
 */
public final class StringInfo extends Constant {
    /**
     * 表示常量的标签。
     */
    public static final U1 TAG = U1.of(8);

    private final U2 stringIndex;

    /**
     * 使用所属的常量池及包含常量数据的输入流初始化 {@link StringInfo} 类的新实例。
     *
     * @param pool 表示所属常量池的 {@link ConstantPool}。
     * @param in 表示包含常量数据的输入流的 {@link InputStream}。
     * @throws IllegalArgumentException {@code pool} 或 {@code in} 为 {@code null}。
     * @throws IOException 读取过程发生输入输出异常。
     */
    public StringInfo(ConstantPool pool, InputStream in) throws IOException {
        super(pool, TAG);
        Validation.notNull(in, "The input stream to read constant data cannot be null.");
        this.stringIndex = U2.read(in);
    }

    /**
     * 获取字符串数据在常量池中的索引。
     *
     * @return 表示常量池中索引的 {@link U2}。
     */
    public U2 stringIndex() {
        return this.stringIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof StringInfo) {
            StringInfo another = (StringInfo) obj;
            return another.pool() == this.pool() && Objects.equals(this.stringIndex, another.stringIndex);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.pool(), StringInfo.class, this.stringIndex});
    }

    @Override
    public String toString() {
        return StringUtils.format("[tag={0}, string_index={1}]", this.tag(), this.stringIndex());
    }

    @Override
    public void write(OutputStream out) throws IOException {
        super.write(out);
        this.stringIndex().write(out);
    }
}
