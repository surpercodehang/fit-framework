/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.merge;

/**
 * 表示合并器。
 *
 * @param <V> 表示待合并元素类型的 {@link V}。
 * @author 季聿阶
 * @since 2022-07-30
 */
public interface Merger<V> {
    /**
     * 获取合并器中的冲突处理器的集合。
     *
     * @return 表示冲突处理器集合的 {@link ConflictResolverCollection}。
     */
    ConflictResolverCollection conflictResolvers();

    /**
     * 合并两个指定的数据。
     *
     * @param v1 表示待合并的第一个数据的 {@link V}。
     * @param v2 表示待合并的第二个数据的 {@link V}。
     * @return 表示合并结果的 {@link V}。
     * @throws ConflictException 当合并过程中发生异常时。
     */
    V merge(V v1, V v2);
}
