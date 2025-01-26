/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.http.server.handler.PropertyValueMapper;
import modelengine.fit.http.server.handler.SourceFetcher;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 表示单一来源的 {@link PropertyValueMapper}。
 * <p>{@link UniqueSourcePropertyValueMapper} 会将数据从指定数据源取出，然后设置到指定的位置，并做简单的数组转换。</p>
 *
 * @author 季聿阶
 * @since 2022-08-28
 */
public class UniqueSourcePropertyValueMapper implements PropertyValueMapper {
    private final PropertyValueMapper propertyValueMapper;

    /**
     * 通过数据来源获取器和目标数据是否为数组的标记来实例化 {@link UniqueSourcePropertyValueMapper}。
     * <p>{@code isDestinationArray} 表示来源数据在目标数据中是否为数组形式：
     * <ul>
     *     <li>当数据来源为 {@link QueryFetcher}、{@link HeaderFetcher} 或 {@link FormUrlEncodedEntityFetcher}
     *     时，因为其获取到的数据默认为数组，需要根据实际参数的类型来决定来源数据是否需要为数组形式；</li>
     *     <li>当数据来源为其它情况时，默认在目标数据中不体现数组形式。</li>
     * </ul>
     * </p>
     *
     * @param sourceFetcher 表示数据来源获取器的 {@link SourceFetcher}。
     * @param isDestinationArray 表示目标数据是否为数组的标记的 {@code boolean}。
     * @throws IllegalArgumentException 当 {@code sourceFetcher} 为 {@code null} 时。
     */
    public UniqueSourcePropertyValueMapper(SourceFetcher sourceFetcher, boolean isDestinationArray) {
        List<SourceFetcherInfo> sourceFetcherInfos = Collections.singletonList(new SourceFetcherInfo(notNull(
                sourceFetcher,
                "The source fetch cannot be null."), null, isDestinationArray));
        this.propertyValueMapper = new MultiSourcesPropertyValueMapper(sourceFetcherInfos);
    }

    @Override
    public Object map(HttpClassicServerRequest request, HttpClassicServerResponse response,
            Map<String, Object> context) {
        return this.propertyValueMapper.map(request, response, context);
    }
}
