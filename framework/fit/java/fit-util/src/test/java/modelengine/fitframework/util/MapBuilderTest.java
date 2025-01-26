/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * 为 {@link MapBuilder} 提供单元测试。
 *
 * @author 梁济时
 * @since 1.0
 */
public class MapBuilderTest {
    /** 表示用以测试的第一个键。 */
    private static final String KEY_1 = "key1";

    /** 表示用以测试的第一个值。 */
    private static final String VALUE_1 = "value1";

    /** 表示用以测试的第一个值的新值。 */
    private static final String NEW_VALUE_1 = "newValue1";

    /** 表示用以测试的第二个键。 */
    private static final String KEY_2 = "key2";

    /** 表示用以测试的第二个值。 */
    private static final String VALUE_2 = "value2";

    /** 表示用以测试的第三个键。 */
    private static final String KEY_3 = "key3";

    /** 表示用以测试的第三个值。 */
    private static final String VALUE_3 = "value3";

    /** 表示结果中元素的数量。 */
    private static final int RESULT_SIZE = 2;

    /**
     * 目标方法：{@link MapBuilder#build()}
     * <p>当重复设置某个键的值时，最终包含最后设置的值。</p>
     */
    @Test
    public void should_return_map_that_contains_new_value() {
        Map<String, String> map = MapBuilder.<String, String>get().put(KEY_1, VALUE_1).put(KEY_1, NEW_VALUE_1).build();
        assertNotNull(map);
        assertTrue(map.containsKey(KEY_1));
        assertEquals(map.get(KEY_1), NEW_VALUE_1);
    }

    /**
     * 目标方法：{@link MapBuilder#clear()}
     * <p>当发生清理时，结果只包含清理后添加的元素。</p>
     */
    @Test
    public void should_return_map_that_contains_elements_after_clear() {
        Map<String, String> map = MapBuilder.<String, String>get()
                .put(KEY_1, VALUE_1)
                .clear()
                .put(KEY_2, VALUE_2)
                .put(KEY_3, VALUE_3)
                .build();
        assertNotNull(map);
        assertEquals(map.size(), RESULT_SIZE);
        assertFalse(map.containsKey(KEY_1));
        assertTrue(map.containsKey(KEY_2));
        assertTrue(map.containsKey(KEY_3));
        assertEquals(map.get(KEY_2), VALUE_2);
        assertEquals(map.get(KEY_3), VALUE_3);
    }
}
