/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.support;

import static modelengine.fit.http.server.handler.MockHttpClassicServerRequest.HEADER_KEY;
import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.http.server.handler.MockHttpClassicServerRequest;
import modelengine.fit.http.server.handler.PropertyValueMapper;
import modelengine.fit.http.server.support.DefaultHttpClassicServerRequest;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 表示 {@link MultiSourcesPropertyValueMapper} 的单元测试。
 *
 * @author 邬涨财
 * @since 2024-02-17
 */
public class MultiSourcePropertyValueMapperTest {
    @Test
    @DisplayName("将 Http 请求和响应通过规则映射成为 Map")
    void givenMapWhenDestinationNameNotEmptyThenReturnMap() {
        final MockHttpClassicServerRequest serverRequest = new MockHttpClassicServerRequest();
        final DefaultHttpClassicServerRequest request = serverRequest.getRequest();
        List<SourceFetcherInfo> sourceFetcherInfos =
                Collections.singletonList(new SourceFetcherInfo(new HeaderFetcher(ParamValue.custom()
                        .name(HEADER_KEY)
                        .build()), "a.b", false));
        PropertyValueMapper mapper = new MultiSourcesPropertyValueMapper(sourceFetcherInfos);
        final Object value = mapper.map(request, null, null);
        Map<String, Map<?, ?>> map = ObjectUtils.cast(value);
        assertThat(map).containsKey("a");
    }
}
