/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.exception.FieldVisitException;
import modelengine.fitframework.exception.MethodInvocationException;
import modelengine.fitframework.exception.MethodNotFoundException;
import modelengine.fitframework.exception.ObjectInstantiationException;
import modelengine.fitframework.inspection.Nullable;
import modelengine.fitframework.inspection.Validation;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 为反射提供工具方法。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public final class ReflectionUtils {
    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPERS = MapBuilder.<Class<?>, Class<?>>get()
            .put(byte.class, Byte.class)
            .put(short.class, Short.class)
            .put(int.class, Integer.class)
            .put(long.class, Long.class)
            .put(float.class, Float.class)
            .put(double.class, Double.class)
            .put(char.class, Character.class)
            .put(boolean.class, Boolean.class)
            .build();

    private static final Map<Class<?>, Object> PRIMITIVE_DEFAULT_VALUES = MapBuilder.<Class<?>, Object>get()
            .put(byte.class, (byte) 0)
            .put(short.class, (short) 0)
            .put(int.class, 0)
            .put(long.class, (long) 0)
            .put(float.class, (float) 0)
            .put(double.class, (double) 0)
            .put(char.class, '\0')
            .put(boolean.class, false)
            .build();

    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private ReflectionUtils() {}

    /**
     * 获取指定类型中声明的符合指定参数类型列表的构造方法。
     *
     * @param clazz 表示指定类型的 {@link Class}{@code <}{@link T}{@code >}。
     * @param parameterTypes 表示入参类型数组的 {@link Class}{@code <}{@link Object}{@code >[]}。
     * @param <T> 表示指定类型的 {@link T}。
     * @return 表示符合入参类型的构造方法的 {@link Constructor}{@code <}{@link T}{@code >}。
     * @throws IllegalArgumentException 当 {@code clazz} 为 {@code null} 时。
     * @throws MethodNotFoundException 当没有符合入参类型的构造方法时。
     * @see Class#getDeclaredConstructor(Class[])
     */
    public static <T> Constructor<T> getDeclaredConstructor(Class<T> clazz, Class<?>... parameterTypes) {
        notNull(clazz, "The class to detect constructor cannot be null.");
        try {
            return clazz.getDeclaredConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new MethodNotFoundException(e);
        }
    }

    /**
     * 获取指定类型中声明的所有构造方法。
     *
     * @param clazz 表示指定类型的 {@link Class}{@code <}{@link T}{@code >}。
     * @param <T> 表示指定类型的 {@link T}。
     * @return 表示指定类型声明的所有构造方法的 {@link Constructor}{@code <}{@link T}{@code >[]}。
     * @throws IllegalArgumentException 当 {@code clazz} 为 {@code null} 时。
     * @see Class#getDeclaredConstructors()
     */
    public static <T> Constructor<T>[] getDeclaredConstructors(Class<T> clazz) {
        notNull(clazz, "The class to detect constructors cannot be null.");
        return ObjectUtils.cast(clazz.getDeclaredConstructors());
    }

    /**
     * 在指定类型中查找指定名称的字段。
     * <p>若类型中未定义该名称的字段，则持续向上查找其所有的父类型。</p>
     *
     * @param clazz 表示待从中查找字段的类型的 {@link Class}。
     * @param name 表示字段的名称的 {@link String}。
     * @return 若存在该名称的字段，则为表示该字段的 {@link Field}，否则为 {@code null}。
     */
    public static Field lookupField(Class<?> clazz, String name) {
        return lookupField(clazz, name, null);
    }

    /**
     * 在指定类型中查找指定名称的字段。
     * <p>若类型中未定义该名称的字段，则持续向上查找其所有的父类型。</p>
     *
     * @param clazz 表示待从中查找字段的类型的 {@link Class}。
     * @param name 表示字段的名称的 {@link String}。
     * @param type 表示所期望的字段的类型的 {@link Class}。
     * @return 若存在该名称的字段，则为表示该字段的 {@link Field}，否则为 {@code null}。
     */
    public static Field lookupField(Class<?> clazz, String name, Class<?> type) {
        return lookupClassElement(clazz,
                Class::getDeclaredFields,
                field -> Objects.equals(field.getName(), name) && (type == null || Objects.equals(field.getType(),
                        type)));
    }

    /**
     * 在指定的类型中查找指定名称的无参方法。
     *
     * @param clazz 表示待查找的方法所在的类型的 {@link Class}。
     * @param name 表示待查找的方法的类型的 {@link String}。
     * @return 若存在该方法，则为表示该方法的 {@link Method}，否则为 {@code null}。
     */
    public static Method lookupMethod(Class<?> clazz, String name) {
        return lookupMethod(clazz, name, (Class<?>[]) null);
    }

    /**
     * 在指定的类型中查找指定名称和入参类型的方法。
     *
     * @param clazz 表示待查找的方法所在的类型的 {@link Class}。
     * @param name 表示待查找的方法的类型的 {@link String}。
     * @param parameterTypes 表示参数类型的列表的 {@link Class}{@code []}。
     * @return 若存在该方法，则为表示该方法的 {@link Method}，否则为 {@code null}。
     */
    public static Method lookupMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        Class<?>[] actualParameterTypes = ObjectUtils.nullIf(parameterTypes, new Class[0]);
        return lookupClassElement(clazz,
                Class::getDeclaredMethods,
                method -> Objects.equals(method.getName(), name) && Arrays.equals(method.getParameterTypes(),
                        actualParameterTypes));
    }

    @Nullable
    private static <T> T lookupClassElement(Class<?> clazz, Function<Class<?>, T[]> lister, Predicate<T> predicate) {
        Queue<Class<?>> queue = new LinkedList<>();
        queue.add(clazz);
        while (!queue.isEmpty()) {
            Class<?> current = queue.poll();
            T[] elements = lister.apply(current);
            for (T element : elements) {
                if (predicate.test(element)) {
                    return element;
                }
            }
            Optional.ofNullable(current.getSuperclass()).ifPresent(queue::add);
            queue.addAll(Arrays.asList(current.getInterfaces()));
        }
        return null;
    }

    /**
     * 获取指定类型中声明的所有字段信息。
     *
     * @param clazz 表示指定类型的 {@link Class}{@code <?>}。
     * @return 表示指定类型中声明的所有字段信息的 {@link Field}{@code []}。
     * @throws IllegalArgumentException 当 {@code clazz} 为 {@code null} 时。
     * @see Class#getDeclaredFields()
     */
    public static Field[] getDeclaredFields(Class<?> clazz) {
        return getDeclaredFields(clazz, false);
    }

    /**
     * 获取指定类型中声明的所有字段信息。
     *
     * @param clazz 表示指定类型的 {@link Class}{@code <?>}。
     * @param withInherited 表示是否需要包含父类的标志的 {@code boolean}。
     * @return 表示指定类型中声明的所有字段信息的 {@link Field}{@code []}。
     * @throws IllegalArgumentException 当 {@code clazz} 为 {@code null} 时。
     * @see Class#getDeclaredFields()
     */
    public static Field[] getDeclaredFields(Class<?> clazz, boolean withInherited) {
        notNull(clazz, "The class to detect fields cannot be null.");
        if (!withInherited) {
            return clazz.getDeclaredFields();
        }
        List<Field> fields = new ArrayList<>();
        Class<?> actualClass = clazz;
        while (actualClass != null) {
            Field[] currentFields = actualClass.getDeclaredFields();
            fields.addAll(Arrays.asList(currentFields));
            actualClass = actualClass.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }

    /**
     * 获取指定类型中声明的指定名字的字段信息。
     *
     * @param clazz 表示指定类型的 {@link Class}{@code <?>}。
     * @param fieldName 表示指定字段名字的 {@link String}。
     * @return 表示指定类型中声明的字段信息的 {@link Field}。
     * @throws IllegalArgumentException 当 {@code clazz} 为 {@code null} 时，或当 {@code fieldName} 为空白字符串时。
     * @see Class#getDeclaredField(String)
     */
    public static Field getDeclaredField(Class<?> clazz, String fieldName) {
        notNull(clazz, "The class to detect field cannot be null.");
        notBlank(fieldName, "The field name cannot be blank.");
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new FieldVisitException(e);
        }
    }

    /**
     * 获取指定类型中声明的符合指定名称和入参的方法。
     *
     * @param clazz 表示指定类型的 {@link Class}{@code <}{@link Object}{@code >}。
     * @param methodName 表示方法名称的 {@link String}。
     * @param parameterClasses 表示方法入参类型数组的 {@link Class}{@code <}{@link Object}{@code >[]}。
     * @return 表示方法信息的 {@link Method}。
     * @throws IllegalArgumentException 当 {@code clazz} 或 {@code methodName} 为 {@code null} 时。
     * @throws MethodNotFoundException 当不存在符合该名称和入参类型的方法时。
     * @see Class#getDeclaredMethod(String, Class[])
     */
    public static Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?>... parameterClasses) {
        notNull(clazz, "The class to detect method cannot be null.");
        notNull(methodName, "The method name cannot be null.");
        try {
            return clazz.getDeclaredMethod(methodName, parameterClasses);
        } catch (NoSuchMethodException e) {
            throw new MethodNotFoundException(e);
        }
    }

    /**
     * 获取指定类型中声明的所有方法。
     *
     * @param clazz 表示指定类型的 {@link Class}{@code <}{@link Object}{@code >}。
     * @return 表示指定类型中声明的方法数组的 {@link Method}{@code []}，其中每一个方法都不会为 {@code null}。
     * @throws IllegalArgumentException 当 {@code clazz} 为 {@code null} 时。
     * @see Class#getDeclaredMethods()
     */
    public static Method[] getDeclaredMethods(Class<?> clazz) {
        notNull(clazz, "The class to detect methods cannot be null.");
        return clazz.getDeclaredMethods();
    }

    /**
     * 通过反射获取字段的值。
     *
     * @param owner 表示字段所属的对象的 {@link Object}。
     * @param field 表示待获取的字段的 {@link Field}
     * @return 表示字段的值的 {@link Object}。
     * @throws IllegalArgumentException 当 {@code field} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code owner} 为 {@code null}，且 {@code field} 是一个实例属性时。
     * @throws FieldVisitException 当访问字段失败时。
     * @see Field#get(Object)
     */
    public static Object getField(Object owner, Field field) {
        notNull(field, "The field to get value cannot be null.");
        if (owner == null && !Modifier.isStatic(field.getModifiers())) {
            // 当属性是一个实例属性，但调用时属性所在类的实例为 null 时，会抛出 NullPointerException，提前进行校验。
            throw new IllegalArgumentException(StringUtils.format(
                    "The specified owner is null and the field is an instance field. [field={0}]",
                    field.getName()));
        }
        try {
            field.setAccessible(true);
            return field.get(owner);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            // 当属性和实例对象不匹配时，会抛出 IllegalArgumentException，统一转换为 FieldVisitException。
            throw new FieldVisitException(e);
        }
    }

    /**
     * 获取指定名称的字段的值。
     *
     * @param owner 表示字段所属的对象的 {@link Object}。
     * @param fieldName 表示待获取值的字段的名称的 {@link String}。
     * @return 表示字段的值的 {@link Object}。
     * @throws IllegalArgumentException 当 {@code owner} 为 {@code null} 时，或当 {@code name} 为 {@code null} 或空白字符串时。
     * @throws FieldVisitException 当访问字段失败时。
     * @see Class#getDeclaredField(String)
     * @see Field#get(Object)
     */
    public static Object getField(Object owner, String fieldName) {
        Validation.notBlank(fieldName, "The name of field to get value cannot be blank.");
        try {
            Field field = getClass(owner).getDeclaredField(fieldName);
            return getField(owner, field);
        } catch (NoSuchFieldException e) {
            throw new FieldVisitException(e);
        }
    }

    /**
     * 获取指定方法所属的接口类。
     * <p>指定方法可能存在以下几种情况：
     * <ul>
     *     <li>当指定方法是接口中的方法时，则返回其所属的接口类。</li>
     *     <li>当指定方法覆盖了接口中的方法时，则返回其覆盖的接口类。</li>
     *     <li>当指定方法没有覆盖任何接口中的方式时，则返回 {@link Optional#empty()}。</li>
     * </ul>
     * </p>
     *
     * @param method 表示指定方法的 {@link Method}。
     * @return 表示指定方法所属接口类的 {@link Optional}{@code <}{@link Class}{@code <}{@link Object}{@code >>}。
     * @throws IllegalArgumentException 当 {@code method} 为 {@code null} 时。
     */
    public static Optional<Class<?>> getInterface(Method method) {
        return getInterfaceMethod(method).map(Method::getDeclaringClass);
    }

    /**
     * 获取指定方法在所属接口类中的方法。
     * <p>指定方法可能存在以下几种情况：
     * <ul>
     *     <li>当指定方法是接口中的方法时，则返回其自身。</li>
     *     <li>当指定方法覆盖了接口中的方法时，则返回其覆盖的接口中的方法。</li>
     *     <li>当指定方法没有覆盖任何接口中的方式时，则返回 {@link Optional#empty()}。</li>
     * </ul>
     * </p>
     *
     * @param method 表示指定方法的 {@link Method}。
     * @return 表示指定方法在所属接口类中的方法的 {@link Optional}{@code <}{@link Method}{@code >}。
     * @throws IllegalArgumentException 当 {@code method} 为 {@code null} 时。
     */
    public static Optional<Method> getInterfaceMethod(Method method) {
        notNull(method, "The method to get interface method cannot be null.");
        Class<?> declaringClass = method.getDeclaringClass();
        if (declaringClass.isInterface()) {
            return Optional.of(method);
        }
        Set<Class<?>> visitedClasses = new HashSet<>();
        Stack<Class<?>> stack = new Stack<>();
        pushSuperClassAndInterfaces(declaringClass, stack);
        while (!stack.empty()) {
            Class<?> cur = stack.pop();
            if (visitedClasses.contains(cur)) {
                continue;
            }
            if (!cur.isInterface()) {
                pushSuperClassAndInterfaces(cur, stack);
                continue;
            }
            try {
                Method declaredMethod = cur.getDeclaredMethod(method.getName(), method.getParameterTypes());
                return Optional.of(declaredMethod);
            } catch (NoSuchMethodException e) {
                visitedClasses.add(cur);
                pushSuperClassAndInterfaces(cur, stack);
            }
        }
        return Optional.empty();
    }

    private static void pushSuperClassAndInterfaces(Class<?> declaringClass, Stack<Class<?>> stack) {
        Stream.of(declaringClass.getInterfaces()).forEach(stack::push);
        if (declaringClass.getSuperclass() != null) {
            stack.push(declaringClass.getSuperclass());
        }
    }

    /**
     * 获取指定方法的参数。
     *
     * @param method 表示待获取参数的方法的 {@link T}。
     * @param <T> 表示方法的实际类型的 {@link T}。
     * @return 表示参数信息的 {@link Parameter}{@code []}。
     * @throws IllegalArgumentException 当 {@code method} 为 {@code null} 时。
     * @see Executable#getParameters()
     */
    public static <T extends Executable> Parameter[] getParameters(T method) {
        notNull(method, "The method to get parameters cannot be null.");
        return method.getParameters();
    }

    /**
     * 获取指定名称的属性的值。
     * <p>首先尝试通过字段的 {@code getter} 方法进行获取，若获取失败，再通过 {@link #getField(Object, String) 字段本身} 获取。</p>
     *
     * @param owner 表示属性所在的对象的 {@link Object}。
     * @param name 表示待获取值的属性的名称的 {@link String}。
     * @return 表示属性的值的 {@link Object}。
     * @throws IllegalArgumentException 当 {@code owner} 为 {@code null} 时。
     * @throws FieldVisitException 当访问属性失败时。
     * @see PropertyDescriptor#getReadMethod()
     * @see #getField(Object, String)
     * @see #invoke(Object, Method, Object...)
     */
    public static Object getProperty(Object owner, String name) {
        try {
            PropertyDescriptor descriptor = new PropertyDescriptor(name, getClass(owner));
            Method getter = descriptor.getReadMethod();
            return invoke(owner, getter);
        } catch (IntrospectionException | MethodInvocationException e) {
            return getField(owner, name);
        }
    }

    /**
     * 忽略基本数据类型。
     *
     * @param source 表示原始类型的 {@link Class}{@code <}{@link Object}{@code >}。
     * @return 若类型为基本数据类型，则返回其包装类的类型的 {@link Class}{@code <}{@link Object}{@code >}；否则返回原始类型的
     * {@link Class}{@code <}{@link Object}{@code >}。
     * @throws IllegalArgumentException 当 {@code source} 为 {@code null} 时。
     */
    public static Class<?> ignorePrimitiveClass(Class<?> source) {
        notNull(source, "Source class cannot be null.");
        return ObjectUtils.nullIf(PRIMITIVE_WRAPPERS.get(source), source);
    }

    /**
     * 获取基本类型的默认值。
     * <p>如果是非基本类型，则返回 {@code null}。</p>
     *
     * @param source 表示基本类型的 {@link Class}{@code <}{@link Object}{@code >}。
     * @return 表示基本类型的默认值的 {@link Object}。
     */
    public static Object getPrimitiveDefaultValue(Class<?> source) {
        notNull(source, "Source class cannot be null.");
        return PRIMITIVE_DEFAULT_VALUES.get(source);
    }

    /**
     * 判断指定类型是否为基本类型。
     *
     * @param source 表示指定类型的 {@link Class}{@code <}{@link Object}{@code >}。
     * @return 如果指定类型是基本类型，则返回 {@code true}，否则，返回 {@code false}。
     */
    public static boolean isPrimitive(Class<?> source) {
        notNull(source, "Source class cannot be null.");
        return PRIMITIVE_DEFAULT_VALUES.containsKey(source);
    }

    /**
     * 判断指定类型是否为基本类型的包装类。
     *
     * @param source 表示指定类型的 {@link Class}{@code <}{@link Object}{@code >}。
     * @return 如果指定类型是基本类型的包装类，则返回 {@code true}，否则，返回 {@code false}。
     */
    public static boolean isPrimitiveWrapper(Class<?> source) {
        notNull(source, "Source class cannot be null.");
        return PRIMITIVE_WRAPPERS.containsValue(source);
    }

    /**
     * 使用默认构造方法，初始化指定类型的新实例。
     *
     * @param clazz 表示待实例化的类型的 {@link Class}{@code <}{@link T}{@code >}。
     * @param <T> 表示待构造对象的实际类型的 {@link T}。
     * @return 表示使用默认构造方法初始化的新实例。
     * @throws IllegalArgumentException 当 {@code clazz} 为 {@code null} 时。
     * @throws ObjectInstantiationException 当执行构造方法发生异常时。
     * @see Class#getDeclaredConstructor(Class[])
     * @see Constructor#newInstance(Object...)
     */
    public static <T> T instantiate(Class<T> clazz) {
        notNull(clazz, "The class to instantiate new object cannot be null.");
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            return instantiate(constructor);
        } catch (NoSuchMethodException e) {
            throw new ObjectInstantiationException(e);
        }
    }

    /**
     * 使用指定的构造方法和参数初始化指定类型的新实例。
     *
     * @param constructor 表示用以初始化新实例的构造方法的 {@link Constructor}{@code <}{@link T}{@code >}。
     * @param parameters 表示用以实例化的参数的 {@link Object}{@code []}。
     * @param <T> 表示待构造对象的实际类型的 {@link T}。
     * @return 表示初始化的对象的新实例的 {@link T}。
     * @throws IllegalArgumentException 当 {@code constructor} 为 {@code null} 时。
     * @throws ObjectInstantiationException 执行构造方法发生异常时。
     * @see Constructor#newInstance(Object...)
     */
    public static <T> T instantiate(Constructor<T> constructor, Object... parameters) {
        notNull(constructor, "The constructor to instantiate new object cannot be null.");
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(parameters);
        } catch (InvocationTargetException e) {
            throw new ObjectInstantiationException(e.getCause());
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException e) {
            // 当调用实例化时参数错误会抛出 IllegalArgumentException，统一转换为 ObjectInstantiationException。
            throw new ObjectInstantiationException(e);
        }
    }

    /**
     * 执行指定方法。
     * <p><b>注意：如果希望异常透传，需要捕获 {@link MethodInvocationException}，然后取出其原因继续抛出。</b></p>
     *
     * @param owner 表示待执行方法的主体对象的 {@link Object}。
     * @param method 表示待执行的方法的 {@link Method}。
     * @param parameters 表示执行方法时所使用的入参的 {@link Object}{@code []}。
     * @return 表示执行方法后的返回值的 {@link Object}。
     * @throws IllegalArgumentException 当 {@code method} 为 {@code null} 时。
     * @throws MethodInvocationException 当执行方法发生异常时。
     * @see Method#invoke(Object, Object...)
     */
    public static Object invoke(Object owner, Method method, Object... parameters) {
        notNull(method, "The method to invoke cannot be null.");
        if (owner == null && !Modifier.isStatic(method.getModifiers())) {
            // 当方法是一个实例方法，但调用时方法所在类的实例为 null 时，会抛出 NullPointerException，提前进行校验。
            throw new IllegalArgumentException(StringUtils.format(
                    "The specified owner is null and the method is an instance method. [method={0}]",
                    method.getName()));
        }
        try {
            method.setAccessible(true);
            return method.invoke(owner, parameters);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            while (cause instanceof InvocationTargetException) {
                InvocationTargetException invocationTargetException = ObjectUtils.cast(cause);
                cause = invocationTargetException.getCause();
            }
            throw new MethodInvocationException(cause);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            // 当调用参数不一致时，会抛出 IllegalArgumentException，统一转换为 MethodInvocationException。
            throw new MethodInvocationException(e);
        }
    }

    /**
     * 执行指定方法。
     *
     * @param owner 表示待执行方法的主体对象的 {@link Object}。
     * @param method 表示待执行的方法的 {@link Method}。
     * @param returnType 表示待执行的方法的返回类型的 {@link Class}{@code <}{@link T}{@code >}。
     * @param parameters 表示执行方法时所使用的入参的 {@link Object}{@code []}。
     * @param <T> 表示待执行的方法的真实返回类型的 {@link T}。
     * @return 表示执行方法后的返回值的 {@link T}。
     * @throws IllegalArgumentException 当 {@code method} 为 {@code null} 时。
     * @throws MethodInvocationException 当执行方法发生异常时。
     * @see #invoke(Object, Method, Object...)
     */
    public static <T> T invokeWithReturnType(Object owner, Method method, Class<T> returnType, Object... parameters) {
        Object result = invoke(owner, method, parameters);
        if (result == null) {
            return null;
        }
        if (!result.getClass().isAssignableFrom(returnType)) {
            throw new MethodInvocationException(StringUtils.format(
                    "Return type is mismatch. [returnType={0}, actualType={1}]",
                    returnType.getName(),
                    result.getClass().getName()));
        }
        return returnType.cast(result);
    }

    /**
     * 设置字段的值。
     *
     * @param owner 表示字段所属的对象的 {@link Object}。
     * @param field 表示待设置值的字段的 {@link Field}。
     * @param value 表示待设置到字段的值的 {@link Object}。
     * @throws IllegalArgumentException 当 {@code field} 为 {@code null} 时。
     * @throws FieldVisitException 当访问属性失败时。
     * @see Field#set(Object, Object)
     */
    public static void setField(Object owner, Field field, Object value) {
        notNull(field, "The field to set value cannot be null.");
        if (owner == null && !Modifier.isStatic(field.getModifiers())) {
            // 当属性是一个实例属性，但调用时属性所在类的实例为 null 时，会抛出 NullPointerException，提前进行校验。
            throw new IllegalArgumentException(StringUtils.format(
                    "The specified owner is null and the field is an instance field. [field={0}]",
                    field.getName()));
        }
        try {
            field.setAccessible(true);
            field.set(owner, value);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            // 当属性和实例对象不匹配时，会抛出 IllegalArgumentException，统一转换为 FieldVisitException。
            throw new FieldVisitException(e);
        }
    }

    /**
     * 设置指定名称的字段的值。
     *
     * @param owner 表示字段所属的对象的 {@link Object}。
     * @param fieldName 表示待设置值的字段的名称的 {@link String}。
     * @param value 表示待设置到字段的值的 {@link Object}。
     * @throws IllegalArgumentException 当 {@code owner} 为 {@code null} 时，或当 {@code name} 为 {@code null} 或空白字符串时。
     * @throws FieldVisitException 当访问字段失败时。
     * @see Class#getDeclaredField(String)
     * @see #setField(Object, Field, Object)
     */
    public static void setField(Object owner, String fieldName, Object value) {
        Validation.notBlank(fieldName, "The name of field to set value cannot be blank.");
        try {
            Field field = getClass(owner).getDeclaredField(fieldName);
            setField(owner, field, value);
        } catch (NoSuchFieldException e) {
            throw new FieldVisitException(e);
        }
    }

    /**
     * 设置指定名称的属性的值。
     * <p>首先尝试通过字段的 {@code setter} 方法进行设置，若设置失败，再通过 {@link #setField(Object, String, Object) 字段本身}
     * 设置。</p>
     *
     * @param owner 表示属性所在的对象的 {@link Object}。
     * @param name 表示待设置值的属性的名称的 {@link String}。
     * @param value 表示待设置到属性的值的 {@link Object}。
     * @throws IllegalArgumentException 当 {@code owner} 为 {@code null} 时。
     * @throws FieldVisitException 当访问属性失败时。
     * @see PropertyDescriptor#getWriteMethod()
     * @see #setField(Object, String, Object)
     * @see #invoke(Object, Method, Object...)
     */
    public static void setProperty(Object owner, String name, Object value) {
        try {
            PropertyDescriptor descriptor = new PropertyDescriptor(name, getClass(owner));
            Method setter = descriptor.getWriteMethod();
            invoke(owner, setter, value);
        } catch (IntrospectionException | MethodInvocationException e) {
            setField(owner, name, value);
        }
    }

    /**
     * 返回一个字符串，用以表示指定的方法的完整形式。
     *
     * @param method 表示指定方法的 {@link Method}。
     * @return 用以表示指定方法的字符串的 {@link String}。
     */
    public static String toLongString(Method method) {
        return toString(method, Pattern.LONG);
    }

    /**
     * 返回一个字符串，用以表示指定的方法的缩略形式。
     *
     * @param method 表示指定方法的 {@link Method}。
     * @return 用以表示指定方法的字符串的 {@link String}。
     */
    public static String toShortString(Method method) {
        return toString(method, Pattern.SHORT);
    }

    /**
     * 返回一个字符串，用以表示指定的方法。
     *
     * @param method 表示指定方法的 {@link Method}。
     * @return 用以表示指定方法的字符串的 {@link String}。
     */
    public static String toString(Method method) {
        return toString(method, Pattern.NORMAL);
    }

    /**
     * 返回一个字符串，用以表示指定的方法的指定形式。
     *
     * @param method 表示指定方法的 {@link Method}。
     * @param pattern 表示指定格式的 {@link Pattern}。
     * @return 用以表示指定方法的字符串的 {@link String}。
     */
    public static String toString(Method method, Pattern pattern) {
        if (method == null) {
            return StringUtils.EMPTY;
        }
        Pattern actualPattern = ObjectUtils.nullIf(pattern, Pattern.NORMAL);
        StringBuilder sb = new StringBuilder();
        if (actualPattern.includeModifier) {
            sb.append(Modifier.toString(method.getModifiers())).append(' ');
        }
        if (actualPattern.includeReturnTypeAndArguments) {
            appendType(sb, method.getReturnType(), actualPattern.useLongReturnAndArgumentTypeName);
            sb.append(' ');
        }
        if (actualPattern.includeType) {
            appendType(sb, method.getDeclaringClass(), actualPattern.useLongTypeName);
            sb.append('.');
        }
        sb.append(method.getName()).append('(');
        appendTypes(sb,
                method.getParameterTypes(),
                actualPattern.includeReturnTypeAndArguments,
                actualPattern.useLongReturnAndArgumentTypeName);
        sb.append(')');
        return sb.toString();
    }

    /**
     * 获取指定对象的类型。
     *
     * @param owner 表示指定对象的实例的 {@link Object}。
     * @return 表示指定对象的类型的 {@link Class}{@code <}{@link Object}{@code >}。
     * @throws IllegalArgumentException 当 {@code owner} 为 {@code null} 时。
     */
    private static Class<?> getClass(Object owner) {
        return notNull(owner, "The owner to get class cannot be null.") instanceof Class<?>
                ? (Class<?>) owner
                : owner.getClass();
    }

    private static void appendTypes(StringBuilder sb, Class<?>[] types, boolean includeArgs, boolean useLongTypeName) {
        if (includeArgs) {
            for (int size = types.length, i = 0; i < size; i++) {
                appendType(sb, types[i], useLongTypeName);
                if (i < size - 1) {
                    sb.append(',');
                }
            }
        } else {
            if (types.length != 0) {
                sb.append('.').append('.');
            }
        }
    }

    private static void appendType(StringBuilder sb, Class<?> type, boolean useLongTypeName) {
        if (type.isArray()) {
            appendType(sb, type.getComponentType(), useLongTypeName);
            sb.append('[').append(']');
        } else {
            sb.append(useLongTypeName ? type.getName() : type.getSimpleName());
        }
    }

    /**
     * 表示方法输出字符串的样式。
     */
    public static class Pattern {
        /** 表示简短样式。 */
        public static final Pattern SHORT = new Pattern(false, false, true, false, false);

        /** 表示完整样式。 */
        public static final Pattern LONG = new Pattern(true, true, true, true, true);

        /** 表示通用样式。 */
        public static final Pattern NORMAL = new Pattern(false, true, true, false, true);

        private final boolean includeModifier;
        private final boolean includeReturnTypeAndArguments;
        private final boolean includeType;
        private final boolean useLongReturnAndArgumentTypeName;
        private final boolean useLongTypeName;

        public Pattern(boolean includeModifier, boolean includeReturnTypeAndArguments, boolean includeType,
                boolean useLongReturnAndArgumentTypeName, boolean useLongTypeName) {
            this.includeModifier = includeModifier;
            this.includeReturnTypeAndArguments = includeReturnTypeAndArguments;
            this.includeType = includeType;
            this.useLongReturnAndArgumentTypeName = useLongReturnAndArgumentTypeName;
            this.useLongTypeName = useLongTypeName;
        }
    }

    /**
     * 获取指定方法的签名。
     *
     * @param method 表示待获取签名的方法的 {@link Method}。
     * @return 表示方法的签名的 {@link String}。
     * @throws IllegalArgumentException {@code method} 为 {@code null}。
     */
    public static String signatureOf(Method method) {
        notNull(method, "The method to compute signature cannot be null.");
        StringBuilder builder = new StringBuilder();
        builder.append(method.getDeclaringClass().getName());
        builder.append('.').append(method.getName());
        builder.append('(');
        Parameter[] parameters = method.getParameters();
        if (parameters.length > 0) {
            builder.append(parameters[0].getParameterizedType().getTypeName());
            for (int i = 1; i < parameters.length; i++) {
                builder.append(',').append(' ').append(parameters[i].getParameterizedType().getTypeName());
            }
        }
        builder.append(')').append(' ').append(':').append(' ');
        builder.append(method.getGenericReturnType().getTypeName());
        return builder.toString();
    }

    /**
     * 检查指定的异常类型是否是一个受检异常。
     *
     * @param clazz 表示待检查的异常的类型的 {@link Class}。
     * @return 若该类型的异常是受检异常，则为 {@code true}，否则为 {@code false}。
     */
    public static boolean isCheckedException(Class<?> clazz) {
        notNull(clazz, "The exception class to determine whether is checked cannot be null.");
        return Throwable.class.isAssignableFrom(clazz) && !RuntimeException.class.isAssignableFrom(clazz)
                && !Error.class.isAssignableFrom(clazz);
    }
}
