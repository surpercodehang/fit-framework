/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.attribute;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.jvm.classfile.ClassFile;
import modelengine.fitframework.jvm.classfile.ConstantPool;
import modelengine.fitframework.jvm.classfile.FieldInfo;
import modelengine.fitframework.jvm.classfile.FieldList;
import modelengine.fitframework.jvm.classfile.constant.IntegerInfo;
import modelengine.fitframework.jvm.classfile.lang.U2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * {@link ConstantValueAttribute} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-14
 */
@DisplayName("测试 ConstantValueAttribute 类")
class ConstantValueAttributeTest {
    private static final int TEST_CONSTANT_FIRST_FIELD_VALUE = 10;

    private ConstantPool pool;
    private ConstantValueAttribute lookup;

    @BeforeEach
    @DisplayName("初始化 ConstantValueAttribute 类")
    void init() throws IOException {
        try (InputStream inputStream = ConstantValueAttributeTest.class.getClassLoader()
                .getResourceAsStream("modelengine/fitframework/jvm/test/AttributeTarget.class")) {
            ClassFile classFile = new ClassFile(inputStream);
            this.pool = classFile.constants();
            FieldList fields = classFile.fields();
            FieldInfo fieldInfo = fields.get(U2.ZERO);
            this.lookup = ConstantValueAttribute.lookup(fieldInfo.attributes());
        }
    }

    @Test
    @DisplayName("测试 ConstantValueAttribute 类 constantValueIndex 方法，返回常量字段的值")
    void givenConstantValueAttributeShouldReturnConstantValue() {
        IntegerInfo constant = this.pool.get(this.lookup.constantValueIndex());
        assertThat(constant.intValue()).isEqualTo(TEST_CONSTANT_FIRST_FIELD_VALUE);
    }

    @Test
    @DisplayName("测试 ConstantValueAttribute 类 lookup 方法参数为空时，返回空")
    void givenNullParamShouldReturnNull() throws IOException {
        ConstantValueAttribute attribute = ConstantValueAttribute.lookup(null);
        assertThat(attribute).isNull();
    }

    @Test
    @DisplayName("测试 ConstantValueAttribute 类 lookup 方法参数不包含常量时，返回空")
    void givenParamNoConstantShouldReturnNull() throws IOException {
        ConstantValueAttribute attribute = ConstantValueAttribute.lookup(this.pool.file().attributes());
        assertThat(attribute).isNull();
    }
}
