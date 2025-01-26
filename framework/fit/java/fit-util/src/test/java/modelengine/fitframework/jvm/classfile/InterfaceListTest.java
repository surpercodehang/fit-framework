/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.jvm.classfile;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.jvm.classfile.constant.ClassInfo;
import modelengine.fitframework.jvm.classfile.lang.U2;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * {@link InterfaceList} 的单元测试。
 *
 * @author 郭龙飞
 * @since 2023-02-01
 */
@DisplayName("测试 InterfaceList 类以及子类")
class InterfaceListTest {
    private ClassFile classFile;
    private ConstantPool constantPool;
    private InterfaceList interfaces;

    @BeforeEach
    @DisplayName("初始化 ClassFile 类")
    void init() throws IOException {
        try (InputStream inputStream = ClassFileTest.class.getClassLoader()
                .getResourceAsStream("modelengine/fitframework/jvm/test/AttributeTarget.class")) {
            this.classFile = new ClassFile(inputStream);
            this.constantPool = this.classFile.constants();
            this.interfaces = this.classFile.interfaces();
        }
    }

    @Test
    @DisplayName("当提供 class 文件时，返回 class 文件接口 classFile 信息")
    void givenClassFileThenReturnInterfaceFile() {
        ClassFile file = this.interfaces.file();
        assertThat(this.classFile).isEqualTo(file);
    }

    @Test
    @DisplayName("当提供 class 文件时，返回 class 文件接口列数量")
    void givenClassFileThenReturnInterfaceCount() {
        int intValue = this.interfaces.count().intValue();
        assertThat(intValue).isEqualTo(1);
    }

    @Test
    @DisplayName("当提供 class 文件时，返回 class 文件接口信息")
    void givenClassFileThenReturnInterfaceInfo() {
        Constant constant = this.constantPool.get(this.interfaces.get(U2.ZERO));
        ClassInfo classInfo = ObjectUtils.cast(constant);
        String info = this.constantPool.get(classInfo.nameIndex()).toString();
        assertThat(info).isEqualTo("java/lang/Comparable");
    }

    @Test
    @DisplayName("当提供 class 文件时，返回 class 文件接口信息列表")
    void givenClassFileThenReturnInterface() {
        Iterator<U2> iterator = this.interfaces.iterator();
        StringBuilder builder = new StringBuilder();
        while (iterator.hasNext()) {
            U2 index = iterator.next();
            Constant constant = this.constantPool.get(index);
            ClassInfo classInfo = ObjectUtils.cast(constant);
            builder.append(this.constantPool.get(classInfo.nameIndex()).toString());
        }
        assertThat(builder.toString()).isEqualTo("java/lang/Comparable");
    }
}