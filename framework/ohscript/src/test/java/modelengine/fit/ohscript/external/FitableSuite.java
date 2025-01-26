/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.external;

import modelengine.fitframework.annotation.Alias;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.util.MapBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 表示 {@link FitGenericable} 的测试实现。
 *
 * @author 季聿阶
 * @since 2023-10-23
 */
@Alias("f1")
@Component
public class FitableSuite implements FitGenericable {
    @Override
    @Fitable(id = "f1")
    public String m1(String p1, Integer p2, Long p3, Double p4) {
        return p1 + ":" + Optional.ofNullable(p2).map(Object::toString).orElse("null") + ":" + Optional.ofNullable(p3)
                .map(Object::toString)
                .orElse("null") + ":" + Optional.ofNullable(p4).map(Object::toString).orElse("null");
    }

    @Override
    @Fitable(id = "f1")
    public void m2() {
    }

    @Override
    @Fitable(id = "f1")
    public Map<String, Object> m3(Map<String, Object> p1, List<String> p2) {
        return MapBuilder.<String, Object>get().put("map", p1).put("list", p2).build();
    }
}
