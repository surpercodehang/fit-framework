/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.jvm.classfile.AttributeInfo;
import modelengine.fitframework.jvm.classfile.AttributeList;
import modelengine.fitframework.jvm.classfile.ClassFile;
import modelengine.fitframework.jvm.classfile.ConstantPool;
import modelengine.fitframework.jvm.classfile.attribute.RuntimeVisibleAnnotationsAttribute;
import modelengine.fitframework.jvm.classfile.lang.U1;
import modelengine.fitframework.jvm.classfile.lang.U2;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Objects;

/**
 * {@link AnnotationElementValue} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-01
 */
@DisplayName("测试 AnnotationElementValue 类以及相关类")
class AnnotationElementValueTest {
    private AnnotationElementValue elementValue;
    private RuntimeVisibleAnnotationsAttribute lookup;
    private AnnotationElementValuePairList pairList;

    @BeforeEach
    @DisplayName("初始化 ClassFile 类")
    void init() throws IOException {
        try (InputStream inputStream = AnnotationListTest.class.getClassLoader()
                .getResourceAsStream("modelengine/fitframework/jvm/test/AttributeTarget.class")) {
            ClassFile classFile = new ClassFile(inputStream);
            AttributeList attributes = classFile.attributes();
            this.lookup = RuntimeVisibleAnnotationsAttribute.lookup(attributes);
            AnnotationInfo annotation = this.lookup.annotations().get(U2.ZERO);
            AnnotationElementValuePairList elementValuePairs = annotation.elements();
            AnnotationElementValuePair valuePair = elementValuePairs.get(U2.ZERO);
            this.elementValue = valuePair.value();

            Iterator<AnnotationInfo> annotations = this.lookup.annotations().iterator();
            AnnotationInfo annotationTmp = null;
            while (annotations.hasNext()) {
                AnnotationInfo annotationInfo = annotations.next();
                String typeIndex = classFile.constants().get(annotationInfo.typeIndex()).toString();
                if (Objects.equals(typeIndex, "Lmodelengine/fitframework/jvm/test/AssignmentTarget;")) {
                    annotationTmp = annotationInfo;
                    break;
                }
            }
            Validation.notNull(annotationTmp, "annotation cannot be null.");
            this.pairList = annotationTmp.elements();
        }
    }

    @Test
    @DisplayName("测试 AnnotationElementValue 类 annotation 方法，返回注解信息信息")
    void givenClassFileThenReturnAnnotationOfAnnotationElementValue() {
        AnnotationInfo annotation = this.lookup.annotations().get(U2.ZERO);
        AnnotationInfo newAnnotation = this.elementValue.annotation();
        assertThat(annotation).isEqualTo(newAnnotation);
    }

    @Test
    @DisplayName("测试 AnnotationElementValue 类 attribute 方法，返回注解属性信息")
    void givenClassFileThenReturnAttributeOfAnnotationElementValue() {
        AnnotationInfo annotation = this.lookup.annotations().get(U2.ZERO);
        AttributeInfo attribute = this.elementValue.attribute();
        AttributeInfo newAttributeInfo = annotation.attribute();
        assertThat(attribute).isEqualTo(newAttributeInfo);
    }

    @Test
    @DisplayName("测试 AnnotationElementValue 类 file 方法，返回 classFile 信息")
    void givenClassFileThenReturnClassFileOfAnnotationElementValue() {
        AnnotationInfo annotation = this.lookup.annotations().get(U2.ZERO);
        ClassFile classFile = this.elementValue.file();
        ClassFile newClassFile = annotation.file();
        assertThat(classFile).isEqualTo(newClassFile);
    }

    @Test
    @DisplayName("测试 AnnotationElementValue 类 tag 方法，返回注解元素类型信息")
    void givenClassFileThenReturnClassTagOfAnnotationElementValue() {
        U1 tag = this.elementValue.tag();
        // 枚举类型
        U1 newTag = U1.of((byte) 'B');
        assertThat(tag).isEqualTo(newTag);
    }

    private static <T extends AnnotationElementValue> T getElementValue(AnnotationElementValuePairList pairs,
            String condition) {
        for (AnnotationElementValuePair valuePair : pairs) {
            AnnotationElementValue value = valuePair.value();
            ConstantPool pool = value.file().constants();
            String key = pool.get(value.pair().nameIndex()).toString();
            if (Objects.equals(condition, key)) {
                return ObjectUtils.cast(value);
            }
        }
        throw new IllegalArgumentException("Failed to get AnnotationElementValue subclass.");
    }

    @Nested
    @DisplayName("测试内部类：ByteValue")
    class TestByteValue {
        private static final String VALUE = "3";

        @Test
        @DisplayName("提供 AnnotationElementValue.ByteValue 类时，返回注解元素信息")
        void givenByteValueShouldReturnElementValue() {
            AnnotationElementValue.ByteValue byteValue =
                    getElementValue(AnnotationElementValueTest.this.pairList, "byteValue");
            ConstantPool pool = AnnotationElementValueTest.this.pairList.annotation().file().constants();
            String value = pool.get(byteValue.constValueIndex()).toString();
            assertThat(value).isEqualTo(VALUE);
        }
    }

    @Nested
    @DisplayName("测试内部类：CharValue")
    class TestCharValue {
        private static final char VALUE = 'c';

        @Test
        @DisplayName("提供 AnnotationElementValue.CharValue 类时，返回注解元素信息")
        void givenCharValueShouldReturnElementValue() {
            AnnotationElementValue.CharValue charValue =
                    getElementValue(AnnotationElementValueTest.this.pairList, "charValue");
            ConstantPool pool = AnnotationElementValueTest.this.pairList.annotation().file().constants();
            String value = pool.get(charValue.constValueIndex()).toString();
            String expect = Integer.valueOf(VALUE).toString();
            assertThat(value).isEqualTo(expect);
        }
    }

    @Nested
    @DisplayName("测试内部类：DoubleValue")
    class TestDoubleValue {
        private static final String VALUE = "12.1";

        @Test
        @DisplayName("提供 AnnotationElementValue.DoubleValue 类时，返回注解元素信息")
        void givenDoubleValueShouldReturnElementValue() {
            AnnotationElementValue.DoubleValue doubleValue =
                    getElementValue(AnnotationElementValueTest.this.pairList, "doubleValue");
            ConstantPool pool = AnnotationElementValueTest.this.pairList.annotation().file().constants();
            String value = pool.get(doubleValue.constValueIndex()).toString();
            assertThat(value).isEqualTo(VALUE);
        }
    }

    @Nested
    @DisplayName("测试内部类：FloatValue")
    class TestFloatValue {
        private static final String VALUE = "4.0";

        @Test
        @DisplayName("提供 AnnotationElementValue.FloatValue 类时，返回注解元素信息")
        void givenFloatValueShouldReturnElementValue() {
            AnnotationElementValue.FloatValue floatValue =
                    getElementValue(AnnotationElementValueTest.this.pairList, "floatValue");
            ConstantPool pool = AnnotationElementValueTest.this.pairList.annotation().file().constants();
            String value = pool.get(floatValue.constValueIndex()).toString();
            assertThat(value).isEqualTo(VALUE);
        }
    }

    @Nested
    @DisplayName("测试内部类：IntegerValue")
    class TestIntegerValue {
        private static final String VALUE = "23";

        @Test
        @DisplayName("提供 AnnotationElementValue.IntegerValue 类时，返回注解元素信息")
        void givenIntegerValueShouldReturnElementValue() {
            AnnotationElementValue.IntegerValue integerValue =
                    getElementValue(AnnotationElementValueTest.this.pairList, "integerValue");
            ConstantPool pool = AnnotationElementValueTest.this.pairList.annotation().file().constants();
            String value = pool.get(integerValue.constValueIndex()).toString();
            assertThat(value).isEqualTo(VALUE);
        }
    }

    @Nested
    @DisplayName("测试内部类：LongValue")
    class TestLongValue {
        private static final String VALUE = "5";

        @Test
        @DisplayName("提供 AnnotationElementValue.LongValue 类时，返回注解元素信息")
        void givenLongValueShouldReturnElementValue() {
            AnnotationElementValue.LongValue longValue =
                    getElementValue(AnnotationElementValueTest.this.pairList, "longValue");
            ConstantPool pool = AnnotationElementValueTest.this.pairList.annotation().file().constants();
            String value = pool.get(longValue.constValueIndex()).toString();
            assertThat(value).isEqualTo(VALUE);
        }
    }

    @Nested
    @DisplayName("测试内部类：ShortValue")
    class TestShortValue {
        private static final String VALUE = "6";

        @Test
        @DisplayName("提供 AnnotationElementValue.ShortValue 类时，返回注解元素信息")
        void givenShortValueShouldReturnElementValue() {
            AnnotationElementValue.ShortValue shortValue =
                    getElementValue(AnnotationElementValueTest.this.pairList, "shortValue");
            ConstantPool pool = AnnotationElementValueTest.this.pairList.annotation().file().constants();
            String value = pool.get(shortValue.constValueIndex()).toString();
            assertThat(value).isEqualTo(VALUE);
        }
    }

    @Nested
    @DisplayName("测试内部类：BooleanValue")
    class TestBooleanValue {
        private static final String VALUE = "1";

        @Test
        @DisplayName("提供 AnnotationElementValue.BooleanValue 类时，返回注解元素信息")
        void givenBooleanValueShouldReturnElementValue() {
            AnnotationElementValue.BooleanValue booleanValue =
                    getElementValue(AnnotationElementValueTest.this.pairList, "booleanValue");
            ConstantPool pool = AnnotationElementValueTest.this.pairList.annotation().file().constants();
            String value = pool.get(booleanValue.constValueIndex()).toString();
            assertThat(value).isEqualTo(VALUE);
        }
    }

    @Nested
    @DisplayName("测试内部类：StringValue")
    class TestStringValue {
        private static final String VALUE = "stringValue";

        @Test
        @DisplayName("提供 AnnotationElementValue.StringValue 类时，返回注解元素信息")
        void givenStringValueShouldReturnElementValue() {
            AnnotationElementValue.StringValue stringValue =
                    getElementValue(AnnotationElementValueTest.this.pairList, "stringValue");
            ConstantPool pool = AnnotationElementValueTest.this.pairList.annotation().file().constants();
            String value = pool.get(stringValue.constValueIndex()).toString();
            assertThat(value).isEqualTo(VALUE);
        }
    }

    @Nested
    @DisplayName("测试内部类：EnumValue")
    class TestEnumValue {
        private static final String VALUE = "BLUE";

        @Test
        @DisplayName("提供 AnnotationElementValue.StringValue 类时，返回注解元素值信息")
        void givenEnumValueShouldReturnElementNameValue() {
            AnnotationElementValue.EnumValue enumValue =
                    getElementValue(AnnotationElementValueTest.this.pairList, "fruitColor");
            ConstantPool pool = AnnotationElementValueTest.this.pairList.annotation().file().constants();
            String value = pool.get(enumValue.constNameIndex()).toString();
            assertThat(value).isEqualTo(VALUE);
        }

        @Test
        @DisplayName("提供 AnnotationElementValue.StringValue 类时，返回注解元素类型信息")
        void givenEnumValueShouldReturnElementTypeValue() {
            AnnotationElementValue.EnumValue enumValue =
                    getElementValue(AnnotationElementValueTest.this.pairList, "fruitColor");
            ConstantPool pool = AnnotationElementValueTest.this.pairList.annotation().file().constants();
            String value = pool.get(enumValue.typeNameIndex()).toString();
            assertThat(value).isEqualTo("Lmodelengine/fitframework/jvm/test/AssignmentTarget$EnumValue;");
        }
    }

    @Nested
    @DisplayName("测试内部类：ArrayValue")
    class TestArrayValue {
        private static final int COUNT = 2;
        private static final String VALUE = "ab";

        @Test
        @DisplayName("提供 AnnotationElementValue.ArrayValue 类时，返回注解元素数组数量信息")
        void givenArrayValueShouldReturnElementCount() {
            AnnotationElementValue.ArrayValue arrayValue =
                    getElementValue(AnnotationElementValueTest.this.pairList, "arrayValue");
            int value = arrayValue.count().intValue();
            assertThat(value).isEqualTo(COUNT);
        }

        @Test
        @DisplayName("提供 AnnotationElementValue.ArrayValue 类时，获取注解元素数组第一个")
        void givenArrayValueShouldReturnFirstElement() {
            AnnotationElementValue.ArrayValue arrayValue =
                    getElementValue(AnnotationElementValueTest.this.pairList, "arrayValue");
            ConstantPool pool = AnnotationElementValueTest.this.pairList.annotation().file().constants();
            AnnotationElementValue.StringValue stringValue = ObjectUtils.cast(arrayValue.get(U2.ZERO));
            String value = pool.get(stringValue.constValueIndex()).toString();
            assertThat(value).isEqualTo("a");
        }

        @Test
        @DisplayName("提供 AnnotationElementValue.ArrayValue 类时，遍历注解元素数组信息")
        void givenArrayValueWhenIteratorThenReturnElementIndexValue() {
            AnnotationElementValue.ArrayValue arrayValue =
                    getElementValue(AnnotationElementValueTest.this.pairList, "arrayValue");
            ConstantPool pool = AnnotationElementValueTest.this.pairList.annotation().file().constants();
            Iterator<AnnotationElementValue> iterator = arrayValue.iterator();
            StringBuilder builder = new StringBuilder();
            while (iterator.hasNext()) {
                AnnotationElementValue.StringValue stringValue = ObjectUtils.cast(iterator.next());
                String value = pool.get(stringValue.constValueIndex()).toString();
                builder.append(value);
            }
            assertThat(builder.toString()).isEqualTo(VALUE);
        }
    }

    @Nested
    @DisplayName("测试内部类：ClassValue")
    class TestClassValue {
        private static final String VALUE = "Ljava/lang/String;";

        @Test
        @DisplayName("提供 AnnotationElementValue.ClassValue 类时，返回注解元素信息")
        void givenClassValueShouldReturnElementValue() {
            AnnotationElementValue.ClassValue classValue =
                    getElementValue(AnnotationElementValueTest.this.pairList, "classValue");
            ConstantPool pool = AnnotationElementValueTest.this.pairList.annotation().file().constants();
            String value = pool.get(classValue.classInfoIndex()).toString();
            assertThat(value).isEqualTo(VALUE);
        }
    }

    @Nested
    @DisplayName("测试内部类：AnnotationValue")
    class TestAnnotationValue {
        private static final String VALUE = "Lmodelengine/fitframework/jvm/test/NestAssignmentTarget;";

        @Test
        @DisplayName("提供 AnnotationElementValue.AnnotationValue 类时，返回注解元素信息")
        void givenClassValueShouldReturnElementValue() {
            AnnotationElementValue.AnnotationValue annotationValue =
                    getElementValue(AnnotationElementValueTest.this.pairList, "annotationValue");
            ConstantPool pool = AnnotationElementValueTest.this.pairList.annotation().file().constants();
            AnnotationInfo annotationInfo = annotationValue.annotationValue();
            String value = pool.get(annotationInfo.typeIndex()).toString();
            assertThat(value).isEqualTo(VALUE);
        }
    }
}