/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.build.service;

import modelengine.fitframework.exception.ErrorCode;
import modelengine.fitframework.jvm.classfile.ClassFile;
import modelengine.fitframework.jvm.classfile.ConstantPool;
import modelengine.fitframework.jvm.classfile.annotation.AnnotationElementValue;
import modelengine.fitframework.jvm.classfile.annotation.AnnotationElementValuePair;
import modelengine.fitframework.jvm.classfile.annotation.AnnotationInfo;
import modelengine.fitframework.jvm.classfile.attribute.RuntimeVisibleAnnotationsAttribute;
import modelengine.fitframework.jvm.classfile.constant.IntegerInfo;
import modelengine.fitframework.jvm.classfile.constant.Utf8Info;
import modelengine.fitframework.jvm.classfile.lang.U2;
import modelengine.fitframework.plugin.maven.support.AbstractCompiler;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为服务提供编译程序。
 *
 * @author 季聿阶
 * @since 2023-06-17
 */
final class ServiceCompiler extends AbstractCompiler {
    private static final String SERVICE_MANIFEST = "service.xml";
    private static final String ERROR_MANIFEST = "errors.xml";

    private final Map<String, Integer> errors = new HashMap<>();

    ServiceCompiler(MavenProject project, Log log) {
        super(project, log, null);
    }

    @Override
    protected void output(String outputDirectory, String fitRootDirectory) throws MojoExecutionException {
        this.scanClassFiles(outputDirectory);
        this.outputErrorManifest(fitRootDirectory);
        this.outputServiceManifest(fitRootDirectory);
    }

    private void scanClassFiles(String outputDirectory) throws MojoExecutionException {
        try (Stream<Path> paths = Files.walk(Paths.get(outputDirectory))) {
            List<Path> classPaths = paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(ClassFile.FILE_EXTENSION))
                    .collect(Collectors.toList());
            for (Path classPath : classPaths) {
                this.scanErrorCode(outputDirectory, classPath);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to list class files.", e);
        }
    }

    private void scanErrorCode(String outputDirectory, Path path) throws MojoExecutionException {
        String fullClassName = this.getFullClassName(outputDirectory, path);
        this.log().debug(StringUtils.format("Scan class: {0}...", fullClassName));
        try (InputStream in = Files.newInputStream(path)) {
            ClassFile classFile = new ClassFile(in);
            RuntimeVisibleAnnotationsAttribute annotationsAttribute =
                    RuntimeVisibleAnnotationsAttribute.lookup(classFile.attributes());
            if (annotationsAttribute == null) {
                return;
            }
            ConstantPool constants = classFile.constants();
            for (AnnotationInfo info : annotationsAttribute.annotations()) {
                Utf8Info annotation = ObjectUtils.cast(constants.get(info.typeIndex()));
                if (Objects.equals(annotation.stringValue(), getErrorCodeInternalName())) {
                    AnnotationElementValuePair pair = info.elements().get(U2.ZERO);
                    AnnotationElementValue.IntegerValue value = ObjectUtils.cast(pair.value());
                    int code = constants.<IntegerInfo>get(value.constValueIndex()).intValue();
                    this.errors.put(fullClassName, code);
                    this.log()
                            .info(StringUtils.format("@ErrorCode detected. [error={0}, code={1}]",
                                    fullClassName,
                                    code));
                }
            }
        } catch (IOException e) {
            throw new MojoExecutionException(StringUtils.format("Failed to load class. [file={0}]", path), e);
        }
    }

    private String getFullClassName(String outputDirectory, Path classFile) {
        return classFile.toString()
                .substring(outputDirectory.length() + 1)
                .replace(File.separator, ".")
                .replace(ClassFile.FILE_EXTENSION, "");
    }

    private static String getErrorCodeInternalName() {
        return 'L' + ErrorCode.class.getName().replace('.', '/') + ';';
    }

    private void outputErrorManifest(String outputDirectory) throws MojoExecutionException {
        if (MapUtils.isEmpty(this.errors)) {
            return;
        }
        String fileName = outputDirectory + File.separator + ERROR_MANIFEST;
        try (OutputStream out = Files.newOutputStream(new File(fileName).toPath())) {
            ErrorManifest manifest = new ErrorManifest(this.errors);
            manifest.write(out);
            this.log().info(StringUtils.format("Write error manifest. [file={0}]", fileName));
        } catch (IOException e) {
            throw new MojoExecutionException(StringUtils.format("Failed to write error manifest. [file={0}]", fileName),
                    e);
        }
    }

    private void outputServiceManifest(String outputDirectory) throws MojoExecutionException {
        String fileName = outputDirectory + File.separator + SERVICE_MANIFEST;
        try (OutputStream out = Files.newOutputStream(new File(fileName).toPath())) {
            ServiceManifest manifest = new ServiceManifest();
            manifest.write(out);
            this.log().info(StringUtils.format("Write service manifest. [file={0}]", fileName));
        } catch (IOException e) {
            throw new MojoExecutionException(StringUtils.format("Failed to write service manifest. [file={0}]",
                    fileName), e);
        }
    }
}
