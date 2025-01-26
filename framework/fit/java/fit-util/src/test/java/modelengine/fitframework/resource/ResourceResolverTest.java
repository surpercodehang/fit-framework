/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource;

import static org.junit.jupiter.api.Assertions.fail;

import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 表示 {@link ResourceResolver} 的单元测试。
 *
 * @author 梁济时
 * @since 2022-07-21
 */
@DisplayName("测试 ResourceResolver 类")
class ResourceResolverTest {
    private static final String DIRECTORY_PREFIX = "ResourceResolver-";

    private static File directory;
    private static ResourceResolver resolver;
    private static URLClassLoader loader;

    private static final Element JAR_FIT_ROOT = new Element(0x01, "jar", "fit-root.txt");
    private static final Element JAR_FIT_HELLO = new Element(0x02, "jar", "conf/fit-hello.txt");
    private static final Element FILE_FIT_ROOT = new Element(0x04, "file", "fit-root.txt");
    private static final Element FILE_FIT_HELLO = new Element(0x08, "file", "conf/fit-hello.txt");
    private static final Element[] ELEMENTS =
            new Element[] {JAR_FIT_ROOT, JAR_FIT_HELLO, FILE_FIT_ROOT, FILE_FIT_HELLO};

    @BeforeAll
    static void setup() throws IOException {
        // directory 临时目录结构：
        // fit-root.txt
        // conf
        // +- fit-hello.txt
        directory = Files.createTempDirectory(DIRECTORY_PREFIX).toFile().getCanonicalFile();
        Files.createDirectory(new File(directory, "directory").toPath());
        Files.write(new File(directory, "directory/fit-root.txt").toPath(),
                bytes("resource/directory/fit-root.txt"),
                StandardOpenOption.CREATE_NEW);
        Files.createDirectory(new File(directory, "directory/conf").toPath());
        Files.write(new File(directory, "directory/conf/fit-hello.txt").toPath(),
                bytes("resource/directory/conf/fit-hello.txt"),
                StandardOpenOption.CREATE_NEW);

        // direct.jar 临时文件结构：
        // fit-root.txt
        // conf
        // +- fit-hello.txt
        try (FileOutputStream out = new FileOutputStream(new File(directory, "direct.jar"))) {
            out.write(zip(MapBuilder.<String, byte[]>get()
                    .put("fit-root.txt", bytes("resource/jar/fit-root.txt"))
                    .put("conf", null)
                    .put("conf/fit-hello.txt", bytes("resource/jar/conf/fit-hello.txt"))
                    .build()));
        }

        String baseUrl = directory.toURI().toURL().toExternalForm();
        URL[] urls = new URL[] {
                new URL(baseUrl + "directory/"), new URL(baseUrl + "direct.jar")
        };
        loader = new URLClassLoader(urls, null);
        resolver = ResourceResolver.forClassLoader(loader);
    }

    private static byte[] zip(Map<String, byte[]> entries) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            try (ZipOutputStream zip = new ZipOutputStream(out)) {
                for (Map.Entry<String, byte[]> entry : entries.entrySet()) {
                    entry(zip, entry.getKey(), entry.getValue());
                }
            }
            return out.toByteArray();
        }
    }

    private static void entry(ZipOutputStream zip, String name, byte[] data) throws IOException {
        if (data == null) {
            ZipEntry entry = new ZipEntry(name + "/");
            zip.putNextEntry(entry);
        } else {
            ZipEntry entry = new ZipEntry(name);
            zip.putNextEntry(entry);
            zip.write(data);
        }
    }

    private static byte[] bytes(String key) throws IOException {
        List<byte[]> buffers = new LinkedList<>();
        int total = 0;
        try (InputStream in = IoUtils.resource(ResourceResolverTest.class.getClassLoader(), key)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) >= 0) {
                buffers.add(Arrays.copyOf(buffer, read));
                total += read;
            }
        }
        byte[] bytes = new byte[total];
        int offset = 0;
        for (byte[] buffer : buffers) {
            System.arraycopy(buffer, 0, bytes, offset, buffer.length);
            offset += buffer.length;
        }
        return bytes;
    }

    @AfterAll
    static void teardown() throws IOException {
        loader.close();
        Stack<File> stack = new Stack<>();
        stack.push(directory);
        Stack<File> files = new Stack<>();
        while (!stack.isEmpty()) {
            File file = stack.pop();
            Optional.ofNullable(file.listFiles()).map(Stream::of).orElse(Stream.empty()).forEach(stack::push);
            files.push(file);
        }
        while (!files.isEmpty()) {
            Files.delete(files.pop().toPath());
        }
        loader.close();
    }

    static int match(Resource[] resources) throws MalformedURLException {
        int result = 0;
        for (Resource resource : resources) {
            for (Element element : ELEMENTS) {
                result = element.match(result, resource);
            }
        }
        return result;
    }

    static void verify(int flags, Element... elements) {
        for (Element element : elements) {
            element.verify(flags);
        }
    }

    @Test
    @DisplayName("通过精确的路径解析资源")
    void should_resolve_resources_by_exact_path() throws IOException {
        Resource[] resources = resolver.resolve("fit-root.txt");
        int flags = match(resources);
        verify(flags, JAR_FIT_ROOT, FILE_FIT_ROOT);
    }

    @Test
    @DisplayName("通过通配符解析资源")
    void should_resolve_resources_by_wildcard_path() throws Exception {
        Resource[] resources = resolver.resolve("**/fit-*.txt");
        int flags = match(resources);
        verify(flags, JAR_FIT_ROOT, JAR_FIT_HELLO, FILE_FIT_ROOT, FILE_FIT_HELLO);
    }

    private static final class Element {
        private final int flag;
        private final String protocol;
        private final String key;

        private Element(int flag, String protocol, String key) {
            this.flag = flag;
            this.protocol = protocol;
            this.key = key;
        }

        int match(int flags, Resource resource) throws MalformedURLException {
            int result = flags;
            if (StringUtils.equalsIgnoreCase(this.protocol, resource.url().getProtocol())
                    && StringUtils.endsWithIgnoreCase(resource.url().toExternalForm(), this.key)) {
                result |= this.flag;
            }
            return result;
        }

        void verify(int flags) {
            if ((flags & this.flag) != this.flag) {
                fail(StringUtils.format("Missing resource. [protocol={0}, key={1}]", this.protocol, this.key));
            }
        }
    }
}
