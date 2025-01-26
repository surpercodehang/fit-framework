/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.jvm.classfile.lang.U2;

import java.io.IOException;
import java.io.InputStream;

/**
 * 为方法提供信息。
 *
 * @author 梁济时
 * @since 2022-06-09
 */
public final class MethodInfo {
    private final U2 accessFlags;
    private final U2 nameIndex;
    private final U2 descriptorIndex;
    private final AttributeList attributes;

    private final MethodList list;

    /**
     * 获取方法所属的方法列表。
     *
     * @param list 表示方法列表的 {@link MethodList}。
     * @param in 表示输入流的 {@link InputStream}。
     * @throws IOException 如果发生 I/O 错误。
     */
    public MethodInfo(MethodList list, InputStream in) throws IOException {
        this.list = Validation.notNull(list, "The owning list of a method cannot be null.");
        Validation.notNull(in, "The input stream to read field info cannot be null.");
        this.accessFlags = U2.read(in);
        this.nameIndex = U2.read(in);
        this.descriptorIndex = U2.read(in);
        this.attributes = new AttributeList(list.file(), in);
    }

    /**
     * 获取方法所属的方法列表。
     *
     * @return 表示方法列表的 {@link MethodList}。
     */
    public MethodList list() {
        return this.list;
    }

    /**
     * 获取方法所属的类文件。
     *
     * @return 表示方法所属的类文件的 {@link ClassFile}。
     */
    public ClassFile file() {
        return this.list().file();
    }

    /**
     * 获取方法的访问级别。
     *
     * @return 表示方法的访问级别的 {@link U2}。
     */
    public U2 accessFlags() {
        return this.accessFlags;
    }

    /**
     * 获取方法名称在常量池中的索引。
     * <p>该索引处必然是一个 {@link modelengine.fitframework.jvm.classfile.constant.Utf8Info CONSTANT_Utf8_info}
     * 类型的常量。</p>
     *
     * @return 表示名称在常量池中的索引的 {@link U2}。
     */
    public U2 nameIndex() {
        return this.nameIndex;
    }

    /**
     * 获取方法的描述下标序号。
     *
     * @return 表示方法的描述下标序号的 {@link U2}。
     */
    public U2 descriptorIndex() {
        return this.descriptorIndex;
    }

    /**
     * 获取方法所属类文件的属性列表。
     *
     * @return 表示方法所属类文件的属性列表的 {@link AttributeList}。
     */
    public AttributeList attributes() {
        return this.attributes;
    }
}
