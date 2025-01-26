/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.merge.support;

import modelengine.fitframework.merge.Conflict;
import modelengine.fitframework.merge.ConflictResolver;

/**
 * 表示覆盖的冲突处理器。
 * <p>覆盖策略指的是直接使用第二个数据作为冲突处理的结果。</p>
 *
 * @param <K> 表示冲突键的类型的 {@link K}。
 * @param <V> 表示冲突值的类型的 {@link V}。
 * @author 季聿阶
 * @since 2022-07-30
 */
public class OverrideConflictResolver<K, V> implements ConflictResolver<K, V, Conflict<K>> {
    @Override
    public Result<V> resolve(V v1, V v2, Conflict<K> context) {
        return Result.<V>builder().resolved(true).result(v2).build();
    }
}
