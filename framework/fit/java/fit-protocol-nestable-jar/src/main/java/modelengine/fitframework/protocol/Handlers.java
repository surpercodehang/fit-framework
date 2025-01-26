/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 为协议的处理程序提供工具方法。
 *
 * @author 梁济时
 * @since 2022-09-25
 */
public final class Handlers {
    private static final String PROTOCOL_PACKAGE_PROPERTY_KEY = "java.protocol.handler.pkgs";
    private static final String PROTOCOL_PACKAGE_SEPARATOR = "|";
    private static final String PROTOCOL_PACKAGE_SEPARATOR_REGEX = "\\|";
    private static final String CURRENT_PACKAGE;

    static {
        String className = Handlers.class.getName();
        CURRENT_PACKAGE = className.substring(0, className.lastIndexOf('.'));
    }

    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private Handlers() {}

    /**
     * 将当前包注册到 {@code java.protocol.handler.pkgs} 系统属性中。
     */
    public static void register() {
        URL.setURLStreamHandlerFactory(null);
        List<String> existingPackages = obtainExistingPackages();
        List<String> packages = new ArrayList<>(existingPackages.size() + 1);
        packages.add(CURRENT_PACKAGE);
        for (String existingPackage : existingPackages) {
            String actual = existingPackage.trim();
            if (!actual.isEmpty()) {
                packages.add(actual);
            }
        }
        String property = String.join(PROTOCOL_PACKAGE_SEPARATOR, packages);
        System.setProperty(PROTOCOL_PACKAGE_PROPERTY_KEY, property);
    }

    private static List<String> obtainExistingPackages() {
        String property = System.getProperty(PROTOCOL_PACKAGE_PROPERTY_KEY);
        if (property == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(property.split(PROTOCOL_PACKAGE_SEPARATOR_REGEX));
    }
}
