/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.protocol;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.ReflectionUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;

/**
 * {@link MessageHeaders} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-07-11
 */
@DisplayName("测试 MessageHeaderNames")
public class MessageHeaderNamesTest {
    @ParameterizedTest(name = "[{index}] {0} 的常量名字和值匹配")
    @MethodSource("namesProvider")
    @DisplayName("当给定常用的消息头，可以正确获取其名字")
    void givenMessageNameThenGetItsCorrectName(Field field) {
        final int modifiers = field.getModifiers();
        if (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)) {
            String value = ObjectUtils.cast(ReflectionUtils.getField(null, field));
            assertThat(value.replace('-', '_')).isEqualToIgnoringCase(field.getName());
        }
    }

    static Stream<Field> namesProvider() {
        Field[] fields = MessageHeaderNames.class.getDeclaredFields();
        return Stream.of(fields);
    }
}
