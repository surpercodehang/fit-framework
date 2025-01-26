/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.event;

/**
 * 为应用程序提供事件定义。
 *
 * @author 梁济时
 * @since 2022-11-18
 */
public interface Event {
    /**
     * 获取事件的发布者。
     *
     * @return 表示事件发布者的 {@link Object}。
     */
    Object publisher();
}
