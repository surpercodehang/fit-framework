/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.support;

import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.DependencyChain;

import java.util.Collections;
import java.util.Iterator;

/**
 * 为 {@link DependencyChain} 提供空实现。
 *
 * @author 梁济时
 * @since 2022-07-08
 */
public class EmptyDependencyChain implements DependencyChain {
    /** 表示单例实现。 */
    public static final DependencyChain INSTANCE = new EmptyDependencyChain();

    /**
     * 隐藏默认构造方法，避免单例类被实例化。
     */
    private EmptyDependencyChain() {}

    @Override
    public DependencyChain next(BeanMetadata metadata) {
        return new DefaultDependencyChain(null, metadata);
    }

    @Override
    public Iterator<BeanMetadata> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public String toString() {
        return "non-dependency";
    }
}
