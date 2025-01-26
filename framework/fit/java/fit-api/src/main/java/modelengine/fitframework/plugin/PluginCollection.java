/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.plugin;

import java.net.URL;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * 为插件提供集合。
 * <p>插件集合中的每一个插件的 URL 名字都需要满足以下格式：{@link #JAR_NAME_PATTERN}。</p>
 *
 * @author 梁济时
 * @since 2022-12-05
 */
public interface PluginCollection extends Iterable<Plugin> {
    /**
     * 表示 JAR 文件名的格式。
     * <p>例如：
     * <ul>
     *     <li>main-1.0.jar</li>
     *     <li>main-1.0.0.jar</li>
     *     <li>main-1.0.0-SNAPSHOT.jar</li>
     *     <li>main-1.0.0-SNAPSHOT-random.jar</li>
     * </ul>
     * <b>注意：随机字符串虽然允许存在，但是不影响插件名的判定，即上述的最后两个文件认为是同一个插件。</b>
     * </p>
     */
    Pattern JAR_NAME_PATTERN = Pattern.compile("^([^.]+)-((\\d+(\\.\\d+)*)(-SNAPSHOT)?)(-.+)?\\.jar$");

    /**
     * 获取集合中包含插件的数量。
     *
     * @return 表示插件数量的 32 位整数。
     */
    int size();

    /**
     * 获取一个值，该值指示集合是否是空的。
     *
     * @return 若集合是空的，则为 {@code true}，否则为 {@code false}。
     */
    default boolean empty() {
        return this.size() < 1;
    }

    /**
     * 添加插件。
     *
     * @param location 表示待添加的插件所在位置的 {@link URL}。
     * @return 表示新添加的插件的 {@link Plugin}。
     * @throws IllegalArgumentException {@code url} 为 {@code null}。
     * @throws IllegalStateException 已添加过该位置的插件，或插件的格式不正确。
     */
    Plugin add(URL location);

    /**
     * 移除指定位置的插件。
     *
     * @param location 表示待移除的插件所在的位置的 {@link URL}。
     * @return 若包含该位置的插件，则为表示已移除的插件的 {@link Plugin}，否则为 {@code null}。
     */
    Plugin remove(URL location);

    /**
     * 获取指定索引处的插件。
     *
     * @param index 表示插件所在集合中的索引的 32 位整数。
     * @return 表示该索引处的插件的 {@link Plugin}。
     * @throws IndexOutOfBoundsException 索引超出限制。
     */
    Plugin get(int index);

    /**
     * 获取指定位置的插件。
     *
     * @param location 表示插件所在位置的 {@link URL}。
     * @return 若存在该位置的插件，则为表示该插件的 {@link Plugin}，否则为 {@code null}。
     */
    Plugin get(URL location);

    /**
     * 检查是否包含指定位置的插件。
     *
     * @param location 表示插件所在位置的 {@link URL}。
     * @return 若存在该位置的插件，则为 {@code true}，否则为 {@code false}。
     */
    boolean contains(URL location);

    /**
     * 返回一个操作流，用以操作集合中包含的插件。
     *
     * @return 表示用以操作集合中的插件的操作流的 {@link Stream}{@code <}{@link Plugin}{@code >}。
     */
    Stream<Plugin> stream();
}
