/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.serialization.tlv.support;

import static java.nio.charset.StandardCharsets.UTF_8;
import static modelengine.fitframework.inspection.Validation.isTrue;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.serialization.TagLengthValues;
import modelengine.fitframework.serialization.tlv.ValueSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 表示 {@link ValueSerializer} 的异常的属性集的实现。
 *
 * @author 季聿阶
 * @since 2023-06-20
 */
public class ExceptionPropertiesValueSerializer implements ValueSerializer<Map<String, String>> {
    /** 表示 {@link ExceptionPropertiesValueSerializer} 的单例。 */
    public static final ValueSerializer<Map<String, String>> INSTANCE = new ExceptionPropertiesValueSerializer();

    private ExceptionPropertiesValueSerializer() {}

    @Override
    public byte[] serialize(@Nonnull Map<String, String> value) {
        TagLengthValues propertiesTlv = TagLengthValues.create();
        int currentTag = 0;
        for (Map.Entry<String, String> entry : value.entrySet()) {
            propertiesTlv.putTag(currentTag++, entry.getKey().getBytes(UTF_8));
            propertiesTlv.putTag(currentTag++, entry.getValue().getBytes(UTF_8));
        }
        return propertiesTlv.serialize();
    }

    @Override
    public Map<String, String> deserialize(@Nonnull byte[] bytes) {
        TagLengthValues propertiesTlv = TagLengthValues.deserialize(bytes);
        Set<Integer> tags = propertiesTlv.getTags();
        int size = tags.size();
        isTrue(size % 2 == 0, "Properties size is incorrect. [size={0}]", size);
        Map<String, String> properties = new HashMap<>();
        for (int i = 0; i < size; i++) {
            isTrue(tags.contains(i), "Properties tag not found. [tag={0}]", i);
            byte[] keyBytes = propertiesTlv.getValue(i++);
            isTrue(tags.contains(i), "Properties tag not found. [tag={0}]", i);
            byte[] valueBytes = propertiesTlv.getValue(i);
            properties.put(new String(keyBytes, UTF_8), new String(valueBytes, UTF_8));
        }
        return properties;
    }
}
