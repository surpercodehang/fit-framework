/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.test.domain.listener;

import modelengine.fitframework.test.annotation.EnableMybatis;
import modelengine.fitframework.test.domain.resolver.TestContextConfiguration;
import modelengine.fitframework.test.domain.util.AnnotationUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * 用于字段注入 mybatis 的监听器。
 *
 * @author 易文渊
 * @since 2024-07-21
 */
public class MybatisTestListener implements TestListener {
    private static final Set<String> DEFAULT_SCAN_PACKAGES =
            new HashSet<>(Arrays.asList("modelengine.fitframework.transaction", "modelengine.fit.integration.mybatis"));

    @Override
    public Optional<TestContextConfiguration> config(Class<?> clazz) {
        if (!AnnotationUtils.getAnnotation(clazz, EnableMybatis.class).isPresent()) {
            return Optional.empty();
        }
        TestContextConfiguration configuration =
                TestContextConfiguration.custom().testClass(clazz).scannedPackages(DEFAULT_SCAN_PACKAGES).build();
        return Optional.of(configuration);
    }
}