/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven;

import modelengine.fitframework.plugin.maven.support.PomPropertiesEntryResolver;
import modelengine.fitframework.plugin.maven.support.PomXmlEntryResolver;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 为归档件提供坐标解析程序。
 *
 * @author 梁济时
 * @since 2020-11-26
 */
public class JarMavenCoordinateResolver {
    /** 表示 maven 文件在 jar 包中的目录。 */
    public static final String MAVEN_ENTRY_PREFIX = "META-INF/maven/";

    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private JarMavenCoordinateResolver() {}

    /**
     * 解析jar包返回maven坐标
     *
     * @param jarFile 表示已经压缩的 {@link File}
     * @return Maven归档件的坐标实体 {@link MavenCoordinate}
     */
    public static MavenCoordinate resolve(File jarFile) {
        return JarEntryResolver.resolve(jarFile, JarMavenCoordinateResolver::resolvers).orElse(null);
    }

    private static List<JarEntryResolver<MavenCoordinate>> resolvers() {
        return Arrays.asList(PomXmlEntryResolver.INSTANCE, PomPropertiesEntryResolver.INSTANCE);
    }
}
