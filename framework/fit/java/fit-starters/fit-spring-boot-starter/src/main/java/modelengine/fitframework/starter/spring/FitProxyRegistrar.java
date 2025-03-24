/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.starter.spring;

import static modelengine.fitframework.inspection.Validation.notBlank;

import modelengine.fitframework.annotation.Genericable;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.starter.spring.annotation.EnableFitProxy;
import modelengine.fitframework.util.StringUtils;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.util.Arrays;

/**
 * 表示 FIT 定义的动态代理注册器。
 *
 * @author 杭潇
 * @author 季聿阶
 * @since 2025-02-22
 */
public class FitProxyRegistrar implements ImportBeanDefinitionRegistrar {
    private static final String BASE_PACKAGES = "basePackages";
    private static final String BROKER_CLIENT = "brokerClient";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
            @Nonnull BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttributes =
                AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableFitProxy.class.getName()));
        String[] basePackages = this.getBasePackages(annotationAttributes);
        Class<?>[] targetInterfaces = this.scanInterfaces(basePackages);
        for (Class<?> targetInterface : targetInterfaces) {
            this.registerInterfaceBeanDefinition(registry, targetInterface);
        }
    }

    private String[] getBasePackages(AnnotationAttributes annotationAttributes) {
        if (annotationAttributes != null) {
            String[] basePackages = annotationAttributes.getStringArray(BASE_PACKAGES);
            if (basePackages.length > 0) {
                return basePackages;
            }
        }
        return new String[0];
    }

    private Class<?>[] scanInterfaces(String... basePackages) {
        ClassPathScanningCandidateComponentProvider scanner = this.createInterfaceScanner();
        return Arrays.stream(basePackages)
                .flatMap(pkg -> scanner.findCandidateComponents(pkg).stream())
                .map(this::loadClass)
                .filter(this::hasGenericableMethod)
                .toArray(Class[]::new);
    }

    private ClassPathScanningCandidateComponentProvider createInterfaceScanner() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                // 过滤条件：仅允许独立类（非内部类/嵌套类，可独立实例化）。
                return beanDefinition.getMetadata().isIndependent();
            }
        };
        scanner.addIncludeFilter((metadataReader, factory) -> metadataReader.getClassMetadata().isInterface());
        return scanner;
    }

    @SuppressWarnings("DataFlowIssue")
    private Class<?> loadClass(BeanDefinition beanDefinition) {
        String beanClassName = notBlank(beanDefinition.getBeanClassName(), "The bean class name cannot be blank.");
        try {
            return ClassUtils.forName(beanClassName, FitProxyRegistrar.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(StringUtils.format("Failed to load the interface class. [class={0}]",
                    beanClassName), e);
        }
    }

    private boolean hasGenericableMethod(Class<?> interfaceType) {
        return Arrays.stream(interfaceType.getMethods())
                .anyMatch(method -> method.isAnnotationPresent(Genericable.class));
    }

    private void registerInterfaceBeanDefinition(BeanDefinitionRegistry registry, Class<?> interfaceType) {
        // 创建一个通用的 Bean 定义对象 (BeanDefinition)，用于描述如何实例化 Bean。
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        // 指定实际负责创建 Bean 的工厂类，原因是接口无法直接实例化，需要通过 FactoryBean 的 getObject() 生成代理对象。
        beanDefinition.setBeanClassName(FitProxyFactoryBean.class.getName());
        // 构造参数顺序必须与 FitProxyFactoryBean 构造函数一致。
        // 参数1：BrokerClient 的 Bean 的引用。
        beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(new RuntimeBeanReference(BROKER_CLIENT));
        // 参数2：接口的全限定类名，Spring 会将其解析为 Class 对象。
        beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(interfaceType.getName());
        // 使用全限定类名作为 Bean 名称，避免不同包且同名的接口冲突。
        String beanName = interfaceType.getName();
        if (registry.containsBeanDefinition(beanName)) {
            return;
        }
        // 将 beanDefinition 注册到 Spring 容器中，使得容器后续能实例化该 Bean。
        registry.registerBeanDefinition(beanName, beanDefinition);
    }
}
