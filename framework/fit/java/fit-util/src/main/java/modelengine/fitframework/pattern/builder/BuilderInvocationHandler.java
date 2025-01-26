/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.pattern.builder;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * 构建器的动态代理。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2022-06-22
 */
public class BuilderInvocationHandler implements InvocationHandler {
    private final Class<?> objectClass;
    private final Class<?> builderClass;
    private final Map<String, Object> fields;

    public BuilderInvocationHandler(Class<?> objectClass, Class<?> builderClass, Map<String, Object> fields) {
        this.objectClass = objectClass;
        this.builderClass = builderClass;
        this.fields = fields;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Objects.equals(method.getName(), ObjectMethodUtils.getToMapMethodName())) {
            return this.fields;
        } else if (method.getDeclaringClass() == Object.class) {
            return ObjectMethodUtils.invokeObjectMethod(method, args, this.builderClass, this.fields);
        } else if (method.isDefault()) {
            throw new UnsupportedOperationException("Not supported default method to invoke.");
        } else if (args == null || args.length < 1) {
            if (Objects.equals(method.getName(), "build")) {
                return this.build();
            } else {
                throw new IllegalStateException(String.format(Locale.ROOT,
                        "Method to write property must have only one parameter. [class=%s, name=%s]",
                        method.getDeclaringClass().getName(),
                        method.getName()));
            }
        } else if (args.length > 1) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "Method to write property must contains only one parameter. [class=%s, name=%s]",
                    method.getDeclaringClass().getName(),
                    method.getName()));
        } else {
            this.fields.put(method.getName(), args[0]);
            return proxy;
        }
    }

    private Object build() {
        this.validateFields();
        ClassLoader loader = this.objectClass.getClassLoader();
        Class<?>[] interfaceClasses = new Class<?>[] {this.objectClass, ObjectProxy.class};
        ObjectInvocationHandler handler = new ObjectInvocationHandler(this.objectClass, this.fields);
        return Proxy.newProxyInstance(loader, interfaceClasses, handler);
    }

    private void validateFields() {
        for (Method method : this.objectClass.getMethods()) {
            Nonnull annotation = method.getAnnotation(Nonnull.class);
            if (annotation != null && this.fields.get(method.getName()) == null) {
                throw new IllegalStateException(StringUtils.format("The {0} cannot be null.", method.getName()));
            }
        }
    }
}
