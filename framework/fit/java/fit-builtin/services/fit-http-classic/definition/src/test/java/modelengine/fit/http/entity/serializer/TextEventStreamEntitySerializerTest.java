/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.entity.serializer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.entity.TextEvent;
import modelengine.fit.http.entity.TextEventStreamEntity;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 表示 {@link TextEventStreamSerializer} 的单元测试。
 *
 * @author 易文渊
 * @since 2024-08-03
 */
@DisplayName("测试 TextEventStreamEntitySerializer 类")
public class TextEventStreamEntitySerializerTest {
    private TextEventStreamSerializer textEventStreamSerializer;
    private final HttpMessage httpMessage = mock(HttpMessage.class);

    @BeforeEach
    void setup() {
        ObjectSerializer jsonSerializer = new ObjectSerializerImplement();
        this.textEventStreamSerializer = new TextEventStreamSerializer(String.class, jsonSerializer);
    }

    static class TextEventStringProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(Arguments.of("data:1\n\ndata:2\n\ndata:3\n\n", "123"),
                    Arguments.of("data: 1 \n\ndata:2  \n\ndata:3\n\n", "123"),
                    Arguments.of("data:1\ndata:2\n\ndata:3\n\n", "1\n23"));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(TextEventStringProvider.class)
    @DisplayName("调用 deserializeEntity() 方法，返回值与给定值相等")
    void giveTextStreamResponseThenDeserializeOk(String input, String output) {
        byte[] givenByte = input.getBytes(StandardCharsets.UTF_8);
        TextEventStreamEntity entity = ObjectUtils.cast(this.textEventStreamSerializer.deserializeEntity(givenByte,
                StandardCharsets.UTF_8,
                this.httpMessage));
        Optional<String> result =
                entity.stream().map(event -> ObjectUtils.<String>cast(event.data())).reduce(String::concat).block();
        assertThat(result).hasValue(output);
    }

    @Test
    @DisplayName("调用 deserializeEntity() 方法，返回完整对象")
    void giveFullTextStreamResponseThenDeserializeOk() {
        String text = "id: 001\nevent:message\nretry:1000\n: this is a comment\ndata:test\n\n";
        TextEventStreamEntity entity = ObjectUtils.cast(this.textEventStreamSerializer.deserializeEntity(
                text.getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8,
                this.httpMessage));
        List<TextEvent> events = entity.stream().blockAll();
        assertThat(events).hasSize(1)
                .element(0)
                .extracting(TextEvent::id, TextEvent::event, TextEvent::retry, TextEvent::comment, TextEvent::data)
                .contains("001", "message", Duration.ofMillis(1000), null, "test");
    }
}