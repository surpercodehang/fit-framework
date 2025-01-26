/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.client.filter.loadbalance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.client.Client;
import modelengine.fitframework.broker.Endpoint;
import modelengine.fitframework.broker.FitableMetadata;
import modelengine.fitframework.broker.Format;
import modelengine.fitframework.broker.Genericable;
import modelengine.fitframework.broker.GenericableMethod;
import modelengine.fitframework.broker.SerializationService;
import modelengine.fitframework.broker.Target;
import modelengine.fitframework.conf.runtime.CommunicationProtocol;
import modelengine.fitframework.conf.runtime.SerializationFormat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link ProtocolAndFormatSupportedFilter} 的单元测试。
 *
 * @author 季聿阶
 * @since 2024-07-14
 */
@DisplayName("测试 ProtocolAndFormatSupportedFilter")
public class ProtocolAndFormatSupportedFilterTest {
    private ProtocolAndFormatSupportedFilter filter;
    private Client client;
    private SerializationService serializationService;
    private FitableMetadata fitable;

    @BeforeEach
    void setup() {
        this.client = mock(Client.class);
        when(this.client.getSupportedProtocols()).thenReturn(Collections.singleton("http"));
        this.serializationService = mock(SerializationService.class);
        when(this.serializationService.getSupportedFormats(any())).thenReturn(Collections.singletonList(
                SerializationFormat.JSON.code()));
        this.fitable = mock(FitableMetadata.class);
        Genericable genericable = mock(Genericable.class);
        when(this.fitable.genericable()).thenReturn(genericable);
        when(genericable.id()).thenReturn("g");
        GenericableMethod method = mock(GenericableMethod.class);
        when(genericable.method()).thenReturn(method);
        when(this.fitable.id()).thenReturn("f");
    }

    @AfterEach
    void teardown() {
        this.fitable = null;
        this.filter = null;
    }

    @Nested
    @DisplayName("当指定了协议和格式之后")
    class AfterSpecifiedProtocolAndFormat {
        @BeforeEach
        void setup() {
            ProtocolAndFormatSupportedFilterTest.this.filter =
                    new ProtocolAndFormatSupportedFilter(Collections.singletonList(this.getClient()),
                            ProtocolAndFormatSupportedFilterTest.this.serializationService,
                            CommunicationProtocol.HTTP,
                            SerializationFormat.JSON);
        }

        @Test
        @DisplayName("当存在本进程地址时，返回地址列表中包含该地址")
        void shouldReturnLocalTarget() {
            List<Target> actual = ProtocolAndFormatSupportedFilterTest.this.filter.filter(
                    ProtocolAndFormatSupportedFilterTest.this.fitable,
                    "local",
                    Arrays.asList(Target.custom().workerId("local").build(), Target.custom().build()),
                    null);
            assertThat(actual).hasSize(1);
        }

        @Test
        @DisplayName("当存在符合要求的协议和格式的地址时，返回地址列表中包含该地址")
        void shouldReturnCorrectTargetWithCorrectProtocolAndFormat() {
            List<Target> actual = ProtocolAndFormatSupportedFilterTest.this.filter.filter(
                    ProtocolAndFormatSupportedFilterTest.this.fitable,
                    "local",
                    Arrays.asList(Target.custom()
                                    .endpoints(Collections.singletonList(Endpoint.custom()
                                            .protocol(CommunicationProtocol.HTTP.name(),
                                                    CommunicationProtocol.HTTP.code())
                                            .build()))
                                    .formats(Collections.singletonList(Format.custom()
                                            .name(SerializationFormat.JSON.name())
                                            .code(SerializationFormat.JSON.code())
                                            .build()))
                                    .build(),
                            Target.custom()
                                    .endpoints(Collections.singletonList(Endpoint.custom()
                                            .protocol(CommunicationProtocol.HTTP.name(),
                                                    CommunicationProtocol.HTTP.code())
                                            .build()))
                                    .formats(Collections.singletonList(Format.custom()
                                            .name(SerializationFormat.CBOR.name())
                                            .code(SerializationFormat.CBOR.code())
                                            .build()))
                                    .build()),
                    null);
            assertThat(actual).hasSize(1);
        }

        private Client getClient() {
            return ProtocolAndFormatSupportedFilterTest.this.client;
        }
    }

    @Nested
    @DisplayName("当未指定协议和格式之后")
    class AfterUnknownProtocolAndFormat {
        @BeforeEach
        void setup() {
            ProtocolAndFormatSupportedFilterTest.this.filter =
                    new ProtocolAndFormatSupportedFilter(Collections.singletonList(this.getClient()),
                            ProtocolAndFormatSupportedFilterTest.this.serializationService,
                            null,
                            null);
        }

        @Test
        @DisplayName("当存在符合要求的协议和格式的地址时，返回地址列表中包含该地址")
        void shouldReturnCorrectTargetWithCorrectProtocolAndFormat() {
            List<Target> actual = ProtocolAndFormatSupportedFilterTest.this.filter.filter(
                    ProtocolAndFormatSupportedFilterTest.this.fitable,
                    "local",
                    Arrays.asList(Target.custom()
                                    .endpoints(Collections.singletonList(Endpoint.custom()
                                            .protocol(CommunicationProtocol.HTTP.name(),
                                                    CommunicationProtocol.HTTP.code())
                                            .build()))
                                    .formats(Collections.singletonList(Format.custom()
                                            .name(SerializationFormat.JSON.name())
                                            .code(SerializationFormat.JSON.code())
                                            .build()))
                                    .build(),
                            Target.custom()
                                    .endpoints(Collections.singletonList(Endpoint.custom()
                                            .protocol(CommunicationProtocol.HTTP.name(),
                                                    CommunicationProtocol.HTTP.code())
                                            .build()))
                                    .formats(Collections.singletonList(Format.custom()
                                            .name(SerializationFormat.CBOR.name())
                                            .code(SerializationFormat.CBOR.code())
                                            .build()))
                                    .build()),
                    null);
            assertThat(actual).hasSize(1);
        }

        private Client getClient() {
            return ProtocolAndFormatSupportedFilterTest.this.client;
        }
    }
}
