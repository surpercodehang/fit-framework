/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.test.domain.listener;

import modelengine.fitframework.test.domain.resolver.TestClassResolver;
import modelengine.fitframework.test.domain.resolver.TestContextConfiguration;

import java.util.Optional;

/**
 * 用于 Resolver 配置解析的监听器。
 *
 * @author 易文渊
 * @since 2024-07-21
 */
public class ResolverListener implements TestListener {
    @Override
    public Optional<TestContextConfiguration> config(Class<?> clazz) {
        TestContextConfiguration configuration = TestClassResolver.create().resolve(clazz);
        return Optional.of(configuration);
    }
}
