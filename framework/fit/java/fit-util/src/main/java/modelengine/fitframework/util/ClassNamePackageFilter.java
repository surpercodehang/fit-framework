/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 为类型名称提供依据包名的过滤器。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-08-31
 */
public class ClassNamePackageFilter implements Predicate<String> {
    private static final Predicate<String> ALL_VALID_FILTER = className -> true;
    private static final String PACKAGE_SEPARATOR = ".";

    private final List<String> basePackages;

    /**
     * 使用有效的包名称初始化 {@link ClassNamePackageFilter} 类的新实例。
     *
     * @param basePackages 表示有效的包名称的 {@link List}{@code <}{@link String}{@code >}。
     */
    private ClassNamePackageFilter(List<String> basePackages) {
        this.basePackages = basePackages;
    }

    /**
     * 创建一个根据包名进行类名过滤的检验器。
     *
     * @param basePackages 表示一系列待过滤的包名集合的 {@link Collection}{@code <}{@link String}{@code >}。
     * @return 表示创建出来的过滤类名的校验器的 {@link Predicate}{@code <}{@link String}{@code >}。
     */
    public static Predicate<String> create(Collection<String> basePackages) {
        Collection<String> actualBasePackageCollection = ObjectUtils.nullIf(basePackages, Collections.emptyList());
        List<String> actualBasePackages = actualBasePackageCollection.stream()
                .map(StringUtils::trim)
                .filter(StringUtils::isNotBlank)
                .map(ClassNamePackageFilter::appendSeparator)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(actualBasePackages)) {
            return ALL_VALID_FILTER;
        } else {
            return new ClassNamePackageFilter(actualBasePackages);
        }
    }

    @Override
    public boolean test(String className) {
        return this.basePackages.stream().anyMatch(className::startsWith);
    }

    private static String appendSeparator(String packageName) {
        if (packageName.endsWith(PACKAGE_SEPARATOR)) {
            return packageName;
        } else {
            return packageName + PACKAGE_SEPARATOR;
        }
    }
}
