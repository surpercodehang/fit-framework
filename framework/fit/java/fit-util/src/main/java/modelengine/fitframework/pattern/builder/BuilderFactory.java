/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.pattern.builder;

import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 构建器的工厂。
 * <p>对指定的对象生成构建器的<b>推荐</b>做法如下：
 * <ul>
 *     <li>指定对象使用接口声明。</li>
 *     <li>在指定对象内部创建构建器接口。</li>
 *     <li>需要构建的对象属性直接使用属性名作为 {@code get} 方法的方法名。</li>
 *     <li>在构建器中，对应属性的 {@code set} 方法名也使用属性名，同时增加唯一参数作为设置值。</li>
 *     <li>在构建器中，增加 {@code build} 方法，用于构建对象。</li>
 *     <li>在指定对象接口和构建器中，<b>禁止</b>使用 {@code default} 方法。</li>
 * </ul>
 * </p>
 * <p><b>推荐</b>的构建器模板如下：</p>
 * <pre>
 * /**
 *  * 表示需要构建的对象。
 *  *&#47;
 * public interface JavaBean {
 *     /**
 *      * 获取对象的第一个属性。
 *      *
 *      * @return 表示对象的第一个属性。
 *      *&#47;
 *     String field1();
 *
 *     /**
 *      * 获取对象的第二个属性。
 *      *
 *      * @return 表示对象的第二个属性。
 *      *&#47;
 *     Integer field2();
 *
 *     /**
 *      * 获取对象的第三个属性。
 *      *
 *      * @return 表示对象的第三个属性。
 *      *&#47;
 *     int field3();
 *
 *     /**
 *      * 表示需要构建的对象的构建器。
 *      *&#47;
 *     interface Builder {
 *         /**
 *          * 向构建器中设置第一个属性值。
 *          *
 *          * @param field1 表示待设置的第一个属性值。
 *          * @return 表示当前构建器。
 *          *&#47;
 *         Builder field1(String field1);
 *
 *         /**
 *          * 向构建器中设置第二个属性值。
 *          *
 *          * @param field2 表示待设置的第二个属性值。
 *          * @return 表示当前构建器。
 *          *&#47;
 *         Builder field2(Integer field2);
 *
 *         /**
 *          * 向构建器中设置第三个属性值。
 *          *
 *          * @param field3 表示待设置的第三个属性值。
 *          * @return 表示当前构建器。
 *          *&#47;
 *         Builder field3(int field3);
 *
 *         /**
 *          * 构建对象。
 *          *
 *          * @return 表示构建出的对象。
 *          *&#47;
 *         JavaBean build();
 *     }
 *
 *     /**
 *      * 获取对象的构建器。
 *      *
 *      * @return 表示对象的构建器。
 *      *&#47;
 *     static Builder builder() {
 *         return builder(null);
 *     }
 *
 *     /**
 *      * 获取对象的构建器，同时将指定对象的值对构建器进行填充。
 *      *
 *      * @param 表示指定对象值。
 *      * @return 表示对象的构建器。
 *      *&#47;
 *     static Builder builder(JavaBean javaBean) {
 *         return BuilderFactory.get(JavaBean.class, Builder.class).create(javaBean);
 *     }
 * }
 * </pre>
 *
 * @param <O> 表示待构建的对象的类型的 {@link O}。
 * @param <B> 表示待构建的对象的构建器类型的 {@link B}。
 * @author 梁济时
 * @author 季聿阶
 * @since 2022-06-22
 */
public class BuilderFactory<O, B> {
    private static final Map<Identity<?, ?>, BuilderFactory<?, ?>> FACTORIES = new ConcurrentHashMap<>();

    private final Class<O> objectClass;
    private final Class<B> builderClass;

    private BuilderFactory(Identity<O, B> identity) {
        this.objectClass = identity.objectClass;
        this.builderClass = identity.builderClass;
    }

    /**
     * 获取指定对象类型的构建器工厂。
     *
     * @param objectClass 表示指定对象类型的 {@link Class}{@code <}{@link O}{@code >}。
     * @param builderClass 表示指定对象构建器类型的 {@link Class}{@code <}{@link B}{@code >}。
     * @param <O> 表示指定对象类型的 {@link O}。
     * @param <B> 表示指定对象构建器类型的 {@link B}。
     * @return 表示指定对象类型的构建器工厂的 {@link BuilderFactory}{@code <}{@link O}{@code , }{@link B}{@code >}。
     */
    public static <O, B> BuilderFactory<O, B> get(Class<O> objectClass, Class<B> builderClass) {
        return ObjectUtils.cast(FACTORIES.computeIfAbsent(new Identity<>(objectClass, builderClass),
                BuilderFactory::new));
    }

    /**
     * 根据指定的初始化值创建构建器。
     *
     * @param initial 表示指定的初始化值的 {@link Object}。
     * @return 表示初始化后的构建器的 {@link B}。
     */
    public B create(Object initial) {
        ClassLoader loader = this.builderClass.getClassLoader();
        BuilderInvocationHandler handler =
                new BuilderInvocationHandler(this.objectClass, this.builderClass, this.map(initial));
        return ObjectUtils.cast(Proxy.newProxyInstance(loader,
                new Class[] {this.builderClass, ObjectProxy.class},
                handler));
    }

    private Map<String, Object> map(Object object) {
        Map<String, Object> map = new HashMap<>();
        Method[] methods = this.objectClass.getDeclaredMethods();
        for (Method method : methods) {
            this.put(map, method, object);
        }
        return map;
    }

    private void put(Map<String, Object> map, Method method, Object object) {
        if (method.isDefault() || Modifier.isStatic(method.getModifiers())) {
            return;
        }
        if (Modifier.isPrivate(method.getModifiers())) {
            // 当接口中的 default 方法中存在 Lambda 表达式时，会自动转化成 private 方法。
            return;
        }
        if (method.getParameterCount() >= 1) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "The method to read property cannot contain any parameter. [class=%s, method=%s]",
                    this.objectClass.getName(),
                    method.getName()));
        }
        if (object != null) {
            map.put(method.getName(), ReflectionUtils.invoke(object, method));
        } else if (ReflectionUtils.isPrimitive(method.getReturnType())) {
            map.put(method.getName(), ReflectionUtils.getPrimitiveDefaultValue(method.getReturnType()));
        } else {
            map.put(method.getName(), null);
        }
    }

    private static class Identity<O, B> {
        private final Class<O> objectClass;
        private final Class<B> builderClass;

        private Identity(Class<O> objectClass, Class<B> builderClass) {
            Validation.notNull(objectClass, "The object class of a builder factory cannot be null.");
            Validation.notNull(builderClass, "The builder class of a builder factory cannot be null.");
            this.objectClass = objectClass;
            this.builderClass = builderClass;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || this.getClass() != obj.getClass()) {
                return false;
            }
            Identity<?, ?> identity = (Identity<?, ?>) obj;
            return Objects.equals(this.objectClass, identity.objectClass) && Objects.equals(this.builderClass,
                    identity.builderClass);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new Object[] {this.objectClass, this.builderClass});
        }
    }
}
