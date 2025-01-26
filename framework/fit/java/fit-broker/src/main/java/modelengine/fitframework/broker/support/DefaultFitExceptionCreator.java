/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.support;

import modelengine.fitframework.broker.ExceptionInfo;
import modelengine.fitframework.broker.FitExceptionCreator;
import modelengine.fitframework.exception.Errors;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.plugin.PluginStartedObserver;
import modelengine.fitframework.plugin.PluginStoppedObserver;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.runtime.FitRuntimeStartedObserver;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.XmlUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 表示 {@link FitExceptionCreator} 的默认实现。
 *
 * @author 何天放
 * @since 2024-05-11
 */
public class DefaultFitExceptionCreator
        implements FitExceptionCreator, PluginStartedObserver, PluginStoppedObserver, FitRuntimeStartedObserver {
    private static final Logger log = Logger.get(DefaultFitExceptionCreator.class);
    private static final String ERRORS_RESOURCE_FILE = "FIT-INF/errors.xml";

    private final BeanContainer container;
    private volatile Map<Integer, Class<? extends FitException>> errorCodes = new HashMap<>();

    /**
     * 通过 Bean 容器构建具有默认实现的 {@link FitExceptionCreator}。
     *
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     */
    public DefaultFitExceptionCreator(BeanContainer container) {
        this.container = container;
    }

    @Override
    public void onPluginStarted(Plugin plugin) {
        this.updateErrorCodes();
    }

    @Override
    public void onPluginStopped(Plugin plugin) {
        this.updateErrorCodes();
    }

    @Override
    public void onRuntimeStarted(FitRuntime runtime) {
        this.updateErrorCodes();
    }

    @Override
    public FitException buildException(ExceptionInfo exceptionInfo) {
        Optional<Class<? extends FitException>> opClass = Errors.exceptionClass(exceptionInfo.code());
        FitException exception;
        if (opClass.isPresent()) {
            Constructor<? extends FitException> declaredConstructor =
                    ReflectionUtils.getDeclaredConstructor(opClass.get(), String.class);
            exception = ReflectionUtils.instantiate(declaredConstructor, exceptionInfo.message());
        } else {
            if (this.errorCodes.containsKey(exceptionInfo.code())) {
                Class<? extends FitException> responseExceptionClass = this.errorCodes.get(exceptionInfo.code());
                Constructor<? extends FitException> declaredConstructor =
                        ReflectionUtils.getDeclaredConstructor(responseExceptionClass, String.class);
                exception = ReflectionUtils.instantiate(declaredConstructor, exceptionInfo.message());
            } else {
                exception = new FitException(exceptionInfo.code(), exceptionInfo.message());
            }
        }
        exception.setProperties(exceptionInfo.properties());
        exception.associateFitable(exceptionInfo.genericableId(), exceptionInfo.fitableId());
        return exception;
    }

    private void updateErrorCodes() {
        try {
            this.errorCodes = this.getErrorCodesFromResources();
        } catch (IOException e) {
            throw new IllegalStateException(StringUtils.format("Failed to read errors from resources. [file={0}]",
                    ERRORS_RESOURCE_FILE), e);
        }
    }

    private Map<Integer, Class<? extends FitException>> getErrorCodesFromResources() throws IOException {
        Map<Integer, Class<? extends FitException>> exceptions = new HashMap<>();
        Enumeration<URL> resources = this.container.runtime().sharedClassLoader().getResources(ERRORS_RESOURCE_FILE);
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            try (InputStream in = url.openStream()) {
                exceptions.putAll(this.readInputStream(in));
            }
        }
        return exceptions;
    }

    private Map<Integer, Class<? extends FitException>> readInputStream(InputStream in) {
        Document xml = XmlUtils.load(in);
        Map<Integer, Class<? extends FitException>> exceptions = new HashMap<>();
        Element root = XmlUtils.child(xml, "errors");
        if (root == null) {
            return exceptions;
        }
        NodeList errorNodes = XmlUtils.filterByName(root.getChildNodes(), "error");
        for (int i = 0; i < errorNodes.getLength(); i++) {
            Node errorNode = errorNodes.item(i);
            Element errorElement = ObjectUtils.cast(errorNode);
            int code = Integer.parseInt(errorElement.getAttribute("code"));
            String className = errorElement.getAttribute("class");
            Class<? extends FitException> fitExceptionClass;
            try {
                fitExceptionClass = ObjectUtils.cast(this.container.runtime().sharedClassLoader().loadClass(className));
            } catch (ClassNotFoundException e) {
                log.warn("Failed to load exception class, use FitException instead. [code={}, class={}]",
                        code,
                        className);
                fitExceptionClass = FitException.class;
            }
            exceptions.put(code, fitExceptionClass);
        }
        return exceptions;
    }
}
