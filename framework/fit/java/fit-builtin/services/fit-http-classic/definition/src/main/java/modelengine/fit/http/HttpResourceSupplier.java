/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http;

/**
 * {@link HttpResource} 的提供者。
 *
 * @author 季聿阶
 * @since 2022-08-19
 */
public interface HttpResourceSupplier {
    /**
     * 获取 Http 的资源。
     *
     * @return 表示 Http 资源的 {@link HttpResource}。
     */
    HttpResource httpResource();
}
