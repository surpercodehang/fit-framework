/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.beans;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 表示 {@link BeanAccessor} 的单元测试。
 *
 * @author 杭潇
 * @since 2023-02-14
 */
@DisplayName("测试 BeanAccessor 类")
public class BeanAccessorTest {
    private final Class<?> actualClass = BeanAccessor.class;
    private final BeanAccessor beanAccessor = BeanAccessor.of(actualClass);

    @Test
    @DisplayName("获取 Bean 的类型与给定值相等")
    void theTypeOfBeanShouldBeEqualsToGivenType() {
        Class<?> type = this.beanAccessor.type();
        assertThat(type).isEqualTo(this.actualClass);
    }

    @Test
    @DisplayName("调用获取方法，设置值为 null，执行成功")
    void givenNullValueWhenInvokeAcceptThenExecuteSuccessfully() {
        Map<String, Object> map = new HashMap<>();
        map.put("beanKey", null);
        assertDoesNotThrow(() -> this.beanAccessor.accept(beanAccessor, map));
    }

    @Test
    @DisplayName("调用获取映射方法获取别名，执行成功")
    void givenAliaValueWhenInvokeAcceptThenExecuteSuccessfully() {
        BeanAccessor accessor = BeanAccessor.of(Object5.class);
        assertThat(accessor.getAlias("foo_bar")).isEqualTo("fooBar");
        assertThat(accessor.getAlias("fooBar")).isEqualTo("foo_bar");
    }

    @Test
    @DisplayName("给定不存在的属性的名称，抛出异常")
    void givenNotExistPropertyThenThrowException() {
        String property = "notExistProperty";
        IllegalStateException illegalStateException =
                catchThrowableOfType(() -> this.beanAccessor.get(this.beanAccessor, property),
                        IllegalStateException.class);
        assertThat(illegalStateException).hasMessage(
                StringUtils.format("Property with specific name not found. [bean={0}, property={1}]",
                        this.actualClass.getName(), property));
    }
}
