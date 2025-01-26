/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.ioc.annotation.support;

import modelengine.fitframework.ioc.annotation.AnnotationPropertyForward;
import modelengine.fitframework.ioc.annotation.AnnotationPropertyForwarder;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * 为 {@link AnnotationPropertyForwarder} 提供组合模式的实现。
 *
 * @author 梁济时
 * @since 2022-05-19
 */
public class AnnotationPropertyForwarderComposite implements AnnotationPropertyForwarder {
    private final List<AnnotationPropertyForwarder> forwarders;

    /**
     * 初始化 {@link AnnotationPropertyForwarderComposite} 类的新实例。
     */
    public AnnotationPropertyForwarderComposite() {
        this.forwarders = new LinkedList<>();
    }

    /**
     * 添加一个 {@link AnnotationPropertyForwarder} 到组合中。
     *
     * @param forwarder 表示要添加的 {@link AnnotationPropertyForwarder}。
     */
    public void add(AnnotationPropertyForwarder forwarder) {
        if (forwarder != null) {
            this.forwarders.add(forwarder);
        }
    }

    /**
     * 从组合中移除一个 {@link AnnotationPropertyForwarder}。
     *
     * @param forwarder 表示要移除的 {@link AnnotationPropertyForwarder}。
     */
    public void remove(AnnotationPropertyForwarder forwarder) {
        if (forwarder != null) {
            this.forwarders.remove(forwarder);
        }
    }

    /**
     * 获取组合中的 {@link AnnotationPropertyForwarder} 数量。
     *
     * @return 表示数量的 {@code int}。
     */
    public int size() {
        return this.forwarders.size();
    }

    @Override
    public Optional<AnnotationPropertyForward> forward(Method propertyMethod) {
        for (AnnotationPropertyForwarder forwarder : this.forwarders) {
            Optional<AnnotationPropertyForward> result = forwarder.forward(propertyMethod);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }
}
