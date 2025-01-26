/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.test.annotation;

import modelengine.fitframework.annotation.Forward;
import modelengine.fitframework.annotation.ScanPackages;
import modelengine.fitframework.test.adapter.north.junit5.FitExtension;
import modelengine.fitframework.test.domain.db.DatabaseModel;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于集成测试。
 *
 * @author 易文渊
 * @since 2024-07-26
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ScanPackages
@EnableDataSource
@EnableMybatis
@EnableMockMvc
@ExtendWith(FitExtension.class)
public @interface IntegrationTest {
    /**
     * 指示需要扫描的包。
     *
     * @return 表示待扫描的包的 {@link String}{@code []}。
     */
    @Forward(annotation = ScanPackages.class, property = "value")
    String[] scanPackages() default {};

    /**
     * 获取测试数据源兼容模式。
     *
     * @return 表示数据源兼容模式的 {@link DatabaseModel}。
     */
    @Forward(annotation = EnableDataSource.class, property = "model")
    DatabaseModel databaseModel() default DatabaseModel.NONE;
}
