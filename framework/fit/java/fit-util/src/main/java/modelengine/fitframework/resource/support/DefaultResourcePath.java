/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.resource.support;

import modelengine.fitframework.resource.ResourcePath;
import modelengine.fitframework.resource.ResourceResolver;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.wildcard.Pattern;
import modelengine.fitframework.util.wildcard.SymbolSequence;
import modelengine.fitframework.util.wildcard.SymbolType;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为资源路径提供节点定义。
 *
 * @author 梁济时
 * @since 2022-07-22
 */
public final class DefaultResourcePath implements ResourcePath {
    private static final String PATH_WILDCARD = String.valueOf(ResourceResolver.WILDCARD) + ResourceResolver.WILDCARD;

    private final Node head;

    private DefaultResourcePath(Node head) {
        this.head = head;
    }

    @Override
    public ResourcePath.Node head() {
        return this.head;
    }

    /**
     * 从指定的样式中解析资源路径信息。
     *
     * @param pattern 表示资源路径样式的 {@link DefaultResourcePath}。
     * @return 表示样式描述的资源路径的 {@link DefaultResourcePath}。
     */
    public static ResourcePath parse(String pattern) {
        Node curHead = null;
        if (StringUtils.isNotEmpty(pattern)) {
            List<String> labels = Stream.of(StringUtils.split(pattern, ResourceResolver.PATH_SEPARATOR))
                    .map(StringUtils::trim)
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());
            int index = 0;
            curHead = Node.of(labels.get(index++));
            Node current = curHead;
            while (index < labels.size()) {
                String label = StringUtils.trim(labels.get(index++));
                Node next = Node.of(label);
                current.next = next;
                next.previous = current;
                current = next;
            }
        }
        return new DefaultResourcePath(curHead);
    }

    /**
     * 为资源路径提供节点。
     *
     * @author 梁济时
     * @since 2022-07-27
     */
    private abstract static class Node implements ResourcePath.Node {
        private Node previous;
        private Node next;

        /**
         * 获取路径中的上一个节点。
         *
         * @return 表示路径中的上一个节点的 {@link Node}。
         */
        @Override
        public Node previous() {
            return this.previous;
        }

        /**
         * 获取路径中的下一个节点。
         *
         * @return 表示路径中的下一个节点的 {@link Node}。
         */
        @Override
        public Node next() {
            return this.next;
        }

        @Override
        public String toString() {
            return this.label();
        }

        private static Node of(String label) {
            StringBuilder builder = new StringBuilder(label.length());
            boolean wildcard = false;
            boolean exact = true;
            for (int i = 0; i < label.length(); i++) {
                char ch = label.charAt(i);
                if (ch == ResourceResolver.WILDCARD) {
                    exact = false;
                    if (!wildcard) {
                        wildcard = true;
                        builder.append(ch);
                    }
                } else {
                    wildcard = false;
                    builder.append(ch);
                }
            }
            if (builder.length() == 1 && builder.charAt(0) == ResourceResolver.WILDCARD && label.length() > 1) {
                return new MultipleWildcardNode();
            } else if (exact) {
                return new ExactNode(builder.toString());
            } else {
                return new SingleWildcardNode(StringUtils.split(builder.toString(), ResourceResolver.PATH_SEPARATOR));
            }
        }
    }

    private static final class ExactNode extends Node {
        private final String label;

        private ExactNode(String label) {
            this.label = label;
        }

        @Override
        public String label() {
            return this.label;
        }
    }

    private static final class SingleWildcardNode extends Node {
        private final String[] labels;

        private SingleWildcardNode(String[] labels) {
            this.labels = labels;
        }

        @Override
        public String label() {
            return StringUtils.join(ResourceResolver.WILDCARD, this.labels);
        }
    }

    private static final class MultipleWildcardNode extends Node {
        @Override
        public String label() {
            return StringUtils.EMPTY + ResourceResolver.WILDCARD + ResourceResolver.WILDCARD;
        }
    }

    @Override
    public Pattern<String> asPattern() {
        return Pattern.<String>custom()
                .pattern(SymbolSequence.fromList(this.labels()))
                .symbol()
                .matcher(DefaultResourcePath::matchPath)
                .symbol()
                .classifier(DefaultResourcePath::classifyPath)
                .build();
    }

    private static SymbolType classifyPath(String symbol) {
        if (StringUtils.equalsIgnoreCase(symbol, PATH_WILDCARD)) {
            return SymbolType.MULTIPLE_WILDCARD;
        } else {
            return SymbolType.NORMAL;
        }
    }

    private static boolean matchPath(String pattern, String value) {
        return Pattern.forCharSequence(pattern, '\0', ResourceResolver.WILDCARD).matches(value);
    }
}
