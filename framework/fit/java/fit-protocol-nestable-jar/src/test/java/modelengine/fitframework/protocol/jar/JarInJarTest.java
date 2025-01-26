/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.protocol.jar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

@DisplayName("测试 JAR in JAR")
class JarInJarTest {
    private static final Map<String, Long> NESTED_CRC32S = new HashMap<>();
    private static final String NESTED_JAR_ENTRY_NAME = "FIT-INF/lib/nested.jar";

    private static File JAR_FILE;
    private static Long NESTED_JAR_CRC32;

    @BeforeAll
    static void setupAll() throws IOException {
        JAR_FILE = Files.createTempFile("JarInJar-", ".jar").toFile();

        byte[] nestedJarBytes;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            try (JarBuilder builder = JarBuilder.of(out)) {
                addClass(builder, Handler.class);
                addClass(builder, Jar.class);
                addClass(builder, Jar.Entry.class);
                addClass(builder, Jar.EntryCollection.class);
            }
            nestedJarBytes = out.toByteArray();
        }
        try (JarBuilder builder = JarBuilder.of(JAR_FILE)) {
            NESTED_JAR_CRC32 = builder.store(NESTED_JAR_ENTRY_NAME, nestedJarBytes);
        }
    }

    private static void addClass(JarBuilder builder, Class<?> clazz) throws IOException {
        String resourceKey = clazz.getName().replace('.', '/') + ".class";
        InputStream in = JarInJarTest.class.getClassLoader().getResourceAsStream(resourceKey);
        if (in == null) {
            fail("Resource not found: " + resourceKey);
            return;
        }
        byte[] bytes;
        try {
            bytes = content(in);
        } finally {
            in.close();
        }
        long crc32 = builder.add(resourceKey, bytes);
        NESTED_CRC32S.put(resourceKey, crc32);
    }

    @AfterAll
    static void teardownAll() throws IOException {
        Files.delete(JAR_FILE.toPath());
    }

    @Test
    @DisplayName("集成测试")
    void test() throws IOException {
        Jar jar = Jar.from(JAR_FILE);
        Jar.Entry entry = jar.entries().get(NESTED_JAR_ENTRY_NAME);
        assertEquals(NESTED_JAR_CRC32, Integer.toUnsignedLong(entry.crc32()));
        Jar nestedJar = entry.asJar();
        for (Map.Entry<String, Long> nestedCrc32 : NESTED_CRC32S.entrySet()) {
            entry = nestedJar.entries().get(nestedCrc32.getKey());
            assertEquals(nestedCrc32.getValue(), Integer.toUnsignedLong(entry.crc32()));
            long crc32 = crc32(entry);
            assertEquals(nestedCrc32.getValue(), crc32);
        }
    }

    private static long crc32(Jar.Entry entry) throws IOException {
        try (InputStream in = entry.read()) {
            byte[] bytes = content(in);
            CRC32 crc32 = new CRC32();
            crc32.update(bytes);
            return crc32.getValue();
        }
    }

    private static byte[] content(InputStream in) throws IOException {
        final int bufferSize = 128;
        byte[] buffer = new byte[bufferSize];
        List<byte[]> buffers = new LinkedList<>();
        int read;
        int total = 0;
        while ((read = in.read(buffer)) > -1) {
            if (read > 0) {
                buffers.add(Arrays.copyOf(buffer, read));
                total += read;
            }
        }
        byte[] bytes = new byte[total];
        int offset = 0;
        for (byte[] current : buffers) {
            System.arraycopy(current, 0, bytes, offset, current.length);
            offset += current.length;
        }
        return bytes;
    }
}
