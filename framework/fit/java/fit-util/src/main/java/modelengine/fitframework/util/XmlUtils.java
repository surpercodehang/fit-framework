/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fitframework.inspection.Validation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * 为操作 XML 文档提供工具方法。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2020-07-24
 */
public final class XmlUtils {
    /**
     * 表示允许引用一般性的外部实体。<b>为避免 XXE 漏洞，应禁用该特性。</b>
     */
    private static final String FEATURE_ALLOW_EXTERNAL_GENERAL_ENTITIES =
            "http://xml.org/sax/features/external-general-entities";

    /**
     * 表示允许引用外部参数实体。<b>为避免XXE漏洞，应禁用该特性。</b>
     */
    private static final String FEATURE_ALLOW_EXTERNAL_PARAMETER_ENTITIES =
            "http://xml.org/sax/features/external-parameter-entities";

    /**
     * 表示允许加载外部的文档类型定义。<b>为避免 XXE 漏洞，应禁用该特性。</b>
     */
    private static final String FEATURE_ALLOW_LOAD_EXTERNAL_DTD =
            "http://apache.org/xml/features/nonvalidating/load-external-dtd";

    /**
     * 表示禁用内联的 DOCTYPE 声明，即禁用 DTD。<b>为避免 XXE 漏洞，应启用该特性。</b>
     */
    private static final String FEATURE_DISALLOW_DOCTYPE_DECL = "http://apache.org/xml/features/disallow-doctype-decl";

    /**
     * 表示用以判定节点为数据节点的校验器。
     */
    private static final Predicate<Node> PREDICATE_ELEMENT = node -> node instanceof Element;

    /**
     * 表示文本标签。
     */
    private static final String TEXT_TAG = "#text";

    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    private XmlUtils() {}

    /**
     * 在指定的节点下创建一个子元素。
     *
     * @param parentNode 表示待创建子元素的父节点的 {@link Node}。
     * @param factory 表示用以创建子节点的方法的 {@link Function}{@code <}{@link Document}{@code , }{@link T}{@code >}。
     * @param <T> 表示 XML 节点的实际类型的 {@link T}。
     * @return 表示新创建的子元素的 {@link T}。
     * @throws IllegalArgumentException 当 {@code parentNode} 或 {@code factory} 为 {@code null} 时。
     * @throws IllegalStateException 当 {@code parentNode} 未属于任何文档时。
     */
    public static <T extends Node> T appendChild(Node parentNode, Function<Document, T> factory) {
        notNull(parentNode, "The parent node to append child node cannot be null.");
        notNull(factory, "The factory to create child node cannot be null.");
        Document document;
        if (parentNode instanceof Document) {
            document = (Document) parentNode;
        } else {
            document = parentNode.getOwnerDocument();
        }
        notNull(document,
                () -> new IllegalStateException("The XML node to create child does not belong to any document."));
        T child = factory.apply(document);
        parentNode.appendChild(child);
        return child;
    }

    /**
     * 在指定的节点下创建一个子元素。
     *
     * @param parentNode 表示待创建子元素的父节点的 {@link Node}。
     * @param tagName 表示子元素的标签名称的 {@link String}。
     * @return 表示新创建的子元素的 {@link Element}。
     * @throws IllegalArgumentException 当 {@code parentNode} 或 {@code tagName} 为 {@code null}，或 {@code tagName}
     * 为空字符串时。
     * @throws IllegalStateException 当 {@code parentNode} 未属于任何文档时。
     */
    public static Element appendElement(Node parentNode, String tagName) {
        notNull(parentNode, "The parent node to create child element cannot be null.");
        Validation.notBlank(tagName, "The name of child tag to create cannot be blank.");
        return appendChild(parentNode, document -> document.createElement(tagName));
    }

    /**
     * 在指定的节点下创建一个文本块。
     *
     * @param parentNode 表示待追加文本内容的父节点的 {@link Node}。
     * @param text 表示待追加的文本的 {@link String}。
     * @throws IllegalArgumentException {@code parentNode} 为 {@code null}。
     * @throws IllegalStateException 当 {@code parentNode} 未属于任何文档时。
     */
    public static void appendText(Node parentNode, String text) {
        notNull(parentNode, "The parent node to append text cannot be null.");
        if (StringUtils.isNotBlank(text)) {
            appendChild(parentNode, document -> document.createTextNode(text));
        }
    }

    /**
     * 获取指定节点下的指定 XPath 的子节点。
     *
     * @param parentNode 表示父节点的 {@link Node}。
     * @param xpath 表示待获取的子节点的 XPath 的 {@link String}。
     * @param <T> 表示子元素类型的 {@link T}。
     * @return 若存在 XPath 对应的子节点，则为表示该节点的 {@link Node}；否则为 {@code null}。
     */
    public static <T extends Node> T child(Node parentNode, String xpath) {
        if (StringUtils.isBlank(xpath)) {
            return cast(parentNode);
        }
        String[] path = StringUtils.split(xpath, '/');
        Node node = parentNode;
        for (int i = 0; i < path.length && node != null; i++) {
            node = firstElement(node.getChildNodes(), predicateTagName(path[i], false));
        }
        return cast(node);
    }

    /**
     * 创建一个 XML 文档。
     *
     * @return 表示新创建的 XML 文档的 {@link Document}。
     * @throws IllegalStateException 当创建的文档构建器无法满足默认的安全选项时。
     */
    public static Document createDocument() {
        return createDocumentBuilder().newDocument();
    }

    /**
     * 从指定的节点列表中过滤符合指定条件的节点。
     * <p>当 {@code predicate} 为 {@code null} 时则认为所有节点都符合条件。</p>
     *
     * @param nodes 表示待过滤的节点列表的 {@link NodeList}。
     * @param predicate 表示用以判定节点是否符合条件的校验器的 {@link Predicate}{@code <}{@link Node}{@code >}。
     * @return 表示符合条件的节点的列表的 {@link NodeList}。
     */
    public static NodeList filter(NodeList nodes, Predicate<Node> predicate) {
        if (nodes == null) {
            return null;
        }
        Predicate<Node> actualPredicate = FunctionUtils.and(PREDICATE_ELEMENT, predicate);
        List<Node> matchedNodes = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (actualPredicate.test(node)) {
                matchedNodes.add(node);
            }
        }
        return new ListNodeListAdapter(matchedNodes);
    }

    /**
     * 从指定的节点列表中过滤被设置了指定属性的节点。
     * <p>属性的值进行比较时，不忽略大小写。</p>
     *
     * @param nodes 表示待过滤的节点列表的 {@link NodeList}。
     * @param attribute 表示待匹配的属性的名称的 {@link String}。
     * @param value 表示待匹配的属性的值的 {@link String}。
     * @return 表示符合条件的节点的列表的 {@link NodeList}。
     * @see #filterByAttribute(NodeList, String, String, boolean)
     */
    public static NodeList filterByAttribute(NodeList nodes, String attribute, String value) {
        return filterByAttribute(nodes, attribute, value, false);
    }

    /**
     * 从指定的节点列表中过滤被设置了指定属性的节点。
     *
     * @param nodes 表示待过滤的节点列表的 {@link NodeList}。
     * @param attribute 表示待匹配的属性的名称的 {@link String}。
     * @param value 表示待匹配的属性的值的 {@link String}。
     * @param ignoreCase 若为 {@code true}，则匹配属性的值时忽略大小写；否则进行常规匹配。
     * @return 表示符合条件的节点的列表的 {@link NodeList}。
     * @see #filter(NodeList, Predicate)
     */
    public static NodeList filterByAttribute(NodeList nodes, String attribute, String value, boolean ignoreCase) {
        Predicate<Node> predicate = predicateAttribute(attribute, value, ignoreCase);
        return filter(nodes, predicate);
    }

    /**
     * 从指定的节点列表中过滤特定名称的节点。
     * <p>节点名称比较时，不忽略大小写。</p>
     *
     * @param nodes 表示待过滤的节点列表的 {@link NodeList}。
     * @param name 表示期望的节点名称的 {@link String}。
     * @return 表示符合条件的节点的列表的 {@link NodeList}。
     * @see #filterByName(NodeList, String, boolean)
     */
    public static NodeList filterByName(NodeList nodes, String name) {
        return filterByName(nodes, name, false);
    }

    /**
     * 从指定的节点列表中过滤特定名称的节点。
     *
     * @param nodes 表示待过滤的节点列表的 {@link NodeList}。
     * @param name 表示期望的节点名称的 {@link String}。
     * @param ignoreCase 若为 {@code true}，则匹配节点名称时忽略大小写；否则进行常规匹配。
     * @return 表示符合条件的节点的列表的 {@link NodeList}。
     * @see #filter(NodeList, Predicate)
     */
    public static NodeList filterByName(NodeList nodes, String name, boolean ignoreCase) {
        return filter(nodes, predicateTagName(name, ignoreCase));
    }

    /**
     * 若节点列表为空，则返回 {@code null}；否则返回第一个 {@link Element} 节点。
     *
     * @param nodes 表示节点列表的 {@link NodeList}。
     * @return 若节点列表不为空，则为第一个节点的 {@link Node}；否则为 {@code null}。
     * @see #firstElement(NodeList, Predicate)
     */
    public static Node firstElement(NodeList nodes) {
        return firstElement(nodes, null);
    }

    /**
     * 从指定的节点列表中过滤出第一个符合条件的 {@link Element} 节点。若没有符合条件的节点，则返回 {@code null}。
     * <p>当 {@code predicate} 为 {@code null} 时则认为所有节点都符合条件。</p>
     *
     * @param nodes 表示待过滤的节点列表的 {@link NodeList}。
     * @param predicate 表示用以判定节点是否符合条件的校验器的 {@link Predicate}{@code <}{@link Node}{@code >}。
     * @return 若存在符合条件的节点，则为第一个符合条件的节点的 {@link Node}；否则为 {@code null}。
     */
    public static Node firstElement(NodeList nodes, Predicate<Node> predicate) {
        if (nodes != null && nodes.getLength() > 0) {
            Predicate<Node> actualPredicate = FunctionUtils.and(PREDICATE_ELEMENT, predicate);
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (actualPredicate.test(node)) {
                    return node;
                }
            }
        }
        return null;
    }

    /**
     * 获取指定节点上定义的指定名称的属性的值。
     *
     * @param node 表示待获取属性的节点的 {@link Node}。
     * @param attribute 表示属性的名称的 {@link String}。
     * @return 若已定义了该属性，则为表示属性的值的 {@link String}；负责为 {@code null}。
     */
    public static String getAttributeValue(Node node, String attribute) {
        if (node == null || StringUtils.isBlank(attribute)) {
            return null;
        }
        NamedNodeMap attributeNodes = node.getAttributes();
        if (attributeNodes == null) {
            return null;
        }
        Node attributeNode = attributeNodes.getNamedItem(attribute);
        return attributeNode == null ? null : attributeNode.getTextContent();
    }

    /**
     * 获取指定节点指定属性的内容。
     *
     * @param node 表示指定节点的 {@link Node}。
     * @param attribute 表示指定属性的 {@link String}。
     * @return 表示指定节点指定属性的内容的 {@link String}。
     */
    public static String content(Node node, String attribute) {
        if (node == null || StringUtils.isBlank(attribute)) {
            return null;
        }
        Element child = child(node, attribute);
        return ObjectUtils.mapIfNotNull(child, Element::getTextContent);
    }

    /**
     * 从指定的输入流中解析 XML 文档。
     *
     * @param in 表示包含 XML 文档信息的输入流的 {@link InputStream}。
     * @return 表示从输入流中解析到的 XML 文档的 {@link Document}。
     * @throws IllegalArgumentException 当 {@code in} 为 {@code null} 时。
     * @throws IllegalStateException 当设置安全选项失败时，或创建的文档构建器无法满足默认的安全选项时，或解析 XML 时发生各种异常时。
     */
    public static Document load(InputStream in) {
        try {
            return createDocumentBuilder().parse(in);
        } catch (SAXException | IOException e) {
            throw new IllegalStateException("Failed to parse XML from the input stream.", e);
        }
    }

    /**
     * 设置 {@link DocumentBuilderFactory} 的安全选项。
     *
     * @param factory 表示待设置安全选项的文档构建器工厂的 {@link DocumentBuilderFactory}。
     * @throws IllegalStateException 当设置安全选项失败时。
     */
    private static void configureSecurity(DocumentBuilderFactory factory) {
        factory.setExpandEntityReferences(false);
        configureSecurity((feature, value) -> {
            try {
                factory.setFeature(feature, value);
            } catch (ParserConfigurationException e) {
                throw new IllegalStateException(StringUtils.format(
                        "Failed to {0} feature for document builder factory. [feature={1}]",
                        operation(value),
                        feature), e);
            }
        });
    }

    /**
     * 设置 {@link DocumentBuilderFactory} 的安全选项。
     *
     * @param factory 表示待设置安全选项的文档构建器工厂的 {@link DocumentBuilderFactory}。
     * @throws IllegalStateException 当设置安全选项失败时。
     */
    private static void configureSecurity(FeatureConfigurator factory) {
        factory.configure(FEATURE_DISALLOW_DOCTYPE_DECL, true);
        factory.configure(FEATURE_ALLOW_EXTERNAL_GENERAL_ENTITIES, false);
        factory.configure(FEATURE_ALLOW_EXTERNAL_PARAMETER_ENTITIES, false);
        factory.configure(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.configure(FEATURE_ALLOW_LOAD_EXTERNAL_DTD, false);
    }

    /**
     * 创建一个文档构建器，同时进行安全设置。
     *
     * @return 表示创建出来的文档构建器的 {@link DocumentBuilder}。
     * @throws IllegalStateException 当设置安全选项失败时，或者创建的文档构建器无法满足默认的安全选项时。
     */
    private static DocumentBuilder createDocumentBuilder() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        configureSecurity(factory);
        try {
            return factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("Failed to create document builder.", e);
        }
    }

    /**
     * 获取操作的名称。
     *
     * @param value 表示操作的值的布尔值的 {@code boolean}。
     * @return 若 {@code value} 为 {@code true}，则为 {@code enable}；否则为 {@code disable}。
     */
    private static String operation(boolean value) {
        return value ? "enable" : "disable";
    }

    /**
     * 生成一个用以通过指定属性值作为过滤条件的节点过滤校验器。
     *
     * @param attribute 表示待过滤的属性名的 {@link String}。
     * @param value 表示待过滤的属性值的 {@link String}。
     * @param ignoreCase 表示比较时是否忽略大小写的 {@code boolean}。
     * @return 表示用以判定 XML 节点的校验器的 {@link Predicate}{@code <}{@link Node}{@code >}。
     */
    private static Predicate<Node> predicateAttribute(String attribute, String value, boolean ignoreCase) {
        Equalizer<String> equalizer = StringUtils.equalizer(ignoreCase);
        return node -> equalizer.equals(getAttributeValue(node, attribute), value);
    }

    /**
     * 生成一个用以通过指定标签名称作为过滤条件的节点过滤校验器。
     * <p>节点标签名称又称作为节点名称，即 {@link Node#getNodeName()} 方法的返回值。</p>
     *
     * @param name 表示待过滤的标签名称的 {@link String}。
     * @param ignoreCase 表示比较时是否忽略大小写的 {@code boolean}。
     * @return 表示用以判定 XML 节点的校验器的 {@link Predicate}{@code <}{@link Node}{@code >}。
     */
    private static Predicate<Node> predicateTagName(String name, boolean ignoreCase) {
        Equalizer<String> equalizer = StringUtils.equalizer(ignoreCase);
        return node -> equalizer.equals(node.getNodeName(), name);
    }

    /**
     * 为特性提供配置方法。
     *
     * @author 梁济时
     * @since 2020-12-13
     */
    @FunctionalInterface
    private interface FeatureConfigurator {
        /**
         * 配置特性。
         *
         * @param feature 表示待配置的特性的 {@link String}。
         * @param value 若启用特性，则为 {@code true}；否则为 {@code false}。
         * @throws IllegalStateException 当配置错误时。
         */
        void configure(String feature, boolean value);
    }

    /**
     * 将指定的 XML 文档保存到输出流中。
     *
     * @param out 表示待将文档保存到的输出流的 {@link OutputStream}。
     * @return 表示文档的写入器的 {@link Writer}。
     * @throws IllegalArgumentException {@code out} 为 {@code null}。
     */
    public static Writer writer(OutputStream out) {
        return new Writer(out);
    }

    /**
     * 为文档提供写入程序。
     *
     * @author 梁济时
     * @since 2022-09-05
     */
    public static class Writer {
        private final OutputStream out;

        private int indentWidth;
        private boolean indentEnabled;

        private Writer(OutputStream out) {
            this.out = notNull(out, "The output stream of writer cannot be null.");
        }

        /**
         * 启用缩进。
         *
         * @return 表示当前写入程序的 {@link Writer}。
         */
        public Writer enableIndent() {
            this.indentEnabled = true;
            return this;
        }

        /**
         * 设置缩进的宽度。
         *
         * @param indentWidth 表示缩进宽度的 {@code int}。
         * @return 表示当前写入程序的 {@link Writer}。
         */
        public Writer indentWidth(int indentWidth) {
            this.indentWidth = indentWidth;
            return this;
        }

        private static void setFeature(TransformerFactory transformerFactory, String name, boolean isEnabled) {
            try {
                transformerFactory.setFeature(name, isEnabled);
            } catch (TransformerConfigurationException ex) {
                throw new IllegalStateException(StringUtils.format(
                        "Failed to {0} feature for document builder factory. [feature={1}]",
                        operation(true),
                        name), ex);
            }
        }

        /**
         * 写入指定的XML文档。
         *
         * @param document 表示待保存的XML文档的 {@link Document}。
         * @throws IllegalArgumentException {@code document} 为 {@code null}。
         * @throws IllegalStateException 保存文档过程发生XML异常。
         */
        public void write(Document document) {
            notNull(document, "The XML document to save cannot be null.");
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            setFeature(transformerFactory, XMLConstants.FEATURE_SECURE_PROCESSING, true);
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            Transformer transformer;
            try {
                transformer = transformerFactory.newTransformer();
            } catch (TransformerConfigurationException ex) {
                throw new IllegalStateException(StringUtils.format(
                        "Failed to instantiate transformer to save XML document. [error={0}]",
                        ex.getMessage()), ex);
            }
            if (this.indentEnabled) {
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
                        Integer.toString(this.indentWidth));
            }

            // OutputKeys.DOCTYPE_SYSTEM 未设置，因此 DOCTYPE_PUBLIC 属性不会生效，但此处必须设置该属性
            // 见 com.sun.org.apache.xml.internal.serializer.ToXMLStream 类的 startDocumentInternal() 方法中 164 行
            // 只有当 DOCTYPE_SYSTEM、DOCTYPE_PUBLIC 或 standalone 有值时，才会在根标签处换行
            // 否则根标签会紧跟在 XML 声明行后不换行
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, StringUtils.EMPTY);

            try {
                transformer.transform(new DOMSource(document), new StreamResult(this.out));
            } catch (TransformerException ex) {
                throw new IllegalStateException(StringUtils.format(
                        "Failed to save XML document to output stream. [error={0}]",
                        ex.getMessage()), ex);
            }
        }
    }

    /**
     * 将 XML 文件转换成 {@link Map}{@code <}{@link String}, {@link Object}{@code >} 格式。
     * 返回 {@link Map}{@code <}{@link String}, {@link Object}{@code >} 不包含根节点。
     *
     * @param document 表示待转换的 XML 文档的 {@link Document}。
     * @return 表示转换完成的 {@link Map}{@code <}{@link String}, {@link Object}{@code >}。
     * @throws IllegalArgumentException 当 {@code document} 为 {@code null} 时。
     */
    public static Map<String, Object> toMap(Document document) {
        notNull(document, "The XML document to parse cannot be null.");
        Map<String, Object> map = new HashMap<>();
        if (document.hasChildNodes()) {
            Node rootNode = document.getChildNodes().item(0);
            clearBlankTextNodes(rootNode);
            parseNode(rootNode, map);
            return map;
        }
        return MapBuilder.<String, Object>get().build();
    }

    /**
     * 将当前节点里的全部空文本节点删除。
     *
     * @param node 表示待删除空文子节点的根节点 {@link Node}。
     */
    private static void clearBlankTextNodes(Node node) {
        NodeList nodeList = node.getChildNodes();
        for (int i = nodeList.getLength() - 1; i >= 0; i--) {
            Node curNode = nodeList.item(i);
            if (curNode.getNodeType() == Node.TEXT_NODE && StringUtils.isBlank(curNode.getNodeValue())) {
                node.removeChild(curNode);
            } else {
                clearBlankTextNodes(curNode);
            }
        }
    }

    /**
     * 将根节点的全部子节点信息添加到 {@link Map}{@code <}{@link String}, {@link Object}{@code >}。
     *
     * @param node 表示待转换的根节点 {@link Node}。
     * @param map 表示待添加信息的 {@link Map}{@code <}{@link String}, {@link Object}{@code >}。
     */
    private static void parseNode(Node node, Map<String, Object> map) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node curNode = nodeList.item(i);
            String tagName = curNode.getNodeName();
            Map<String, Object> item = new HashMap<>();
            parseNodeAttributes(curNode, item);
            parseChild(curNode, item);
            insertToMap(map, tagName, item);
        }
    }

    /**
     * 将指定节点的属性分别添加到 {@link Map}{@code <}{@link String}, {@link Object}{@code >} 里。
     * <p>
     * <ul>
     *     <li>Key：指定属性的 {@link String}; 格式: @attributeName。</li>
     *     <li>Value：节点属性。</li>
     * </ul>
     * </p>
     *
     * @param node 表示指定节点的 {@link Node}。
     * @param item 表示存入数据的 {@link Map}{@code <}{@link String}, {@link Object}{@code >}。
     */
    private static void parseNodeAttributes(Node node, Map<String, Object> item) {
        if (node.hasAttributes()) {
            for (int i = 0; i < node.getAttributes().getLength(); i++) {
                Node attribute = node.getAttributes().item(i);
                item.put("@" + attribute.getNodeName(), attribute.getNodeValue());
            }
        }
    }

    /**
     * 将的子节点信息添加到 Map 里。
     *
     * @param node 表示提取数据的根节点 {@link Node}。
     * @param item 表示存入数据的 {@link Map}{@code <}{@link String}, {@link Object}{@code >}。
     */
    private static void parseChild(Node node, Map<String, Object> item) {
        if (node.hasChildNodes()) {
            // 检查子节点长度是否为 1 且是文本节点，用于避免子节点是空文本节点
            if (node.getChildNodes().getLength() == 1 && node.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE) {
                item.put(TEXT_TAG, node.getChildNodes().item(0).getTextContent());
            } else {
                parseNode(node, item);
            }
        } else {
            if (StringUtils.isNotBlank(node.getTextContent())) {
                item.put(TEXT_TAG, node.getTextContent());
            }
        }
    }

    /**
     * 将子节点信息存入父节点的信息 Map。
     * <p>
     * <ul>
     *     <li>Key：{@code tagName}。</li>
     *     <li>Value：{@code item}
     *     若键已经在 {@code map} 里，构建新数组 {@link List}{@code <}{@link Object}{@code >} 并存入；若否，则直接存入。</li>
     * </ul>
     * </p>
     *
     * @param map 表示存入数据的 {@link Map}{@code <}{@link String}, {@link Object}{@code >}。
     * @param tagName 表示待放入 {@code map} 里的键。
     * @param item 表示待放入 {@code map} 里的值。
     */
    private static void insertToMap(Map<String, Object> map, String tagName, Map<String, Object> item) {
        if (map.containsKey(tagName)) {
            Object existingValue = map.get(tagName);
            if (existingValue instanceof List) {
                List<Object> list = cast(existingValue);
                list.add(item);
            } else {
                List<Object> itemList = new ArrayList<>();
                itemList.add(existingValue);
                itemList.add(item);
                map.put(tagName, itemList);
            }
        } else {
            map.put(tagName, item);
        }
    }

    /**
     * 将 {@link #toMap(Document)} 输出的 {@code Map} 准备成可以转换成 Java Bean 的 {@code Map} 格式。
     *
     * @param map 表示待转换的 {@link Map}{@code <}{@link String}, {@link Object}{@code >}。
     * @return 表示转换完成的 {@link Map}{@code <}{@link Object}, {@link Object}{@code >}。
     */
    private static Map<Object, Object> reformat(Map<String, Object> map) {
        Map<Object, Object> newMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object key = ObjectUtils.toJavaObject(entry.getKey());
            Object value = ObjectUtils.toJavaObject(entry.getValue());
            Object text;
            if (value instanceof Map) {
                Map<String, Object> innerMap = cast(value);
                if (innerMap.containsKey(TEXT_TAG)) {
                    text = innerMap.get(TEXT_TAG);
                    newMap.put(key, text);
                } else {
                    newMap.put(key, reformat(innerMap));
                }
            } else if (value instanceof List) {
                List<Map<String, Object>> innerList = cast(value);
                List<Map<Object, Object>> resultList = new ArrayList<>();
                for (Map<String, Object> item : innerList) {
                    Map<String, Object> innerValue = cast(item);
                    resultList.add(reformat(innerValue));
                }
                newMap.put(key, resultList);
            } else {
                newMap.put(key, value);
            }
        }
        return newMap;
    }

    /**
     * 将 XML 文件的转换成指定类型的对象。
     *
     * @param document 表示待转换的 XML 文档的 {@link Document}。
     * @param type 表示指定类型的 {@link Type}。
     * @param <T> 表示转换后的对象的类型的 {@link T}。
     * @return 表示转换后的对象的 {@link T}。
     */
    public static <T> T toObject(Document document, Type type) {
        Map<String, Object> xmlMap = XmlUtils.toMap(document);
        Map<Object, Object> map = XmlUtils.reformat(xmlMap);
        return ObjectUtils.toCustomObject(map, type);
    }

    /**
     * 创建用以持有指定标签名称的节点的持有程序。
     *
     * @param name 表示待持有的 XML 节点标签名称的 {@link String}。
     * @return 表示持有该标签的持有程序的 {@link NodeHolder}。
     */
    public static NodeHolder holder(String name) {
        return new NodeHolder(name);
    }

    /**
     * 为 XML 中的节点提供持有程序。
     *
     * @author 梁济时
     * @since 2022-10-21
     */
    public static final class NodeHolder {
        private final String name;
        private Node node;

        private NodeHolder(String name) {
            this.name = name;
        }

        /**
         * 尝试接收指定 XML 节点。
         *
         * @param node 表示待接收的 XML 节点的 {@link Node}。
         * @return 表示当前持有程序的 {@link NodeHolder}。
         */
        public NodeHolder accept(Node node) {
            if (this.node == null && StringUtils.equalsIgnoreCase(node.getNodeName(), this.name)) {
                this.node = node;
            }
            return this;
        }

        /**
         * 尝试从指定的节点列表中接收节点。
         *
         * @param nodes 表示候选节点的列表的 {@link NodeList}。
         * @return 表示当前持有程序的 {@link NodeHolder}。
         */
        public NodeHolder accept(NodeList nodes) {
            accept(nodes, this);
            return this;
        }

        /**
         * 检查是否已经接收了节点。
         *
         * @return 若已接收了节点，则为 {@code true}，否则为 {@code false}。
         */
        public boolean ready() {
            return this.node != null;
        }

        /**
         * 获取已接收的节点。
         *
         * @return 表示已接收的节点的 {@link Node}。
         * @throws IllegalStateException 尚未接收节点。
         */
        public Node require() {
            if (this.node == null) {
                throw new IllegalStateException(StringUtils.format("Node {0} not found.", this.name));
            }
            return this.node;
        }

        /**
         * 获取子节点的列表。
         *
         * @return 表示所包含的子节点的 {@link NodeList}。
         * @throws IllegalStateException 尚未接收节点。
         */
        public NodeList children() {
            return this.require().getChildNodes();
        }

        /**
         * 获取节点包含的字符串的值。
         *
         * @return 表示包含的字符串的值的 {@link String}。
         * @throws IllegalStateException 尚未接收节点，或节点未包含有效的字符串的值。
         */
        public String stringValue() {
            String value = StringUtils.trim(this.require().getTextContent());
            if (StringUtils.isEmpty(value)) {
                throw new IllegalStateException(StringUtils.format("Content of node {0} is not specified.", this.name));
            }
            return value;
        }

        /**
         * 获取节点包含的 32 位整数的值。
         *
         * @return 表示 32 位整数的值。
         * @throws IllegalStateException 尚未接收节点，或节点的值不是有效的 32 位整数。
         */
        public int intValue() {
            String text = this.stringValue();
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException ex) {
                throw new IllegalStateException(StringUtils.format(
                        "Content of node {0} is not a 32-bit integer. [value={1}]",
                        this.name,
                        text));
            }
        }

        @Override
        public String toString() {
            return this.name;
        }

        /**
         * 在指定的节点列表中为指定持有程序接收节点。
         *
         * @param nodes 表示节点列表的 {@link NodeList}。
         * @param holders 表示待接收节点的持有程序的 {@link NodeHolder}{@code []}。
         */
        public static void accept(NodeList nodes, NodeHolder... holders) {
            for (int i = 0; i < nodes.getLength(); i++) {
                Node curNode = nodes.item(i);
                for (NodeHolder holder : holders) {
                    holder.accept(curNode);
                }
            }
        }
    }
}
