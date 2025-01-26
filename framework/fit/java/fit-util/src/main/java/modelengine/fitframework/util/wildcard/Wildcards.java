/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util.wildcard;

import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 为通配符匹配提供工具方法。
 *
 * @author 梁济时
 * @since 2022-07-29
 */
final class Wildcards {
    static final EmptySymbolSequence<?> EMPTY = new EmptySymbolSequence<>();

    static <E> Iterator<E> iterator(SymbolSequence<E> sequence) {
        return new SymbolIterator<>(sequence);
    }

    static <V, S> Iterator<SymbolTree.Node<V, S>> iterator(SymbolTree.NodeCollection<V, S> nodes) {
        return new SymbolNodeIterator<>(nodes);
    }

    static SymbolSequence<Character> sequence(CharSequence chars) {
        return new CharSequenceAdapter(chars);
    }

    static <E> SymbolSequence<E> sequence(List<E> list) {
        return new ListAdapter<>(list);
    }

    static <E> SymbolSequence<E> sequence(E[] array) {
        return new ArrayAdapter<>(array);
    }

    static <V, S> List<V> match(Pattern<S> matcher, SymbolTree<V, S> tree) {
        List<V> list = new LinkedList<>();
        for (SymbolTree.Node<V, S> node : tree.roots()) {
            match(list, matcher, node);
        }
        return list;
    }

    private static class EmptySymbolSequence<T> implements SymbolSequence<T> {
        private EmptySymbolSequence() {}

        @Override
        public int length() {
            return 0;
        }

        @Override
        public T at(int index) {
            throw new IndexOutOfBoundsException(StringUtils.format(
                    "The index to lookup element in sequence is out of bounds. [index={0}, length={1}]",
                    index, this.length()));
        }

        @Override
        public java.util.Iterator<T> iterator() {
            return Collections.emptyIterator();
        }
    }

    private static final class SymbolIterator<E> implements java.util.Iterator<E> {
        private final SymbolSequence<E> sequence;
        private int index;

        private SymbolIterator(SymbolSequence<E> sequence) {
            this.sequence = sequence;
            this.index = 0;
        }

        @Override
        public boolean hasNext() {
            return this.index < this.sequence.length();
        }

        @Override
        public E next() {
            if (this.index < this.sequence.length()) {
                return this.sequence.at(this.index++);
            } else {
                throw new NoSuchElementException(StringUtils.format(
                        "No element at current position in symbol sequence. [sequence={0}, position={1}]",
                        this.sequence, this.index));
            }
        }
    }

    private static final class SymbolNodeIterator<V, S> implements Iterator<SymbolTree.Node<V, S>> {
        private final SymbolTree.NodeCollection<V, S> nodes;
        private int index;

        private SymbolNodeIterator(SymbolTree.NodeCollection<V, S> nodes) {
            this.nodes = nodes;
            this.index = 0;
        }

        @Override
        public boolean hasNext() {
            return this.index < this.nodes.size();
        }

        @Override
        public SymbolTree.Node<V, S> next() {
            if (this.index < this.nodes.size()) {
                return this.nodes.get(this.index++);
            } else {
                throw new NoSuchElementException(StringUtils.format(
                        "No node at current position in collection. [nodes={0}, position={1}]",
                        this.nodes, this.index));
            }
        }
    }

    private static final class CharSequenceAdapter implements SymbolSequence<Character> {
        private final CharSequence chars;

        private CharSequenceAdapter(CharSequence chars) {
            this.chars = chars;
        }

        @Override
        public int length() {
            return this.chars.length();
        }

        @Override
        public Character at(int index) {
            return this.chars.charAt(index);
        }

        @Override
        public String toString() {
            return this.chars.toString();
        }
    }

    private static final class ListAdapter<E> implements SymbolSequence<E> {
        private final List<E> list;

        private ListAdapter(List<E> list) {
            this.list = Collections.unmodifiableList(list);
        }

        @Override
        public int length() {
            return this.list.size();
        }

        @Override
        public E at(int index) {
            return this.list.get(index);
        }

        @Override
        public String toString() {
            return this.list.toString();
        }
    }

    private static final class ArrayAdapter<E> implements SymbolSequence<E> {
        private final E[] array;

        private ArrayAdapter(E[] array) {
            this.array = array;
        }

        @Override
        public int length() {
            return this.array.length;
        }

        @Override
        public E at(int index) {
            return this.array[index];
        }

        @Override
        public String toString() {
            return Arrays.toString(this.array);
        }
    }

    private static <V, S> void match(List<V> list, Matcher<S> matcher, SymbolTree.Node<V, S> node) {
        Result<S> result = matcher.match(node.symbol());
        if (node.children().empty()) {
            if (result.matched()) {
                list.add(node.value());
            }
        } else {
            for (SymbolTree.Node<V, S> child : node.children()) {
                match(list, result, child);
            }
        }
    }
}
