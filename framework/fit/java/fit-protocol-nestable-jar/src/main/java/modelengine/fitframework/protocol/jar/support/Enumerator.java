/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar.support;

import java.io.IOException;

/**
 * 为内容提供迭代程序。
 *
 * @param <T> 表示内容的类型。
 * @author 梁济时
 * @since 2022-09-19
 */
interface Enumerator<T> {
    /**
     * 获取一个值，该值指示是否还有未遍历的元素。
     *
     * @return 若存在未遍历的元素，则为 {@code true}；否则为 {@code false}。
     */
    boolean more();

    /**
     * 获取下一个待遍历的元素。
     *
     * @return 表示待遍历的元素的 {@link Object}。
     * @throws IOException 获取下一个元素过程发生输入输出异常。
     */
    T next() throws IOException;
}
