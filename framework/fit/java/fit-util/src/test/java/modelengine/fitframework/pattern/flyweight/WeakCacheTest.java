/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.pattern.flyweight;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import modelengine.fitframework.util.ThreadUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.ref.WeakReference;

@DisplayName("测试 WeakCache 接口")
final class WeakCacheTest {
    private static final class Item {
        private final String name;

        private Item(String name) {
            this.name = name;
        }

        private String name() {
            return this.name;
        }
    }

    @Test
    @DisplayName("当缓存对象被释放后，在 GC 时被释放")
    void shouldReturnSameInstanceWhenHasStrongReference() {
        WeakCache<String, Item> cache = WeakCache.create(Item::new, Item::name);
        String key = "key";
        Item item1 = cache.get(key);
        Item item2 = cache.get(key);
        assertSame(item1, item2);
    }

    @Test
    @DisplayName("当缓存对象没有强引用并发生 GC 后，再返回的对象不是之前的实例")
    void shouldReturnDifferentInstancesWhenNoStrongReference() {
        WeakCache<String, Item> cache = WeakCache.create(Item::new, Item::name);
        String key = "key";
        Item item = cache.get(key);
        int id1 = System.identityHashCode(item);
        item = null;
        WeakReference<Object> ref = new WeakReference<>(new Object());
        System.gc();
        while (ref.get() != null) {
            ThreadUtils.sleep(0L);
        }
        item = cache.get(key);
        int id2 = System.identityHashCode(item);
        assertNotEquals(id1, id2);
    }
}
