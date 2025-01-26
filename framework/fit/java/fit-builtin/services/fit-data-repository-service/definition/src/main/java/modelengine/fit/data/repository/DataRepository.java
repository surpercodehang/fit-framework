/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.data.repository;

import modelengine.fit.data.repository.entity.Metadata;
import modelengine.fitframework.annotation.Genericable;

/**
 * 表示数据仓库数据的服务。
 *
 * @author 季聿阶
 * @since 2024-01-21
 */
public interface DataRepository {
    /**
     * 获取缓存数据的元数据。
     *
     * @param id 表示缓存数据的唯一标识的 {@link String}。
     * @return 表示获取到的缓存数据的元数据的 {@link Metadata}。
     */
    @Genericable(id = "modelengine.fit.bigdata.cache.read.meta")
    Metadata getMetadata(String id);

    /**
     * 获取字符串类型的缓存数据。
     *
     * @param id 表示缓存数据的唯一标识的 {@link String}。
     * @return 表示获取到的字符串类型的缓存数据的 {@link String}。
     */
    @Genericable(id = "modelengine.fit.bigdata.cache.read.str")
    String getString(String id);

    /**
     * 获取字节数组类型的缓存数据。
     *
     * @param id 表示缓存数据的唯一标识的 {@link String}。
     * @return 表示获取到的字节数组类型的缓存数据的 {@code byte[]}。
     */
    @Genericable(id = "modelengine.fit.bigdata.cache.read.bytes")
    byte[] getBytes(String id);

    /**
     * 保存指定的数据。
     *
     * @param id 表示需要保存的数据的唯一标识的 {@link String}。
     * @param data 表示需要保存的数据的 {@link Object}。
     */
    @Genericable(id = "modelengine.fit.bigdata.cache.save")
    void save(String id, Object data);

    /**
     * 删除指定的数据。
     *
     * @param id 表示需要删除的数据的唯一标识的 {@link String}。
     */
    @Genericable(id = "modelengine.fit.bigdata.cache.delete")
    void delete(String id);
}
