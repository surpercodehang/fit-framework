/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin.support;

import modelengine.fitframework.protocol.jar.Jar;
import modelengine.fitframework.protocol.jar.JarLocation;
import modelengine.fitframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 为嵌套的归档件提供发现程序。
 *
 * @author 梁济时
 * @since 2023-03-01
 */
final class NestedJarDiscovery {
    private final Jar jar;
    private final List<Collector> collectors;

    /**
     * 使用外层归档件初始化 {@link NestedJarDiscovery} 类的新实例。
     *
     * @param jar 表示待从中发现嵌套归档件的外层归档件的 {@link Jar}。
     */
    NestedJarDiscovery(Jar jar) {
        this.jar = jar;
        this.collectors = new LinkedList<>();
    }

    /**
     * 添加一个嵌套归档件的消费程序。
     *
     * @param predicate 表示待消费的归档件所需符合的条件的判定方法的 {@link Predicate}{@code <}{@link Jar.Entry}{@code >}。
     * @param consumer 表示嵌套归档件的 URL 的消费方法的 {@link Consumer}{@code <}{@link URL}{@code >}。
     */
    void addConsumer(Predicate<Jar.Entry> predicate, Consumer<URL> consumer) {
        this.collectors.add(new Collector(predicate, consumer));
    }

    /**
     * 启动发现过程。
     */
    void start() {
        for (Jar.Entry entry : this.jar.entries()) {
            accept(this.collectors, entry);
        }
    }

    private static boolean isNestedJar(Jar.Entry entry) {
        return !entry.directory() && StringUtils.endsWithIgnoreCase(entry.name(), Jar.FILE_EXTENSION);
    }

    private static void accept(Iterable<Collector> collectors, Jar.Entry entry) {
        if (!isNestedJar(entry)) {
            return;
        }
        for (Collector collector : collectors) {
            if (collector.predicate.test(entry)) {
                collector.consumer.accept(urlOfJar(entry.location().asJar()));
            }
        }
    }

    static URL urlOfJar(JarLocation location) {
        try {
            return location.toUrl();
        } catch (MalformedURLException ex) {
            throw new IllegalStateException(StringUtils.format(
                    "Failed to obtain URL of nested JAR. [location={0}]", location), ex);
        }
    }

    private static class Collector {
        private final Predicate<Jar.Entry> predicate;
        private final Consumer<URL> consumer;

        private Collector(Predicate<Jar.Entry> predicate, Consumer<URL> consumer) {
            this.predicate = predicate;
            this.consumer = consumer;
        }
    }
}
