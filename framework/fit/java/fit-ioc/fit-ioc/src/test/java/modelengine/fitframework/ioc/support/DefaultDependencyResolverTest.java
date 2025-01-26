/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.ioc.BeanApplicableScope;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.DependencyResolver;
import modelengine.fitframework.ioc.DependencyResolvingResult;
import modelengine.fitframework.ioc.UnresolvableDependencyException;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 表示 {@link DefaultDependencyResolver} 的单元测试。
 *
 * @author 杭潇
 * @since 2023-02-28
 */
@DisplayName("测试 DefaultDependencyResolver 类")
public class DefaultDependencyResolverTest {
    private final DependencyResolver defaultDependencyResolver = new DefaultDependencyResolver();
    private BeanMetadata beanMetadata;
    private Type type;
    private final String name = "testBeanMetadata";
    private Set<String> aliases;
    private final String stereotype = "testStereotype";
    private final boolean preferred = false;
    private final boolean lazy = false;
    private Set<String> dependencies;
    private final BeanApplicableScope applicable = BeanApplicableScope.INSENSITIVE;
    private final AnnotationMetadata annotations = mock(AnnotationMetadata.class);
    private final Config config = mock(Config.class);

    @BeforeEach
    void setup() {
        BeanContainer container = mock(BeanContainer.class);
        List<BeanFactory> list = new ArrayList<>();
        list.add(mock(BeanFactory.class));
        when(container.all(List.class)).thenReturn(list);
        when(container.all(Map.class)).thenReturn(list);
        this.aliases = new HashSet<>();
        this.aliases.add("testSetting");
        this.type = String.class.getGenericSuperclass();
        this.beanMetadata = new DefaultBeanMetadata(container,
                this.name,
                this.aliases,
                this.type,
                this.stereotype,
                this.preferred,
                this.lazy,
                this.dependencies,
                this.applicable,
                this.annotations,
                this.config);
    }

    @Nested
    @DisplayName("给定 bean 的名称值为空")
    class GivenEmptyBeanName {
        private DependencyResolver resolver() {
            return DefaultDependencyResolverTest.this.defaultDependencyResolver;
        }

        @Test
        @DisplayName("给定依赖不可解，抛出异常")
        void givenDependenciesUnresolvableThenThrowException() {
            DefaultDependencyResolverTest.this.dependencies = new HashSet<>();
            DefaultDependencyResolverTest.this.dependencies.add("testDependencies");
            BeanContainer beanContainer = new DefaultBeanContainer(Mockito.mock(Plugin.class));
            DefaultDependencyResolverTest.this.beanMetadata = new DefaultBeanMetadata(beanContainer,
                    DefaultDependencyResolverTest.this.name,
                    DefaultDependencyResolverTest.this.aliases,
                    DefaultDependencyResolverTest.this.type,
                    DefaultDependencyResolverTest.this.stereotype,
                    DefaultDependencyResolverTest.this.preferred,
                    DefaultDependencyResolverTest.this.lazy,
                    DefaultDependencyResolverTest.this.dependencies,
                    DefaultDependencyResolverTest.this.applicable,
                    DefaultDependencyResolverTest.this.annotations,
                    DefaultDependencyResolverTest.this.config);
            UnresolvableDependencyException unresolvableDependencyException = catchThrowableOfType(() -> this.resolver()
                    .resolve(DefaultDependencyResolverTest.this.beanMetadata,
                            StringUtils.EMPTY,
                            DefaultDependencyResolverTest.this.type,
                            DefaultDependencyResolverTest.this.annotations), UnresolvableDependencyException.class);
            assertThat(unresolvableDependencyException).hasMessage(StringUtils.format(
                    "Dependency unresolvable. [source={0}, dependency.name={1}, dependency.type={2}]",
                    DefaultDependencyResolverTest.this.beanMetadata.name(),
                    StringUtils.EMPTY,
                    DefaultDependencyResolverTest.this.type));
        }

        @Nested
        @DisplayName("给定传参类型为 ParameterizedType 的实例")
        class TheTypeIsInstanceOfParameterizedType {
            @Test
            @DisplayName("参数类型是 List，返回依赖结果值应表示为空")
            void theRawTypeIsListClassThenDependencyResolvingResultMeansEmpty() {
                ParameterizedType inputType = mock(ParameterizedType.class);
                when(inputType.getRawType()).thenReturn(List.class);
                Type[] types = new Type[] {String.class, Integer.class};
                when(inputType.getActualTypeArguments()).thenReturn(types);

                DependencyResolvingResult resolve = GivenEmptyBeanName.this.resolver()
                        .resolve(DefaultDependencyResolverTest.this.beanMetadata,
                                StringUtils.EMPTY,
                                inputType,
                                DefaultDependencyResolverTest.this.annotations);
                assertThat(resolve.toString()).isEqualTo("dependency=[]");
            }

            @Test
            @DisplayName("参数类型是 Map 且获取实际类型参数第一个值为 String 类，返回依赖结果值应表示为空")
            void theRawTypeIsMapClassAndActualTypeArgumentIsStringClassThenDependencyResolvingResultMeansEmpty() {
                ParameterizedType inputType = mock(ParameterizedType.class);
                when(inputType.getRawType()).thenReturn(Map.class);
                Type[] types = new Type[] {String.class, Integer.class};
                when(inputType.getActualTypeArguments()).thenReturn(types);

                DependencyResolvingResult resolve = GivenEmptyBeanName.this.resolver()
                        .resolve(DefaultDependencyResolverTest.this.beanMetadata,
                                StringUtils.EMPTY,
                                inputType,
                                DefaultDependencyResolverTest.this.annotations);
                assertThat(resolve.toString()).isEqualTo("dependency={}");
            }
        }
    }

    @Nested
    @DisplayName("给定 bean 的名称值不为空")
    class GivenNotEmptyBeanName {
        @Test
        @DisplayName("获取的依赖结果返回表示失败的结果")
        void getDependencyResolvingResultThenReturnIsFailedResult() {
            DependencyResolvingResult resolve = DefaultDependencyResolverTest.this.defaultDependencyResolver.resolve(
                    DefaultDependencyResolverTest.this.beanMetadata,
                    DefaultDependencyResolverTest.this.name,
                    DefaultDependencyResolverTest.this.type,
                    DefaultDependencyResolverTest.this.annotations);
            assertThat(resolve.toString()).isEqualTo("non-dependency");
        }
    }
}
