/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.jvm.classfile.ClassFile;
import modelengine.fitframework.jvm.support.DefaultClassDeclaration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * {@link ClassDeclaration} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-03
 */
@DisplayName("测试 ClassDeclaration 类")
class ClassDeclarationTest {
    private DefaultClassDeclaration declaration;

    @BeforeEach
    @DisplayName("初始化 ClassDeclaration 类")
    void init() throws IOException {
        try (InputStream inputStream = ClassDeclarationTest.class.getClassLoader()
                .getResourceAsStream("modelengine/fitframework/jvm/test/AttributeTarget.class")) {
            ClassFile classFile = new ClassFile(inputStream);
            this.declaration = new DefaultClassDeclaration(classFile);
        }
    }

    @Test
    @DisplayName("测试 ClassDeclaration 类 superclass 方法，返回其父类")
    void givenClassFileThenReturnSuperclass() {
        String superclass = this.declaration.superclass();
        assertThat(superclass).isEqualTo("java.lang.Object");
    }

    @Test
    @DisplayName("测试 ClassDeclaration 类 toString 方法，返回其名称")
    void givenClassFileThenReturnToString() {
        String name = this.declaration.toString();
        assertThat(name).isEqualTo("modelengine.fitframework.jvm.test.AttributeTarget");
    }

    @Test
    @DisplayName("测试 ClassDeclaration 类 modifiers 方法，返回其修饰符列表")
    void givenClassFileThenReturnModifiers() {
        Set<Modifier> modifiers = this.declaration.modifiers();
        assertThat(modifiers.size()).isEqualTo(2);
    }
}