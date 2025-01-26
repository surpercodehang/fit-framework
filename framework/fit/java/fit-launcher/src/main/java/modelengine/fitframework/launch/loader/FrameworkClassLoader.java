/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.launch.loader;

import java.io.File;
import java.net.URL;

/**
 * 为FIT框架提供类加载程序。
 *
 * @author 梁济时
 * @since 2022-06-29
 */
public class FrameworkClassLoader extends AbstractClassLoader {
    private FrameworkClassLoader(URL[] urls, ClassLoader sharedClassLoader) {
        super(urls, sharedClassLoader);
    }

    /**
     * 使用 FIT 根目录及公共类加载器创建 FIT 框架类加载器。
     *
     * @param home 表示 FIT 根目录的 {@link File}。
     * @param sharedClassLoader 表示公共类加载程序的 {@link ClassLoader}。
     * @return 表示创建出来的框架类加载器的 {@link FrameworkClassLoader}。
     */
    public static FrameworkClassLoader create(File home, ClassLoader sharedClassLoader) {
        URL[] jars = jars(new File(home, "lib"), new File(home, "third-party"));
        return new FrameworkClassLoader(jars, sharedClassLoader);
    }
}
