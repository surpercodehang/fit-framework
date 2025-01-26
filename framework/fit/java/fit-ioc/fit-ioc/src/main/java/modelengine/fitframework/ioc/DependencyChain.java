/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc;

import modelengine.fitframework.ioc.support.EmptyDependencyChain;

/**
 * 为依赖解析提供依赖链定义，以判定循环依赖的发生。
 * <p>假定有A、B、C三个Bean，其依赖关系为：A->B->C。则：</p>
 * <ul>
 *     <li>当解析A的依赖时，依赖链为空</li>
 *     <li>当解析B的依赖时，依赖链中有节点A</li>
 *     <li>当解析C的依赖时，依赖链为B、A，即迭代访问链中数据时，首先可获取B，再获取A</li>
 * </ul>
 *
 * @author 梁济时
 * @since 2022-07-07
 */
public interface DependencyChain extends Iterable<BeanMetadata> {
    /**
     * 通过指定下一个依赖Bean的元数据，生成新的依赖链。
     *
     * @param metadata 表示新的所依赖Bean的元数据的 {@link BeanMetadata}。
     * @return 表示包含新依赖Bean的依赖链的 {@link DependencyChain}。
     * @throws IllegalArgumentException {@code metadata} 为 {@code null}。
     * @throws CircularDependencyException 发生循环依赖。
     */
    DependencyChain next(BeanMetadata metadata);

    /**
     * 获取空的依赖链。
     *
     * @return 表示空的依赖链的 {@link DependencyChain}。
     */
    static DependencyChain empty() {
        return EmptyDependencyChain.INSTANCE;
    }
}
