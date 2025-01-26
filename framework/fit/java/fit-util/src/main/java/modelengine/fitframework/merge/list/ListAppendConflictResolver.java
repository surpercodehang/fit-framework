/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.merge.list;

import modelengine.fitframework.merge.Conflict;
import modelengine.fitframework.merge.ConflictResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示 {@link List} 数据的冲突处理器。
 * <p>处理冲突元素时，会将第二个冲突列表中的元素添加到第一个冲突列表中，形成一个新的列表。</p>
 *
 * @param <K> 表示冲突键的类型的 {@link K}。
 * @param <E> 表示冲突值的列表中的元素类型的 {@link E}。
 * @author 季聿阶
 * @since 2022-08-01
 */
public class ListAppendConflictResolver<K, E> implements ConflictResolver<K, List<E>, Conflict<K>> {
    @Override
    public Result<List<E>> resolve(List<E> v1, List<E> v2, Conflict<K> context) {
        List<E> list = new ArrayList<>();
        list.addAll(v1);
        list.addAll(v2);
        return Result.<List<E>>builder().resolved(true).result(list).build();
    }
}
