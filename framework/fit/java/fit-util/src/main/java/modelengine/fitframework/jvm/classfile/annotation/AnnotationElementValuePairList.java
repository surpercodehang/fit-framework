/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.annotation;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.jvm.classfile.lang.U2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 为注解元素的键值对提供列表。
 *
 * @author 梁济时
 * @since 2022-10-29
 */
public final class AnnotationElementValuePairList implements Iterable<AnnotationElementValuePair> {
    private final AnnotationInfo annotation;
    private final U2 count;
    private final List<AnnotationElementValuePair> list;

    /**
     * 使用所属的注解及包含元素键值对列表信息的输入流初始化 {@link AnnotationElementValuePairList} 类的新实例。
     *
     * @param annotation 表示所属的注解的 {@link AnnotationInfo}。
     * @param in 表示包含列表信息的输入流的 {@link InputStream}。
     * @throws IllegalArgumentException {@code annotation} 或 {@code in} 为 {@code null}。
     * @throws IOException 读取过程发生输入输出异常。
     */
    public AnnotationElementValuePairList(AnnotationInfo annotation, InputStream in) throws IOException {
        this.annotation = notNull(annotation, "The owning annotation of element value pair list cannot be null.");
        notNull(in, "The input stream that contains element value pair list cannot be null.");
        this.count = U2.read(in);
        this.list = new ArrayList<>(this.count.intValue());
        for (U2 i = U2.ZERO; i.compareTo(this.count) < 0; i = i.add(U2.ONE)) {
            this.list.add(new AnnotationElementValuePair(this, in));
        }
    }

    /**
     * 获取所属的注解。
     *
     * @return 表示所属注解的 {@link AnnotationInfo}。
     */
    public AnnotationInfo annotation() {
        return this.annotation;
    }

    /**
     * 获取列表中包含元素的数量。
     *
     * @return 表示元素数量的 {@link U2}。
     */
    public U2 count() {
        return this.count;
    }

    /**
     * 获取指定索引处的元素。
     *
     * @param index 表示元素所在索引的 {@link U2}。
     * @return 表示该索引位置的元素的 {@link AnnotationElementValuePair}。
     * @throws IllegalArgumentException {@code index} 为 {@code null}。
     * @throws IndexOutOfBoundsException {@code index} 超出索引限制。
     */
    public AnnotationElementValuePair get(U2 index) {
        notNull(index, "The index of element value pair to lookup cannot be null.");
        return this.list.get(index.intValue());
    }

    @Nonnull
    @Override
    public Iterator<AnnotationElementValuePair> iterator() {
        return this.list.iterator();
    }
}
