/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.jvm.classfile.lang.U2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

/**
 * {@link MethodList} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-01-31
 */
@DisplayName("测试 MethodList 类以及子类")
class MethodListTest {
    private static final int TEST_METHOD_COUNT = 4;

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
    @DisplayName("当提供 class 文件时，返回 class 文件默认构造方法信息")
    void givenClassFileThenReturnMethodDefault() {
        AccessFlag[] accessFlags = {AccessFlag.ACC_PUBLIC};
        int accessFlag = this.getAccessFlag(accessFlags);
        this.matchMethod("<init>", accessFlag, "()V");
    }

    @Test
    @DisplayName("当提供 class 文件包含自定义方法时，返回 class 文件自定义方法信息")
    void givenClassFileThenReturnMethodCustomize() {
        AccessFlag[] accessFlags = {AccessFlag.ACC_PRIVATE};
        int accessFlag = this.getAccessFlag(accessFlags);
        this.matchMethod("test", accessFlag, "()Ljava/lang/Object;");
    }

    @Test
    @DisplayName("当提供 class 文件时，返回 class 文件方法总数")
    void givenClassFileThenReturnMethodCount() {
        int count = this.classFile.methods().count().intValue();
        assertThat(count).isEqualTo(TEST_METHOD_COUNT);
    }

    @Test
    @DisplayName("当提供 class 文件时，返回 class 文件方法属性信息")
    void givenClassFileThenReturnMethodInfo() {
        MethodList methods = this.classFile.methods();
        MethodInfo methodInfo = methods.get(U2.ZERO);
        AttributeList attributes = methodInfo.attributes();
        int count = attributes.count().intValue();
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("测试 MethodInfo 类 attributes 方法，返回正常结果")
    void givenClassFileThenReturnMethodInfoAttributes() {
        MethodList methods = this.classFile.methods();
        MethodInfo methodInfo = methods.get(U2.ZERO);
        MethodList list = methodInfo.list();
        assertThat(list).isEqualTo(methods);
        ClassFile file = methodInfo.file();
        assertThat(file).isEqualTo(this.classFile);
    }

    private void matchMethod(String name, int accessFlag, String FieldType) {
        Iterator<MethodInfo> iterator = this.classFile.methods().iterator();
        MethodInfo method = null;
        while (iterator.hasNext()) {
            MethodInfo methodInfo = iterator.next();
            String methodName = this.constantPool.get(methodInfo.nameIndex()).toString();
            if (Objects.equals(name, methodName)) {
                method = methodInfo;
                break;
            }
        }

        // 访问修饰符比较
        Validation.notNull(method, "method cannot be null.");
        int accessFlagsJvm = method.accessFlags().intValue();
        assertThat(accessFlagsJvm).isEqualTo(accessFlag);

        // 类型比较
        String type = this.constantPool.get(method.descriptorIndex()).toString();
        assertThat(type).isEqualTo(FieldType);
    }

    private int getAccessFlag(AccessFlag[] accessFlags) {
        return Arrays.stream(accessFlags).mapToInt(item -> item.value().intValue()).sum();
    }
}
