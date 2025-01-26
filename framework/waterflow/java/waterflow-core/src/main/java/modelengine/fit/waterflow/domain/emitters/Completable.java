/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.emitters;

/**
 * 完成能力的接口定义
 *
 * @author 宋永坦
 * @since 1.0
 */
public interface Completable {
    /**
     * 完成事件的通知方法
     */
    void complete();
}
