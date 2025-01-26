/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.datasource;

/**
 * 表示数据源访问模式的枚举。
 *
 * @author 易文渊
 * @since 2024-07-27
 */
public enum AccessMode {
    /**
     * 独占数据源。
     */
    EXCLUSIVE,

    /**
     * 共享数据源。
     */
    SHARED
}
