/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.serialization.entity.Struct;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Supplier;

/**
 * {@link MessageSerializer} 的基本测试用例。
 *
 * @author 季聿阶
 * @since 2022-09-10
 */
public class MessageSerializerTest {
    private final Supplier<MessageSerializer> messageSerializerSupplier;
    private MessageSerializer messageSerializer;

    public MessageSerializerTest(Supplier<MessageSerializer> messageSerializerSupplier) {
        this.messageSerializerSupplier = messageSerializerSupplier;
    }

    @BeforeEach
    void setup() {
        this.messageSerializer = this.messageSerializerSupplier.get();
    }

    @AfterEach
    void teardown() {
        this.messageSerializer = null;
    }

    @Test
    @DisplayName("当参数为基本类型时，则序列化及反序列化结果正确")
    void shouldBeCorrectForPrimitiveTypes() {
        Type[] argumentTypes = new Type[] {
                int.class, long.class, float.class, double.class, boolean.class
        };
        Object[] values = new Object[] {
                1, -1L, 1.1f, -1.1d, true
        };
        this.testRequest(argumentTypes, values);
        this.testResponse(boolean.class, false);
    }

    @Test
    @DisplayName("当参数为基本类型的边界值时，则序列化及反序列化结果正确")
    void shouldBeCorrectForPrimitiveTypesAndBoundaryValues() {
        Type[] argumentTypes = new Type[] {
                int.class, int.class, long.class, long.class, float.class, float.class, float.class, double.class,
                double.class, double.class
        };
        Object[] values = new Object[] {
                Integer.MIN_VALUE, Integer.MAX_VALUE, Long.MIN_VALUE, Long.MAX_VALUE, Float.MAX_VALUE, Float.MIN_VALUE,
                Float.MIN_NORMAL, Double.MAX_VALUE, Double.MIN_VALUE, Double.MIN_NORMAL
        };
        this.testRequest(argumentTypes, values);
        this.testResponse(int.class, 0);
    }

    @Test
    @DisplayName("当参数为空值时，则序列化及反序列化结果正确")
    void shouldBeCorrectForVoidTypes() {
        Type[] argumentTypes = new Type[] {void.class, Void.class};
        Object[] values = new Object[] {null, null};
        this.testRequest(argumentTypes, values);
        this.testResponse(void.class, null);
    }

    @Test
    @DisplayName("当参数为结构体类型时，则序列化及反序列结果正确")
    void shouldBeCorrectForStructType() {
        Struct struct = new Struct();
        struct.setF1("f1");
        struct.setF3(true);
        struct.setF4(Arrays.asList(1L, 2L));
        struct.setF5(MapBuilder.<String, Double>get().put("1", 1d).build());
        this.testRequest(new Type[] {Struct.class}, new Object[] {struct});
        this.testResponse(Struct.class, struct);
    }

    private void testRequest(Type[] argumentTypes, Object[] values) {
        byte[] bytes = this.messageSerializer.serializeRequest(argumentTypes, values);
        Object[] arguments = this.messageSerializer.deserializeRequest(argumentTypes, bytes);
        assertThat(arguments).isNotNull().hasSameSizeAs(values).hasSameElementsAs(Arrays.asList(values));
    }

    private void testResponse(Type dataType, Object value) {
        byte[] bytes = this.messageSerializer.serializeResponse(dataType, value);
        Object response = this.messageSerializer.deserializeResponse(dataType, bytes);
        assertThat(response).isEqualTo(value);
    }
}
