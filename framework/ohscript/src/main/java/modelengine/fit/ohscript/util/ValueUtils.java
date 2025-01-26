/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.util;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.ohscript.script.interpreter.ReturnValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于对象在 OhScript 和 Java 中互相转换的工具类。
 *
 * @author 季聿阶
 * @since 2023-10-23
 */
public class ValueUtils {
    /**
     * 将 OhScript 对象转换为 Java 对象。
     *
     * @param object OhScript 对象
     * @return Java 对象
     */
    public static Object fromOhScript(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof Map) {
            return fromOhScriptByMap(cast(object));
        } else if (object instanceof List) {
            return fromOhScriptByList(cast(object));
        } else if (object instanceof ReturnValue) {
            return fromOhScriptByReturnValue(cast(object));
        } else {
            return object;
        }
    }

    private static Map<String, Object> fromOhScriptByMap(Map<String, Object> map) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String actualKey = isActualKey(key) ? getActualKey(key) : key;
            Object actualValue = value;
            if (value instanceof ReturnValue) {
                actualValue = fromOhScript(((ReturnValue) value).value());
            }
            result.put(actualKey, actualValue);
        }
        return result;
    }

    /**
     * 将 OhScript 列表对象转换为 Java 列表对象。
     *
     * @param list OhScript 列表对象
     * @return Java 列表对象
     */
    private static List<Object> fromOhScriptByList(List<Object> list) {
        List<Object> result = new ArrayList<>();
        for (Object obj : list) {
            result.add(fromOhScript(obj));
        }
        return result;
    }

    private static Object fromOhScriptByReturnValue(ReturnValue returnValue) {
        return fromOhScript(returnValue.value());
    }

    private static boolean isActualKey(String entryKey) {
        return entryKey.startsWith(".");
    }

    /**
     * 获取实际的键名。
     *
     * @param entryKey 原始键名
     * @return 实际的键名
     */
    public static String getActualKey(String entryKey) {
        return entryKey.substring(1);
    }
}
