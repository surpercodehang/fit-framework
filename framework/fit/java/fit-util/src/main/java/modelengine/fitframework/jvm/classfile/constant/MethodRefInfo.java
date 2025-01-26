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
 * 表示方法引用常量。
 * <ul>
 *     <li><b>Tag: </b>10</li>
 *     <li><b>class file format: </b>45.3</li>
 *     <li><b>Java SE: </b>1.0.2</li>
 * </ul>
 *
 * @author 梁济时
 * @since 2022-06-07
 */
public final class MethodRefInfo extends Constant {
    /**
     * 表示常量的标签。
     */
    public static final U1 TAG = U1.of(10);

    private final U2 classIndex;
    private final U2 nameAndTypeIndex;

    /**
     * 使用所属的常量池及包含常量数据的输入流初始化 {@link MethodRefInfo} 类的新实例。
     *
     * @param pool 表示所属的常量池的 {@link ConstantPool}。
     * @param in 表示包含常量数据的输入流的 {@link InputStream}。
     * @throws IllegalArgumentException {@code pool} 或 {@code in} 为 {@code null}。
     * @throws IOException 读取过程发生输入输出异常。
     */
    public MethodRefInfo(ConstantPool pool, InputStream in) throws IOException {
        super(pool, TAG);
        Validation.notNull(in, "The input stream to read constant data cannot be null.");
        this.classIndex = U2.read(in);
        this.nameAndTypeIndex = U2.read(in);
    }

    /**
     * 获取所属类型在常量池中的索引。
     *
     * @return 表示常量池中索引的 {@link U2}。
     */
    public U2 classIndex() {
        return this.classIndex;
    }

    /**
     * 获取名称类型在常量池中的索引。
     *
     * @return 表示常量池中索引的 {@link U2}。
     */
    public U2 nameAndTypeIndex() {
        return this.nameAndTypeIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof MethodRefInfo) {
            MethodRefInfo another = (MethodRefInfo) obj;
            return this.pool() == another.pool() && Objects.equals(this.classIndex, another.classIndex)
                    && Objects.equals(this.nameAndTypeIndex, another.nameAndTypeIndex);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.pool(), MethodRefInfo.class, this.classIndex, this.nameAndTypeIndex});
    }

    @Override
    public String toString() {
        return StringUtils.format("[tag={0}, class_index={1}, name_and_type_index={2}]",
                this.tag(), this.classIndex(), this.nameAndTypeIndex());
    }

    @Override
    public void write(OutputStream out) throws IOException {
        super.write(out);
        this.classIndex().write(out);
        this.nameAndTypeIndex().write(out);
    }
}
