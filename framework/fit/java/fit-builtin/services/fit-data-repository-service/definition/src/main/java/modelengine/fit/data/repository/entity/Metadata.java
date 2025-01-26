/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.data.repository.entity;

/**
 * 表示缓存数据的元数据信息。
 *
 * @author 邬涨财
 * @since 2024-01-22
 */
public class Metadata {
    private String type;
    private int length;

    /**
     * 设置元数据的类型名。
     *
     * @param type 表示待设置的元数据的类型名的 {@link String}。
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取元数据的类型名。
     *
     * @return 表示元数据的类型名的 {@link String}。
     */
    public String getType() {
        return this.type;
    }

    /**
     * 设置元数据的长度。
     *
     * @param length 表示待设置的元数据的长度的 {@code int}。
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * 获取元数据的长度。
     *
     * @return 表示元数据的长度的 {@code int}。
     */
    public int getLength() {
        return this.length;
    }

    @Override
    public String toString() {
        return "Metadata{type='" + type + ", length=" + length + '}';
    }
}
