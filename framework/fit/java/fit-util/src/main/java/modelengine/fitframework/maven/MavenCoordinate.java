/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.maven;

import modelengine.fitframework.maven.support.DefaultMavenCoordinate;
import modelengine.fitframework.protocol.jar.Jar;

import java.io.IOException;
import java.io.InputStream;

/**
 * 表示 Maven 坐标。
 *
 * @author 梁济时
 * @since 2022-10-18
 */
public interface MavenCoordinate {
    /** 表示 pom 文件的名字。 */
    String POM_FILE_NAME = "pom.xml";

    /**
     * 表示归档件的分组名称。
     *
     * @return 表示分组名称的 {@link String}。
     */
    String groupId();

    /**
     * 获取归档件的名称。
     *
     * @return 表示归档件名称的 {@link String}。
     */
    String artifactId();

    /**
     * 获取归档件的版本。
     *
     * @return 表示归档件版本的 {@link String}。
     */
    String version();

    /**
     * 使用制成件的分组标识、制成件标识及版本创建
     *
     * @param groupId 表示制成件分组标识的 {@link String}。
     * @param artifactId 表示制成件标识的 {@link String}。
     * @param version 表示制成件版本信息的 {@link String}。
     * @return 表示制成件坐标的 {@link MavenCoordinate}。
     */
    static MavenCoordinate create(String groupId, String artifactId, String version) {
        return new DefaultMavenCoordinate(groupId, artifactId, version);
    }

    /**
     * 从指定字符串中解析 Maven 坐标信息。
     *
     * @param text 表示包含 Maven 坐标信息的字符串的 {@link String}。
     * @return 表示从字符串中解析到的 Maven 坐标的 {@link DefaultMavenCoordinate}。
     * @throws IllegalArgumentException 当 {@code text} 为 {@code null} 或格式不正确时。
     */
    static MavenCoordinate parse(String text) {
        return DefaultMavenCoordinate.parse(text);
    }

    /**
     * 从指定 JAR 中读取 Maven 坐标。
     *
     * @param jar 表示待读取 Maven 坐标的 JAR 的 {@link Jar}。
     * @return 若 JAR 中存在 Maven 的 {@code pom.xml} 文件，则为表示从其中读取到的坐标信息的 {@link MavenCoordinate}，否则为
     * {@code null}。
     * @throws IllegalArgumentException {@code jar} 为 {@code null}。
     * @throws IllegalStateException {@code pom.xml} 的格式不正确。
     * @throws IOException 当读取过程中发生输入输出异常时。
     */
    static MavenCoordinate read(Jar jar) throws IOException {
        return MavenCoordinateReader.read(jar);
    }

    /**
     * 从指定的输入流中加载 Maven 坐标。
     *
     * @param in 表示指定输入流的 {@link InputStream}。
     * @return 表示加载得到的 Maven 坐标的 {@link MavenCoordinate}。
     * @throws IOException 当读取过程中发生输入输出异常时。
     */
    static MavenCoordinate load(InputStream in) throws IOException {
        return MavenCoordinateReader.read(in);
    }
}
