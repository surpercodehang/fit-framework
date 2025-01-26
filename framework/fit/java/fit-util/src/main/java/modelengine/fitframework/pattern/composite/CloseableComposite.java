/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.pattern.composite;

import modelengine.fitframework.util.ObjectUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为 {@link Closeable} 提供组合。
 *
 * @author 梁济时
 * @since 2022-07-27
 */
public class CloseableComposite implements Closeable {
    private final List<Closeable> closeables;

    private CloseableComposite(List<Closeable> closeables) {
        this.closeables = closeables;
    }

    @Override
    public void close() throws IOException {
        List<IOException> exceptions = new LinkedList<>();
        for (Closeable closeable : this.closeables) {
            try {
                closeable.close();
            } catch (IOException ex) {
                exceptions.add(ex);
            }
        }
        if (!exceptions.isEmpty()) {
            IOException exception = new IOException("Failed to close resources. Lookup suppressed for detail.");
            exceptions.forEach(exception::addSuppressed);
            throw exception;
        }
    }

    /**
     * 组合指定的可关闭对象。
     *
     * @param closeables 表示待组合的可关闭对象的 {@link Closeable}{@code []}。
     * @return 表示组合后的可关闭对象的 {@link Closeable}。
     */
    public static Closeable combine(Closeable... closeables) {
        return combine(ObjectUtils.<Closeable[], Collection<Closeable>>mapIfNotNull(closeables, Arrays::asList));
    }

    /**
     * 组合指定的可关闭对象。
     *
     * @param closeables 表示待组合的可关闭对象的 {@link Collection}{@code <}{@link Closeable}{@code >}。
     * @return 表示组合后的可关闭对象的 {@link Closeable}。
     */
    public static Closeable combine(Collection<? extends Closeable> closeables) {
        List<Closeable> actual = Optional.ofNullable(closeables)
                .map(Collection::stream)
                .orElse(Stream.empty())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (actual.isEmpty()) {
            return null;
        } else if (actual.size() > 1) {
            return new CloseableComposite(actual);
        } else {
            return actual.get(0);
        }
    }
}
