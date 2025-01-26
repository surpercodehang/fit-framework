/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.broker.support;

import modelengine.fitframework.broker.FitableExecutor;
import modelengine.fitframework.broker.Format;
import modelengine.fitframework.broker.Target;
import modelengine.fitframework.conf.runtime.SerializationFormat;
import modelengine.fitframework.ioc.BeanContainer;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * 表示 {@link FitableExecutor} 的泛化远程调用实现。
 *
 * @author 季聿阶
 * @since 2023-10-23
 */
public class GenericRemoteFitableExecutor extends RemoteFitableExecutor {
    private static final Format DEFAULT =
            Format.custom().name(SerializationFormat.JSON.name()).code(SerializationFormat.JSON.code()).build();

    GenericRemoteFitableExecutor(BeanContainer container) {
        super(container);
    }

    @Override
    protected Format chooseFormat(Target target) {
        return target.formats().stream().filter(GenericRemoteFitableExecutor::isSupported).findFirst().orElse(DEFAULT);
    }

    /**
     * 判断指定的序列化方式是否满足泛化调用的要求。
     * <p>泛化调用仅支持 {@link SerializationFormat#JSON} 和 {@link SerializationFormat#CBOR} 两种序列化方式。</p>
     *
     * @param format 表示指定的序列化方式的 {@link Format}。
     * @return 如果支持，则返回 {@code true}，否则，返回 {@code false}。
     */
    private static boolean isSupported(Format format) {
        return format.code() == SerializationFormat.JSON.code() || format.code() == SerializationFormat.CBOR.code();
    }

    @Override
    protected Type[] getGenericParameterTypes(Method method, Object[] args) {
        Type[] types = new Type[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = null;
        }
        return types;
    }

    @Override
    protected Type getGenericReturnType(Method method) {
        return null;
    }
}
