/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.pattern.builder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * 对象的动态代理。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2022-06-22
 */
public class ObjectInvocationHandler implements InvocationHandler {
    private final Class<?> objectClass;
    private final Map<String, Object> fields;

    public ObjectInvocationHandler(Class<?> objectClass, Map<String, Object> fields) {
        this.objectClass = objectClass;
        this.fields = fields;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Objects.equals(method.getName(), ObjectMethodUtils.getToMapMethodName())) {
            return this.fields;
        } else if (method.getDeclaringClass() == Object.class) {
            return ObjectMethodUtils.invokeObjectMethod(method, args, this.objectClass, this.fields);
        } else if (method.isDefault()) {
            throw new UnsupportedOperationException("Not supported default method to invoke.");
        } else {
            if (args == null || args.length < 1) {
                return fields.get(method.getName());
            } else {
                throw new IllegalStateException(String.format(Locale.ROOT,
                        "Method to read property cannot contain arguments. [class=%s, method=%s]",
                        method.getDeclaringClass().getName(), method.getName()));
            }
        }
    }
}
