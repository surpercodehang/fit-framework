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
 * 表示一个方法类型。
 *
 * @author 梁济时
 * @since 2022-06-07
 */
public final class MethodTypeInfo extends Constant {
    /**
     * 表示常量的标签。
     */
    public static final U1 TAG = U1.of(16);

    private final U2 descriptorIndex;

    /**
     * 构造一个表示方法类型的常量。
     *
     * @param pool 表示常量池的 {@link ConstantPool}。
     * @param in 表示输入流的 {@link InputStream}。
     * @throws IOException 如果发生 I/O 错误。
     */
    public MethodTypeInfo(ConstantPool pool, InputStream in) throws IOException {
        super(pool, TAG);
        Validation.notNull(in, "The input stream to read constant data cannot be null.");
        this.descriptorIndex = U2.read(in);
    }

    /**
     * 表示方法描述符在常量池中的索引。
     *
     * @return 表示方法描述符在常量池中的索引的 {@link U2}。
     */
    public U2 descriptorIndex() {
        return this.descriptorIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof MethodTypeInfo) {
            MethodTypeInfo another = (MethodTypeInfo) obj;
            return another.pool() == this.pool() && Objects.equals(another.descriptorIndex, this.descriptorIndex);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.pool(), MethodTypeInfo.class, this.descriptorIndex});
    }

    @Override
    public String toString() {
        return StringUtils.format("[tag={0}, descriptor_index={1}]", this.tag(), this.descriptorIndex());
    }

    @Override
    public void write(OutputStream out) throws IOException {
        super.write(out);
        this.descriptorIndex().write(out);
    }
}
