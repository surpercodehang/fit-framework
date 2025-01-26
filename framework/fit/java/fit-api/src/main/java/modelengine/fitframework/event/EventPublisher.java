/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.event;

/**
 * 为事件提供发布程序。
 *
 * @author 梁济时
 * @since 2022-11-22
 */
public interface EventPublisher {
    /**
     * 发布事件。
     *
     * @param event 表示待发布的事件的 {@link Event}。
     * @param <E> 表示事件的类型。
     */
    <E extends Event> void publishEvent(E event);
}
