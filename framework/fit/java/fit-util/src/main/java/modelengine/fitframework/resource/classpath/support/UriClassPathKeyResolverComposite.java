/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource.classpath.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.resource.classpath.ClassPathKey;
import modelengine.fitframework.resource.classpath.UriClassPathKeyResolver;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 为 {@link UriClassPathKeyResolver} 提供组合模式的实现。
 *
 * @author 梁济时
 * @since 2022-07-27
 */
public class UriClassPathKeyResolverComposite implements UriClassPathKeyResolver {
    private final UriClassPathKeyResolver resolver1;
    private final UriClassPathKeyResolver resolver2;

    /**
     * 使用待组合的两个类路径键解析程序初始化 {@link UriClassPathKeyResolverComposite} 类的新实例。
     *
     * @param resolver1 表示待组合的第一个类路径键解析程序的 {@link UriClassPathKeyResolver}。
     * @param resolver2 表示待组合的第二个类路径键解析程序的 {@link UriClassPathKeyResolver}。
     * @throws IllegalArgumentException {@code resolver1} 或 {@code resolver2} 为 {@code null}。
     */
    public UriClassPathKeyResolverComposite(UriClassPathKeyResolver resolver1, UriClassPathKeyResolver resolver2) {
        this.resolver1 = notNull(resolver1, "The first resolver to combine cannot be null.");
        this.resolver2 = notNull(resolver2, "The second resolver to combine cannot be null.");
    }

    @Override
    public Optional<ClassPathKey> resolve(URI uri) throws IOException {
        Optional<ClassPathKey> classpath = this.resolver1.resolve(uri);
        if (!classpath.isPresent()) {
            classpath = this.resolver2.resolve(uri);
        }
        return classpath;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {UriClassPathKeyResolverComposite.class, this.resolver1, this.resolver2});
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof UriClassPathKeyResolverComposite) {
            UriClassPathKeyResolverComposite another = (UriClassPathKeyResolverComposite) obj;
            return Objects.equals(this.resolver1, another.resolver1) && Objects.equals(this.resolver2,
                    another.resolver2);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return StringUtils.format("[resolver1={0}, resolver2={1}]", this.resolver1, this.resolver2);
    }
}
