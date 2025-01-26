/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.flowable.util;

/**
 * 表示订阅关系发生的观察者。
 *
 * @author 何天放
 * @since 2024-05-22
 */
public interface OnSubscribedObserver {
    /**
     * 通知订阅关系发生。
     */
    void notifyOnSubscribed();
}
