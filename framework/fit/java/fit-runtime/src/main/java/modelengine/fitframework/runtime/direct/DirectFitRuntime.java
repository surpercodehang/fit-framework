/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.runtime.direct;

import modelengine.fitframework.plugin.RootPlugin;
import modelengine.fitframework.runtime.FitStarter;
import modelengine.fitframework.runtime.shared.SharedUrlClassLoader;
import modelengine.fitframework.runtime.support.AbstractFitRuntime;
import modelengine.fitframework.util.ClassUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 为 FIT 运行时提供直接调用启动场景的实现。
 * <p>直接调用启动指的是在 IDEA 中运行启动。</p>
 *
 * @author 梁济时
 * @since 2023-02-07
 */
public final class DirectFitRuntime extends AbstractFitRuntime {
    private final SharedUrlClassLoader sharedClassLoader;

    /**
     * 使用入口类和命令行参数来初始化 {@link AbstractFitRuntime} 类的新实例。
     *
     * @param entry 表示入口类的 {@link Class}{@code <?>}。
     * @param args 表示命令行参数的 {@link String}{@code []}。
     */
    public DirectFitRuntime(Class<?> entry, String[] args) {
        super(entry, args);
        this.sharedClassLoader = DirectSharedClassLoader.create();
    }

    @Override
    protected URL locateRuntime() {
        Method method = Stream.of(FitStarter.class.getDeclaredMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()))
                .filter(m -> Modifier.isStatic(m.getModifiers()))
                .filter(m -> m.getParameterCount() == 2)
                .filter(m -> m.getParameters()[0].getType() == Class.class)
                .filter(m -> m.getParameters()[1].getType() == String[].class)
                .findAny()
                .orElseThrow(() -> new IllegalStateException(StringUtils.format("Entry method not found in {0} class.",
                        FitStarter.class.getName())));
        StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        int index = 0;
        while (index < traces.length) {
            StackTraceElement trace = traces[index];
            if (Objects.equals(trace.getClassName(), method.getDeclaringClass().getName())
                    && Objects.equals(trace.getMethodName(), method.getName())) {
                break;
            } else {
                index++;
            }
        }
        if (++index >= traces.length) {
            throw new IllegalStateException(StringUtils.format(
                    "The method to call the {0} not found in stack traces of the current thread.",
                    FitStarter.class.getName()));
        }
        String callerClassName = traces[index].getClassName();
        Class<?> callerClass = ClassUtils.tryLoadClass(DirectFitRuntime.class.getClassLoader(), callerClassName);
        if (callerClass == null) {
            callerClass = ClassUtils.tryLoadClass(Thread.currentThread().getContextClassLoader(), callerClassName);
        }
        if (callerClass == null) {
            throw new IllegalStateException(StringUtils.format(
                    "The caller class not found in class loader. [callerClass={0}]",
                    callerClassName));
        }
        return ClassUtils.locateOfProtectionDomain(callerClass);
    }

    @Override
    protected SharedUrlClassLoader obtainSharedClassLoader() {
        return this.sharedClassLoader;
    }

    @Override
    protected RootPlugin createRootPlugin() {
        return new DirectRootPlugin(this);
    }
}
