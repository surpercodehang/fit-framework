/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.merge.list.support;

import modelengine.fitframework.merge.Conflict;
import modelengine.fitframework.merge.ConflictException;
import modelengine.fitframework.merge.ConflictResolver;
import modelengine.fitframework.merge.ConflictResolverCollection;
import modelengine.fitframework.merge.list.ListMerger;
import modelengine.fitframework.merge.support.AbstractMerger;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 处理 {@link List} 的合并器。
 *
 * @param <E> 表示 {@link List} 中元素类型的 {@link E}。
 * @author 季聿阶
 * @since 2022-08-10
 */
public class DefaultListMerger<E> extends AbstractMerger<List<E>> implements ListMerger<E> {
    /**
     * 通过冲突处理器的集合来实例化 {@link DefaultListMerger}。
     *
     * @param conflictResolvers 表示冲突处理器的集合的 {@link ConflictResolverCollection}。
     */
    public DefaultListMerger(ConflictResolverCollection conflictResolvers) {
        super(conflictResolvers);
    }

    @Override
    public List<E> merge(List<E> l1, List<E> l2) {
        boolean isL1Empty = CollectionUtils.isEmpty(l1);
        boolean isL2Empty = CollectionUtils.isEmpty(l2);
        if (isL1Empty && isL2Empty) {
            return Collections.emptyList();
        } else if (isL1Empty) {
            return l2;
        } else if (isL2Empty) {
            return l1;
        } else {
            return this.mergeNonEmptyLists(l1, l2);
        }
    }

    private List<E> mergeNonEmptyLists(List<E> l1, List<E> l2) {
        Class<List<E>> clazz = ObjectUtils.cast(List.class);
        ConflictResolver.Result<List<E>> ret =
                this.conflictResolvers().get(clazz).resolve(l1, l2, Conflict.builder().build());
        if (!ret.resolved()) {
            if (ret.cause() != null) {
                throw ret.cause();
            }
            throw new ConflictException(StringUtils.format("Conflict in merge list process. [v1={0}, v2={1}]", l1, l2));
        }
        return ret.result();
    }
}
