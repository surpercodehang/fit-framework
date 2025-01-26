/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.launch;

import modelengine.fitframework.launch.loader.FrameworkClassLoader;
import modelengine.fitframework.launch.loader.SharedClassLoader;
import modelengine.fitframework.protocol.Handlers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.util.Locale;

/**
 * 为 FIT 应用提供启动程序。
 *
 * @author 梁济时
 * @since 2022-06-10
 */
public class DiscreteFitLauncher {
    public static void main(String[] args) throws Throwable {
        Handlers.register();
        File home = home();
        URLClassLoader sharedClassLoader = SharedClassLoader.create(home);
        URLClassLoader frameworkClassLoader = FrameworkClassLoader.create(home, sharedClassLoader);
        Class<?> clazz = frameworkClassLoader.loadClass("modelengine.fitframework.runtime.FitStarter");
        Method method = clazz.getDeclaredMethod("start", Class.class, String[].class);
        Thread.currentThread().setContextClassLoader(frameworkClassLoader);
        try {
            method.invoke(null, null, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    private static File home() {
        CodeSource source = DiscreteFitLauncher.class.getProtectionDomain().getCodeSource();
        if (source == null) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "Failed to obtain code source of entry class. [entry=%s]",
                    DiscreteFitLauncher.class.getName()));
        }
        URL location = source.getLocation();
        if (location == null) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "Failed to locate launcher JAR. [entry=%s]",
                    DiscreteFitLauncher.class.getName()));
        }
        URI uri;
        try {
            uri = location.toURI();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "Failed to obtain URI of protection domain. [url=%s]",
                    location.toExternalForm()), e);
        }
        File jar = new File(uri);
        try {
            jar = jar.getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to canonicalize file of launcher jar.", e);
        }
        if (!jar.isFile()) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "The protection domain is not a file. [url=%s]",
                    location.toExternalForm()));
        }
        return jar.getParentFile();
    }
}
