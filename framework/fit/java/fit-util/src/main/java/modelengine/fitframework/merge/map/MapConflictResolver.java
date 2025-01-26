/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.merge.map;

import modelengine.fitframework.merge.ConflictException;
import modelengine.fitframework.merge.ConflictResolver;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Map;

/**
 * 表示 {@link Map} 数据的冲突处理器。
 * <p>处理冲突元素时，会将两个 {@link Map} 继续进行合并。</p>
 *
 * @param <K> 表示冲突键的类型的 {@link K}。
 * @param <SK> 表示冲突值的映射中的键的类型的 {@link SK}。
 * @param <SV> 表示冲突值的映射中的值的类型的 {@link SV}。
 * @author 季聿阶
 * @since 2022-07-30
 */
public class MapConflictResolver<K, SK, SV> implements ConflictResolver<K, Map<SK, SV>, MapConflict<K, Map<SK, SV>>> {
    @Override
    public Result<Map<SK, SV>> resolve(Map<SK, SV> v1, Map<SK, SV> v2, MapConflict<K, Map<SK, SV>> context) {
        try {
            Map<SK, SV> merged = ObjectUtils.cast(context.merger().merge(ObjectUtils.cast(v1), ObjectUtils.cast(v2)));
            return Result.<Map<SK, SV>>builder().resolved(true).result(merged).build();
        } catch (ConflictException e) {
            return Result.<Map<SK, SV>>builder().resolved(false).cause(e).build();
        }
    }
}
