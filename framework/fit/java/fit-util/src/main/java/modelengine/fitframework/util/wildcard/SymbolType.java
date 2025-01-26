/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.wildcard;

/**
 * 表示符号的类型。
 *
 * @author 梁济时
 * @since 2022-07-29
 */
public enum SymbolType {
    /**
     * 表示是一个普通字符。
     */
    NORMAL,

    /**
     * 表示用以匹配单个字符的通配符。
     */
    SINGLE_WILDCARD,

    /**
     * 表示用以匹配多字符的通配符。
     */
    MULTIPLE_WILDCARD,
}
