/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.constant;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.jvm.classfile.ClassFile;
import modelengine.fitframework.jvm.classfile.Constant;
import modelengine.fitframework.jvm.classfile.ConstantPool;
import modelengine.fitframework.jvm.classfile.lang.U1;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * {@link ClassInfo} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-08
 */
@DisplayName("测试 ClassInfo 类")
class ClassInfoTest {
    private final String url = "modelengine/fitframework/jvm/test/AttributeTarget.class";
    private final U1 tag = ClassInfo.TAG;

    @Nested
    @DisplayName("测试方法：equals")
    class TestEquals {
        @SuppressWarnings("EqualsWithItself")
        @Test
        @DisplayName("提供 ClassInfo 类 equals 方法与本身比较时，返回 true")
        void givenSelfShouldReturnTrue() {
            ClassInfo classInfo = getConstant(ClassInfoTest.this.url, ClassInfoTest.this.tag);
            assertThat(classInfo.equals(classInfo)).isTrue();
        }

        @Test
        @DisplayName("提供 ClassInfo 类 equals 方法与相同类型的新对象比较时，返回 false")
        void givenSameTypeShouldReturnFalse() {
            ClassInfo classInfo = getConstant(ClassInfoTest.this.url, ClassInfoTest.this.tag);
            ClassInfo newClassInfo = getConstant(ClassInfoTest.this.url, ClassInfoTest.this.tag);
            assertThat(classInfo.equals(newClassInfo)).isFalse();
        }

        @SuppressWarnings("EqualsBetweenInconvertibleTypes")
        @Test
        @DisplayName("提供 ClassInfo 类 equals 方法与其他类型对象比较时，返回 false")
        void givenOtherTypeShouldReturnFalse() {
            ClassInfo classInfo = getConstant(ClassInfoTest.this.url, ClassInfoTest.this.tag);
            DoubleInfo otherClassInfo = getConstant(ClassInfoTest.this.url, DoubleInfo.TAG);
            assertThat(classInfo.equals(otherClassInfo)).isFalse();
        }
    }

    @Test
    @DisplayName("提供 ClassInfo 类 hasCode 方法与相同类型的新对象比较时，返回正常结果")
    void givenClassInfoShouldReturnHasCode() {
        ClassInfo classInfo = getConstant(this.url, this.tag);
        ClassInfo newClassInfo = getConstant(this.url, this.tag);
        assertThat(classInfo.hashCode()).isNotEqualTo(newClassInfo.hashCode());
    }

    @Test
    @DisplayName("提供 ClassInfo 类 toString 方法与相同值的新对象比较时，返回正常结果")
    void givenClassInfoShouldReturnStringValue() {
        ClassInfo classInfo = getConstant(this.url, this.tag);
        ClassInfo newClassInfo = getConstant(this.url, this.tag);
        assertThat(classInfo.toString()).isEqualTo(newClassInfo.toString());
    }

    /**
     * 获取常量对象。
     *
     * @param url 表示类文件 url 的 {@link String}。
     * @param tag 表示常量的标签的 {@link U1}。
     * @return 表示常量对象的 {@link T}。
     */
    public static <T extends Constant> T getConstant(String url, U1 tag) {
        Constant constant = null;
        try (InputStream in = ClassInfoTest.class.getClassLoader().getResourceAsStream(url)) {
            ClassFile classFile = new ClassFile(in);
            ConstantPool pool = classFile.constants();
            for (Constant tmp : pool) {
                if (tmp.tag().equals(tag)) {
                    constant = tmp;
                    break;
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("The url be null.");
        }
        Validation.notNull(constant, "constant cannot be null.");
        // ClassInfo 类常量
        return ObjectUtils.cast(constant);
    }
}