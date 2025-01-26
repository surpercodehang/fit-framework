/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.annotation;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.jvm.classfile.AttributeInfo;
import modelengine.fitframework.jvm.classfile.lang.U2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 为 {@link AnnotationInfo} 提供列表。
 *
 * @author 梁济时
 * @since 2022-10-29
 */
public final class AnnotationList implements Iterable<AnnotationInfo> {
    private final U2 count;
    private final List<AnnotationInfo> list;

    /**
     * 使用注解所属的属性及包含注解列表信息的输入流初始化 {@link AnnotationList} 类的新实例。
     *
     * @param attribute 表示所属属性的 {@link AttributeInfo}。
     * @param in 表示包含注解列表信息的输入流的 {@link InputStream}。
     * @throws IllegalArgumentException {@code attribute} 或 {@code in} 为 {@code null}。
     * @throws IOException 当读取过程发生输入输出异常时。
     */
    public AnnotationList(AttributeInfo attribute, InputStream in) throws IOException {
        notNull(attribute, "The owning attribute of annotations cannot be null.");
        this.count = U2.read(notNull(in, "The input stream that contains annotations cannot be null."));
        this.list = new ArrayList<>(this.count.intValue());
        for (U2 i = U2.ZERO; i.compareTo(this.count) < 0; i = i.add(U2.ONE)) {
            this.list.add(new AnnotationInfo(attribute, in));
        }
    }

    /**
     * 获取列表中注解的数量。
     *
     * @return 表示注解数量的 {@link U2}。
     */
    public U2 count() {
        return this.count;
    }

    /**
     * 获取指定索引处的注解。
     *
     * @param index 表示注解所在索引的 {@link U2}。
     * @return 表示该索引处的注解的 {@link AnnotationInfo}。
     * @throws IllegalArgumentException {@code index} 为 {@code null}。
     * @throws IndexOutOfBoundsException {@code index} 超出索引限制。
     */
    public AnnotationInfo get(U2 index) {
        notNull(index, "The index of annotation to lookup cannot be null.");
        return this.list.get(index.intValue());
    }

    @Nonnull
    @Override
    public Iterator<AnnotationInfo> iterator() {
        return this.list.iterator();
    }
}
