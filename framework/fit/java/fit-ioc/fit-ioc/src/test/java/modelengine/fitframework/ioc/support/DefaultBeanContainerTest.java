/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.annotation.Stereotype;
import modelengine.fitframework.ioc.AbstractBeanContainerTest;
import modelengine.fitframework.ioc.AmbiguousBeanException;
import modelengine.fitframework.ioc.BeanApplicableScope;
import modelengine.fitframework.ioc.BeanDefinition;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.BeanRegisteredObserver;
import modelengine.fitframework.ioc.BeanResolver;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.ioc.lifecycle.bean.BeanDestroyer;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInitializer;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInjector;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.plugin.PluginKey;
import modelengine.fitframework.plugin.PluginMetadata;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 表示 DefaultBeanContainer 的单元测试。
 *
 * @author 梁济时
 * @since 2022-04-28
 */
@DisplayName("测试 DefaultBeanContainer 类")
class DefaultBeanContainerTest extends AbstractBeanContainerTest {
    private DefaultBeanContainer container;
    private PluginMetadata pluginMetadata;
    private final String name = "testName";
    private final Set<String> emptySet = Collections.emptySet();
    private final String stereotype = "testStereotype";
    private final Map<String, Object> properties = MapBuilder.<String, Object>get().put("testKey", "testValue").build();
    private final AnnotationMetadata annotations = mock(AnnotationMetadata.class);
    private final Type superType = this.getClass().getGenericSuperclass();

    static class Bean {
        private static final Long ID = 100L;
        private static final String NAME = "my-bean";
        private static final String VALUE = "my-value";

        private final Long id;
        private String name;
        private String value;
        private boolean initialized = false;
        private boolean destroyed = false;

        Bean(Long id) {
            this.id = id;
        }

        void setValue(String value) {
            this.value = value;
        }

        void initialize() {
            this.initialized = true;
        }

        void destroy() {
            this.destroyed = true;
        }

        static boolean isPreferredConstructor(Constructor<?> constructor) {
            return constructor.getParameterCount() == 1 && constructor.getParameters()[0].getType() == Long.class;
        }

        static boolean isFieldInjectable(Field field) {
            return field.getName().equals("name");
        }

        static BeanInjector fieldInjector() {
            return bean -> (ObjectUtils.<Bean>cast(bean)).name = NAME;
        }

        static boolean isMethodInjectable(Method method) {
            return method.getName().equals("setValue") && method.getParameterCount() == 1
                    && method.getParameters()[0].getType() == String.class;
        }

        static BeanInjector methodInjector() {
            return bean -> (ObjectUtils.<Bean>cast(bean)).setValue(VALUE);
        }

        static boolean isMethodInitializer(Method method) {
            return method.getName().equals("initialize") && method.getParameterCount() == 0;
        }

        static BeanInitializer initializer() {
            return bean -> ObjectUtils.<Bean>cast(bean).initialize();
        }

        static boolean isMethodDestroyer(Method method) {
            return method.getName().equals("destroy") && method.getParameterCount() == 0;
        }

        static BeanDestroyer destroyer() {
            return bean -> ObjectUtils.<Bean>cast(bean).destroy();
        }
    }

    @BeforeEach
    void setup() {
        BeanResolver beanResolver = mock(BeanResolver.class);
        AnnotationMetadataResolver annotationMetadataResolver = mock(AnnotationMetadataResolver.class);

        FitRuntime runtime = mock(FitRuntime.class);
        when(runtime.resolverOfAnnotations()).thenReturn(annotationMetadataResolver);
        when(runtime.resolverOfBeans()).thenReturn(beanResolver);
        Plugin plugin = mock(Plugin.class);
        when(plugin.runtime()).thenReturn(runtime);
        this.container = new DefaultBeanContainer(plugin);
        when(plugin.container()).thenReturn(this.container);
        when(plugin.children()).thenReturn(EMPTY_PLUGIN_COLLECTION);
        this.pluginMetadata = mock(PluginMetadata.class);
        when(this.pluginMetadata.name()).thenReturn("testMetadata");
        when(plugin.metadata()).thenReturn(this.pluginMetadata);

        when(beanResolver.bean(any(), any(Class.class))).thenReturn(Optional.of(BeanDefinition.custom()
                .name(Bean.class.getName())
                .type(Bean.class)
                .stereotype(Stereotype.SINGLETON)
                .build()));
        when(beanResolver.preferred(any(), argThat(Bean::isPreferredConstructor))).thenReturn(true);
        when(beanResolver.parameter(any(),
                argThat(parameter -> parameter.getType() == Long.class))).thenReturn(Optional.of(() -> 100L));
        when(beanResolver.injector(any(),
                argThat(Bean::isFieldInjectable))).thenReturn(Optional.of(Bean.fieldInjector()));
        when(beanResolver.injector(any(),
                argThat(Bean::isMethodInjectable))).thenReturn(Optional.of(Bean.methodInjector()));
        when(beanResolver.initializer(any(),
                argThat(Bean::isMethodInitializer))).thenReturn(Optional.of(Bean.initializer()));
        when(beanResolver.destroyer(any(), argThat(Bean::isMethodDestroyer))).thenReturn(Optional.of(Bean.destroyer()));
    }

    @Test
    void should_contains_registered_bean() {
        this.container.register(Bean.class);
        Optional<Bean> optional = this.container.factory(Bean.class).map(BeanFactory::get);
        assertTrue(optional.isPresent());
        Bean bean = optional.get();
        assertEquals(Bean.ID, bean.id);
        assertEquals(Bean.NAME, bean.name);
        assertEquals(Bean.VALUE, bean.value);
        assertTrue(bean.initialized);
    }

    @Nested
    @DisplayName("注册一个 Bean")
    class RegistryBean {
        private final Long id = 1024L;

        @Test
        @DisplayName("通过单个 Bean 注册，注册成功")
        void registerWithSingleBeanThenRegisteredSuccessfully() {
            Bean bean = new Bean(this.id);
            DefaultBeanContainerTest.this.container.register(bean);
            Optional<Bean> optional =
                    DefaultBeanContainerTest.this.container.factory(bean.getClass()).map(BeanFactory::get);
            assertTrue(optional.isPresent());
            Bean getBean = optional.get();
            assertEquals(this.id, getBean.id);
            assertTrue(getBean.initialized);
        }

        @Test
        @DisplayName("通过 Bean 与其名称注册，注册成功")
        void registerWithBeanAndBeanNameThenRegisteredSuccessfully() {
            Bean bean = new Bean(this.id);
            String beanName = "testName";
            DefaultBeanContainerTest.this.container.register(bean, beanName);
            Optional<Bean> optional =
                    DefaultBeanContainerTest.this.container.factory(bean.getClass()).map(BeanFactory::get);
            assertTrue(optional.isPresent());
            Bean getBean = optional.get();
            assertEquals(getBean.id, this.id);
            assertTrue(getBean.initialized);
        }

        @Test
        @DisplayName("通过 Bean 与其类型注册，注册成功")
        void registerWithBeanAndBeanTypeThenRegisteredSuccessfully() {
            Bean bean = new Bean(this.id);
            DefaultBeanContainerTest.this.container.register(bean, bean.getClass());
            Optional<Bean> optional =
                    DefaultBeanContainerTest.this.container.factory(bean.getClass()).map(BeanFactory::get);
            assertTrue(optional.isPresent());
            Bean getBean = optional.get();
            assertEquals(getBean.id, this.id);
            assertTrue(getBean.initialized);
        }

        @Nested
        @DisplayName("通过 Bean 的定义注册")
        class RegisterWithBeanDefinition {
            @Test
            @DisplayName("给定类型值为 Class 的实例，注册成功")
            void givenTypeIsInstanceOfClassThenRegisteredSuccessfully() {
                DefaultBeanDefinition beanDefinition = new DefaultBeanDefinition(DefaultBeanContainerTest.this.name,
                        DefaultBeanContainerTest.this.superType,
                        DefaultBeanContainerTest.this.emptySet,
                        DefaultBeanContainerTest.this.stereotype,
                        DefaultBeanContainerTest.this.annotations,
                        false,
                        false,
                        DefaultBeanContainerTest.this.emptySet,
                        BeanApplicableScope.ANYWHERE,
                        DefaultBeanContainerTest.this.properties);
                List<BeanMetadata> register = DefaultBeanContainerTest.this.container.register(beanDefinition);
                assertThat(register).hasSize(1);
            }

            @Test
            @DisplayName("给定类型不为 Class 的实例，抛出异常")
            void givenTypeNotInstanceOfClassThenThrowException() {
                Type type = mock(Type.class);
                DefaultBeanDefinition beanDefinition = new DefaultBeanDefinition(DefaultBeanContainerTest.this.name,
                        type,
                        DefaultBeanContainerTest.this.emptySet,
                        DefaultBeanContainerTest.this.stereotype,
                        DefaultBeanContainerTest.this.annotations,
                        false,
                        false,
                        DefaultBeanContainerTest.this.emptySet,
                        BeanApplicableScope.ANYWHERE,
                        DefaultBeanContainerTest.this.properties);
                IllegalArgumentException illegalArgumentException =
                        catchThrowableOfType(() -> DefaultBeanContainerTest.this.container.register(beanDefinition),
                                IllegalArgumentException.class);
                assertThat(illegalArgumentException).hasMessage(StringUtils.format(
                        "The type of bean to register must be a class. [type={0}]",
                        type.getTypeName()));
            }
        }
    }

    @Nested
    @DisplayName("测试 subscribe() 方法")
    class TestSubscribe {
        @Test
        @DisplayName("给定值为非 null 的 Bean 注册观察者对象，执行成功未抛出异常")
        void givenNotNullBeanRegisteredObserverThenExecuteSuccessfully() {
            BeanRegisteredObserver beanRegisteredObserver = mock(BeanRegisteredObserver.class);
            assertDoesNotThrow(() -> DefaultBeanContainerTest.this.container.subscribe(beanRegisteredObserver));
        }

        @Test
        @DisplayName("给定值为 null 的对象，执行成功未抛出异常")
        void givenNullValueThenExecuteSuccessfully() {
            assertDoesNotThrow(() -> DefaultBeanContainerTest.this.container.subscribe((BeanRegisteredObserver) null));
        }
    }

    @Nested
    @DisplayName("测试 unsubscribe() 方法")
    class TestUnsubscribe {
        @Test
        @DisplayName("给定值为非 null 的 Bean 注册观察者对象，执行成功未抛出异常")
        void givenNotNullBeanRegisteredObserverThenExecuteSuccessfully() {
            BeanRegisteredObserver beanRegisteredObserver = mock(BeanRegisteredObserver.class);
            assertDoesNotThrow(() -> getContainer().unsubscribe(beanRegisteredObserver));
        }

        @Test
        @DisplayName("给定值为 null 的对象，执行成功未抛出异常")
        void givenNullValueThenExecuteSuccessfully() {
            assertDoesNotThrow(() -> getContainer().unsubscribe((BeanRegisteredObserver) null));
        }

        private DefaultBeanContainer getContainer() {
            return DefaultBeanContainerTest.this.container;
        }
    }

    @Test
    @DisplayName("获取容器的信息，返回值与给定值相等")
    void theContainerNameShouldBeEqualsToTheGivenValue() {
        String containerName = this.container.name();
        assertThat(containerName).isEqualTo(PluginKey.identify(this.pluginMetadata))
                .isEqualTo(this.container.toString());
    }

    @Test
    @DisplayName("销毁指定的单例 Bean，执行成功")
    void destroySingletonThenExecuteSuccessfully() {
        Bean bean = new Bean(110L);
        this.container.register(bean);
        String beanName = this.container.factories().toString().split(",")[0].split("=")[1];
        assertDoesNotThrow(() -> this.container.destroySingleton(beanName));
    }

    @Test
    @DisplayName("移除指定的 Bean，执行成功")
    void removeBeanThenExecuteSuccessfully() {
        Bean bean = new Bean(110L);
        this.container.register(bean);
        String beanName = this.container.factories().toString().split(",")[0].split("=")[1];
        assertDoesNotThrow(() -> this.container.removeBean(beanName));
    }

    @Test
    @DisplayName("启动容器，执行成功")
    void startContainerThenExecuteSuccessfully() {
        Bean bean = new Bean(110L);
        this.container.register(bean);
        assertDoesNotThrow(() -> this.container.start());
    }

    @Nested
    @DisplayName("测试 factory() 方法")
    class TestFactory {
        private final DefaultBeanDefinition defaultBeanDefinition1 = Mockito.mock(DefaultBeanDefinition.class);
        private final DefaultBeanDefinition defaultBeanDefinition2 = Mockito.mock(DefaultBeanDefinition.class);

        @BeforeEach
        void setup() {
            when(this.defaultBeanDefinition1.type()).thenReturn(String.class);
            when(this.defaultBeanDefinition1.name()).thenReturn("testDefaultBeanDefinition1");
            DefaultBeanContainerTest.this.container.register(this.defaultBeanDefinition1);
            when(this.defaultBeanDefinition2.type()).thenReturn(String.class);
            when(this.defaultBeanDefinition2.name()).thenReturn("testDefaultBeanDefinition2");
            DefaultBeanContainerTest.this.container.register(this.defaultBeanDefinition2);
        }

        @Test
        @DisplayName("当注册的两个 Bean 均不为首选，抛出异常")
        void givenBothBeanNotPreferredThenThrowException() {
            AmbiguousBeanException ambiguousBeanException =
                    catchThrowableOfType(() -> DefaultBeanContainerTest.this.container.factory(String.class),
                            AmbiguousBeanException.class);
            assertThat(ambiguousBeanException).hasMessageStartingWith("Ambiguous bean of specific type found.");
        }

        @Test
        @DisplayName("当注册的多个 Bean 仅有一个为首选，返回值不为 null")
        void givenOnlyOneBeanPreferredThenReturnIsNotNull() {
            Type type = String.class;
            DefaultBeanDefinition beanDefinition = new DefaultBeanDefinition(DefaultBeanContainerTest.this.name,
                    type,
                    DefaultBeanContainerTest.this.emptySet,
                    DefaultBeanContainerTest.this.stereotype,
                    DefaultBeanContainerTest.this.annotations,
                    true,
                    false,
                    DefaultBeanContainerTest.this.emptySet,
                    BeanApplicableScope.ANYWHERE,
                    DefaultBeanContainerTest.this.properties);
            DefaultBeanContainerTest.this.container.register(beanDefinition);
            Optional<BeanFactory> factory = DefaultBeanContainerTest.this.container.factory(String.class);
            assertThat(factory).isNotNull();
        }

        @Test
        @DisplayName("当注册的两个均为首选 Bean，抛出异常")
        void givenBothBeanPreferredThenThrowException() {
            Type type = String.class;
            DefaultBeanDefinition beanDefinition = new DefaultBeanDefinition(DefaultBeanContainerTest.this.name,
                    type,
                    DefaultBeanContainerTest.this.emptySet,
                    DefaultBeanContainerTest.this.stereotype,
                    DefaultBeanContainerTest.this.annotations,
                    true,
                    false,
                    DefaultBeanContainerTest.this.emptySet,
                    BeanApplicableScope.ANYWHERE,
                    DefaultBeanContainerTest.this.properties);
            DefaultBeanContainerTest.this.container.register(beanDefinition);
            beanDefinition = new DefaultBeanDefinition("testBeanDefinitionName",
                    type,
                    DefaultBeanContainerTest.this.emptySet,
                    DefaultBeanContainerTest.this.stereotype,
                    DefaultBeanContainerTest.this.annotations,
                    true,
                    false,
                    DefaultBeanContainerTest.this.emptySet,
                    BeanApplicableScope.ANYWHERE,
                    DefaultBeanContainerTest.this.properties);
            DefaultBeanContainerTest.this.container.register(beanDefinition);
            AmbiguousBeanException ambiguousBeanException =
                    catchThrowableOfType(() -> DefaultBeanContainerTest.this.container.factory(String.class),
                            AmbiguousBeanException.class);
            assertThat(ambiguousBeanException).hasMessageStartingWith(
                    "Ambiguous preferred bean of specific type " + "found.");
        }
    }

    @Test
    @DisplayName("释放对象占用的资源，释放成功")
    void disposeResourceThenExecuteSuccessfully() {
        Bean bean = new Bean(110L);
        this.container.register(bean);
        assertDoesNotThrow(() -> this.container.dispose0());
    }

    @Test
    @DisplayName("调用 onDisposed() 方法，执行成功")
    void invokeOnDisposedMethodThenExecuteSuccessfully() {
        assertDoesNotThrow(() -> this.container.dispose());
    }
}
