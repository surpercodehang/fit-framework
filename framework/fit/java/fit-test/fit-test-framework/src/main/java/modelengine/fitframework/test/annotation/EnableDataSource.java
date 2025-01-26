/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.test.annotation;

import modelengine.fitframework.test.domain.db.DatabaseModel;
import modelengine.fitframework.test.domain.listener.DataSourceListener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于测试框架注入数据源。
 *
 * @author 易文渊
 * @see DataSourceListener
 * @since 2024-07-21
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableDataSource {
    /**
     * 获取测试数据源兼容模式。
     *
     * @return 表示数据源兼容模式的 {@link DatabaseModel}。
     */
    DatabaseModel model() default DatabaseModel.NONE;
}
