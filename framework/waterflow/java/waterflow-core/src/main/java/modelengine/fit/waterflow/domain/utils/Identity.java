/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.utils;

/**
 * ID生成器接口
 *
 * @author 高诗意
 * @since 1.0
 */
@FunctionalInterface
public interface Identity {
    /**
     * getId
     *
     * @return String
     */
    String getId();
}
