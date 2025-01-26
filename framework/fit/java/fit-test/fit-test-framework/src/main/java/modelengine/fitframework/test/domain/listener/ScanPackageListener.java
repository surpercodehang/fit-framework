/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.test.domain.listener;

import modelengine.fitframework.annotation.ScanPackages;
import modelengine.fitframework.test.domain.resolver.TestContextConfiguration;
import modelengine.fitframework.test.domain.util.AnnotationUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

/**
 * 表示包扫描器的监听器。
 *
 * @author 易文渊
 * @since 2024-07-26
 */
public class ScanPackageListener implements TestListener {
    @Override
    public Optional<TestContextConfiguration> config(Class<?> clazz) {
        return AnnotationUtils.getAnnotation(clazz, ScanPackages.class)
                .map(annotation -> TestContextConfiguration.custom()
                        .testClass(clazz)
                        .scannedPackages(new HashSet<>(Arrays.asList(annotation.value())))
                        .build());
    }
}