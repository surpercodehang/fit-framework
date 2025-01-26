/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.descriptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import modelengine.fitframework.jvm.test.AttributeTarget;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.List;

/**
 * {@link ClassDescriptor} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-01-31
 */
@DisplayName("测试 ClassDescriptor 类以及子类")
class ClassDescriptorTest {
    @Test
    @DisplayName("当提供 class 文件时，返回 class 文件类 void 描述符")
    void givenClassFileThenReturnClassVoidOfDescription() {
        ClassDescriptor voidDescription = ClassDescriptor.of(void.class);
        String type = voidDescription.term();
        assertThat(type).isEqualTo("V");
    }

    @Test
    @DisplayName("当提供 class 文件时，解析 class 文件类 void 描述符")
    void givenClassFileThenReturnClassParseVoidDescription() {
        ClassDescriptor parse = ClassDescriptor.parse("V");
        String term = parse.toString();
        assertThat(term).isEqualTo("void");
    }

    @Test
    @DisplayName("当提供 class 文件时，返回 class 文件类 byte 描述符")
    void givenClassFileThenReturnClassByteOfDescription() {
        ClassDescriptor voidDescription = ClassDescriptor.of(byte.class);
        String type = voidDescription.term();
        assertThat(type).isEqualTo("B");
    }

    @Test
    @DisplayName("当提供 class 文件时，解析 class 文件类 byte 描述符")
    void givenClassFileThenReturnClassParseByteDescription() {
        ClassDescriptor parse = ClassDescriptor.parse("B");
        ClassDescriptor.Primitive primitive = ObjectUtils.cast(parse);
        String name = primitive.toString();
        assertThat(name).isEqualTo("byte");
    }

    @Test
    @DisplayName("当提供 class 文件时，返回 class 文件类 char 描述符")
    void givenClassFileThenReturnClassCharOfDescription() {
        ClassDescriptor voidDescription = ClassDescriptor.of(char.class);
        String type = voidDescription.term();
        assertThat(type).isEqualTo("C");
    }

    @Test
    @DisplayName("当提供 class 文件时，解析 class 文件类 char 描述符")
    void givenClassFileThenReturnClassParseCharDescription() {
        ClassDescriptor parse = ClassDescriptor.parse("C");
        ClassDescriptor.Primitive primitive = ObjectUtils.cast(parse);
        String name = primitive.type().getName();
        assertThat(name).isEqualTo("char");
    }

    @Test
    @DisplayName("当提供 class 文件时，返回 class 文件类 double 描述符")
    void givenClassFileThenReturnClassDoubleOfDescription() {
        ClassDescriptor voidDescription = ClassDescriptor.of(double.class);
        String type = voidDescription.term();
        assertThat(type).isEqualTo("D");
    }

    @Test
    @DisplayName("当提供 class 文件时，解析 class 文件类 double 描述符")
    void givenClassFileThenReturnClassDoubleDescription() {
        ClassDescriptor parse = ClassDescriptor.parse("D");
        ClassDescriptor.Primitive primitive = ObjectUtils.cast(parse);
        String name = primitive.type().getName();
        assertThat(name).isEqualTo("double");
    }

    @Test
    @DisplayName("当提供 class 文件时，返回 class 文件类 float 描述符")
    void givenClassFileThenReturnClassFloatOfDescription() {
        ClassDescriptor voidDescription = ClassDescriptor.of(float.class);
        String type = voidDescription.term();
        assertThat(type).isEqualTo("F");
    }

    @Test
    @DisplayName("当提供 class 文件时，解析 class 文件类 float 描述符")
    void givenClassFileThenReturnClassParseFloatDescription() {
        ClassDescriptor parse = ClassDescriptor.parse("F");
        ClassDescriptor.Primitive primitive = ObjectUtils.cast(parse);
        String name = primitive.type().getName();
        assertThat(name).isEqualTo("float");
    }

    @Test
    @DisplayName("当提供 class 文件时，返回 class 文件类 int 描述符")
    void givenClassFileThenReturnClassIntOfDescription() {
        ClassDescriptor voidDescription = ClassDescriptor.of(int.class);
        String type = voidDescription.term();
        assertThat(type).isEqualTo("I");
    }

    @Test
    @DisplayName("当提供 class 文件时，返回 class 文件类 int 描述符")
    void givenClassFileThenReturnClassParseIntDescription() {
        ClassDescriptor parse = ClassDescriptor.parse("I");
        ClassDescriptor.Primitive primitive = ObjectUtils.cast(parse);
        String name = primitive.type().getName();
        assertThat(name).isEqualTo("int");
    }

    @Test
    @DisplayName("当提供 class 文件时，返回 class 文件类 long 描述符")
    void givenClassFileThenReturnClassLongOfDescription() {
        ClassDescriptor voidDescription = ClassDescriptor.of(long.class);
        String type = voidDescription.term();
        assertThat(type).isEqualTo("J");
    }

    @Test
    @DisplayName("当提供 class 文件时，解析 class 文件类 long 描述符")
    void givenClassFileThenReturnClassLongDescription() {
        ClassDescriptor parse = ClassDescriptor.parse("J");
        ClassDescriptor.Primitive primitive = ObjectUtils.cast(parse);
        String name = primitive.type().getName();
        assertThat(name).isEqualTo("long");
    }

    @Test
    @DisplayName("当提供 class 文件时，返回 class 文件类 short 描述符")
    void givenClassFileThenReturnClassShortOfDescription() {
        ClassDescriptor voidDescription = ClassDescriptor.of(short.class);
        String type = voidDescription.term();
        assertThat(type).isEqualTo("S");
    }

    @Test
    @DisplayName("当提供 class 文件时，解析 class 文件类 short 描述符")
    void givenClassFileThenReturnClassParseShortDescription() {
        ClassDescriptor parse = ClassDescriptor.parse("S");
        ClassDescriptor.Primitive primitive = ObjectUtils.cast(parse);
        String name = primitive.type().getName();
        assertThat(name).isEqualTo("short");
    }

    @Test
    @DisplayName("当提供 class 文件时，返回 class 文件类 boolean 描述符")
    void givenClassFileThenReturnClassBooleanOfDescription() {
        ClassDescriptor voidDescription = ClassDescriptor.of(boolean.class);
        String type = voidDescription.term();
        assertThat(type).isEqualTo("Z");
    }

    @Test
    @DisplayName("当提供 class 文件时，解析 class 文件类 boolean 描述符")
    void givenClassFileThenReturnClassParseBooleanDescription() {
        ClassDescriptor parse = ClassDescriptor.parse("Z");
        ClassDescriptor.Primitive primitive = ObjectUtils.cast(parse);
        String name = primitive.type().getName();
        assertThat(name).isEqualTo("boolean");
    }

    @Test
    @DisplayName("当提供 class 文件时，返回 class 文件类 Array 描述符")
    void givenClassFileThenReturnClassArrayOfDescription() {
        ClassDescriptor voidDescription = ClassDescriptor.of(Array[].class);
        String type = voidDescription.term();
        assertThat(type).isEqualTo("[Ljava/lang/reflect/Array;");
    }

    @Test
    @DisplayName("当提供 class 文件时，解析 class 文件类 Array 描述符")
    void givenClassFileThenReturnClassParseArrayDescription() {
        ClassDescriptor parse = ClassDescriptor.parse("[Ljava/lang/reflect/Array;");
        ClassDescriptor.Array array = ObjectUtils.cast(parse);
        String name = array.toString();
        assertThat(name).isEqualTo("java.lang.reflect.Array[]");
    }

    @Test
    @DisplayName("当提供 class 文件时，验证 class 文件类 Array 描述符 equals 和 hasCode")
    void givenClassFileThenReturnClassCheckArrayDescription() {
        ClassDescriptor parseDescription = ClassDescriptor.parse("[Ljava/lang/reflect/Array;");
        ClassDescriptor ofDescription = ClassDescriptor.of(Array[].class);
        ClassDescriptor.Array parseArray = ObjectUtils.cast(parseDescription);
        ClassDescriptor.Array ofArray = ObjectUtils.cast(ofDescription);
        assertThat(parseArray.equals(ofArray)).isTrue();
        assertThat(parseArray.hashCode()).isEqualTo(ofArray.hashCode());
    }

    @Test
    @DisplayName("当提供 class 文件时，返回 class 文件类 Reference 描述符")
    void givenClassFileThenReturnClassReferenceOfDescription() {
        ClassDescriptor referenceDescriptor = ClassDescriptor.of(AttributeTarget.class);
        assertThat(referenceDescriptor.term())
                .isEqualTo("Lmodelengine/fitframework/jvm/test/AttributeTarget;");
    }

    @Test
    @DisplayName("当提供 class 文件时，解析 class 文件类 Reference 描述符")
    void givenClassFileThenReturnClassParseReferenceDescription() {
        ClassDescriptor parse =
                ClassDescriptor.parse("Lmodelengine/fitframework/jvm/test/AttributeTarget;");
        ClassDescriptor.Reference reference = ObjectUtils.cast(parse);
        String name = reference.name();
        assertThat(name).isEqualTo("modelengine.fitframework.jvm.test.AttributeTarget");
    }

    @Test
    @DisplayName("当提供 class 文件时，返回 class 文件方法 Reference 描述符")
    void givenClassFileThenReturnMethodReferenceOfDescription() {
        MethodDescriptor methodDescriptor = MethodDescriptor.parse("(ID)V");
        List<ClassDescriptor> parameterTypes = methodDescriptor.parameterTypes();
        assertThat(parameterTypes).hasSize(2);
        for (ClassDescriptor descriptor : parameterTypes) {
            boolean is = descriptor.is(ClassDescriptor.Primitive.class);
            assertThat(is).isTrue();
        }
        ClassDescriptor returnType = methodDescriptor.returnType();
        assertThat(returnType.toString()).isEqualTo("void");
    }

    @Test
    @DisplayName("测试 ClassDescriptor.Reference 类 equals 方法，返回正常结果")
    void givenClassFileThenReturnClassDescriptorReferenceEqual() {
        ClassDescriptor attribute =
                ClassDescriptor.parse("Lmodelengine/fitframework/jvm/test/AttributeTarget;");
        ClassDescriptor.Reference attributeReference = ObjectUtils.cast(attribute);
        assertThat(attributeReference.equals("assignmentReference")).isFalse();
        ClassDescriptor.Reference reference = attributeReference;
        assertThat(attributeReference.equals(reference)).isTrue();
        assertThat(attributeReference.equals(null)).isFalse();
    }

    @Test
    @DisplayName("测试 ClassDescriptor.Array 类 equals 方法，返回正常结果")
    void givenClassFileThenReturnClassDescriptorArrayEqual() {
        ClassDescriptor attribute = ClassDescriptor.parse("[Ljava/lang/reflect/Array;");
        ClassDescriptor.Array attributeArray = ObjectUtils.cast(attribute);
        assertThat(attributeArray.equals("Array")).isFalse();
        ClassDescriptor.Array array = attributeArray;
        assertThat(attributeArray.equals(array)).isTrue();
        assertThat(attributeArray.equals(null)).isFalse();
    }

    @Test
    @DisplayName("当提供 class 文件时，class 文件方法描述符长度异常")
    void givenClassFileThenReturnDescriptionLengthException() {
        assertThatThrownBy(() -> MethodDescriptor.parse("ID"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("当提供 class 文件时，class 文件方法描述符格式异常")
    void givenClassFileThenReturnDescriptionFormatException() {
        assertThatThrownBy(() -> MethodDescriptor.parse("(ID)"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("当提供 class 文件时，class 文件方法描述符参数异常")
    void givenClassFileThenReturnDescriptionParameterException() {
        assertThatThrownBy(() -> MethodDescriptor.parse("(IDV)V"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("当提供 class 文件时，class 文件方法描述符返回类型异常")
    void givenClassFileThenReturnDescriptionReturnException() {
        assertThatThrownBy(() -> MethodDescriptor.parse("(ID)VV"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("当提供 class 文件时，class 文件方法解析描述符类型不存在异常")
    void givenClassFileThenReturnMethodDescriptionTypeException() {
        assertThatThrownBy(() -> MethodDescriptor.parse("(ID)W"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("当提供 class 文件时，class 文件方法 Reference 类型解析描述符不完整异常")
    void givenClassFileThenReturnDescriptionReferenceTypeIncompleteException() {
        assertThatThrownBy(() -> MethodDescriptor.parse("(ID)Lmodelengine/fitframework/jvm/test/AttributeTarget"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("当解析 class 文件类描述符为空时，抛出解析异常信息")
    void givenClassFileThenReturnDescriptionParseNullException() {
        assertThatThrownBy(() -> ClassDescriptor.parse("")).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("当解析 class 文件类描述符超过一个时，抛出解析异常信息")
    void givenClassFileThenReturnDescriptionParseMoreOneException() {
        assertThatThrownBy(() -> ClassDescriptor.parse("ID")).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("当解析 class 文件类描述符为 Reference 空值时，抛出解析异常信息")
    void givenClassFileThenReturnDescriptionParseReferenceNullException() {
        assertThatThrownBy(() -> ClassDescriptor.parse("L;")).isInstanceOf(IllegalStateException.class);
    }
}