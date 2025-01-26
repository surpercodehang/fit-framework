/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.interpreter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * oh的上下文，一般用于获取执行结果
 *
 * @since 1.0
 */
public class Oh {
    private final Object rawData;

    private final Map<String, Object> fields;

    private final OhType type;

    /**
     * 构造函数，用于创建一个Oh对象，该对象包含一个映射类型的字段
     *
     * @param rawData 原始数据
     * @param type Oh对象的类型
     * @param fields 一个包含多个字段的映射
     */
    public Oh(Object rawData, OhType type, Map<String, Object> fields) {
        this.rawData = rawData;
        this.type = type;
        this.fields = fields;
    }

    /**
     * 构造函数，用于创建一个Oh对象，该对象包含一个列表类型的字段
     *
     * @param fields 一个包含多个字段的列表
     */
    public Oh(List<Object> fields) {
        this.rawData = null;
        this.type = OhType.LIST;
        this.fields = new HashMap<>();
        for (int i = 0; i < fields.size(); i++) {
            this.fields.put(Integer.toString(i), fields.get(0));
        }
    }

    /**
     * 获取Oh对象中指定索引的字段
     *
     * @param index 字段的索引
     * @return 返回指定索引的字段
     */
    public Object get(Integer index) {
        return this.get(index.toString());
    }

    /**
     * 获取Oh对象中指定键的字段
     *
     * @param key 字段的键
     * @return 返回指定键的字段
     */
    public Object get(String key) {
        return this.fields.get(key);
    }

    /**
     * 获取Oh对象的类型
     *
     * @return 返回Oh对象的类型
     */
    public OhType type() {
        return this.type;
    }

    /**
     * 获取Oh对象中字段的数量
     *
     * @return 返回Oh对象中字段的数量
     */
    public int size() {
        return fields.size();
    }
}
