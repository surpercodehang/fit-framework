/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.datasource;

import modelengine.fitframework.annotation.Scope;
import modelengine.fitframework.ioc.BeanSupplier;

import javax.sql.DataSource;

/**
 * FIT 框架数据源工厂的 Bean 接口定义。
 *
 * @author 易文渊
 * @since 2024-07-27
 */
public interface FitDataSource extends BeanSupplier<DataSource> {
    /**
     * 获取数据源的名字。
     *
     * @return 表示数据源名字的 {@link String}。
     */
    String name();

    /**
     * 获取数据源的访问模式。
     *
     * @return 表示数据源访问模式的 {@link Scope}。
     */
    AccessMode mode();

    /**
     * 判断数据源是否已经加载。
     *
     * @return 如果数据源已经加载，则返回 {@code true}；否则返回 {@code false}。
     */
    boolean isLoaded();
}