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
import modelengine.fitframework.jvm.classfile.lang.U4;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * 表示单精度浮点数类型常量。
 * <ul>
 *     <li><b>Tag: </b>4</li>
 *     <li><b>class file format: </b>45.3</li>
 *     <li><b>Java SE: </b>1.0.2</li>
 * </ul>
 *
 * @author 梁济时
 * @since 2022-06-07
 */
public final class FloatInfo extends Constant {
    /**
     * 表示常量的标签。
     */
    public static final U1 TAG = U1.of(4);

    private final U4 data;

    /**
     * 使用所属的常量池及包含常量数据的输入流初始化 {@link FloatInfo} 类的新实例。
     *
     * @param pool 表示所属的常量池的 {@link ConstantPool}。
     * @param in 表示包含常量数据的输入流的 {@link InputStream}。
     * @throws IllegalArgumentException {@code pool} 或 {@code in} 为 {@code null}。
     * @throws IOException 读取过程发生输入输出异常。
     */
    public FloatInfo(ConstantPool pool, InputStream in) throws IOException {
        super(pool, TAG);
        Validation.notNull(in, "The input stream to read constant data cannot be null.");
        this.data = U4.read(in);
    }

    /**
     * 获取常量包含的数据。
     *
     * @return 表示常量数据的 {@link U4}。
     */
    public U4 data() {
        return this.data;
    }

    /**
     * 获取单精度浮点数数据。
     *
     * @return 表示常量所包含数据的单精度浮点数。
     */
    public float floatValue() {
        return Float.intBitsToFloat(this.data.intValue());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof FloatInfo) {
            FloatInfo another = (FloatInfo) obj;
            return this.pool() == another.pool() && Objects.equals(this.data, another.data);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.pool(), FloatInfo.class, this.data});
    }

    @Override
    public String toString() {
        return Float.toString(this.floatValue());
    }

    @Override
    public void write(OutputStream out) throws IOException {
        super.write(out);
        this.data.write(out);
    }
}
