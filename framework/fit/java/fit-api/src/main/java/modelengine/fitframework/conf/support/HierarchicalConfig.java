/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf.support;

import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.fitframework.util.ObjectUtils.mapIfNotNull;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.conf.ModifiableConfig;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 为 {@link ModifiableConfig} 提供层次化的实现。
 *
 * @author 梁济时
 * @since 2022-12-29
 */
public class HierarchicalConfig extends AbstractModifiableConfig {
    private final Map<String, Object> values;

    /**
     * 初始化 {@link HierarchicalConfig} 类的新实例。
     *
     * @param name 表示配置的名称的 {@link String}。
     */
    public HierarchicalConfig(String name) {
        this(name, null);
    }

    /**
     * 使用配置的名称及初始值初始化 {@link HierarchicalConfig} 类的新实例。
     *
     * @param name 表示配置的名称的 {@link String}。
     * @param values 表示配置的初始值的 {@link Map}{@code <?, ?>}。
     */
    public HierarchicalConfig(String name, Map<?, ?> values) {
        super(name);
        this.values = new HashMap<>();
        Map<String, Object> canonical;
        if (values != null && (canonical = canonicalizeMap(values)) != null) {
            for (Map.Entry<String, ?> entry : canonical.entrySet()) {
                if (entry.getValue() != null) {
                    this.values.put(Config.canonicalizeKey(entry.getKey()), entry.getValue());
                }
            }
        }
    }

    @Override
    public Set<String> keys() {
        Set<String> keys = new HashSet<>();
        dumpKeys(keys, ConfigPath.EMPTY, this.values);
        return keys;
    }

    @Override
    public Object getWithCanonicalKey(String key) {
        ConfigPath path = ConfigPath.parse(key);
        if (path.empty()) {
            return null;
        }
        Map<String, Object> map = path.parent().get(this.values, false);
        return mapIfNotNull(map, m -> m.get(path.name()));
    }

    @Override
    public void setWithCanonicalKey(String key, Object value) {
        ConfigPath path = ConfigPath.parse(key);
        if (path.empty()) {
            throw new IllegalArgumentException("The key of config to modify value cannot be blank.");
        }
        Object actual = canonicalize(value);
        if (actual == null) {
            this.clear(path);
            return;
        }
        Map<String, Object> map = path.parent().get(this.values, true);
        Object oldValue = map.put(path.name(), actual);
        if (!Objects.equals(oldValue, actual)) {
            this.notifyValueChanged(path.toString());
        }
    }

    private void clear(ConfigPath path) {
        this.clear(this.values, path.keys(), 0);
    }

    private void clear(Map<String, Object> values, List<String> keys, int index) {
        String key = keys.get(index);
        Object value = values.get(key);
        if (value == null) {
            return;
        }
        if (index == keys.size() - 1) {
            values.remove(key);
            return;
        }
        if (value instanceof Map) {
            Map<String, Object> next = cast(value);
            this.clear(next, keys, index + 1);
            if (next.isEmpty()) {
                values.remove(key);
            }
        }
    }

    private static Object canonicalize(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Map) {
            return canonicalizeMap((Map<?, ?>) value);
        } else if (value instanceof Collection) {
            return canonicalizeCollection((Collection<?>) value);
        } else if (value.getClass().isArray()) {
            return canonicalizeArray(value);
        } else {
            return canonicalizeScalar(value);
        }
    }

    private static Map<String, Object> canonicalizeMap(Map<?, ?> map) {
        Map<String, Object> canonical = new HashMap<>(map.size());
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            ConfigPath path = ConfigPath.parse(Config.canonicalizeKey(ObjectUtils.toString(entry.getKey())));
            if (path.empty()) {
                continue;
            }
            Object value = canonicalize(entry.getValue());
            if (value == null) {
                continue;
            }
            Map<String, Object> last = path.parent().get(canonical, true);
            last.put(path.name(), value);
        }
        if (canonical.isEmpty()) {
            return null;
        } else {
            return canonical;
        }
    }

    private static List<Object> canonicalizeCollection(Collection<?> collection) {
        List<Object> canonical = new ArrayList<>(collection.size());
        for (Object item : collection) {
            Object value = canonicalize(item);
            if (value != null) {
                canonical.add(value);
            }
        }
        if (canonical.isEmpty()) {
            return null;
        } else {
            return canonical;
        }
    }

    private static List<Object> canonicalizeArray(Object array) {
        int length = Array.getLength(array);
        List<Object> canonical = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            Object item = Array.get(array, i);
            Object value = canonicalize(item);
            if (value != null) {
                canonical.add(value);
            }
        }
        if (canonical.isEmpty()) {
            return null;
        } else {
            return canonical;
        }
    }

    /**
     * 规范化标量的值，值将被汇聚到以下类型：
     * <ul>
     *     <li>{@link java.math.BigInteger}</li>
     *     <li>{@link java.math.BigDecimal}</li>
     *     <li>{@link java.lang.String}</li>
     *     <li>{@link java.lang.Boolean}</li>
     *     <li>{@link java.util.Date}</li>
     * </ul>
     *
     * @param scalar 表示原始的标量值的 {@link Object}。
     * @return 表示规范化后的标量值的 {@link Object}。
     */
    private static Object canonicalizeScalar(Object scalar) {
        if (scalar instanceof String) {
            return canonicalizeString((String) scalar);
        } else if (scalar instanceof BigInteger || scalar instanceof BigDecimal || scalar instanceof Boolean
                || scalar instanceof Date) {
            return scalar;
        } else if (scalar instanceof Long || scalar instanceof Integer || scalar instanceof Short
                || scalar instanceof Byte) {
            return BigInteger.valueOf(ObjectUtils.<Number>cast(scalar).longValue());
        } else if (scalar instanceof Float || scalar instanceof Double) {
            return BigDecimal.valueOf(ObjectUtils.<Number>cast(scalar).doubleValue());
        } else if (scalar instanceof Character) {
            return scalar.toString();
        } else {
            throw new IllegalStateException(StringUtils.format("Unknown type of scalar. [value={0}, type={1}]",
                    scalar,
                    scalar.getClass().getName()));
        }
    }

    private static String canonicalizeString(String string) {
        String canonical = StringUtils.trim(string);
        if (StringUtils.isEmpty(canonical)) {
            return null;
        } else {
            return canonical;
        }
    }

    private static void dumpKeys(Set<String> keys, ConfigPath parent, Map<String, Object> values) {
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            ConfigPath path = parent.child(entry.getKey());
            Object value = entry.getValue();
            if (value instanceof Map) {
                dumpKeys(keys, path, cast(value));
            } else {
                keys.add(Config.canonicalizeKey(path.toString()));
            }
        }
    }
}
