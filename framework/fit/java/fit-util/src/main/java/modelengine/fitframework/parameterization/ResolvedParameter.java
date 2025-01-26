/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.parameterization;

/**
 * 表示解析到的变量信息。
 *
 * @author 梁济时
 * @since 2020-07-24
 */
public interface ResolvedParameter {
    /**
     * 获取解析到的变量的名称。
     *
     * @return 表示变量名称的 {@link String}。
     */
    String getName();

    /**
     * 获取解析到的变量所在源字符串中的位置。
     *
     * @return 表示变量所在源字符串中索引的 32 位整数。
     */
    int getPosition();

    /**
     * 获取解析到的变量在源字符串中的长度。
     *
     * @return 表示变量在源字符串中长度的 32 位整数。
     */
    int getLength();
}
