/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.transaction.transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.aop.AopInterceptor;
import modelengine.fitframework.aop.interceptor.aspect.interceptor.AspectInterceptorResolver;
import modelengine.fitframework.aop.proxy.AopProxyFactories;
import modelengine.fitframework.aop.proxy.bytebuddy.ByteBuddyAopProxyFactory;
import modelengine.fitframework.aop.proxy.support.JdkDynamicAopProxyFactory;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.annotation.support.DefaultAnnotationMetadataResolver;
import modelengine.fitframework.ioc.support.DefaultBeanContainer;
import modelengine.fitframework.ioc.support.DefaultBeanResolver;
import modelengine.fitframework.ioc.support.DefaultDependencyResolver;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.plugin.PluginCollection;
import modelengine.fitframework.plugin.PluginMetadata;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.transaction.Transaction;
import modelengine.fitframework.transaction.TransactionIsolationLevel;
import modelengine.fitframework.transaction.TransactionManager;
import modelengine.fitframework.transaction.TransactionPropagationPolicy;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.transaction.aspect.TransactionAspect;
import modelengine.fitframework.transaction.support.DefaultTransactionManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;

import javax.sql.DataSource;

/**
 * 为 {@link Transactional} 提供单元测试。
 *
 * @author 梁济时
 * @since 2022-08-27
 */
@DisplayName("测试 Transactional 注解")
class TransactionalTest {
    /**
     * 测试 {@link Transactional} 注解的行为。
     */
    @Component
    public static class TestService {
        @Transactional(propagation = TransactionPropagationPolicy.REQUIRES_NEW,
                isolation = TransactionIsolationLevel.READ_COMMITTED)
        public void run(Runnable action) {
            action.run();
        }
    }

    /**
     * 创建一个 {@link BeanContainer} 实例，用于测试。
     *
     * @throws SQLException 如果无法获取数据库连接。
     */
    @Test
    @DisplayName("执行被 @Transactional 注解修饰的方法，在事务中执行")
    void should_execute_in_transaction() throws SQLException {
        BeanContainer container = container();
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);

        TransactionManager transactionManager = new DefaultTransactionManager(container);
        container.registry().register(container);

        container.registry().register(new AopProxyFactories());
        container.registry().register(new AopInterceptor(container));
        container.registry().register(AspectInterceptorResolver.class);
        container.registry().register(new JdkDynamicAopProxyFactory());
        container.registry().register(new ByteBuddyAopProxyFactory());

        container.registry().register(TransactionAspect.class);
        container.registry().register(dataSource);
        container.registry().register(transactionManager);
        container.registry().register(TestService.class);

        TestService service = container.beans().get(TestService.class);
        assertNull(transactionManager.active());
        service.run(() -> {
            Transaction transaction = transactionManager.active();
            assertNotNull(transaction);
            assertEquals(TransactionPropagationPolicy.REQUIRES_NEW, transaction.metadata().propagation());
            assertEquals(TransactionIsolationLevel.READ_COMMITTED, transaction.metadata().isolation());
        });
        assertNull(transactionManager.active());
    }

    /**
     * 创建一个 {@link BeanContainer} 实例，用于测试。
     *
     * @return 表示创建的实例的  {@link BeanContainer}。
     */
    protected static BeanContainer container() {
        FitRuntime runtime = mock(FitRuntime.class);
        when(runtime.resolverOfBeans()).thenReturn(new DefaultBeanResolver());
        when(runtime.resolverOfDependencies()).thenReturn(new DefaultDependencyResolver());
        when(runtime.resolverOfAnnotations()).thenReturn(new DefaultAnnotationMetadataResolver());
        Plugin plugin = mock(Plugin.class);
        PluginMetadata metadata = mock(PluginMetadata.class);
        when(metadata.group()).thenReturn("modelengine.fitframework");
        when(metadata.name()).thenReturn("test");
        when(metadata.version()).thenReturn("1.0");
        when(plugin.metadata()).thenReturn(metadata);
        when(plugin.runtime()).thenReturn(runtime);
        BeanContainer container = new DefaultBeanContainer(plugin);
        when(plugin.container()).thenReturn(container);
        PluginCollection emptyPluginCollection = mock(PluginCollection.class);
        when(emptyPluginCollection.iterator()).thenReturn(Collections.emptyIterator());
        when(plugin.children()).thenReturn(emptyPluginCollection);
        return container;
    }
}
