/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.support.PropertyKey;
import modelengine.fitframework.util.support.PropertyKeys;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.IntStream;

/**
 * 提供解析 .properties 文件的工具类。
 *
 * @author 季聿阶
 * @since 2021-01-15
 */
public class PropertiesUtils {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private PropertiesUtils() {}

    /**
     * 将指定的 {@code .properties} 文件的内容转换成深层嵌套的键值对的形式。
     *
     * @param file 表示指定的 {@code .properties} 文件的 {@link File}。
     * @return 表示转换后的键值对的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @throws IllegalArgumentException 当 {@code file} 为 {@code null} 时。
     * @throws IllegalStateException 当读取文件过程中发生输入输出异常时。
     * @see #mapFrom(Properties) 深层嵌套的键值对格式
     */
    public static Map<String, Object> mapFrom(File file) {
        Validation.notNull(file, "The properties file cannot be null.");
        if (!file.exists()) {
            return Collections.emptyMap();
        }
        try (InputStream in = new FileInputStream(file)) {
            return mapFrom(in);
        } catch (IOException e) {
            throw new IllegalStateException(StringUtils.format(
                    "Failed to load configuration from properties. [file={0}]",
                    file.getName()), e);
        }
    }

    /**
     * 将指定的 {@code .properties} 文件的输入流的内容转换成深层嵌套的键值对的形式。
     *
     * @param in 表示指定的 {@code .properties} 文件的输入流的 {@link InputStream}。
     * @return 表示转换后的键值对的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @throws IllegalArgumentException 当 {@code in} 为 {@code null} 时。
     * @throws IllegalStateException 当读取输入流过程中发生输入输出异常时。
     * @see #mapFrom(Properties) 深层嵌套的键值对格式
     */
    public static Map<String, Object> mapFrom(InputStream in) {
        Validation.notNull(in, "The input stream of properties cannot be null.");
        Properties properties = new Properties();
        try {
            properties.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load configuration from properties input stream.", e);
        }
        return mapFrom(properties);
    }

    /**
     * 将指定的 {@code .properties} 文件的内容转换成深层嵌套的键值对的形式。
     * <p><b>注意：该键值对并非简单对 {@code .properties} 文件内容进行按 {@code =} 分隔，而是会将其中键的内容按照 {@code .}
     * 进行分隔，形成一个深层次的键值对。</b></p>
     * <p>例如：
     * <pre>
     *     genericables[0].id=g1
     *     genericables[0].name=modelengine.fit.G1
     *     genericables[0].tags[0]=t1
     *     genericables[0].tags[1]=t2
     *     genericables[0].route=a1
     *     genericables[0].fitables[0].id=modelengine.fit.F1.f1
     *     genericables[0].fitables[0].tags[0]=f1
     *     genericables[0].fitables[0].tags[1]=f2
     *     genericables[0].fitables[0].aliases[0]=a1
     *     genericables[0].fitables[0].aliases[1]=a2
     * </pre>
     * 这样的格式会转换成下面的键值对：
     * <pre>
     *     {
     *         "genericables": [
     *             {
     *                 "id": "g1",
     *                 "name": "modelengine.fit.G1",
     *                 "tags": ["t1", "t2"],
     *                 "route": "a1",
     *                 "fitables": [
     *                     {
     *                         "id": "modelengine.fit.F1.f1",
     *                         "tags": ["f1", "f2"],
     *                         "aliases": ["a1", "a2"]
     *                     }
     *                 ]
     *             }
     *         ]
     *     }
     * </pre>
     * 所有最终的值都是 {@code String}。
     * </p>
     *
     * @param properties 表示指定的 {@code .properties} 文件的 {@link Properties}。
     * @return 表示转换后的键值对的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @throws IllegalArgumentException 当 {@code properties} 为 {@code null} 时。
     */
    public static Map<String, Object> mapFrom(Properties properties) {
        Validation.notNull(properties, "The properties cannot be null.");
        SortedMap<String, String> source = new TreeMap<>();
        properties.forEach((key, value) -> source.put(ObjectUtils.cast(key), ObjectUtils.cast(value)));
        SortedMap<String, Object> result = new TreeMap<>();
        for (Map.Entry<String, String> entry : source.entrySet()) {
            PropertyKeys propertyKeys = new PropertyKeys(entry.getKey());
            mapFromPropertyKeys(propertyKeys, 0, entry.getValue(), result);
        }
        return result;
    }

    private static void mapFromArrayValue(PropertyKeys keys, int index, String value,
            SortedMap<String, Object> result) {
        PropertyKey propertyKey = keys.get(index);
        List<SortedMap<String, Object>> currentValues =
                ObjectUtils.cast(result.computeIfAbsent(propertyKey.getActualKey(),
                        key -> new ArrayList<Map<String, Object>>()));
        if (propertyKey.getArrayIndex() >= currentValues.size()) {
            int gap = propertyKey.getArrayIndex() - currentValues.size() + 1;
            IntStream.range(0, gap).forEach(times -> currentValues.add(new TreeMap<>()));
        }
        SortedMap<String, Object> actualValue = currentValues.get(propertyKey.getArrayIndex());
        mapFromPropertyKeys(keys, index + 1, value, actualValue);
    }

    private static void mapFromLastArrayValue(PropertyKey propertyKey, String value, SortedMap<String, Object> result) {
        List<String> currentValues =
                ObjectUtils.cast(result.computeIfAbsent(propertyKey.getActualKey(), key -> new ArrayList<String>()));
        currentValues.add(value);
    }

    private static void mapFromLastStringValue(PropertyKey key, String value, SortedMap<String, Object> result) {
        result.put(key.getActualKey(), value);
    }

    private static void mapFromObjectValue(PropertyKeys keys, int index, String value,
            SortedMap<String, Object> result) {
        SortedMap<String, Object> currentValue =
                ObjectUtils.cast(result.computeIfAbsent(keys.get(index).getActualKey(),
                        key -> new TreeMap<String, Object>()));
        mapFromPropertyKeys(keys, index + 1, value, currentValue);
    }

    private static void mapFromPropertyKeys(PropertyKeys keys, int index, String value,
            SortedMap<String, Object> result) {
        PropertyKey key = keys.get(index);
        if (keys.isLast(index)) {
            if (key.isArray()) {
                mapFromLastArrayValue(key, value, result);
            } else {
                mapFromLastStringValue(key, value, result);
            }
        } else {
            if (key.isArray()) {
                mapFromArrayValue(keys, index, value, result);
            } else {
                mapFromObjectValue(keys, index, value, result);
            }
        }
    }
}
