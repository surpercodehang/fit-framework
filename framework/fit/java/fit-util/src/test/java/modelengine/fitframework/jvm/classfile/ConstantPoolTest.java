/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.jvm.classfile.constant.MethodRefInfo;
import modelengine.fitframework.jvm.classfile.lang.U1;
import modelengine.fitframework.jvm.classfile.lang.U2;
import modelengine.fitframework.util.Convert;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * {@link ConstantPool} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-07
 */
@DisplayName("测试 ConstantPool 类以及相关类")
class ConstantPoolTest {
    private static final int EMPTY_CLASS_CONSTANT_POOL_SIZE = 15;

    private ConstantPool pool;

    @BeforeEach
    @DisplayName("初始化 ConstantPool 类")
    void init() throws IOException {
        try (InputStream inputStream = ConstantPoolTest.class.getClassLoader()
                .getResourceAsStream("modelengine/fitframework/jvm/test/AttributeTarget.class")) {
            ClassFile classFile = new ClassFile(inputStream);
            this.pool = classFile.constants();
        }
    }

    @Test
    @DisplayName("提供空类对象时，返回固定长度常量池常量数量")
    void givenEmptyClassShouldReturnFixedPoolCount() throws IOException {
        try (InputStream inputStream = ConstantPoolTest.class.getClassLoader()
                .getResourceAsStream("modelengine/fitframework/jvm/test/EmptyTest.class")) {
            ClassFile classFile = new ClassFile(inputStream);
            int count = classFile.constants().count().intValue();
            assertThat(count).isEqualTo(EMPTY_CLASS_CONSTANT_POOL_SIZE);
        }
    }

    @Test
    @DisplayName("提供 ConstantPool 对象，使用流遍历常量池")
    void givenConstantPoolWhenStreamThenGetConstant() {
        Iterator<Constant> iterator = this.pool.stream().iterator();
        while (iterator.hasNext()) {
            Constant constant = iterator.next();
            assertThat(constant).isNotNull();
        }
    }

    @Test
    @DisplayName("提供 ConstantPool 对象，迭代遍历常量池")
    void givenConstantPoolWhenIteratorThenGetConstant() {
        Iterator<Constant> iterator = this.pool.iterator();
        while (iterator.hasNext()) {
            Constant constant = iterator.next();
            assertThat(constant).isNotNull();
        }
    }

    @Test
    @DisplayName("提供 ConstantPool 对象，遍历常量池异常抛出")
    void givenConstantPoolWhenOutOfBoundsIteratorThenThrowException() {
        Iterator<Constant> iterator = this.pool.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            if (!iterator.hasNext()) {
                assertThatThrownBy(() -> iterator.next()).isInstanceOf(NoSuchElementException.class);
            }
        }
    }

    @Test
    @DisplayName("提供 Constant 对象，返回 classFile 类信息")
    void givenConstantShouldReturnClassFile() {
        ClassFile file = this.pool.get(U2.ONE).file();
        ClassFile classFile = ConstantPoolTest.this.pool.file();
        assertThat(file).isEqualTo(classFile);
    }

    @Test
    @DisplayName("提供 Constant 对象，返回 tag 信息")
    void givenConstantShouldReturnTag() {
        U1 tag = this.pool.get(U2.ONE).tag();
        // MethodRefInfo 类型
        U1 newTag = MethodRefInfo.TAG;
        assertThat(tag).isEqualTo(newTag);
    }

    @Test
    @DisplayName("提供 Constant 对象，写入 value 信息")
    void givenConstantWhenWriteThenGetCorrectInfo() throws IOException {
        String expect = "byteTest";
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // 遍历获取 byteTest 常量索引
            Iterator<FieldInfo> iterator = this.pool.file().fields().iterator();
            FieldInfo fieldJvm = null;
            while (iterator.hasNext()) {
                FieldInfo fieldInfo = iterator.next();
                String fieldName = this.pool.get(fieldInfo.nameIndex()).toString();
                if (Objects.equals(expect, fieldName)) {
                    fieldJvm = fieldInfo;
                    break;
                }
            }
            Validation.notNull(fieldJvm, "field cannot be null.");
            Constant constant = this.pool.get(fieldJvm.nameIndex());
            constant.write(out);
            String result = Convert.toString(out.toByteArray());
            assertThat(result).contains(expect);
        }
    }
}
