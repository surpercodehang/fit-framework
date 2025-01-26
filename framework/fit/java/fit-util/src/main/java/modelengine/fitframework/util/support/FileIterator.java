/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.util.FileUtils;

import java.io.File;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * 为 {@link Iterator} 提供文件的实现。
 *
 * @author 梁济时
 * @since 2022-07-01
 */
public class FileIterator implements Iterator<File> {
    private final File root;
    private final Stack<File> stack;
    private File next;

    /**
     * 构造一个新的 {@link FileIterator} 实例。
     *
     * @param root 表示要枚举的根文件的 {@link File}。
     */
    public FileIterator(File root) {
        this.root = notNull(root, "The root file to enumerate cannot be null.");
        this.stack = new Stack<>();
        this.stack.push(this.root);
        this.moveNext();
    }

    private void moveNext() {
        while (!this.stack.empty()) {
            File nextFile = this.stack.pop();
            if (nextFile.isFile()) {
                this.next = nextFile;
                return;
            }
            File[] children = nextFile.listFiles();
            if (children == null) {
                continue;
            }
            for (int i = children.length - 1; i >= 0; i--) {
                this.stack.push(children[i]);
            }
        }
        this.next = null;
    }

    @Override
    public boolean hasNext() {
        return this.next != null;
    }

    @Override
    public File next() {
        if (this.next == null) {
            throw new NoSuchElementException();
        }
        File nextFile = this.next;
        this.moveNext();
        return nextFile;
    }

    @Override
    public String toString() {
        return FileUtils.path(this.root);
    }
}
