/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.jvm.classfile.attribute.ConstantValueAttribute;
import modelengine.fitframework.jvm.classfile.constant.ClassInfo;
import modelengine.fitframework.jvm.classfile.constant.StringInfo;
import modelengine.fitframework.jvm.classfile.constant.Utf8Info;
import modelengine.fitframework.jvm.classfile.lang.U2;
import modelengine.fitframework.jvm.test.AttributeTarget;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

/**
 * {@link FieldList} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-01-31
 */
@DisplayName("测试 FieldList 类以及子类")
class FieldListTest {
    private static final int FIELD_LIST_LENGTH = 11;

    private ClassFile classFile;
    private ConstantPool constantPool;

    @BeforeEach
    @DisplayName("初始化 ClassFile 类")
    void init() throws IOException {
        try (InputStream inputStream = ClassFileTest.class.getClassLoader()
                .getResourceAsStream("modelengine/fitframework/jvm/test/AttributeTarget.class")) {
            this.classFile = new ClassFile(inputStream);
            this.constantPool = this.classFile.constants();
        }
    }

    @Test
    @DisplayName("当提供 class 文件时，返回 class 文件主类常量")
    void givenClassFileThenReturnConstantClass() {
        Constant constant = this.constantPool.get(this.classFile.thisClass());
        ClassInfo classInfo = ObjectUtils.cast(constant);
        Utf8Info utf8Info = this.constantPool.get(classInfo.nameIndex());
        String classJvm = utf8Info.stringValue();
        String classReflect = StringUtils.replace(AttributeTarget.class.getName(), '.', '/');
        assertThat(classJvm).isEqualTo(classReflect);
    }

    @Test
    @DisplayName("当提供 class 文件包含字段 byte 时，返回 class 文件 byte 字段正常信息")
    void givenClassFileThenReturnFieldByte() throws IOException {
        AccessFlag[] accessFlags = {AccessFlag.ACC_FINAL, AccessFlag.ACC_PUBLIC};
        int accessFlag = this.getAccessFlag(accessFlags);
        this.matchType("byteTest", accessFlag, "B", "0");
    }

    @Test
    @DisplayName("当提供 class 文件包含字段 char 时，返回 class 文件 char 字段正常信息")
    void givenClassFileThenReturnFieldChar() throws IOException {
        AccessFlag[] accessFlags = {AccessFlag.ACC_FINAL, AccessFlag.ACC_PUBLIC};
        int accessFlag = this.getAccessFlag(accessFlags);
        this.matchType("charTest", accessFlag, "C", Integer.valueOf('a').toString());
    }

    @Test
    @DisplayName("当提供 class 文件包含字段 double 时，返回 class 文件 double 字段正常信息")
    void givenClassFileThenReturnFieldDouble() throws IOException {
        AccessFlag[] accessFlags = {AccessFlag.ACC_FINAL, AccessFlag.ACC_PRIVATE};
        int accessFlag = this.getAccessFlag(accessFlags);
        this.matchType("doubleTest", accessFlag, "D", "12.1");
    }

    @Test
    @DisplayName("当提供 class 文件包含字段 float 时，返回 class 文件 float 字段正常信息")
    void givenClassFileThenReturnFieldFloat() throws IOException {
        AccessFlag[] accessFlags = {AccessFlag.ACC_FINAL, AccessFlag.ACC_PRIVATE};
        int accessFlag = this.getAccessFlag(accessFlags);
        this.matchType("floatTest", accessFlag, "F", "11.2");
    }

    @Test
    @DisplayName("当提供 class 文件包含字段 int 时，返回 class 文件 int 字段正常信息")
    void givenClassFileThenReturnFieldInt() throws IOException {
        AccessFlag[] accessFlags = {AccessFlag.ACC_STATIC, AccessFlag.ACC_FINAL, AccessFlag.ACC_PUBLIC};
        int accessFlag = this.getAccessFlag(accessFlags);
        this.matchType("INT_TEST", accessFlag, "I", "10");
    }

    @Test
    @DisplayName("当提供 class 文件包含字段 long 时，返回 class 文件 long 字段正常信息")
    void givenClassFileThenReturnFieldLong() throws IOException {
        AccessFlag[] accessFlags = {AccessFlag.ACC_PROTECTED, AccessFlag.ACC_FINAL};
        int accessFlag = this.getAccessFlag(accessFlags);
        this.matchType("longTest", accessFlag, "J", "11");
    }

    @Test
    @DisplayName("当提供 class 文件包含字段 short 时，返回 class 文件 short 字段正常信息")
    void givenClassFileThenReturnFieldShort() throws IOException {
        AccessFlag[] accessFlags = {AccessFlag.ACC_PROTECTED, AccessFlag.ACC_FINAL};
        int accessFlag = this.getAccessFlag(accessFlags);
        this.matchType("shortTest", accessFlag, "S", "13");
    }

    @Test
    @DisplayName("当提供 class 文件包含字段 boolean 时，返回 class 文件 boolean 字段正常信息")
    void givenClassFileThenReturnFieldBoolean() throws IOException {
        AccessFlag[] accessFlags = {AccessFlag.ACC_PROTECTED, AccessFlag.ACC_FINAL};
        int accessFlag = this.getAccessFlag(accessFlags);
        this.matchType("boolTest", accessFlag, "Z", "1");
    }

    @Test
    @DisplayName("当提供 class 文件包含字段 String 时，返回 class 文件 String 字段正常信息")
    void givenClassFileThenReturnFieldString() throws IOException {
        AccessFlag[] accessFlags = {AccessFlag.ACC_PRIVATE, AccessFlag.ACC_FINAL};
        int accessFlag = this.getAccessFlag(accessFlags);
        this.matchType("strTest", accessFlag, "Ljava/lang/String;", "aaaa");
    }

    @Test
    @DisplayName("当提供 class 文件包含字段 arr 时，返回 class 文件 arr 字段正常信息")
    void givenClassFileThenReturnFieldAttributeTarget() throws IOException {
        AccessFlag[] accessFlags = {AccessFlag.ACC_PRIVATE, AccessFlag.ACC_FINAL};
        int accessFlag = this.getAccessFlag(accessFlags);
        this.matchType("arrTest", accessFlag, "[Ljava/lang/String;", "null");
    }

    @Test
    @DisplayName("当提供 class 文件时，返回 class 文件字段信息列表长度")
    void givenClassFileThenReturnFieldCount() {
        FieldList fields = this.classFile.fields();
        int count = fields.count().intValue();
        assertThat(count).isEqualTo(FIELD_LIST_LENGTH);
    }

    @Test
    @DisplayName("当提供 class 文件时，返回 class 文件字段信息")
    void givenClassFileThenReturnField() {
        FieldList fields = this.classFile.fields();
        FieldInfo fieldInfo = fields.get(U2.ZERO);
        FieldList list = fieldInfo.list();
        assertThat(list).isEqualTo(fields);
    }

    private void matchType(String name, int accessFlag, String FieldType, Object value) throws IOException {
        Iterator<FieldInfo> iterator = this.classFile.fields().iterator();
        FieldInfo fieldJvm = null;
        while (iterator.hasNext()) {
            FieldInfo fieldInfo = iterator.next();
            String fieldName = this.constantPool.get(fieldInfo.nameIndex()).toString();
            if (Objects.equals(name, fieldName)) {
                fieldJvm = fieldInfo;
                break;
            }
        }

        // 访问修饰符比较
        Validation.notNull(fieldJvm, "field cannot be null.");
        int accessFlagsJvm = fieldJvm.accessFlags().intValue();
        assertThat(accessFlagsJvm).isEqualTo(accessFlag);

        // 类型比较
        String type = this.constantPool.get(fieldJvm.descriptorIndex()).toString();
        assertThat(type).isEqualTo(FieldType);

        // 声明值比较，暂时不支持数组类型的获取值
        if (!FieldType.startsWith("[")) {
            ConstantValueAttribute lookup = ConstantValueAttribute.lookup(fieldJvm.attributes());
            Constant constant = this.constantPool.get(lookup.constantValueIndex());
            String valueJvm;
            if (constant instanceof StringInfo) {
                U2 strIndex = ((StringInfo) constant).stringIndex();
                valueJvm = this.constantPool.get(strIndex).toString();
            } else {
                valueJvm = lookup.toString();
            }
            assertThat(valueJvm).isEqualTo(value);
        }
    }

    private int getAccessFlag(AccessFlag[] accessFlags) {
        return Arrays.stream(accessFlags).mapToInt(item -> item.value().intValue()).sum();
    }
}
