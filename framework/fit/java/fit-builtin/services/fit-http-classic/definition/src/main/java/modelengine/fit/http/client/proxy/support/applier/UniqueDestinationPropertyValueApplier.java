/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.proxy.support.applier;

import modelengine.fit.http.client.proxy.DestinationSetter;
import modelengine.fit.http.client.proxy.PropertyValueApplier;
import modelengine.fit.http.client.proxy.RequestBuilder;
import modelengine.fit.http.client.proxy.support.setter.DestinationSetterInfo;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.value.ValueFetcher;

import java.util.Collections;

/**
 * 表示 {@link PropertyValueApplier} 的单个目标的实现。
 *
 * @author 王攀博
 * @since 2024-06-07
 */
public class UniqueDestinationPropertyValueApplier implements PropertyValueApplier {
    private final PropertyValueApplier applier;

    public UniqueDestinationPropertyValueApplier(DestinationSetter setter, ValueFetcher valueFetcher) {
        this.applier = new MultiDestinationsPropertyValueApplier(Collections.singletonList(new DestinationSetterInfo(
                setter,
                StringUtils.EMPTY)),
                valueFetcher);
    }

    @Override
    public void apply(RequestBuilder requestBuilder, Object value) {
        this.applier.apply(requestBuilder, value);
    }
}
