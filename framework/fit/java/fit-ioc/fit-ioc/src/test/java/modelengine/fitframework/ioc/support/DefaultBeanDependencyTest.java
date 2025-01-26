/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.mock;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.ioc.BeanApplicableScope;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanDependency;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Type;

/**
 * 表示 {@link DefaultBeanDependency} 的单元测试。
 *
 * @author 杭潇
 * @since 2023-02-28
 */
@DisplayName("测试 DefaultBeanDependency 类")
public class DefaultBeanDependencyTest {
    private final BeanContainer container = new DefaultBeanContainer(Mockito.mock(Plugin.class));
    private final BeanApplicableScope applicable = BeanApplicableScope.INSENSITIVE;
    private final AnnotationMetadata annotations = mock(AnnotationMetadata.class);
    private final Config config = mock(Config.class);

    private BeanDependency beanDependency;
    private BeanMetadata source;
    private String name;
    private Type type;

    @BeforeEach
    void setup() {
        this.source = new DefaultBeanMetadata(this.container,
                "testBeanMetaData",
                null,
                String.class.getGenericSuperclass(),
                "testStereotype",
                false,
                false,
                null,
                this.applicable,
                this.annotations,
                this.config);
        this.name = "testDefaultBeanDependency";
        this.type = String.class;
        this.beanDependency = BeanDependency.create(this.source, this.name, this.type, false, this.annotations);
    }

    @Test
    @DisplayName("获取源 bean 的元数据与给定值相等")
    void theSourceShouldBeEqualsToTheGivenValue() {
        BeanMetadata actualSource = this.beanDependency.source();
        assertThat(actualSource).isEqualTo(this.source);
    }

    @Test
    @DisplayName("获取的 bean 的名称与给定值相等")
    void theNameShouldBeEqualsToTheGivenValue() {
        String actualName = this.beanDependency.name();
        assertThat(actualName).isEqualTo(this.name);
    }

    @Test
    @DisplayName("获取的 bean 的类型值与给定的值相等")
    void theTypeShouldBeEqualsToTheGivenValue() {
        Type actualType = this.beanDependency.type();
        assertThat(actualType).isEqualTo(this.type);
    }

    @Test
    @DisplayName("判断所依赖的 bean 是必须的，返回值与给定值相等")
    void theRequiredIsFalse() {
        boolean actualRequired = this.beanDependency.required();
        assertThat(actualRequired).isFalse();
    }

    @Test
    @DisplayName("获取 bean 依赖的哈希值不为空")
    void theHashCodeIsNotEmpty() {
        // noinspection ResultOfMethodCallIgnored
        assertThatNoException().isThrownBy(() -> this.beanDependency.hashCode());
    }

    @Nested
    @DisplayName("测试 equals() 方法")
    class TestEquals {
        @SuppressWarnings("EqualsWithItself")
        @Test
        @DisplayName("给定相等的值，返回值为 true")
        void givenSameValueThenReturnTrue() {
            boolean equals =
                    DefaultBeanDependencyTest.this.beanDependency.equals(DefaultBeanDependencyTest.this.beanDependency);
            assertThat(equals).isTrue();
        }

        @Nested
        @DisplayName("给定值为 BeanDependency 实例")
        class GivenBeanDependencyInstance {
            @Test
            @DisplayName("给定源 bean 的元数据不相等，返回值为 false")
            void givenDifferentSourceThenReturnFalse() {
                DefaultBeanMetadata defaultBeanMetadata =
                        new DefaultBeanMetadata(DefaultBeanDependencyTest.this.container,
                                "testInfo",
                                null,
                                String.class.getGenericSuperclass(),
                                "testInfo",
                                false,
                                false,
                                null,
                                DefaultBeanDependencyTest.this.applicable,
                                DefaultBeanDependencyTest.this.annotations,
                                DefaultBeanDependencyTest.this.config);
                BeanDependency dependency = BeanDependency.create(defaultBeanMetadata,
                        DefaultBeanDependencyTest.this.name,
                        DefaultBeanDependencyTest.this.type,
                        false,
                        DefaultBeanDependencyTest.this.annotations);
                boolean equals = DefaultBeanDependencyTest.this.beanDependency.equals(dependency);
                assertThat(equals).isFalse();
            }

            @Test
            @DisplayName("给定 Bean 名称不相等，返回值为 false")
            void givenDifferentNameThenReturnFalse() {
                BeanDependency dependency = BeanDependency.create(DefaultBeanDependencyTest.this.source,
                        "testInfo",
                        DefaultBeanDependencyTest.this.type,
                        false,
                        DefaultBeanDependencyTest.this.annotations);
                boolean equals = DefaultBeanDependencyTest.this.beanDependency.equals(dependency);
                assertThat(equals).isFalse();
            }

            @Test
            @DisplayName("给定 Bean 类型不相等，返回值为 false")
            void givenDifferentTypeThenReturnFalse() {
                BeanDependency dependency = BeanDependency.create(DefaultBeanDependencyTest.this.source,
                        DefaultBeanDependencyTest.this.name,
                        Integer.class,
                        false,
                        DefaultBeanDependencyTest.this.annotations);
                boolean equals = DefaultBeanDependencyTest.this.beanDependency.equals(dependency);
                assertThat(equals).isFalse();
            }

            @Test
            @DisplayName("给定是否依赖 bean 的值不相等，返回值为 false")
            void givenDifferentRequiredThenReturnFalse() {
                BeanDependency dependency = BeanDependency.create(DefaultBeanDependencyTest.this.source,
                        DefaultBeanDependencyTest.this.name,
                        DefaultBeanDependencyTest.this.type,
                        true,
                        DefaultBeanDependencyTest.this.annotations);
                boolean equals = DefaultBeanDependencyTest.this.beanDependency.equals(dependency);
                assertThat(equals).isFalse();
            }

            @Test
            @DisplayName("给定源 bean 的参数值相等，返回值为 true")
            void givenSameParametersThenReturnTrue() {
                BeanDependency dependency = BeanDependency.create(DefaultBeanDependencyTest.this.source,
                        DefaultBeanDependencyTest.this.name,
                        DefaultBeanDependencyTest.this.type,
                        false,
                        DefaultBeanDependencyTest.this.annotations);
                boolean equals = DefaultBeanDependencyTest.this.beanDependency.equals(dependency);
                assertThat(equals).isTrue();
            }
        }

        @Test
        @DisplayName("给定类型不同，返回值为 false")
        void givenDifferentInstanceThenReturnFalse() {
            Object object = "stringInstance";
            boolean equals = DefaultBeanDependencyTest.this.beanDependency.equals(object);
            assertThat(equals).isFalse();
        }
    }

    @Test
    @DisplayName("获取 toString 值与给定的参数值相等")
    void theStringValueIsCorrespondToTheGivenParameterValue() {
        String toString = this.beanDependency.toString();
        assertThat(toString).isEqualTo(StringUtils.format("[source={0}, name={1}, type={2}, required={3}]",
                this.source,
                this.name,
                this.type,
                false));
    }
}
