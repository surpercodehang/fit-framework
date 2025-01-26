/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.schedule.cron.support;

import modelengine.fitframework.schedule.cron.CronField;

import java.util.BitSet;

/**
 * 表示 {@link CronField} 的抽象实现类。
 *
 * @author 季聿阶
 * @since 2023-01-03
 */
public abstract class AbstractCronField implements CronField {
    private final BitSet bitSet = new BitSet(64);

    @Override
    public BitSet getBitSet() {
        return this.bitSet;
    }

    @Override
    public void mergeSpecialValue(String specialValue) {}
}
