/*
 * Copyright (c) 2024-2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fitframework.test.domain.db;

import modelengine.fitframework.util.StringUtils;

/**
 * 表示数据库兼容模式的枚举。
 *
 * @author 易文渊
 * @since 2024-07-21
 */
public enum DatabaseModel {
    /** 表示未指定数据库类型。 */
    NONE(""),

    /** 表示 MySQL 数据库。 */
    MYSQL("MySQL"),

    /** 表示 PostgreSQL 数据库。 */
    POSTGRESQL("PostgreSQL");

    private final String model;

    DatabaseModel(String model) {
        this.model = model;
    }

    /**
     * 获取数据库连接的 URL。
     *
     * @return 表示数据库连接 URL 的 {@link String}。
     */
    public String getUrl() {
        String baseUrl = "jdbc:h2:mem:test;DATABASE_TO_LOWER=TRUE";
        return StringUtils.isBlank(this.model) ? baseUrl : baseUrl + ";MODE=" + this.model;
    }
}
