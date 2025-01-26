/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.support;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.http.server.handler.PropertyValueMapper;
import modelengine.fit.http.server.handler.SourceFetcher;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 表示多个来源的 {@link PropertyValueMapper}。
 * <p>{@link MultiSourcesPropertyValueMapper} 会将多个数据从指定的数据源取出，然后设置到指定的位置，并将多个结果进行合并。</p>
 *
 * @author 邬涨财
 * @since 2023-12-17
 */
public class MultiSourcesPropertyValueMapper implements PropertyValueMapper {
    private static final String LIST_SOURCE_VALUE_SEPARATOR = ",";
    private static final char DESTINATION_NAME_SEPARATOR = '.';

    private final List<SourceFetcherInfo> sourceFetcherInfos;

    /**
     * 通过数据来源获取器信息对象来实例化 {@link MultiSourcesPropertyValueMapper}。
     * <p>每个数据来源获取器信息对象由数据来源获取器、目标数据名字和目标数据是否为数组的标记三部分组成。</p>
     * <p>目标数据名字 {@code destinationName} 表示来源数据在目标参数中的位置：
     * <ul>
     *     <li>当目标数据是一个完整的参数时，其为 {@code null}；</li>
     *     <li>当目标数据是一个参数的一部分时，其为 Json 格式描述的路径（JsonPath，不支持数组和通配符的情况），如
     *     {@code a.b}，其代表的含义是来源数据只是目标数据的属性 {@code a} 的值的属性 {@code b} 的值。</li>
     * </ul>
     * </p>
     * <p>目标数据是否为数组的标记 {@code isDestinationArray} 表示来源数据在目标数据中是否为数组形式：
     * <ul>
     *     <li>当数据来源为 {@link QueryFetcher}、{@link HeaderFetcher} 或 {@link FormUrlEncodedEntityFetcher}
     *     时，因为其获取到的数据默认为数组，需要根据实际参数的类型来决定来源数据是否需要为数组形式；</li>
     *     <li>当数据来源为其它情况时，默认在目标数据中不体现数组形式。</li>
     * </ul>
     * </p>
     *
     * @param sourceFetcherInfos 表示数据来源获取器信息对象列表的 {@link List}{@code <}{@link SourceFetcherInfo}{@code >}。
     */
    public MultiSourcesPropertyValueMapper(List<SourceFetcherInfo> sourceFetcherInfos) {
        this.sourceFetcherInfos = ObjectUtils.getIfNull(sourceFetcherInfos, Collections::emptyList);
    }

    @Override
    public Object map(HttpClassicServerRequest request, HttpClassicServerResponse response,
            Map<String, Object> context) {
        return this.sourceFetcherInfos.stream()
                .map(sourceFetcherInfo -> this.fetchSource(request, response, sourceFetcherInfo))
                .filter(Objects::nonNull)
                .reduce((first, second) -> MapUtils.merge(ObjectUtils.cast(first), ObjectUtils.cast(second)))
                .orElse(null);
    }

    private Object fetchSource(HttpClassicServerRequest request, HttpClassicServerResponse response,
            SourceFetcherInfo sourceFetcherInfo) {
        SourceFetcher sourceFetcher = sourceFetcherInfo.sourceFetcher();
        String destinationName = sourceFetcherInfo.destinationName();
        boolean isDestinationArray = sourceFetcherInfo.isDestinationArray();
        Object source = sourceFetcher.get(request, response);
        return this.convert(source, sourceFetcher.isArrayAble(), destinationName, isDestinationArray);
    }

    private Object convert(Object source, boolean isArrayAble, String destinationName, boolean isDestinationArray) {
        Object actualSource = source;
        if (isArrayAble && !isDestinationArray) {
            actualSource = this.handleListSource(actualSource).orElse(null);
        }
        if (StringUtils.isNotBlank(destinationName)) {
            actualSource = this.handleDestinationName(actualSource, destinationName);
        }
        return actualSource;
    }

    private Optional<String> handleListSource(Object source) {
        List<Object> actualList = ObjectUtils.cast(source);
        return CollectionUtils.isEmpty(actualList)
                ? Optional.empty()
                : Optional.of(actualList.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(LIST_SOURCE_VALUE_SEPARATOR)));
    }

    private Object handleDestinationName(Object source, String name) {
        LinkedList<String> attrs = StringUtils.split(name, DESTINATION_NAME_SEPARATOR, LinkedList::new);
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> current = map;
        while (attrs.size() > 1) {
            String attr = attrs.removeFirst();
            Map<String, Object> subMap = new HashMap<>();
            current.put(attr, subMap);
            current = subMap;
        }
        current.put(attrs.getLast(), source);
        return map;
    }
}
