/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.stream.reactive;

import modelengine.fit.waterflow.domain.utils.Identity;

/**
 * StreamIdentity
 *
 * @since 1.0
 */
public interface StreamIdentity extends Identity {
    /**
     * getStreamId
     *
     * @return String
     */
    String getStreamId();
}
