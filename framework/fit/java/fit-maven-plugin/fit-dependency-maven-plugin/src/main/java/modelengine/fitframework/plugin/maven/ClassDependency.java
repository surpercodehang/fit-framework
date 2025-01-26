/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.maven;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * 为类型文件中存在的依赖提供定义。
 *
 * @author 梁济时
 * @since 2020-10-09
 */
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassDependency {
    private static final String CLASS_FILE_EXTENSION = ".java";
    private static final String CLASS_FILE_CHARSET = "UTF-8";
    private static final char CLASS_PACKAGE_SEPARATOR_CHAR = '.';

    private static final String CLASS_IMPORT_PREFIX = "import ";
    private static final String STATIC_IMPORT_PREFIX = "import static ";

    private final File classFile;
    private final String className;
    private final List<String> dependencies;

    /**
     * 提供文件返回该文件的依赖项
     *
     * @param sourceCodeDirectory 表示{@link File} 所在的目录
     * @param file 表示{@link File} 存放的路径
     * @return 表示返回的 {@link Optional<ClassDependency>} 实体
     * @throws MojoExecutionException 压缩过程中发生的IO异常
     */
    public static Optional<ClassDependency> load(File sourceCodeDirectory, File file) throws MojoExecutionException {
        if (!file.getName().endsWith(CLASS_FILE_EXTENSION)) {
            return Optional.empty();
        }
        List<String> dependedClassNames = new ArrayList<>();
        try (Scanner scanner = new Scanner(file, CLASS_FILE_CHARSET)) {
            while (scanner.hasNextLine()) {
                getDependedClassName(scanner.nextLine()).ifPresent(dependedClassNames::add);
            }
        } catch (IOException ex) {
            throw new MojoExecutionException("Fail to read class file: " + file.getName(), ex);
        }
        String relativePath = getRelativePath(file, sourceCodeDirectory);
        String name = relativePath.replace(File.separatorChar, CLASS_PACKAGE_SEPARATOR_CHAR);
        return Optional.of(new ClassDependency(file, name, Collections.unmodifiableList(dependedClassNames)));
    }

    private static Optional<String> getDependedClassName(String line) {
        if (line.startsWith(STATIC_IMPORT_PREFIX)) {
            return Optional.of(line.substring(STATIC_IMPORT_PREFIX.length(), line.lastIndexOf('.')));
        } else if (line.startsWith(CLASS_IMPORT_PREFIX)) {
            return Optional.of(line.substring(CLASS_IMPORT_PREFIX.length(), line.length() - 1));
        } else {
            return Optional.empty();
        }
    }

    private static String getRelativePath(File classFile, File sourceCodeDirectory) throws MojoExecutionException {
        String classFilePath = getCanonicalPath(classFile);
        String sourceCodeDirectoryPath = getCanonicalPath(sourceCodeDirectory);
        if (!classFilePath.startsWith(sourceCodeDirectoryPath)) {
            throw new MojoExecutionException("Class file not in source code directory: " + classFile.getName());
        }
        String relativePath = classFilePath.substring(sourceCodeDirectoryPath.length());
        if (relativePath.charAt(0) == File.separatorChar) {
            relativePath = relativePath.substring(1);
        }
        return relativePath;
    }

    private static String getCanonicalPath(File file) throws MojoExecutionException {
        try {
            return file.getCanonicalPath();
        } catch (IOException ex) {
            throw new MojoExecutionException("Fail to fetch canonical path for file: " + file.getName());
        }
    }
}
