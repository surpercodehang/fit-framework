/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.beans;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 * 表示 {@link BeanPropertyAccessor} 的单元测试。
 *
 * @author 杭潇
 * @since 2023-02-14
 */
@DisplayName("测试 BeanPropertyAccessor 类")
public class BeanPropertyAccessorTest {
    private BeanAccessor beanAccessor;
    private PropertyDescriptor propertyDescriptor;
    private BeanPropertyAccessor beanPropertyAccessor;

    @BeforeEach
    void setup() throws IntrospectionException {
        this.beanAccessor = BeanAccessor.of(BeanAccessor.class);
        MockClass mockClass = new MockClass();
        this.propertyDescriptor = new PropertyDescriptor("f3", mockClass.getClass());
        this.beanPropertyAccessor = BeanPropertyAccessor.of(this.beanAccessor, this.propertyDescriptor);
    }

    @Test
    @DisplayName("初始化中，设置读方法与写方法均为 null，返回值为 null")
    void setReadMethodAndWriteMethodNullWhenInitializeThenReturnNull() throws IntrospectionException {
        this.propertyDescriptor.setReadMethod(null);
        this.propertyDescriptor.setWriteMethod(null);
        BeanPropertyAccessor propertyAccessor = BeanPropertyAccessor.of(this.beanAccessor, this.propertyDescriptor);
        assertThat(propertyAccessor).isNull();
    }

    @Nested
    @DisplayName("测试 get() 方法")
    class TestGet {
        @Test
        @DisplayName("设置读方法值为 null，抛出异常")
        void setReadMethodAsNullThenThrowException() throws IntrospectionException {
            propertyDescriptor.setReadMethod(null);
            beanPropertyAccessor = BeanPropertyAccessor.of(beanAccessor, propertyDescriptor);
            UnsupportedOperationException unsupportedOperationException =
                    catchThrowableOfType(() -> beanPropertyAccessor.get(beanAccessor),
                            UnsupportedOperationException.class);
            assertThat(unsupportedOperationException).hasMessage(
                    StringUtils.format("The property is not readable. [bean={0}, property={1}]",
                            beanAccessor.type().getName(), propertyDescriptor.getName()));
        }
    }

    @Nested
    @DisplayName("测试 set() 方法")
    class TestSet {
        @Test
        @DisplayName("设置写方法值为 null，抛出异常")
        void setWriteMethodAsNullThenThrowException() throws IntrospectionException {
            propertyDescriptor.setWriteMethod(null);
            beanPropertyAccessor = BeanPropertyAccessor.of(beanAccessor, propertyDescriptor);
            UnsupportedOperationException unsupportedOperationException =
                    catchThrowableOfType(() -> beanPropertyAccessor.set(beanAccessor, 10),
                            UnsupportedOperationException.class);
            assertThat(unsupportedOperationException).hasMessage(
                    StringUtils.format("The property is not writable. [bean={0}, property={1}]",
                            beanAccessor.type().getName(), propertyDescriptor.getName()));
        }
    }

    @Test
    @DisplayName("获取 Bean 的属性提供访问程序的参数值与给定值相等")
    void theParameterOfBeanPropertyAccessorShouldBeEqualsToTheGivenParameter() {
        String actual = this.beanPropertyAccessor.toString();
        assertThat(actual).isEqualTo(StringUtils.format("{0}.{1} : {2}", this.beanAccessor.type().getName(),
                this.propertyDescriptor.getName(), "long"));
    }

    static class MockClass {
        private long f3;

        public MockClass() {
            this.f3 = 0;
        }

        /**
         * 用于测试静态方法。
         *
         * @return 表示返回值。
         */
        public long getF3() {
            return f3;
        }

        public void setF3(long f3) {
            this.f3 = f3;
        }
    }
}
