/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.ioc.BeanApplicableScope;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;
import modelengine.fitframework.ioc.lifecycle.bean.BeanCreator;
import modelengine.fitframework.ioc.lifecycle.bean.BeanDecorator;
import modelengine.fitframework.ioc.lifecycle.bean.BeanDestroyer;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInitializer;
import modelengine.fitframework.ioc.lifecycle.bean.BeanInjector;
import modelengine.fitframework.ioc.lifecycle.bean.BeanLifecycle;
import modelengine.fitframework.ioc.lifecycle.bean.support.DefaultBeanLifecycle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 表示 {@link PrototypeBeanFactory} 的单元测试。
 *
 * @author 杭潇
 * @since 2023-02-28
 */
@DisplayName("测试 PrototypeBeanFactory 类")
public class PrototypeBeanFactoryTest {
    private PrototypeBeanFactory prototypeBeanFactory;

    @BeforeEach
    void setup() {
        BeanContainer container = mock(BeanContainer.class);
        List<BeanFactory> list = new ArrayList<>();
        list.add(mock(BeanFactory.class));
        when(container.all(List.class)).thenReturn(list);
        Set<String> strings = new HashSet<>();
        strings.add("testSet");
        BeanApplicableScope applicable = BeanApplicableScope.INSENSITIVE;
        AnnotationMetadata annotations = mock(AnnotationMetadata.class);
        Config config = mock(Config.class);
        BeanMetadata metadata =
                new DefaultBeanMetadata(container, "testBeanMetaData", null, String.class.getGenericSuperclass(),
                        "testStereotype", false, false, strings, applicable, annotations, config);
        BeanCreator creator = mock(BeanCreator.class);
        BeanDecorator decorator = mock(BeanDecorator.class);
        BeanInjector injector = mock(BeanInjector.class);
        BeanInitializer initializer = mock(BeanInitializer.class);
        BeanDestroyer destroyer = mock(BeanDestroyer.class);
        BeanLifecycle lifecycle =
                new DefaultBeanLifecycle(metadata, creator, decorator, injector, initializer, destroyer);
        this.prototypeBeanFactory = new PrototypeBeanFactory(lifecycle);
    }

    @Test
    @DisplayName("获取不存在的 Bean 实例，返回值为 null")
    void getNotExistBeanInstanceThenReturnIsNull() {
        Object[] arguments = {"testBeanMetaData"};
        Object prototypeBeanFactory0 = this.prototypeBeanFactory.get0(arguments);
        assertThat(prototypeBeanFactory0).isNull();
    }

    @Test
    @DisplayName("调用 toString() 方法，返回值与给定参数值相等")
    void invokeToStringThenReturnParametersAreEqualsToTheGivenValue() {
        String toString = this.prototypeBeanFactory.toString();
        String split = toString.split(",")[0].split("=")[1];
        assertThat(split).isEqualTo("testBeanMetaData");
    }

    @Test
    @DisplayName("销毁指定 Bean，执行成功")
    void destroyBeanThenExecuteSuccessfully() {
        assertDoesNotThrow(() -> this.prototypeBeanFactory.destroy("testBeanMetaData"));
    }
}
