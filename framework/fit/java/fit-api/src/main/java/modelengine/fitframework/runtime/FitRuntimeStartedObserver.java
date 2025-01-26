/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.runtime;

/**
 * 为 {@link FitRuntime} 启动完成提供观察者。
 *
 * @author 梁济时
 * @since 2022-08-15
 */
public interface FitRuntimeStartedObserver {
    /**
     * 当FIT运行环境启动完成时被通知。
     *
     * @param runtime 表示已启动完成的运行环境的 {@link FitRuntime}。
     */
    void onRuntimeStarted(FitRuntime runtime);
}
