/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.jvm.classfile.lang.U2;
import modelengine.fitframework.util.Convert;
import modelengine.fitframework.util.IoUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * {@link ClassFileVersion} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-01-31
 */
@DisplayName("测试 ClassFileVersion 类")
class ClassFileVersionTest {
    private ClassFileVersion classFileVersion;

    @BeforeEach
    @DisplayName("初始化 ClassFileVersion 类")
    void init() throws IOException {
        String obj = "java/lang/Object.class";
        try (InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(obj)) {
            ClassFile classFile = new ClassFile(in);
            this.classFileVersion = classFile.version();
        }
    }

    @Test
    @DisplayName("当提供 classFile 文件时，返回 class 文件版本号")
    void givenClassFileThenReturnVersion() throws IOException {
        int minor = this.classFileVersion.minor().intValue();
        int major = this.classFileVersion.major().intValue();
        String obj = "java/lang/String.class";
        try (InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(obj)) {
            // 先读取4为幻数
            IoUtils.read(in, 4);
            int minorVersion = Convert.toInteger(IoUtils.read(in, 2));
            assertThat(minorVersion).isEqualTo(minor);
            int majorVersion = Convert.toInteger(IoUtils.read(in, 2));
            assertThat(majorVersion).isEqualTo(major);
        }
    }

    @Test
    @DisplayName("当提供 classFile 文件时，写入 class 文件版本号")
    void givenClassFileThenReturnWriteVersion() throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            this.classFileVersion.write(out);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            U2 minor = U2.read(in);
            assertThat(minor).isEqualTo(this.classFileVersion.minor());
            U2 major = U2.read(in);
            assertThat(major).isEqualTo(this.classFileVersion.major());
        }
    }

    @Test
    @DisplayName("测试 ClassFileVersion 类 equals 方法创建新对象比较时，返回正常结果")
    void givenClassFileVersionThenReturnCreateEquals() {
        int major = this.classFileVersion.major().intValue();
        int minor = this.classFileVersion.minor().intValue();
        ClassFileVersion newVersion = ClassFileVersion.of(major, minor);
        assertThat(this.classFileVersion.equals(newVersion)).isTrue();
    }

    @Test
    @DisplayName("测试 ClassFileVersion 类 equals 方法空比较时，返回正常结果")
    void givenClassFileVersionThenReturnNullEquals() {
        assertThat(this.classFileVersion.equals(null)).isFalse();
    }

    @Test
    @DisplayName("测试 ClassFileVersion 类 equals 方法本身比较时，返回正常结果")
    void givenClassFileVersionThenReturnSelfEquals() {
        ClassFileVersion tmp = this.classFileVersion;
        assertThat(this.classFileVersion.equals(tmp)).isTrue();
    }

    @Test
    @DisplayName("测试 ClassFileVersion 类 equals 方法其他对象比较时，返回正常结果")
    void givenClassFileVersionThenReturnOtherEquals() {
        assertThat(this.classFileVersion.equals("classFileVersion")).isFalse();
    }

    @Test
    @DisplayName("测试 ClassFileVersion 类 hasCode 方法，返回正常结果")
    void givenClassFileVersionThenReturnHasCode() {
        int major = this.classFileVersion.major().intValue();
        int minor = this.classFileVersion.minor().intValue();
        ClassFileVersion newVersion = ClassFileVersion.of(major, minor);
        assertThat(newVersion.hashCode()).isEqualTo(this.classFileVersion.hashCode());
    }

    @Test
    @DisplayName("测试 ClassFileVersion 类 toString 方法，返回正常结果")
    void givenClassFileVersionThenReturnToString() {
        int major = this.classFileVersion.major().intValue();
        int minor = this.classFileVersion.minor().intValue();
        String version = this.classFileVersion.toString();
        String newVersion = major + "." + minor;
        assertThat(version).isEqualTo(newVersion);
    }

    @Test
    @DisplayName("测试 ClassFileVersion 类 compareTo 方法创建相同对象比较时，返回正常结果")
    void givenClassFileVersionThenReturnEqualCompareTo() {
        int major = this.classFileVersion.major().intValue();
        int minor = this.classFileVersion.minor().intValue();
        ClassFileVersion newVersion = ClassFileVersion.of(major, minor);
        assertThat(this.classFileVersion.compareTo(newVersion)).isEqualTo(0);
    }

    @Test
    @DisplayName("测试 ClassFileVersion 类 compareTo 方法创建不相同对象比较时，返回正常结果")
    void givenClassFileVersionThenReturnNotEqualCompareTo() {
        ClassFileVersion otherVersion = ClassFileVersion.of(522, 0);
        assertThat(this.classFileVersion.compareTo(otherVersion)).isEqualTo(-1);
    }
}