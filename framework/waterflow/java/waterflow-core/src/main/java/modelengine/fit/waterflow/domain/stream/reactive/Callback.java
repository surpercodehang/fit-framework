/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.stream.reactive;

import java.util.List;

/**
 * 用于流结束后的返回对象处理
 *
 * @param <O> 处理的对象类型
 * @since 1.0
 */
public interface Callback<O> {
    /**
     * getAll
     *
     * @return List<O>
     */
    List<O> getAll();

    /**
     * get
     *
     * @return O
     */
    O get();
}
