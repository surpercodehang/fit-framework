/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.runtime.direct;

import modelengine.fitframework.jvm.ClassDeclaration;
import modelengine.fitframework.jvm.classfile.ClassFile;
import modelengine.fitframework.resource.ClassPath;
import modelengine.fitframework.resource.ResourceTree;
import modelengine.fitframework.runtime.shared.SharedUrlClassLoader;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.XmlUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 为直接调用启动场景提供公共包的类加载程序。
 *
 * @author 梁济时
 * @since 2023-02-08
 */
final class DirectSharedClassLoader extends SharedUrlClassLoader {
    private final ClassLoader frameworkClassLoader;
    private final Set<String> redirectedClassNames;

    DirectSharedClassLoader(ClassLoader frameworkClassLoader) {
        super(new URL[0], frameworkClassLoader.getParent());
        this.frameworkClassLoader = frameworkClassLoader;
        this.redirectedClassNames = new HashSet<>();
    }

    /**
     * 将类名添加到重定向的类名集合中。
     *
     * @param className 表示类名的 {@link String}。
     */
    public void redirect(String className) {
        this.redirectedClassNames.add(className);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (this.redirectedClassNames.contains(name)) {
            return this.frameworkClassLoader.loadClass(name);
        } else {
            return super.loadClass(name, resolve);
        }
    }

    static DirectSharedClassLoader create() {
        ClassLoader currentClassLoader = DirectSharedClassLoader.class.getClassLoader();
        DirectSharedClassLoader sharedClassLoader = new DirectSharedClassLoader(currentClassLoader);
        List<ClassPath> classPaths = ClassPath.fromClassLoader(currentClassLoader, false);
        for (ClassPath classPath : classPaths) {
            if (isSharedClassPath(classPath)) {
                try {
                    sharedClassLoader.addURL(classPath.url());
                } catch (MalformedURLException ex) {
                    throw new IllegalStateException(StringUtils.format(
                            "Failed to obtain URL of class path. [classpath={0}]",
                            classPath));
                }
                redirectClasses(classPath.resources(), sharedClassLoader);
            }
        }
        return sharedClassLoader;
    }

    private static boolean isSharedClassPath(ClassPath classPath) {
        if (isService(classPath)) {
            return true;
        }
        return isFrameworkShared(classPath);
    }

    private static boolean isService(ClassPath classPath) {
        ResourceTree.Node node = classPath.resources().nodeAt("FIT-INF/service.xml");
        return node != null;
    }

    private static boolean isFrameworkShared(ClassPath classPath) {
        ResourceTree.FileNode node =
                ObjectUtils.as(classPath.resources().nodeAt("FIT-INF/metadata.xml"), ResourceTree.FileNode.class);
        if (node == null) {
            return false;
        }
        try (InputStream in = node.read()) {
            String shared = readInputStream(in);
            return Boolean.parseBoolean(shared);
        } catch (IOException e) {
            throw new IllegalStateException(StringUtils.format("Failed to read FIT metadata file. [file={0}]",
                    classPath), e);
        }
    }

    private static String readInputStream(InputStream in) {
        Document xml = XmlUtils.load(in);
        Element direct = XmlUtils.child(xml, "metadata/direct");
        return XmlUtils.content(direct, "shared");
    }

    private static void redirectClasses(ResourceTree resources, DirectSharedClassLoader loader) {
        resources.traverse(node -> {
            if (!StringUtils.endsWithIgnoreCase(node.name(), ClassFile.FILE_EXTENSION)) {
                return;
            }
            ClassFile file;
            try (InputStream in = node.read()) {
                file = new ClassFile(in);
            } catch (IOException ex) {
                return;
            }
            ClassDeclaration declaration = ClassDeclaration.load(file);
            loader.redirect(declaration.name());
        });
    }
}
