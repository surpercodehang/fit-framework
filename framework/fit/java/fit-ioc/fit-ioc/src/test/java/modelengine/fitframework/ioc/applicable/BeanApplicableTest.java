/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.applicable;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.ioc.AbstractBeanContainerTest;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.applicable.bean.AnywhereBean;
import modelengine.fitframework.ioc.applicable.bean.ChildrenBean;
import modelengine.fitframework.ioc.applicable.bean.CurrentBean;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.runtime.FitRuntime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@DisplayName("测试 Bean 的可用范围")
class BeanApplicableTest extends AbstractBeanContainerTest {
    private BeanContainer container;

    @BeforeEach
    void setup() {
        this.container = container();
    }

    private static void assertApplicable(BeanContainer container, Class<?> beanClass) {
        Optional<BeanFactory> factory = container.lookup(beanClass);
        assertTrue(factory.isPresent());

        List<BeanFactory> factories = container.all(beanClass);
        assertFalse(factories.isEmpty());
    }

    private static void assertNotApplicable(BeanContainer container, Class<?> beanClass) {
        Optional<BeanFactory> factory = container.lookup(beanClass);
        assertFalse(factory.isPresent());

        List<BeanFactory> factories = container.all(beanClass);
        assertTrue(factories.isEmpty());
    }

    private static BeanContainer child(BeanContainer parent) {
        return children(parent, 1).get(0);
    }

    private static List<BeanContainer> children(BeanContainer parent, int count) {
        Plugin plugin = parent.plugin();
        FitRuntime runtime = plugin.runtime();
        List<Plugin> children = new ArrayList<>(count);
        List<BeanContainer> containers = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Plugin childPlugin = mock(Plugin.class);
            when(childPlugin.parent()).thenReturn(plugin);
            when(childPlugin.children()).thenReturn(EMPTY_PLUGIN_COLLECTION);
            when(childPlugin.runtime()).thenReturn(runtime);
            BeanContainer child = container(childPlugin);
            when(childPlugin.container()).thenReturn(child);
            children.add(childPlugin);
            containers.add(child);
        }
        when(plugin.children()).thenReturn(plugins(children));
        return containers;
    }

    private static Collection<Plugin> append(Collection<Plugin> plugins, Plugin plugin) {
        List<Plugin> results = new ArrayList<>(plugins.size() + 1);
        results.addAll(plugins);
        results.add(plugin);
        return results;
    }

    @Nested
    @DisplayName("测试 ANYWHERE 可用范围")
    class AnywhereTest {
        private final Class<?> beanClass = AnywhereBean.class;

        @Test
        @DisplayName("当 Bean 在当前容器中时可用")
        void should_applicable_when_in_current_container() {
            container.registry().register(this.beanClass);

            assertApplicable(container, this.beanClass);
        }

        @Test
        @DisplayName("当 Bean 在子容器中时可用")
        void should_applicable_when_in_child_container() {
            BeanContainer child = child(container);
            child.registry().register(this.beanClass);

            assertApplicable(container, this.beanClass);
        }

        @Test
        @DisplayName("当 Bean 在父容器中时可用")
        void should_applicable_when_in_parent_container() {
            BeanContainer child = child(container);
            container.registry().register(this.beanClass);

            assertApplicable(child, this.beanClass);
        }

        @Test
        @DisplayName("当 Bean 在兄弟容器中时可用")
        void should_applicable_when_in_brother_container() {
            List<BeanContainer> children = children(container, 2);
            BeanContainer current = children.get(0);
            BeanContainer brother = children.get(1);
            brother.registry().register(this.beanClass);

            assertApplicable(current, this.beanClass);
        }
    }

    @Nested
    @DisplayName("测试 CHILDREN 可用范围")
    class ChildrenTest {
        private final Class<?> beanClass = ChildrenBean.class;

        @Test
        @DisplayName("当 Bean 在当前容器中时可用")
        void should_applicable_when_in_current_container() {
            container.registry().register(this.beanClass);

            assertApplicable(container, this.beanClass);
        }

        @Test
        @DisplayName("当 Bean 在子容器中时可用")
        void should_applicable_when_in_child_container() {
            BeanContainer child = child(container);
            child.registry().register(this.beanClass);

            assertApplicable(container, this.beanClass);
        }

        @Test
        @DisplayName("当 Bean 在父容器中时不可用")
        void should_applicable_when_in_parent_container() {
            BeanContainer child = child(container);
            container.registry().register(this.beanClass);

            assertNotApplicable(child, this.beanClass);
        }

        @Test
        @DisplayName("当 Bean 在兄弟容器中时不可用")
        void should_applicable_when_in_brother_container() {
            List<BeanContainer> children = children(container, 2);
            BeanContainer current = children.get(0);
            BeanContainer brother = children.get(1);
            brother.registry().register(this.beanClass);

            assertNotApplicable(current, this.beanClass);
        }
    }

    @Nested
    @DisplayName("测试 CURRENT 可用范围")
    class CurrentTest {
        private final Class<?> beanClass = CurrentBean.class;

        @Test
        @DisplayName("当 Bean 在当前容器中时可用")
        void should_applicable_when_in_current_container() {
            container.registry().register(this.beanClass);

            assertApplicable(container, this.beanClass);
        }

        @Test
        @DisplayName("当 Bean 在子容器中时不可用")
        void should_applicable_when_in_child_container() {
            BeanContainer child = child(container);
            child.registry().register(this.beanClass);

            assertNotApplicable(container, this.beanClass);
        }

        @Test
        @DisplayName("当 Bean 在父容器中时不可用")
        void should_applicable_when_in_parent_container() {
            BeanContainer child = child(container);
            container.registry().register(this.beanClass);

            assertNotApplicable(child, this.beanClass);
        }

        @Test
        @DisplayName("当 Bean 在兄弟容器中时不可用")
        void should_applicable_when_in_brother_container() {
            List<BeanContainer> children = children(container, 2);
            BeanContainer current = children.get(0);
            BeanContainer brother = children.get(1);
            brother.registry().register(this.beanClass);

            assertNotApplicable(current, this.beanClass);
        }
    }
}
