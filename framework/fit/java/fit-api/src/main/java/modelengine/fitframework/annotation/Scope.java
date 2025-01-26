/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.annotation;

/**
 * 表示组件生效范围的枚举类。
 *
 * @author 邬涨财
 * @since 2023-07-17
 */
public enum Scope {
    /**
     * 表示组件仅在当前插件范围内生效。
     */
    PLUGIN,

    /**
     * 表示组件在全局范围生效。
     */
    GLOBAL
}
