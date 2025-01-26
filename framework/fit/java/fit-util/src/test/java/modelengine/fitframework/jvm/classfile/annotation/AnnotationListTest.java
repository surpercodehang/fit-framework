/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.jvm.classfile.AttributeInfo;
import modelengine.fitframework.jvm.classfile.AttributeList;
import modelengine.fitframework.jvm.classfile.ClassFile;
import modelengine.fitframework.jvm.classfile.ConstantPool;
import modelengine.fitframework.jvm.classfile.attribute.RuntimeVisibleAnnotationsAttribute;
import modelengine.fitframework.jvm.classfile.lang.U2;
import modelengine.fitframework.jvm.test.AssignmentTarget;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * {@link AnnotationList} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-01
 */
@DisplayName("测试 AnnotationList 类以及相关类")
class AnnotationListTest {
    private static final int ATTRIBUTE_LENGTH = 86;

    private ConstantPool constantPool;
    private AttributeList attributes;
    private RuntimeVisibleAnnotationsAttribute lookup;

    @BeforeEach
    @DisplayName("初始化 ClassFile 类")
    void init() throws IOException {
        try (InputStream inputStream = AnnotationListTest.class.getClassLoader()
                .getResourceAsStream("modelengine/fitframework/jvm/test/AttributeTarget.class")) {
            ClassFile classFile = new ClassFile(inputStream);
            this.constantPool = classFile.constants();
            this.attributes = classFile.attributes();
            this.lookup = RuntimeVisibleAnnotationsAttribute.lookup(this.attributes);
        }
    }

    @Test
    @DisplayName("测试 RuntimeVisibleAnnotationsAttribute 类 list 方法，返回注解属性列表")
    void givenClassFileShouldReturnAttributeList() throws IOException {
        List<RuntimeVisibleAnnotationsAttribute> list = RuntimeVisibleAnnotationsAttribute.list(this.attributes);
        assertThat(list).hasSize(1);
    }

    @Test
    @DisplayName("测试 AttributeList 类 get 方法，返回注解属性信息")
    void givenClassFileThenReturnGetOfAttributeList() {
        AttributeInfo attributeInfo = this.attributes.get(U2.ZERO);
        int attributeLength = attributeInfo.attributeLength().intValue();
        assertThat(attributeLength).isEqualTo(2);
    }

    @Test
    @DisplayName("测试 AnnotationList 类 count 方法，返回注解信息列表")
    void givenClassFileThenReturnCountOfAnnotationList() {
        int count = this.lookup.annotations().count().intValue();
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("测试 AnnotationList 类 get 方法，返回注解信息")
    void givenClassFileThenReturnGetOfAnnotationList() {
        AnnotationInfo annotationInfo = this.lookup.annotations().get(U2.ZERO);
        String type = this.constantPool.get(annotationInfo.typeIndex()).toString();
        assertThat(type).isEqualTo("Lmodelengine/fitframework/jvm/test/AssignmentTarget;");
    }

    @Test
    @DisplayName("测试 AnnotationInfo 类 attribute 方法，返回注解信息属性")
    void givenClassFileThenReturnAttributeOfAnnotationInfo() {
        AnnotationInfo annotationInfo = this.lookup.annotations().get(U2.ZERO);
        AttributeInfo attribute = annotationInfo.attribute();
        int attributeLength = attribute.attributeLength().intValue();
        assertThat(attributeLength).isEqualTo(ATTRIBUTE_LENGTH);
    }

    @Test
    @DisplayName("测试 AnnotationInfo 类 file 方法，返回 classFile 信息")
    void givenClassFileThenReturnFileOfAnnotationInfo() {
        AnnotationInfo annotationInfo = this.lookup.annotations().get(U2.ZERO);
        ClassFile classFile = annotationInfo.file();
        assertThat(classFile).isEqualTo(this.constantPool.file());
    }

    @Test
    @DisplayName("测试 AnnotationElementValuePairList 类 annotation 方法，返回注解信息")
    void givenClassFileThenReturnAnnotationOfAnnotationElementValuePairList() {
        AnnotationInfo annotation = this.lookup.annotations().get(U2.ZERO);
        AnnotationElementValuePairList elementValuePairs = annotation.elements();
        AnnotationInfo annotationInfo = elementValuePairs.annotation();
        AttributeInfo attribute = annotationInfo.attribute();
        int attributeLength = attribute.attributeLength().intValue();
        assertThat(attributeLength).isEqualTo(ATTRIBUTE_LENGTH);
    }

    @Test
    @DisplayName("测试 AnnotationElementValuePairList 类 count 方法，返回注解属性数量")
    void givenClassFileThenReturnCountOfAnnotationElementValuePairList() {
        AnnotationInfo annotation = this.lookup.annotations().get(U2.ZERO);
        AnnotationElementValuePairList elementValuePairs = annotation.elements();
        int count = elementValuePairs.count().intValue();
        int length = AssignmentTarget.class.getDeclaredMethods().length;
        assertThat(count).isEqualTo(length);
    }

    @Test
    @DisplayName("测试 AnnotationElementValuePairList 类 get 方法，返回注解元素键值对信息")
    void givenClassFileThenReturnGetOfAnnotationElementValuePairList() {
        AnnotationInfo annotation = this.lookup.annotations().get(U2.ZERO);
        AnnotationElementValuePairList elementValuePairs = annotation.elements();
        AnnotationElementValuePair valuePair = elementValuePairs.get(U2.ZERO);
        AnnotationInfo newAnnotation = valuePair.annotation();
        assertThat(newAnnotation).isEqualTo(annotation);
    }

    @Test
    @DisplayName("测试 AnnotationElementValuePair 类 get 方法，返回注解属性信息")
    void givenClassFileThenReturnAttributeOfAnnotationElementValuePair() {
        AnnotationInfo annotation = this.lookup.annotations().get(U2.ZERO);
        AnnotationElementValuePairList elementValuePairs = annotation.elements();
        AnnotationElementValuePair valuePair = elementValuePairs.get(U2.ZERO);
        AttributeInfo attributeInfo = valuePair.attribute();
        AttributeInfo newAttributeInfo = annotation.attribute();
        assertThat(attributeInfo).isEqualTo(newAttributeInfo);
    }

    @Test
    @DisplayName("测试 AnnotationElementValuePair 类 file 方法，返回 classFile 信息")
    void givenClassFileThenReturnFileOfAnnotationElementValuePair() {
        AnnotationInfo annotation = this.lookup.annotations().get(U2.ZERO);
        AnnotationElementValuePairList elementValuePairs = annotation.elements();
        AnnotationElementValuePair valuePair = elementValuePairs.get(U2.ZERO);
        ClassFile classFile = valuePair.file();
        ClassFile newClassFile = annotation.file();
        assertThat(classFile).isEqualTo(newClassFile);
    }
}
