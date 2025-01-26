/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.support;

import modelengine.fitframework.jvm.scan.PackageScanner;
import modelengine.fitframework.jvm.scan.support.ClassLoaderPackageScanner;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

/**
 * 表示 {@link ClassLoaderPackageScanner} 的插件类加载的实现。
 *
 * @author 季聿阶
 * @since 2023-09-01
 */
public class PluginClassLoaderScanner extends ClassLoaderPackageScanner {
    public PluginClassLoaderScanner(ClassLoader loader, PackageScanner.Callback callback) {
        super(loader, callback);
    }

    @Override
    protected Enumeration<URL> getPackageResources(String basePackage, String resourceName) {
        if (this.getLoader() instanceof PluginClassLoader) {
            PluginClassLoader pluginClassLoader = ObjectUtils.cast(this.getLoader());
            try {
                return pluginClassLoader.findResources(resourceName);
            } catch (IOException e) {
                throw new IllegalStateException(StringUtils.format(
                        "Failed to obtain resources for package. [package={0}, resource={1}]",
                        basePackage,
                        resourceName), e);
            }
        } else {
            return super.getPackageResources(basePackage, resourceName);
        }
    }
}
