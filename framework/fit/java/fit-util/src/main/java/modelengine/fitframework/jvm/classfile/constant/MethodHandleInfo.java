/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.constant;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.jvm.classfile.Constant;
import modelengine.fitframework.jvm.classfile.ConstantPool;
import modelengine.fitframework.jvm.classfile.IllegalClassFormatException;
import modelengine.fitframework.jvm.classfile.lang.U1;
import modelengine.fitframework.jvm.classfile.lang.U2;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * 表示方法句柄。
 * <ul>
 *     <li><b>Tag: </b>15</li>
 *     <li><b>class file format: </b>45.3</li>
 *     <li><b>Java SE: </b>1.0.2</li>
 * </ul>
 *
 * @author 梁济时
 * @since 2022-06-07
 */
public class MethodHandleInfo extends Constant {
    /**
     * 表示常量的标签。
     */
    public static final U1 TAG = U1.of(15);

    private final ReferenceKind referenceKind;
    private final U2 referenceIndex;

    /**
     * 构造一个新的 {@link MethodHandleInfo} 实例。
     *
     * @param pool 表示常量池的 {@link ConstantPool}。
     * @param in 表示输入流的 {@link InputStream}。
     * @throws IOException 如果发生 I/O 错误。
     */
    public MethodHandleInfo(ConstantPool pool, InputStream in) throws IOException {
        super(pool, TAG);
        Validation.notNull(in, "The input stream to read constant data cannot be null.");
        U1 referenceKindValue = U1.read(in);
        this.referenceKind = ReferenceKind.of(referenceKindValue);
        if (this.referenceKind == null) {
            throw new IllegalClassFormatException(StringUtils.format("Unknown reference kind. [value={0}]",
                    referenceKindValue));
        }
        this.referenceIndex = U2.read(in);
    }

    /**
     * 表示引用的种类。
     *
     * @return 表示引用种类的 {@link ReferenceKind}。
     */
    public ReferenceKind referenceKind() {
        return this.referenceKind;
    }

    /**
     * 获取引用在常量池中的索引。
     * TODO 待补充注释
     * <ul>
     *     <li>{@link FieldRefInfo CONSTANT_Fieldref_info}<ul>
     *         <li>{@link ReferenceKind#REF_GET_FIELD REF_getField}</li>
     *         <li>{@link ReferenceKind#REF_GET_STATIC REF_getStatic}</li>
     *         <li>{@link ReferenceKind#REF_PUT_FIELD REF_putField}</li>
     *         <li>{@link ReferenceKind#REF_PUT_STATIC REF_putStatic}</li>
     *     </ul></li>
     *     <li>{@link MethodRefInfo CONSTANT_Methodref_info}<ul>
     *         <li>{@link ReferenceKind#REF_INVOKE_VIRTUAL REF_invokeVirtual}</li>
     *         <li>{@link ReferenceKind#REF_NEW_INVOKE_SPECIAL REF_newInvokeSpecial}</li>
     *     </ul></li>
     *     <li>{@link MethodRefInfo}</li>
     * </ul>
     *
     * @return 表示常量池中索引的 {@link U2}。
     */
    public U2 referenceIndex() {
        return this.referenceIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            MethodHandleInfo that = (MethodHandleInfo) obj;
            return Objects.equals(this.referenceKind(), that.referenceKind())
                    && Objects.equals(this.referenceIndex(), that.referenceIndex());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {this.getClass(), this.referenceKind(), this.referenceIndex()});
    }

    @Override
    public String toString() {
        return StringUtils.format("[tag={0}, reference_kind={1}, reference_index={2}]",
                this.tag(), this.referenceKind().value(), this.referenceIndex());
    }

    @Override
    public void write(OutputStream out) throws IOException {
        super.write(out);
        this.referenceKind().value().write(out);
        this.referenceIndex().write(out);
    }
}
