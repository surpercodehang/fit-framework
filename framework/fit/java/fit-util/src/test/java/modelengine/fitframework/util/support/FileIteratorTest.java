/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * {@link FileIterator} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-14
 */
@DisplayName("测试 FileIterator 类")
class FileIteratorTest {
    private FileIterator iterator;
    private String prefix;

    @BeforeEach
    void setUp() throws URISyntaxException {
        final URL url = this.getClass().getResource("");
        File file = new File(url.toURI());
        this.prefix = new File(this.getClass().getResource("").toURI()).getPath();
        this.iterator = new FileIterator(file);
    }

    @Test
    @DisplayName("当提供文件迭代器进行迭代文件时，返回迭代的文件")
    void givenFileIteratorWhenIteratorThenReturnElement() {
        final List<String> list = new ArrayList<>();
        while (this.iterator.hasNext()) {
            final File next = this.iterator.next();
            list.add(next.toString());
        }
        assertThat(list).hasSizeGreaterThan(0).allMatch(ele -> ele.startsWith(this.prefix));
    }

    @Test
    @DisplayName("当下一个文件为 null 时，抛出异常")
    void givenNextIsNullThenThrowException() {
        FileIterator fileIterator = new FileIterator(new File(""));
        assertThatThrownBy(fileIterator::next).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("测试方法：toString()")
    void testToString() {
        final String toString = this.iterator.toString();
        assertThat(toString).isEqualTo(this.prefix);
    }
}
