/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization.cbor;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.serialization.cbor.test.box.Box;
import modelengine.fitframework.conf.Config;
import modelengine.fitframework.conf.support.PropertiesConfig;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Properties;

/**
 * 为 {@link CborMessageSerializer} 提供单元测试。
 *
 * @author 季聿阶
 * @since 2020-11-23
 */
public class CborMessageSerializerTest {
    private CborMessageSerializer cborMessageSerializer;
    private Properties properties = new Properties();
    private Config config = new PropertiesConfig("test", properties);

    @BeforeEach
    void setup() {
        this.cborMessageSerializer = new CborMessageSerializer(new CborObjectSerializer(),
                this.config);
    }

    @AfterEach
    void teardown() {
        this.cborMessageSerializer = null;
    }

    @Test
    @DisplayName("序列化并反序列的结果，等于原来的值")
    void shouldEqualsOrigin() {
        Type[] argumentTypes = new Type[] {String.class};
        Object[] values = new Object[] {"用以UTF-8编码的中文"};
        byte[] bytes = this.cborMessageSerializer.serializeRequest(argumentTypes, values);
        Object[] arguments = this.cborMessageSerializer.deserializeRequest(argumentTypes, bytes);
        assertThat(arguments).isEqualTo(values);
    }

    @Nested
    @DisplayName("给定一个结构体对象")
    class GivenParamsAreCustom {
        private final CborMessageSerializer messageSerializer = new CborMessageSerializer(new CborObjectSerializer(),
                CborMessageSerializerTest.this.config);

        private Type simpleGenericType;

        /**
         * 测试方法。
         *
         * @param box 测试参数。
         */
        private void testFun1(Box box) {}

        @BeforeEach
        void setup() throws NoSuchMethodException {
            Method method = this.getClass().getDeclaredMethod("testFun1", Box.class);
            this.simpleGenericType = method.getGenericParameterTypes()[0];
        }

        @Nested
        @DisplayName("给定参数为 Box")
        class GivenSimpleParam {
            @Test
            @DisplayName("序列化并反序列化参数数组之后，等于原值")
            void shouldEqualsOriginWhenSerializeAndDeserializeRequest() {
                Box box = Box.builder()
                        .value("hello")
                        .tList(Arrays.asList("s1", "s2"))
                        .tMap(MapBuilder.<String, String>get().put("k", "v").build())
                        .build();

                byte[] bs = GivenParamsAreCustom.this.messageSerializer.serializeRequest(new Type[] {
                        GivenParamsAreCustom.this.simpleGenericType
                }, new Object[] {box});
                assertThat(bs).isNotEmpty();
                Object[] params = GivenParamsAreCustom.this.messageSerializer.deserializeRequest(new Type[] {
                        GivenParamsAreCustom.this.simpleGenericType
                }, bs);
                assertThat(params).isNotEmpty().hasSize(1);
                assertThat(params[0]).isNotNull();
                Box actualBox = ObjectUtils.cast(params[0]);
                assertThat(actualBox.getValue()).isEqualTo("hello");
                assertThat(actualBox.getTList()).hasSize(2).contains("s1", "s2");
                assertThat(actualBox.getTMap()).hasSize(1).containsEntry("k", "v");
            }

            @Test
            @DisplayName("序列化并反序列化返回值之后，等于原值")
            void shouldEqualsOriginWhenSerializeAndDeserializeResponse() {
                Box box = Box.<String>builder()
                        .value("hello")
                        .tList(Arrays.asList("s1", "s2"))
                        .tMap(MapBuilder.<String, String>get().put("k", "v").build())
                        .build();

                Type type = GivenParamsAreCustom.this.simpleGenericType;
                byte[] bs = GivenParamsAreCustom.this.messageSerializer.serializeResponse(type, box);
                assertThat(bs).isNotEmpty();
                Box actualBox = GivenParamsAreCustom.this.messageSerializer.deserializeResponse(type, bs);
                assertThat(actualBox).isNotNull();
                assertThat(actualBox.getValue()).isEqualTo("hello");
                assertThat(actualBox.getTList()).hasSize(2).contains("s1", "s2");
                assertThat(actualBox.getTMap()).hasSize(1).containsEntry("k", "v");
            }
        }
    }
}
