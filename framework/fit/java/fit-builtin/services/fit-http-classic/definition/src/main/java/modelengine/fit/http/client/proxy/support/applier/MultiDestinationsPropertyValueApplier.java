/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.client.proxy.support.applier;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.client.proxy.DestinationSetter;
import modelengine.fit.http.client.proxy.PropertyValueApplier;
import modelengine.fit.http.client.proxy.RequestBuilder;
import modelengine.fit.http.client.proxy.support.setter.DestinationSetterInfo;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.value.ValueFetcher;

import java.util.List;
import java.util.Objects;

/**
 * 表示 {@link PropertyValueApplier} 的多个目标的实现。
 *
 * @author 季聿阶
 * @since 2024-05-11
 */
public class MultiDestinationsPropertyValueApplier implements PropertyValueApplier {
    private static final String LIST_SOURCE_VALUE_SEPARATOR = ",";

    private final List<DestinationSetterInfo> setterInfos;
    private final ValueFetcher valueFetcher;

    /**
     * 使用指定的设置器信息列表和值获取器初始化 {@link MultiDestinationsPropertyValueApplier} 的新实例。
     *
     * @param setterInfos 表示设置器信息列表的 {@link List}{@code <}{@link DestinationSetterInfo}{@code >}。
     * @param valueFetcher 表示值获取器的 {@link ValueFetcher}。
     * @throws IllegalArgumentException 当 {@code setterInfos} 或 {@code valueFetcher} 为 {@code null} 时。
     */
    public MultiDestinationsPropertyValueApplier(List<DestinationSetterInfo> setterInfos, ValueFetcher valueFetcher) {
        this.setterInfos = notNull(setterInfos, "The destination setter infos cannot be null.");
        this.valueFetcher = notNull(valueFetcher, "The value fetcher cannot be null.");
    }

    @Override
    public void apply(RequestBuilder requestBuilder, Object value) {
        this.setterInfos.stream()
                .filter(Objects::nonNull)
                .forEach(setterInfo -> this.setDestination(requestBuilder, value, setterInfo));
    }

    private void setDestination(RequestBuilder requestBuilder, Object source, DestinationSetterInfo setterInfo) {
        DestinationSetter setter = setterInfo.destinationSetter();
        String sourcePath = setterInfo.sourcePath();
        Object value = source;
        if (StringUtils.isNotBlank(sourcePath)) {
            value = this.valueFetcher.fetch(source, sourcePath);
        }
        setter.set(requestBuilder, value);
    }
}