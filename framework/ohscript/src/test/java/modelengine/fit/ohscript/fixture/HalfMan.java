/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.fixture;

/**
 * HalfMain
 * 测试数据类
 *
 * @since 1.0
 */
public abstract class HalfMan implements Human {
    @Override
    public Integer getAge() {
        return 100;
    }
}
