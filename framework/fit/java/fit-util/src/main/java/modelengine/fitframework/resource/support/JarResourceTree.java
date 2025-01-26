/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource.support;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.inspection.Nullable;
import modelengine.fitframework.protocol.jar.Jar;
import modelengine.fitframework.protocol.jar.JarEntryLocation;
import modelengine.fitframework.resource.ResourceTree;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 为 {@link ResourceTree} 提供基于 JAR 的实现。
 *
 * @author 梁济时
 * @since 2023-02-10
 */
final class JarResourceTree extends AbstractResourceTree {
    private final Jar jar;

    JarResourceTree(Jar jar) {
        this.jar = jar;
        for (Jar.Entry entry : this.jar.entries()) {
            if (entry.directory()) {
                continue;
            }
            String[] path = StringUtils.split(entry.name(), JarEntryLocation.ENTRY_PATH_SEPARATOR);
            ResourceNodeCollection collection = super.roots();
            int index = path.length - 1;
            for (int i = 0; i < index; i++) {
                ResourceTreeDirectoryNode directory = collection.mkdirs(path[i]);
                collection = directory.children();
            }
            FileNode file = new FileNode(collection, path[index], entry);
            collection.add(file);
        }
    }

    final Jar jar() {
        return this.jar;
    }

    @Override
    public URL location() throws MalformedURLException {
        return this.jar.location().toUrl();
    }

    @Override
    public String toString() {
        return this.jar.toString();
    }

    private static final class FileNode extends AbstractResourceNode implements ResourceTree.FileNode {
        private final ResourceNodeCollection collection;
        private final String name;
        private final Jar.Entry entry;

        private FileNode(ResourceNodeCollection collection, String name, Jar.Entry entry) {
            this.collection = collection;
            this.name = name;
            this.entry = entry;
        }

        @Override
        public URL url() throws MalformedURLException {
            return this.entry.location().toUrl();
        }

        @Override
        public InputStream read() throws IOException {
            return this.entry.read();
        }

        @Nullable
        @Override
        public DirectoryNode directory() {
            return this.collection.directory();
        }

        @Nonnull
        @Override
        public ResourceTree tree() {
            return this.collection.tree();
        }

        @Override
        public String name() {
            return this.filename();
        }

        @Override
        public String filename() {
            return this.name;
        }

        @Override
        public String path() {
            return this.entry.name();
        }
    }
}
