/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.build.util;

import modelengine.fitframework.maven.MavenCoordinate;
import modelengine.fitframework.protocol.jar.Jar;
import modelengine.fitframework.protocol.jar.JarLocation;
import modelengine.fitframework.util.ClassUtils;
import modelengine.fitframework.util.StringUtils;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.IOException;
import java.net.URL;

/**
 * 为版本提供工具方法。
 *
 * @author 梁济时
 * @since 2023-02-03
 */
public final class VersionHelper {
    /**
     * 读取当前的运行时版本。
     *
     * @return 表示运行时版本的 {@link String}。
     * @throws MojoExecutionException 当读取过程中发生输入输出异常时。
     */
    public static String read() throws MojoExecutionException {
        URL url = ClassUtils.locateOfProtectionDomain(VersionHelper.class);
        JarLocation location = JarLocation.parse(url);
        return read(location);
    }

    private static String read(JarLocation location) throws MojoExecutionException {
        Jar jar;
        try {
            jar = Jar.from(location.file());
            for (String nest : location.nests()) {
                jar = jar.entries().get(nest).asJar();
            }
        } catch (IOException ex) {
            throw new MojoExecutionException(StringUtils.format("Failed to load JAR. [location={0}]", location), ex);
        }
        return read(jar);
    }

    private static String read(Jar jar) throws MojoExecutionException {
        MavenCoordinate coordinate;
        try {
            coordinate = MavenCoordinate.read(jar);
        } catch (IOException ex) {
            throw new MojoExecutionException(StringUtils.format("Failed to load maven coordinate in JAR. [jar={0}]",
                    jar.location()), ex);
        }
        if (coordinate == null) {
            throw new MojoExecutionException(StringUtils.format("No maven file found in JAR. [jar={0}]", jar));
        } else {
            return coordinate.version();
        }
    }
}
