/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.conf.support;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fitframework.conf.Config;
import modelengine.fitframework.conf.ConfigChainListener;
import modelengine.fitframework.conf.ModifiableConfig;
import modelengine.fitframework.conf.ModifiableConfigListener;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@DisplayName("测试 DefaultConfigChain 类")
class DefaultConfigChainTest {
    @Test
    @DisplayName("添加配置时将忽略为 null 的配置")
    void shouldIgnoreNullConfigs() {
        DefaultConfigChain chain = new DefaultConfigChain(null);
        chain.addConfig(null);
        chain.addConfig(Mockito.mock(Config.class));
        assertEquals(1, chain.numberOfConfigs());
    }

    @Test
    @DisplayName("当添加一个可被修改的配置时，将对此配置进行监听")
    void shouldSubscribeValueChangedWhenAddModifiableConfigAndUnsubscribeWhenRemoved() {
        DefaultConfigChain chain = new DefaultConfigChain(null);
        ModifiableConfig config = mock(ModifiableConfig.class);
        chain.addConfig(config);
        verify(config, times(1)).subscribe(any());
        chain.removeConfig(config);
        verify(config, times(1)).unsubscribe(any());
    }

    @Test
    @DisplayName("当配置被添加时，通知监听程序，并且在取消监听后不再通知")
    void shouldNotifyListenerWhenConfigAdded() {
        DefaultConfigChain chain = new DefaultConfigChain(null);
        Config config = mock(Config.class);
        ConfigChainListener listener = mock(ConfigChainListener.class);
        chain.subscribe(listener);
        chain.addConfig(config);
        verify(listener, times(1)).onConfigAdded(chain, config);
        chain.unsubscribe(listener);
        chain.addConfig(config);
        verify(listener, times(1)).onConfigAdded(chain, config);
    }

    @Test
    @DisplayName("当配置被移除时，通知监听程序，并且在取消监听程序后不再通知")
    void shouldNotifyListenerWhenConfigRemoved() {
        DefaultConfigChain chain = new DefaultConfigChain(null);
        Config config1 = mock(Config.class);
        Config config2 = mock(Config.class);
        chain.addConfig(config1);
        chain.addConfig(config2);
        ConfigChainListener listener = mock(ConfigChainListener.class);
        chain.subscribe(listener);
        chain.removeConfig(config1);
        verify(listener, times(1)).onConfigRemoved(chain, config1);
        chain.unsubscribe(listener);
        chain.removeConfig(config2);
        verify(listener, times(0)).onConfigRemoved(chain, config2);
    }

    @Test
    @DisplayName("当移除 null 配置时，不发生异常")
    void shouldDoNothingWhenRemoveNull() {
        DefaultConfigChain chain = new DefaultConfigChain(null);
        assertDoesNotThrow(() -> chain.removeConfig(null));
    }

    @Test
    @DisplayName("当移除的配置本就不在链中，不取消订阅监听程序")
    void shouldNotUnsubscribeListenerWhenRemoveNonExistConfig() {
        DefaultConfigChain chain = new DefaultConfigChain(null);
        ModifiableConfig config = mock(ModifiableConfig.class);
        chain.removeConfig(config);
        verify(config, times(0)).unsubscribe(any());
    }

    @Test
    @DisplayName("可通过索引获取链中的配置")
    void shouldReturnConfigWithIndex() {
        DefaultConfigChain chain = new DefaultConfigChain(null);
        Config config1 = mock(Config.class);
        Config config2 = mock(Config.class);
        chain.addConfig(config1);
        chain.addConfig(config2);
        assertEquals(2, chain.numberOfConfigs());
        assertSame(config1, chain.configAt(0));
        assertSame(config2, chain.configAt(1));
    }

    @Test
    @DisplayName("当注册 null 监听程序时，不会发生异常")
    void shouldDoNothingWhenSubscribeNullListener() {
        DefaultConfigChain chain = new DefaultConfigChain(null);
        Config config = mock(Config.class);
        assertDoesNotThrow(() -> chain.subscribe(null));
        Assertions.assertDoesNotThrow(() -> chain.addConfig(config));
    }

    @Test
    @DisplayName("当取消注册 null 监听程序时，不会发生异常")
    void shouldDoNothingWhenUnsubscribeNullListener() {
        DefaultConfigChain chain = new DefaultConfigChain(null);
        assertDoesNotThrow(() -> chain.unsubscribe(null));
    }

    @Test
    @DisplayName("当值被修改时，通知监听程序")
    void shouldNotifyListenerWhenValueChanged() {
        DefaultConfigChain chain = new DefaultConfigChain(null);
        ConfigChainListener listener = mock(ConfigChainListener.class);
        ModifiableConfig config = mock(ModifiableConfig.class);

        AtomicReference<ModifiableConfigListener> configListener = new AtomicReference<>();
        Answer<Void> answer = invocationOnMock -> {
            configListener.set(invocationOnMock.getArgument(0));
            return null;
        };
        doAnswer(answer).when(config).subscribe(any());

        chain.addConfig(config);
        chain.subscribe(listener);
        configListener.get().onValueChanged(config, "a.b");
        verify(listener, times(1)).onConfigChanged(chain, config, "a.b");
    }

    @Test
    @DisplayName("返回的键的集合包含所有配置的键")
    void shouldReturnKeysFromAllConfigs() {
        DefaultConfigChain chain = new DefaultConfigChain(null);
        Config config1 = mock(Config.class);
        when(config1.keys()).thenReturn(new HashSet<>(Arrays.asList("a.b", "a.c")));
        Config config2 = mock(Config.class);
        when(config2.keys()).thenReturn(new HashSet<>(Arrays.asList("a.b", "a.d")));
        chain.addConfig(config1);
        chain.addConfig(config2);
        Set<String> keys = chain.keys();
        assertEquals(3, keys.size());
        assertTrue(keys.contains("a.b"));
        assertTrue(keys.contains("a.c"));
        assertTrue(keys.contains("a.d"));
    }

    @Test
    @DisplayName("当配置的值都是标量时，返回一个列表，包含所有标量")
    void shouldReturnListOfValuesFromConfigs() {
        final String key = "a.b";
        final Object value1 = 100;
        final Object value2 = "hello";

        DefaultConfigChain chain = new DefaultConfigChain(null);
        MapConfig config1 = new MapConfig("m1", null);
        config1.set(key, value1);
        MapConfig config2 = new MapConfig("m2", null);
        config2.set(key, value2);
        chain.addConfig(config1);
        chain.addConfig(config2);

        Object value = chain.get(key);
        assertNotNull(value);
        assertInstanceOf(List.class, value);
        List<?> list = (List<?>) value;
        assertEquals(2, list.size());
        assertEquals(value1, list.get(0));
        assertEquals(value2, list.get(1));
    }

    @Test
    @DisplayName("当配置的值都是列表时，返回一个列表，包含两个列表中的值")
    void shouldReturnMergedListFromConfigs() {
        final String key = "a.b";
        final List<Integer> value1 = Arrays.asList(0, 1);
        final List<String> value2 = Arrays.asList("hello", "world");

        DefaultConfigChain chain = new DefaultConfigChain(null);
        MapConfig config1 = new MapConfig("m1", null);
        config1.set(key, value1);
        MapConfig config2 = new MapConfig("m2", null);
        config2.set(key, value2);
        chain.addConfig(config1);
        chain.addConfig(config2);

        Object value = chain.get(key);
        assertNotNull(value);
        assertInstanceOf(List.class, value);
        List<?> list = (List<?>) value;
        assertEquals(4, list.size());
        assertEquals(0, list.get(0));
        assertEquals(1, list.get(1));
        assertEquals("hello", list.get(2));
        assertEquals("world", list.get(3));
    }

    @Test
    @DisplayName("当配置的值分别是列表和标量时，返回一个列表，包含所有元素")
    void shouldReturnListContainsScalarAndList() {
        final String key = "a.b";
        final Integer value1 = 100;
        final List<String> value2 = Arrays.asList("hello", "world");

        DefaultConfigChain chain = new DefaultConfigChain(null);
        MapConfig config1 = new MapConfig("m1", null);
        config1.set(key, value1);
        MapConfig config2 = new MapConfig("m2", null);
        config2.set(key, value2);
        chain.addConfig(config1);
        chain.addConfig(config2);

        Object value = chain.get(key);
        assertNotNull(value);
        assertInstanceOf(List.class, value);
        List<?> list = (List<?>) value;
        assertEquals(3, list.size());
        assertEquals(100, list.get(0));
        assertEquals("hello", list.get(1));
        assertEquals("world", list.get(2));
    }

    @Test
    @DisplayName("当配置的值即有对象，又有非对象时，抛出异常")
    void shouldThrowWhenValuesBothContainObjectAndNonObject() {
        final String key = "a.b";
        final Map<String, Object> value1 = MapBuilder.<String, Object>get().put("name", "kitty").build();
        final List<String> value2 = Arrays.asList("hello", "world");

        DefaultConfigChain chain = new DefaultConfigChain(null);
        MapConfig config1 = new MapConfig("m1", null);
        config1.set(key, value1);
        MapConfig config2 = new MapConfig("m2", null);
        config2.set(key, value2);
        chain.addConfig(config1);
        chain.addConfig(config2);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> chain.get(key));
        assertEquals("Cannot merge an object value and a non-object value. [value1={name=kitty}, value2=[hello, "
                + "world]]", exception.getMessage());
    }

    @Test
    @DisplayName("当配置的值都为对象时，返回包含所有对象属性的映射")
    void shouldReturnObjectContainsAllProperties() {
        final String key = "a.b";
        final Map<String, Object> value1 = MapBuilder.<String, Object>get().put("name", "kitty").build();
        final Map<String, Object> value2 = MapBuilder.<String, Object>get().put("age", 19).build();

        DefaultConfigChain chain = new DefaultConfigChain(null);
        MapConfig config1 = new MapConfig("m1", null);
        config1.set(key, value1);
        MapConfig config2 = new MapConfig("m2", null);
        config2.set(key, value2);
        chain.addConfig(config1);
        chain.addConfig(config2);

        Object value = chain.get(key);
        assertInstanceOf(Map.class, value);
        Map<?, ?> map = (Map<?, ?>) value;
        assertEquals(2, map.size());
        assertEquals("kitty", map.get("name"));
        assertEquals(19, map.get("age"));
    }
}
