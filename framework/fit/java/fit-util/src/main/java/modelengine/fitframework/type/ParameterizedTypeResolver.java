/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.type;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.TypeUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Stack;
import java.util.function.Function;

/**
 * 为参数化类型提供解析程序。
 *
 * @author 梁济时
 * @since 2022-07-04
 */
public final class ParameterizedTypeResolver {
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private ParameterizedTypeResolver() {}

    /**
     * 解析类型参数。
     *
     * @param current 表示当前类型的 {@link Type}。
     * @param expected 表示参数化类型的 {@link Class}。
     * @return 表示解析到的类型参数的 {@link List}{@code <}{@link Type}{@code >}。
     * @throws IllegalArgumentException {@code current} 或 {@code expected} 为 {@code null}。
     */
    public static ParameterizedTypeResolvingResult resolve(Type current, Class<?> expected) {
        notNull(current, "The current type to resolve cannot be null.");
        notNull(expected, "The expected class to resolve cannot be null.");
        Function<Class<?>, Type[]> superclassesMapper;
        if (expected.isInterface()) {
            superclassesMapper = sub -> {
                Type[] genericInterfaces = sub.getGenericInterfaces();
                Type supertype = sub.getGenericSuperclass();
                if (supertype == null) {
                    return genericInterfaces;
                } else {
                    Type[] types = new Type[genericInterfaces.length + 1];
                    types[0] = supertype;
                    System.arraycopy(genericInterfaces, 0, types, 1, genericInterfaces.length);
                    return types;
                }
            };
        } else {
            superclassesMapper = sub -> {
                Type supertype = sub.getGenericSuperclass();
                if (supertype == null) {
                    return TypeUtils.EMPTY_ARRAY;
                } else {
                    return new Type[] {supertype};
                }
            };
        }
        return Tree.of(current, superclassesMapper)
                .lookup(expected)
                .map(Node::parameters)
                .map(ParameterizedTypeResolvingResult::success)
                .orElse(ParameterizedTypeResolvingResult.failure());
    }

    private static class Tree {
        private final Node root;

        private Tree(Node root) {
            this.root = root;
        }

        private static Tree of(Type type, Function<Class<?>, Type[]> superclassesMapper) {
            Node node = Node.create(null, type);
            Queue<Node> queue = new LinkedList<>();
            queue.add(node);
            while (!queue.isEmpty()) {
                Node current = queue.poll();
                Type[] superclasses = superclassesMapper.apply(current.raw);
                for (Type superclass : superclasses) {
                    Node child = Node.create(current, superclass);
                    queue.add(child);
                    current.children.add(child);
                }
            }
            return new Tree(node);
        }

        Optional<Node> lookup(Class<?> expected) {
            Stack<Node> nodes = new Stack<>();
            nodes.push(this.root);
            while (!nodes.isEmpty()) {
                Node current = nodes.pop();
                if (current.raw.equals(expected)) {
                    return Optional.of(current);
                }
                for (int i = current.children.size() - 1; i >= 0; i--) {
                    nodes.push(current.children.get(i));
                }
            }
            return Optional.empty();
        }
    }

    private static class Node {
        private final Node parent;
        private final Class<?> raw;
        private final TypeVariable<?>[] variables;
        private final Type[] arguments;
        private final List<Node> children;

        private Node(Node parent, Class<?> raw, Type[] arguments) {
            this.parent = parent;
            this.raw = raw;
            this.variables = raw.getTypeParameters();
            this.arguments = arguments;
            this.children = new LinkedList<>();
        }

        private List<Type> parameters() {
            TypeVariable<?>[] typeParameters = this.raw.getTypeParameters();
            List<Type> types = new ArrayList<>(typeParameters.length);
            for (int i = 0; i < this.variables.length; i++) {
                types.add(this.parameter(i));
            }
            return types;
        }

        private Type parameter(String name) {
            int index = 0;
            while (index < this.arguments.length) {
                TypeVariable<?> variable = this.variables[index];
                if (variable.getName().equals(name)) {
                    return this.parameter(index);
                }
                index++;
            }
            StringBuilder signature = new StringBuilder();
            signature.append(this.raw.getName());
            if (this.variables.length > 0) {
                signature.append('<').append(this.variables[0].getName());
                for (int i = 1; i < this.variables.length; i++) {
                    signature.append(", ").append(this.variables[i].getName());
                }
                signature.append('>');
            }
            throw new IllegalStateException(StringUtils.format(
                    "No type parameter with specific name. [name={0}, type={1}]",
                    name,
                    signature.toString()));
        }

        private Type parameter(int index) {
            Type parameter = this.arguments[index];
            if (parameter instanceof TypeVariable) {
                parameter = this.actual((TypeVariable<?>) parameter);
            }
            return parameter;
        }

        private Type actual(TypeVariable<?> variable) {
            if (this.parent == null) {
                return TypeUtils.wildcard(variable.getBounds(), TypeUtils.EMPTY_ARRAY);
            } else {
                return this.parent.parameter(variable.getName());
            }
        }

        private static Node create(Node parent, Class<?> type) {
            return new Node(parent, type, new Type[0]);
        }

        private static Node create(Node parent, ParameterizedType type) {
            return new Node(parent, (Class<?>) type.getRawType(), type.getActualTypeArguments());
        }

        private static Node create(Node parent, Type type) {
            if (type instanceof Class) {
                return create(parent, (Class<?>) type);
            } else if (type instanceof ParameterizedType) {
                return create(parent, (ParameterizedType) type);
            } else {
                throw new IllegalStateException(StringUtils.format(
                        "The type of tree node must be class or parameterized type. [type={0}]",
                        type.getTypeName()));
            }
        }
    }
}
