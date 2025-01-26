/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.runtime.shared;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * 表示 {@link URLClassLoader} 的共享类加载器的实现。
 *
 * @author 季聿阶
 * @since 2024-09-14
 */
public class SharedUrlClassLoader extends URLClassLoader {
    public SharedUrlClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
}
