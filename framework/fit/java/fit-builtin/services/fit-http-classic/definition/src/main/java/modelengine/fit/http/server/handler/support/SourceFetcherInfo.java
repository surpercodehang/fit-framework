/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.http.server.handler.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.server.handler.SourceFetcher;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

/**
 * 表示数据来源器的相关信息。
 *
 * @author 邬涨财
 * @since 2024-02-17
 */
public class SourceFetcherInfo {
    private final SourceFetcher sourceFetcher;
    private final String destinationName;
    private final boolean isDestinationArray;

    /**
     * 通过数据来源获取器、目标数据名字和目标数据是否为数组标记来初始化 {@link SourceFetcherInfo} 的新实例。
     *
     * @param sourceFetcher 表示数据来源获取器的 {@link SourceFetcher}。
     * @param destinationName 表示目标数据名字的 {@link String}。
     * @param isDestinationArray 表示目标数据是否为数组的标记的 {@code boolean}。
     */
    public SourceFetcherInfo(SourceFetcher sourceFetcher, String destinationName, boolean isDestinationArray) {
        this.sourceFetcher = notNull(sourceFetcher, "The source fetcher cannot be null.");
        this.destinationName = ObjectUtils.nullIf(destinationName, StringUtils.EMPTY);
        this.isDestinationArray = isDestinationArray;
    }

    /**
     * 获取数据来源获取器。
     *
     * @return 表示获取到的数据来源获取器的 {@link SourceFetcher}。
     */
    public SourceFetcher sourceFetcher() {
        return this.sourceFetcher;
    }

    /**
     * 获取目标数据名字。
     *
     * @return 表示获取到的目标数据名字的 {@link String}。
     */
    public String destinationName() {
        return this.destinationName;
    }

    /**
     * 获取目标数据是否为数组的标记。
     *
     * @return 表示目标数据是否为数组的标记的 {@code boolean}。
     */
    public boolean isDestinationArray() {
        return this.isDestinationArray;
    }
}
