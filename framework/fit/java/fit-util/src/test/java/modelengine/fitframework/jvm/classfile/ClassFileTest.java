/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * {@link ClassFile} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-01-11
 */
@DisplayName("测试 ClassFile 类")
class ClassFileTest {
    @Test
    @DisplayName("当提供 class 文件时，返回正常 ClassFile 信息")
    void givenJavaClassThenReturnParseInfo() throws IOException {
        try (InputStream inputStream = ClassFileTest.class.getClassLoader()
                .getResourceAsStream("modelengine/fitframework/jvm/test/AttributeTarget.class")) {
            ClassFile classFile = new ClassFile(inputStream);
            assertThat(classFile).isNotNull();
        }
    }

    @Test
    @DisplayName("当提供非 class 文件时，抛出异常信息")
    void givenNoClassThenReturnParseException() throws IOException {
        byte[] bytes = {1, 2, 3, 4, 5, 6, 7, 8};
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            assertThatThrownBy(() -> new ClassFile(inputStream))
                    .isInstanceOf(IllegalClassFormatException.class);
        }
    }
}
